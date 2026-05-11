@main def run(): Unit =
  val con = Console()

  rich.print(Text.fromMarkup("[bold magenta]Cappy + Rich + NumPy[/]"))
  con.rule("Numpy stats")

  // ---- numpy: build a noisy line, recover slope via least squares.
  val noise = np.array(Array(0.1, -0.2, 0.05, 0.0, 0.15, -0.1, 0.2, -0.05, 0.0, 0.1))
  val xs = np.arange(0, 10).astype("float64")
  val ys = (xs * 2.0) + 1.0 + noise

  val xMean = xs.mean()
  val yMean = ys.mean()
  val dx = xs - xMean
  val dy = ys - yMean
  val slope =
    (dx * dy).sum().asInstanceOf[Double] / (dx * dx).sum().asInstanceOf[Double]
  val intercept = yMean - slope * xMean

  val stats = Table("metric", "value")
  stats.addRow("count",     xs.size.toString)
  stats.addRow("x.mean",    f"$xMean%.3f")
  stats.addRow("y.mean",    f"$yMean%.3f")
  stats.addRow("slope",     f"$slope%.4f")
  stats.addRow("intercept", f"$intercept%.4f")
  con.print(stats)

  // ---- Panel with rich markup inside.
  con.rule("Panel + markup")
  val resultText = Text.fromMarkup(
    f"Linear fit recovered slope ≈ [bold green]$slope%.2f[/]  " +
    f"intercept ≈ [bold green]$intercept%.2f[/]")
  con.print(Panel.styled(resultText, title = "Result", borderStyle = "cyan"))

  // ---- Tree of project sources.
  con.rule("Tree")
  val tree = Tree("[bold]cappy-example[/]")
  val srcs = tree.add("[bold]sources[/]")
  srcs.add("main.scala")
  srcs.add("np-facade.scala")
  srcs.add("rich-facade.scala")
  val cache = tree.add("[bold].cappy-cache/[/]")
  cache.add("main.py")
  cache.add("np-facade.py")
  cache.add("rich-facade.py")
  con.print(tree)

  // ---- Markdown.
  con.rule("Markdown")
  con.print(Markdown(
    """# Cappy
      |
      |**Cappy** runs Scala scripts via the *Scala-Py* backend.
      |```scala
      |@main def run(): Unit = println("Hello world from Cappy")
      |```
      |""".stripMargin))

  // ---- Syntax-highlighted code.
  con.rule("Syntax")
  val snippet =
    """@main def run(): Unit =
      |  val xs = np.arange(0, 10).astype("float64")
      |  Console().print(xs.tolist())
      |""".stripMargin
  con.print(Syntax.styled(snippet, "scala", lineNumbers = true))

  // ---- Columns of small panels.
  con.rule("Columns")
  val cols = Columns(equal = true, expand = true)
  cols.addRenderable(Panel("[green]numpy[/]", "facade"))
  cols.addRenderable(Panel("[blue]rich[/]",  "facade"))
  cols.addRenderable(Panel("[magenta]cappy[/]", "runner"))
  con.print(cols)

  con.rule(":waving_hand: done")
