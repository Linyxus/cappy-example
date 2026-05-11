// A broad walk through the `rich` facade. Run with:
//   ./cappy run src/test-rich.scala

import rich.*

@main def testRich(): Unit =
  val con = Console()

  // ----- 1. Top-level rich.print --------------------------------------
  rich.print(Text.fromMarkup("[bold magenta]Rich Facade Demo[/]"))
  con.line()

  // ----- 2. Rules -----------------------------------------------------
  con.rule("Rules")
  con.ruleStyled("styled rule", "bold cyan")
  con.print(Rule.styled("standalone rule renderable", align = "left"))
  con.print(Rule.styled("with custom characters", characters = "═"))
  con.line()

  // ----- 3. Text ------------------------------------------------------
  con.rule("Text")
  con.print(Text("plain text"))
  con.print(Text("preset style", "bold red"))
  con.print(Text.fromMarkup("[italic yellow on blue]markup parsed[/]"))

  val composed = Text("Hello, ")
    .append("world", "bold green")
    .append("!", "underline magenta")
  con.print(composed)
  con.line()

  // ----- 4. Panels ----------------------------------------------------
  con.rule("Panels")
  con.print(Panel("a bare panel"))
  con.print(Panel("with a title", title = "Title"))
  con.print(Panel.styled(
    Text.fromMarkup("[bold]styled panel[/] with [red]rich[/] content inside"),
    title = "Styled",
    subtitle = "and a subtitle",
    borderStyle = "green",
    expand = false))
  con.print(Panel.fit(Text("fit() shrinks to content")))
  con.line()

  // ----- 5. Tables ----------------------------------------------------
  con.rule("Tables")
  val tbl = Table.styled(
    title = "Quarterly Sales",
    caption = "FY2025",
    showLines = true,
    highlight = true)
  tbl.addColumn("Quarter", "bold cyan", "center")
  tbl.addColumn("Revenue", justify = "right")
  tbl.addColumn("Growth", justify = "right")
  tbl.addRow("Q1", "$12,000", "[green]+5%[/]")
  tbl.addRow("Q2", "$15,500", "[green]+29%[/]")
  tbl.addSection()
  tbl.addRow("Q3", "$11,200", "[red]-28%[/]")
  tbl.addRow("Q4", "$18,800", "[green]+68%[/]")
  con.print(tbl)
  con.line()

  // Table.grid for borderless layout.
  val grid = Table.grid()
  grid.addColumn("left",  justify = "left")
  grid.addColumn("right", justify = "right")
  grid.addRow("[dim]label:[/]", "[bold]value[/]")
  grid.addRow("[dim]status:[/]", "[green]ok[/]")
  con.print(grid)
  con.line()

  // ----- 6. Tree ------------------------------------------------------
  con.rule("Tree")
  val tree = Tree("[bold]project/[/]")
  val srcDir = tree.add("[bold cyan]src/[/]")
  srcDir.add("main.scala")
  srcDir.add("test-rich.scala")
  val npDir = srcDir.add("[bold]numpy/[/]")
  npDir.add("Facade.scala")
  val richDir = srcDir.add("[bold]rich/[/]")
  richDir.add("Facade.scala")
  tree.addStyled("docs/", "dim")
  con.print(tree)
  con.line()

  // ----- 7. Markdown --------------------------------------------------
  con.rule("Markdown")
  con.print(Markdown(
    """## Features
      |
      |- Typed Scala surface over `rich`
      |- *Italic*, **bold**, and `inline code`
      |- Code blocks with syntax highlighting:
      |
      |```python
      |def hello(name: str) -> str:
      |    return f"Hello, {name}!"
      |```
      |
      |> A blockquote, for good measure.
      |""".stripMargin))
  con.line()

  // ----- 8. Syntax ----------------------------------------------------
  con.rule("Syntax")
  val pyCode =
    """def fib(n: int) -> int:
      |    if n < 2:
      |        return n
      |    return fib(n - 1) + fib(n - 2)
      |
      |for i in range(10):
      |    print(fib(i))
      |""".stripMargin
  con.print(Syntax.styled(pyCode, "python", theme = "monokai", lineNumbers = true))
  con.line()

  // ----- 9. Columns ---------------------------------------------------
  con.rule("Columns")
  val cols = Columns(equal = true, expand = true)
  cols.addRenderable(Panel.styled(Text("alpha"), title = "1", borderStyle = "red"))
  cols.addRenderable(Panel.styled(Text("beta"),  title = "2", borderStyle = "green"))
  cols.addRenderable(Panel.styled(Text("gamma"), title = "3", borderStyle = "blue"))
  cols.addRenderable(Panel.styled(Text("delta"), title = "4", borderStyle = "magenta"))
  con.print(cols)
  con.line()

  // ----- 10. Progress -------------------------------------------------
  // No animation — there's no `time.sleep` bound in this facade — but
  // the API surface is exercised: start, addTask, advance, refresh, stop.
  con.rule("Progress")
  val progress = Progress()
  progress.start()
  val taskId = progress.addTask("[cyan]Processing...", total = 100.0)
  var step = 0
  while step < 10 do
    progress.advance(taskId, 10.0)
    step += 1
  progress.refresh()
  progress.stop()
  con.line()

  // ----- 11. Recording console ---------------------------------------
  con.rule("Recording console")
  val rec = Console.recording(width = 50)
  rec.print(Text.fromMarkup("[bold]captured[/] line one"))
  rec.print(Panel.fit(Text("captured panel")))
  val captured = rec.exportText()
  con.print(Panel(captured, title = "exportText() output"))
  con.line()

  // ----- 12. Logging (timestamped) ------------------------------------
  con.rule("Console.log")
  con.log(Text.fromMarkup("[green]ok[/] — log() prepends a timestamp"))
  con.log("a second log entry")
  con.line()

  // ----- 13. Inspect --------------------------------------------------
  con.rule("Inspect")
  rich.inspect("a string value")
  con.line()

  con.rule("[bold green]demo complete[/]")
