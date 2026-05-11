// A typed Scala facade for `numpy.ndarray`.
//
// `NDArray` is an opaque type alias for `PyDynamic`. At runtime an
// `NDArray` value IS the underlying Python `numpy.ndarray` — no Scala
// wrapper instance, no extra field to deref, no per-call allocation.
// The opaqueness is purely a compile-time discipline that hides the raw
// `PyDynamic` API and gives callers a typed surface.
//
// Why the extensions live OUTSIDE the `np` object:
// inside the defining object the alias is transparent, so `a.foo` on
// either static type can match an extension method on `NDArray` —
// recursing forever instead of falling through to `PyDynamic`'s
// `Dynamic` dispatch. Defining the extensions in a sibling scope makes
// the alias opaque at the point where the bodies are checked, so
// `a.py.foo(...)` goes through `selectDynamic` / `applyDynamic` exactly
// like raw `PyDynamic`. The bridge is a pair of inline no-op casts on
// `np`: `asPy: NDArray => PyDynamic` and `fromPy: PyDynamic => NDArray`,
// each alias-transparent inside `np`.

package numpy

import scala.python.*

// File-private extern surface, accessible to both `np` and the sibling
// extension object below.

@extern("numpy")
private object _np extends PyDynamic

@extern("operator")
private object _pyOp extends PyAny:
  @name("getitem") def getItem(c: Any, k: Any): PyDynamic = native
  @name("setitem") def setItem(c: Any, k: Any, v: Any): Unit = native

@extern("builtins")
private object _pyBuiltins extends PyAny:
  @name("len")   def lengthOf(c: Any): Int = native
  @name("float") def toFloat(v: Any): Double = native
  @name("int")   def toInt(v: Any): Int = native
  @name("slice") def slice(start: Any, stop: Any, step: Any): PyDynamic = native

// ====================================================================
//                           The opaque type
// ====================================================================

