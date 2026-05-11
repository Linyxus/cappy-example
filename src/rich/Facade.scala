// A typed Scala facade for Python's `rich` library.

package rich

import scala.python.*
import scala.annotation.targetName

// ====================================================================
//                Opaque types, bridges, and rich.* helpers
// ====================================================================

object core:
  opaque type Console  = PyDynamic
  opaque type Text     = PyDynamic
  opaque type Panel    = PyDynamic
  opaque type Table    = PyDynamic
  opaque type Tree     = PyDynamic
  opaque type Rule     = PyDynamic
  opaque type Markdown = PyDynamic
  opaque type Syntax   = PyDynamic
  opaque type Columns  = PyDynamic
  opaque type Progress = PyDynamic

  // All overloads have the same erased signature (PyDynamic)PyDynamic,
  // so each one needs a distinct @targetName.
  @targetName("asPyConsole")  inline def asPy(c: Console):  PyDynamic = c
  @targetName("asPyText")     inline def asPy(t: Text):     PyDynamic = t
  @targetName("asPyPanel")    inline def asPy(p: Panel):    PyDynamic = p
  @targetName("asPyTable")    inline def asPy(t: Table):    PyDynamic = t
  @targetName("asPyTree")     inline def asPy(t: Tree):     PyDynamic = t
  @targetName("asPyRule")     inline def asPy(r: Rule):     PyDynamic = r
  @targetName("asPyMarkdown") inline def asPy(m: Markdown): PyDynamic = m
  @targetName("asPySyntax")   inline def asPy(s: Syntax):   PyDynamic = s
  @targetName("asPyColumns")  inline def asPy(c: Columns):  PyDynamic = c
  @targetName("asPyProgress") inline def asPy(p: Progress): PyDynamic = p

  inline def consoleFromPy (d: PyDynamic): Console  = d
  inline def textFromPy    (d: PyDynamic): Text     = d
  inline def panelFromPy   (d: PyDynamic): Panel    = d
  inline def tableFromPy   (d: PyDynamic): Table    = d
  inline def treeFromPy    (d: PyDynamic): Tree     = d
  inline def ruleFromPy    (d: PyDynamic): Rule     = d
  inline def markdownFromPy(d: PyDynamic): Markdown = d
  inline def syntaxFromPy  (d: PyDynamic): Syntax   = d
  inline def columnsFromPy (d: PyDynamic): Columns  = d
  inline def progressFromPy(d: PyDynamic): Progress = d
end core

// Public package-level aliases. Opaque to outside callers because
// `core.Console` is opaque outside `object core`.
type Console  = core.Console
type Text     = core.Text
type Panel    = core.Panel
type Table    = core.Table
type Tree     = core.Tree
type Rule     = core.Rule
type Markdown = core.Markdown
type Syntax   = core.Syntax
type Columns  = core.Columns
type Progress = core.Progress

// Re-export bridges so companions and extensions in this package can
// call `asPy(...)` / `consoleFromPy(...)` unqualified.
export core.asPy
export core.{
  consoleFromPy, textFromPy, panelFromPy, tableFromPy, treeFromPy,
  ruleFromPy, markdownFromPy, syntaxFromPy, columnsFromPy, progressFromPy
}

@extern("rich")
private object _mod extends PyDynamic

def print(value: Any): Unit             = _mod.print(value)
def printJson(json: String): Unit        = _mod.print_json(json)
def inspect(value: Any): Unit            = _mod.inspect(value)

// ====================================================================
//                              Console
// ====================================================================

@extern("rich.console")
private object _richConsole extends PyDynamic

object Console:
  def apply(): Console = consoleFromPy(_richConsole.Console())
  /** A console that records output for later `exportText` / `exportHtml`. */
  def recording(): Console =
    consoleFromPy(_richConsole.Console(record = true))
  def recording(width: Int): Console =
    consoleFromPy(_richConsole.Console(record = true, width = width))

extension (c: Console)
  def print(value: Any): Unit                     = asPy(c).print(value)
  def print(a: Any, b: Any): Unit                 = asPy(c).print(a, b)
  def print(a: Any, b: Any, d: Any): Unit         = asPy(c).print(a, b, d)
  def log(value: Any): Unit                       = asPy(c).log(value)
  def rule(title: String): Unit                   = asPy(c).rule(title)
  def ruleStyled(title: String, style: String): Unit =
    asPy(c).rule(title, style = style)
  def line(): Unit                                = asPy(c).line()
  def line(count: Int): Unit                      = asPy(c).line(count)
  def clear(): Unit                               = asPy(c).clear()
  def bell(): Unit                                = asPy(c).bell()
  def input(prompt: String): String               = asPy(c).input(prompt).asInstanceOf[String]
  def exportText(): String                        = asPy(c).export_text().asInstanceOf[String]
  def exportHtml(): String                        = asPy(c).export_html().asInstanceOf[String]

