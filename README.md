# An Example of Cappy

An example of the Python backend of Scala 3 (named "Cappy").

## Prerequisites

- [`uv`](https://docs.astral.sh/uv/): manages the Python environment and runs the generated Python.
- [`cs` (Coursier)](https://get-coursier.io/): launches the Scala 3 to Python compiler.

## Running

```sh
./cappy run src/test-numpy.scala
./cappy run src/test-rich.scala
```

This will:

1. Compile `.scala` files to Python.
2. Run the compiled file with `uv run python`.

## Layout

```
src/
  test-numpy.scal       -- entry @main, exercises the numpy facade
  test-rich.scala       -- entry @main, exercises the rich facade
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