object np:

  /** Typed view over a Python `numpy.ndarray`. Erases to `PyDynamic`. */
  opaque type NDArray = PyDynamic

  /** Inline no-op cast. Public so the sibling extension object can
   *  reach the underlying `PyDynamic` and the alias-transparent
   *  resolution stays inside `np`. */
  inline def asPy(a: NDArray): PyDynamic = a
  inline def fromPy(d: PyDynamic): NDArray = d

  /** Evidence that `T` is a valid numpy shape: either `Int` (a 1-D
   *  shape) or a tuple whose elements are all `Int` (any arity,
   *  including empty for a 0-D shape). */
  opaque type IsShape[T] = Unit
  object IsShape:
    given IsShape[Int] = ()
    given [T <: Tuple](using Tuple.Union[T] <:< Int): IsShape[T] = ()

  // ----- Constants ------------------------------------------------------

  def pi: Double  = _np.pi.asInstanceOf[Double]
  def e: Double   = _np.e.asInstanceOf[Double]
  def inf: Double = _np.inf.asInstanceOf[Double]
  def nan: Double = _np.nan.asInstanceOf[Double]

  // ----- Constructors ---------------------------------------------------

  def array(values: Any): NDArray = _np.array(values)
  def array(values: Any, dtype: String): NDArray =
    _np.array(values, dtype = dtype)
  def asarray(values: Any): NDArray = _np.asarray(values)
  def zeros[T: IsShape](shape: T): NDArray = _np.zeros(shape)
  def zeros[T: IsShape](shape: T, dtype: String): NDArray =
    _np.zeros(shape, dtype = dtype)
  def ones[T: IsShape](shape: T): NDArray = _np.ones(shape)
  def ones[T: IsShape](shape: T, dtype: String): NDArray =
    _np.ones(shape, dtype = dtype)
  def full[T: IsShape](shape: T, fillValue: Any): NDArray =
    _np.full(shape, fillValue)
  def full[T: IsShape](shape: T, fillValue: Any, dtype: String): NDArray =
    _np.full(shape, fillValue, dtype = dtype)
  def empty[T: IsShape](shape: T): NDArray = _np.empty(shape)
  def eye(n: Int): NDArray         = _np.eye(n)
  def eye(n: Int, m: Int): NDArray = _np.eye(n, m)
  def identity(n: Int): NDArray    = _np.identity(n)

  def arange(stop: Int): NDArray = _np.arange(stop)
  def arange(start: Int, stop: Int): NDArray = _np.arange(start, stop)
  def arange(start: Int, stop: Int, step: Int): NDArray =
    _np.arange(start, stop, step)
  def arange(stop: Double): NDArray = _np.arange(stop)
  def arange(start: Double, stop: Double, step: Double): NDArray =
    _np.arange(start, stop, step)

  def linspace(start: Double, stop: Double, num: Int): NDArray =
    _np.linspace(start, stop, num = num)
  def linspace(start: Double, stop: Double, num: Int, endpoint: Boolean): NDArray =
    _np.linspace(start, stop, num = num, endpoint = endpoint)

  def zerosLike(a: NDArray): NDArray = _np.zeros_like(a)
  def onesLike(a: NDArray): NDArray  = _np.ones_like(a)
  def fullLike(a: NDArray, value: Any): NDArray = _np.full_like(a, value)
  def emptyLike(a: NDArray): NDArray = _np.empty_like(a)

  // ----- Universal functions (free, elementwise) -----------------------

  def sqrt(a: NDArray): NDArray  = _np.sqrt(a)
  def exp(a: NDArray): NDArray   = _np.exp(a)
  def log(a: NDArray): NDArray   = _np.log(a)
  def log2(a: NDArray): NDArray  = _np.log2(a)
  def log10(a: NDArray): NDArray = _np.log10(a)
  def absOf(a: NDArray): NDArray = _np.abs(a)
  def sign(a: NDArray): NDArray  = _np.sign(a)
  def sin(a: NDArray): NDArray   = _np.sin(a)
  def cos(a: NDArray): NDArray   = _np.cos(a)
  def tan(a: NDArray): NDArray   = _np.tan(a)
  def floor(a: NDArray): NDArray = _np.floor(a)
  def ceil(a: NDArray): NDArray  = _np.ceil(a)
  def round(a: NDArray): NDArray = _np.round(a)
  def round(a: NDArray, decimals: Int): NDArray =
    _np.round(a, decimals = decimals)

  def clip(a: NDArray, lo: Any, hi: Any): NDArray = _np.clip(a, lo, hi)
  def maximum(a: NDArray, b: NDArray): NDArray    = _np.maximum(a, b)
  def minimum(a: NDArray, b: NDArray): NDArray    = _np.minimum(a, b)

  // ----- Linear algebra -------------------------------------------------

  def dot(a: NDArray, b: NDArray): NDArray    = _np.dot(a, b)
  def matmul(a: NDArray, b: NDArray): NDArray = _np.matmul(a, b)
  def inner(a: NDArray, b: NDArray): NDArray  = _np.inner(a, b)
  def outer(a: NDArray, b: NDArray): NDArray  = _np.outer(a, b)
  def cross(a: NDArray, b: NDArray): NDArray  = _np.cross(a, b)
  def trace(a: NDArray): PyDynamic            = _np.trace(a)

  // ----- Combining / splitting -----------------------------------------

  def concatenate(arrays: NDArray*): NDArray =
    _np.concatenate(arrays.toArray)
  def concatenateOn(axis: Int, arrays: NDArray*): NDArray =
    _np.concatenate(arrays.toArray, axis = axis)
  def stack(arrays: NDArray*): NDArray =
    _np.stack(arrays.toArray)
  def stackOn(axis: Int, arrays: NDArray*): NDArray =
    _np.stack(arrays.toArray, axis = axis)
  def vstack(arrays: NDArray*): NDArray = _np.vstack(arrays.toArray)
  def hstack(arrays: NDArray*): NDArray = _np.hstack(arrays.toArray)
  def split(a: NDArray, sections: Int): PyDynamic = _np.split(a, sections)
  def split(a: NDArray, sections: Int, axis: Int): PyDynamic =
    _np.split(a, sections, axis = axis)

  // ----- Reshape / axis manipulation -----------------------------------

  def expandDims(a: NDArray, axis: Int): NDArray = _np.expand_dims(a, axis = axis)
  def moveaxis(a: NDArray, src: Int, dst: Int): NDArray =
    _np.moveaxis(a, src, dst)

  // ----- Search / sort / where -----------------------------------------

  def where(cond: NDArray, x: NDArray, y: NDArray): NDArray =
    _np.where(cond, x, y)
  def whereOf(cond: NDArray): PyDynamic = _np.where(cond)
  def sort(a: NDArray): NDArray         = _np.sort(a)
  def argsort(a: NDArray): NDArray      = _np.argsort(a)
  def unique(a: NDArray): NDArray       = _np.unique(a)
  def nonzero(a: NDArray): PyDynamic    = _np.nonzero(a)
  def take(a: NDArray, indices: Any): NDArray = _np.take(a, indices)
  def isnan(a: NDArray): NDArray        = _np.isnan(a)
  def isfinite(a: NDArray): NDArray     = _np.isfinite(a)
  def isinf(a: NDArray): NDArray        = _np.isinf(a)
  def arrayEqual(a: NDArray, b: NDArray): Boolean =
    _np.array_equal(a, b).asInstanceOf[Boolean]
  def allclose(a: NDArray, b: NDArray): Boolean =
    _np.allclose(a, b).asInstanceOf[Boolean]
  def allclose(a: NDArray, b: NDArray, rtol: Double, atol: Double): Boolean =
    _np.allclose(a, b, rtol = rtol, atol = atol).asInstanceOf[Boolean]