// ====================================================================
//                              Text
// ====================================================================

@extern("rich.text")
private object _richText extends PyDynamic

object Text:
  def apply(text: String): Text                = textFromPy(_richText.Text(text))
  def apply(text: String, style: String): Text = textFromPy(_richText.Text(text, style = style))
  /** Parse rich markup such as `"[bold red]hi[/]"`. */
  def fromMarkup(markup: String): Text         = textFromPy(_richText.Text.from_markup(markup))

extension (t: Text)
  def append(text: String): Text                = textFromPy(asPy(t).append(text))
  def append(text: String, style: String): Text = textFromPy(asPy(t).append(text, style = style))
  def stylize(style: String): Unit              = asPy(t).stylize(style)
  def highlightRegex(regex: String, style: String): Unit =
    asPy(t).highlight_regex(regex, style)

// ====================================================================
//                              Panel
// ====================================================================

@extern("rich.panel")
private object _richPanel extends PyDynamic

object Panel:
  def apply(renderable: Any): Panel =
    panelFromPy(_richPanel.Panel(renderable))
  def apply(renderable: Any, title: String): Panel =
    panelFromPy(_richPanel.Panel(renderable, title = title))
  /** Full kwarg form. Pass null for unset values. */
  def styled(
    renderable: Any,
    title: String = null,
    subtitle: String = null,
    style: String = "none",
    borderStyle: String = "none",
    expand: Boolean = true,
    highlight: Boolean = false
  ): Panel =
    panelFromPy(_richPanel.Panel(
      renderable,
      title = title, subtitle = subtitle,
      style = style, border_style = borderStyle,
      expand = expand, highlight = highlight
    ))
  /** Shrinks the panel to the renderable's width. */
  def fit(renderable: Any): Panel =
    panelFromPy(_richPanel.Panel.fit(renderable))

// ====================================================================
//                              Table
// ====================================================================

@extern("rich.table")
private object _richTable extends PyDynamic

object Table:
  /** Empty table; populate with `addColumn`. */
  def apply(): Table =
    tableFromPy(_richTable.Table())
  /** Headered table. */
  def apply(h0: String, h1: String): Table =
    tableFromPy(_richTable.Table(h0, h1))
  def apply(h0: String, h1: String, h2: String): Table =
    tableFromPy(_richTable.Table(h0, h1, h2))
  def apply(h0: String, h1: String, h2: String, h3: String): Table =
    tableFromPy(_richTable.Table(h0, h1, h2, h3))

  def styled(
    title: String = null,
    caption: String = null,
    showHeader: Boolean = true,
    showFooter: Boolean = false,
    showEdge: Boolean = true,
    showLines: Boolean = false,
    expand: Boolean = false,
    highlight: Boolean = false
  ): Table =
    tableFromPy(_richTable.Table(
      title = title, caption = caption,
      show_header = showHeader, show_footer = showFooter,
      show_edge = showEdge, show_lines = showLines,
      expand = expand, highlight = highlight
    ))

  /** Borderless grid (handy for laying out renderables). */
  def grid(): Table = tableFromPy(_richTable.Table.grid())

extension (t: Table)
  def addColumn(header: String): Unit                       = asPy(t).add_column(header)
  def addColumn(header: String, justify: String): Unit      = asPy(t).add_column(header, justify = justify)
  def addColumn(header: String, style: String, justify: String): Unit =
    asPy(t).add_column(header, style = style, justify = justify)

  def addRow(c0: Any): Unit                                     = asPy(t).add_row(c0)
  def addRow(c0: Any, c1: Any): Unit                            = asPy(t).add_row(c0, c1)
  def addRow(c0: Any, c1: Any, c2: Any): Unit                   = asPy(t).add_row(c0, c1, c2)
  def addRow(c0: Any, c1: Any, c2: Any, c3: Any): Unit          = asPy(t).add_row(c0, c1, c2, c3)
  def addRow(c0: Any, c1: Any, c2: Any, c3: Any, c4: Any): Unit = asPy(t).add_row(c0, c1, c2, c3, c4)
  def addSection(): Unit                                        = asPy(t).add_section()

// ====================================================================
//                              Tree
// ====================================================================

@extern("rich.tree")
private object _richTree extends PyDynamic

