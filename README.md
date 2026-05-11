# An Example of Cappy

An example of the Python backend of Scala (named "Cappy").

## Prerequisites

- [`uv`](https://docs.astral.sh/uv/) — manages the Python environment
  and runs the generated Python.
- [`cs` (Coursier)](https://get-coursier.io/) — launches the `spc`
  Scala-Py compiler from a remote channel on demand.

## Running

```sh
./cappy run src/main.scala
```

This will:

1. Compile `.scala` files to Python.
2. Run the compiled file with `uv run python`.

## Layout

```
src/
  main.scala            -- entry @main, uses np.* and rich.*
  numpy/Facade.scala    -- package numpy: typed facade for numpy.ndarray
  rich/Facade.scala     -- package rich:  typed facade for the rich library
cappy                   -- compile + run wrapper script
pyproject.toml          -- Python deps for `uv`
```

The two facade files are typed Scala surfaces over the underlying
Python modules. `main.scala` imports them as ordinary Scala
packages:

```scala
import numpy.*
import rich.*
```