end np

// ====================================================================
//                           Extension methods
//
// Defined OUTSIDE `np`, where the alias is opaque. `a.py` lifts back
// to `PyDynamic` via the public `np.asPy`; calls of the form
// `a.py.foo(...)` therefore go through `PyDynamic`'s `Dynamic` dispatch
// (the receiver's static type is `PyDynamic`, not `np.NDArray`, so no
// extension method on `np.NDArray` is in the lookup set). Returns of
// the form `np.fromPy(...)` lift the `PyDynamic` back to `NDArray`.
// ====================================================================

extension (a: np.NDArray)

  /** Escape hatch back to `PyDynamic`. Inline + no-op at runtime. */
  inline def py: PyDynamic = np.asPy(a)

  // ----- Attributes ----------------------------------------------------

  def shape: PyDynamic   = a.py.shape
  def ndim: Int          = a.py.ndim.asInstanceOf[Int]
  def size: Int          = a.py.size.asInstanceOf[Int]
  def dtype: PyDynamic   = a.py.dtype
  def dtypeName: String  = a.py.dtype.name.asInstanceOf[String]
  def itemsize: Int      = a.py.itemsize.asInstanceOf[Int]
  def nbytes: Int        = a.py.nbytes.asInstanceOf[Int]
  def T: np.NDArray      = np.fromPy(a.py.T)
  def real: np.NDArray   = np.fromPy(a.py.real)
  def imag: np.NDArray   = np.fromPy(a.py.imag)

  // ----- Reshaping / axes ---------------------------------------------

  def reshape(s0: Int): np.NDArray =
    np.fromPy(a.py.reshape(s0))
  def reshape(s0: Int, s1: Int): np.NDArray =
    np.fromPy(a.py.reshape(s0, s1))
  def reshape(s0: Int, s1: Int, s2: Int): np.NDArray =
    np.fromPy(a.py.reshape(s0, s1, s2))
  def reshape[T: np.IsShape](shape: T): np.NDArray =
    np.fromPy(a.py.reshape(shape))
  def ravel(): np.NDArray     = np.fromPy(a.py.ravel())
  def flatten(): np.NDArray   = np.fromPy(a.py.flatten())
  def transpose(): np.NDArray = np.fromPy(a.py.transpose())
  def squeeze(): np.NDArray   = np.fromPy(a.py.squeeze())
  def swapaxes(x: Int, y: Int): np.NDArray =
    np.fromPy(a.py.swapaxes(x, y))

  // ----- Indexing / assignment ----------------------------------------

  def apply(i: Int): np.NDArray =
    np.fromPy(_pyOp.getItem(a.py, i))
  def apply(i: Int, j: Int): np.NDArray =
    np.fromPy(_pyOp.getItem(a.py, (i, j)))
  def apply(i: Int, j: Int, k: Int): np.NDArray =
    np.fromPy(_pyOp.getItem(a.py, (i, j, k)))

  /** Slice along the leading axis: `arr.range(start, stop)`. */
  def range(start: Int, stop: Int): np.NDArray =
    np.fromPy(_pyOp.getItem(a.py, _pyBuiltins.slice(start, stop, null)))
  def range(start: Int, stop: Int, step: Int): np.NDArray =
    np.fromPy(_pyOp.getItem(a.py, _pyBuiltins.slice(start, stop, step)))

  def update(i: Int, value: Any): Unit =
    _pyOp.setItem(a.py, i, value)
  def update(i: Int, j: Int, value: Any): Unit =
    _pyOp.setItem(a.py, (i, j), value)

  // ----- Materialise / convert ----------------------------------------

  def item(): PyDynamic    = a.py.item()
  def itemAsDouble: Double = _pyBuiltins.toFloat(a.py.item())
  def itemAsInt: Int       = _pyBuiltins.toInt(a.py.item())
  def tolist(): PyDynamic  = a.py.tolist()
  def copy(): np.NDArray   = np.fromPy(a.py.copy())
  def astype(dtype: String): np.NDArray = np.fromPy(a.py.astype(dtype))

  // ----- Reductions ---------------------------------------------------

  def sum(): PyDynamic            = a.py.sum()
  def sum(axis: Int): np.NDArray  = np.fromPy(a.py.sum(axis = axis))
  def mean(): Double              = _pyBuiltins.toFloat(a.py.mean())
  def mean(axis: Int): np.NDArray = np.fromPy(a.py.mean(axis = axis))
  def min(): PyDynamic            = a.py.min()
  def min(axis: Int): np.NDArray  = np.fromPy(a.py.min(axis = axis))
  def max(): PyDynamic            = a.py.max()
  def max(axis: Int): np.NDArray  = np.fromPy(a.py.max(axis = axis))
  def std(): Double               = _pyBuiltins.toFloat(a.py.std())
  def std(axis: Int): np.NDArray  = np.fromPy(a.py.std(axis = axis))
  def variance(): Double          = _pyBuiltins.toFloat(a.py.`var`())
  def prod(): PyDynamic           = a.py.prod()
  def prod(axis: Int): np.NDArray = np.fromPy(a.py.prod(axis = axis))
  def all(): Boolean              = a.py.all().asInstanceOf[Boolean]
  def any(): Boolean              = a.py.any().asInstanceOf[Boolean]
  def argmin(): Int               = a.py.argmin().asInstanceOf[Int]
  def argmax(): Int               = a.py.argmax().asInstanceOf[Int]
  def cumsum(): np.NDArray        = np.fromPy(a.py.cumsum())
  def cumprod(): np.NDArray       = np.fromPy(a.py.cumprod())

  // ----- Arithmetic operators (Python dunders) -----------------------

  def +(other: np.NDArray): np.NDArray = np.fromPy(a.py.__add__(other.py))
  def +(other: Double): np.NDArray     = np.fromPy(a.py.__add__(other))
  def +(other: Int): np.NDArray        = np.fromPy(a.py.__add__(other))
  def -(other: np.NDArray): np.NDArray = np.fromPy(a.py.__sub__(other.py))
  def -(other: Double): np.NDArray     = np.fromPy(a.py.__sub__(other))
  def -(other: Int): np.NDArray        = np.fromPy(a.py.__sub__(other))
  def *(other: np.NDArray): np.NDArray = np.fromPy(a.py.__mul__(other.py))
  def *(other: Double): np.NDArray     = np.fromPy(a.py.__mul__(other))
  def *(other: Int): np.NDArray        = np.fromPy(a.py.__mul__(other))
  def /(other: np.NDArray): np.NDArray = np.fromPy(a.py.__truediv__(other.py))
  def /(other: Double): np.NDArray     = np.fromPy(a.py.__truediv__(other))
  def /(other: Int): np.NDArray        = np.fromPy(a.py.__truediv__(other))
  def %(other: np.NDArray): np.NDArray = np.fromPy(a.py.__mod__(other.py))
  def %(other: Int): np.NDArray        = np.fromPy(a.py.__mod__(other))
  def **(other: np.NDArray): np.NDArray = np.fromPy(a.py.__pow__(other.py))
  def **(other: Double): np.NDArray     = np.fromPy(a.py.__pow__(other))
  def **(other: Int): np.NDArray        = np.fromPy(a.py.__pow__(other))

  /** `a @@ b` → numpy `a @ b` matrix multiplication. */
  def @@(other: np.NDArray): np.NDArray =
    np.fromPy(a.py.__matmul__(other.py))

  def unary_- : np.NDArray = np.fromPy(a.py.__neg__())

  // ----- Comparisons --------------------------------------------------
  //
  // `==` / `!=` are final on Any, so we use `equ` / `neq` for
  // elementwise equality. Ordering operators ARE overridable.

  def equ(other: np.NDArray): np.NDArray = np.fromPy(a.py.__eq__(other.py))
  def equ(other: Double): np.NDArray     = np.fromPy(a.py.__eq__(other))
  def equ(other: Int): np.NDArray        = np.fromPy(a.py.__eq__(other))
  def neq(other: np.NDArray): np.NDArray = np.fromPy(a.py.__ne__(other.py))
  def neq(other: Double): np.NDArray     = np.fromPy(a.py.__ne__(other))

  def <(other: np.NDArray): np.NDArray  = np.fromPy(a.py.__lt__(other.py))
  def <(other: Double): np.NDArray      = np.fromPy(a.py.__lt__(other))
  def <(other: Int): np.NDArray         = np.fromPy(a.py.__lt__(other))
  def <=(other: np.NDArray): np.NDArray = np.fromPy(a.py.__le__(other.py))
  def <=(other: Double): np.NDArray     = np.fromPy(a.py.__le__(other))
  def >(other: np.NDArray): np.NDArray  = np.fromPy(a.py.__gt__(other.py))
  def >(other: Double): np.NDArray      = np.fromPy(a.py.__gt__(other))
  def >(other: Int): np.NDArray         = np.fromPy(a.py.__gt__(other))
  def >=(other: np.NDArray): np.NDArray = np.fromPy(a.py.__ge__(other.py))
  def >=(other: Double): np.NDArray     = np.fromPy(a.py.__ge__(other))