object Tree:
  def apply(label: Any): Tree =
    treeFromPy(_richTree.Tree(label))
  def apply(label: Any, guideStyle: String): Tree =
    treeFromPy(_richTree.Tree(label, guide_style = guideStyle))
  /** Build a tree whose root is hidden — useful for forests. */
  def hidden(label: Any): Tree =
    treeFromPy(_richTree.Tree(label, hide_root = true))

extension (t: Tree)
  def add(label: Any): Tree                           = treeFromPy(asPy(t).add(label))
  def add(label: Any, style: String): Tree            = treeFromPy(asPy(t).add(label, style = style))
  def addStyled(label: Any, guideStyle: String): Tree =
    treeFromPy(asPy(t).add(label, guide_style = guideStyle))

// ====================================================================
//                              Rule
// ====================================================================

@extern("rich.rule")
private object _richRule extends PyDynamic

object Rule:
  def apply(): Rule              = ruleFromPy(_richRule.Rule())
  def apply(title: String): Rule = ruleFromPy(_richRule.Rule(title))
  def styled(
    title: String,
    style: String = "rule.line",
    align: String = "center",
    characters: String = "─"
  ): Rule =
    ruleFromPy(_richRule.Rule(
      title, style = style, align = align, characters = characters))

// ====================================================================
//                              Markdown
// ====================================================================

@extern("rich.markdown")
private object _richMarkdown extends PyDynamic

object Markdown:
  def apply(markup: String): Markdown =
    markdownFromPy(_richMarkdown.Markdown(markup))
  def styled(
    markup: String,
    codeTheme: String = "monokai",
    justify: String = "left"
  ): Markdown =
    markdownFromPy(_richMarkdown.Markdown(markup, code_theme = codeTheme, justify = justify))

// ====================================================================
//                              Syntax
// ====================================================================

@extern("rich.syntax")
private object _richSyntax extends PyDynamic

object Syntax:
  def apply(code: String, lexer: String): Syntax =
    syntaxFromPy(_richSyntax.Syntax(code, lexer))
  def styled(
    code: String,
    lexer: String,
    theme: String = "monokai",
    lineNumbers: Boolean = false,
    wordWrap: Boolean = false
  ): Syntax =
    syntaxFromPy(_richSyntax.Syntax(
      code, lexer,
      theme = theme, line_numbers = lineNumbers, word_wrap = wordWrap))
  def fromPath(path: String): Syntax =
    syntaxFromPy(_richSyntax.Syntax.from_path(path))

// ====================================================================
//                              Columns
// ====================================================================

@extern("rich.columns")
private object _richColumns extends PyDynamic

object Columns:
  def apply(): Columns = columnsFromPy(_richColumns.Columns())
  def apply(equal: Boolean, expand: Boolean): Columns =
    columnsFromPy(_richColumns.Columns(equal = equal, expand = expand))

extension (c: Columns)
  def addRenderable(r: Any): Unit = asPy(c).add_renderable(r)

// ====================================================================
//                          Prompts & Confirm
// ====================================================================

@extern("rich.prompt")
private object _richPrompt extends PyDynamic

object Prompts:
  def ask(prompt: String): String                  = _richPrompt.Prompt.ask(prompt).asInstanceOf[String]
  def ask(prompt: String, default: String): String = _richPrompt.Prompt.ask(prompt, default = default).asInstanceOf[String]
  def askInt(prompt: String): Int                  = _richPrompt.IntPrompt.ask(prompt).asInstanceOf[Int]
  def askDouble(prompt: String): Double            = _richPrompt.FloatPrompt.ask(prompt).asInstanceOf[Double]
  def confirm(prompt: String): Boolean             = _richPrompt.Confirm.ask(prompt).asInstanceOf[Boolean]

// ====================================================================
//                              Progress
// ====================================================================

@extern("rich.progress")
private object _richProgress extends PyDynamic

object Progress:
  def apply(): Progress = progressFromPy(_richProgress.Progress())

extension (p: Progress)
  def start(): Unit                                       = asPy(p).start()
  def stop(): Unit                                        = asPy(p).stop()
  def refresh(): Unit                                     = asPy(p).refresh()
  def addTask(description: String): Int                   = asPy(p).add_task(description).asInstanceOf[Int]
  def addTask(description: String, total: Double): Int    = asPy(p).add_task(description, total = total).asInstanceOf[Int]
  def advance(taskId: Int, step: Double): Unit            = asPy(p).advance(taskId, step)
  def setProgress(taskId: Int, advance: Double): Unit     = asPy(p).update(taskId, advance = advance)
