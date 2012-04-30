package org.jetbrains.kotlin.template

import kotlin.io.*
import java.io.Writer
import java.io.StringWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.FileWriter
import java.io.File

/**
 * Represents a generic API to templates which should be usable
 * in JavaScript in a browser or on the server side stand alone or in a web app etc.
 *
 * To make things easier to implement in JS this namespace won't refer to any java.io or servlet
 * stuff
 */
trait Template {

    /** Renders the template to the output printer */
    fun render(): Unit
}

/**
 * Represents the output of a template which is a stream of objects
 * usually strings which then write to some underlying output stream
 * such as a java.io.Writer on the server side.
 *
 * We abstract away java.io.* APIs here to make the JS implementation simpler
 */
trait Printer {
    fun print(value: Any): Unit
    //fun print(text: String): Unit

}


class NullPrinter() : Printer {
    override fun print(value: Any) {
        throw UnsupportedOperationException("No Printer defined on the Template")
    }
}

/**
 * Base class for template implementations to hold any helpful behaviour
 */
abstract class TemplateSupport : Template {
}

val newline: String = System.getProperty("line.separator") ?: "\n"

/**
 * Base class for templates generating text output
 * by printing values to some underlying Printer which
 * will typically output to a String, File, stream etc.
 */
abstract class TextTemplate() : TemplateSupport(), Printer {
    public var printer: Printer = NullPrinter()

    fun String.plus(): Unit {
        printer.print(this)
    }

    override fun print(value: Any) = printer.print(value)

    fun println() {
        print(newline)
    }

    fun println(value: Any) {
        print(value)
        println()
    }

    fun text(text: String) {
        println(text) // TODO: escape
    }

    fun tag(name: String, content: TextTemplate.() -> Unit) {
        println("<$name>")
        content()
        println("</$name>")
    }

    fun tag(name: String, content: String) = tag(name) {
        text(content)
    }

    fun tag(name: String) {
        println("<$name>")
    }

    fun h2(content: TextTemplate.() -> Unit) = tag("H2", content)

    fun h2(content: String) = tag("H2", content)

    fun h3(content: TextTemplate.() -> Unit) = tag("H3", content)

    fun h3(content: String) = tag("H3", content)

    fun ul(content: TextTemplate.() -> Unit) = tag("UL", content)

    fun ul(content: String) = tag("UL", content)

    fun li(content: TextTemplate.() -> Unit) = tag("LI", content)

    fun li(content: String) = tag("LI", content)

    fun b(content: TextTemplate.() -> Unit) = tag("B", content)

    fun b(content: String) = tag("B", content)

    fun code(content: TextTemplate.() -> Unit) = tag("CODE", content)

    fun code(content: String) = tag("CODE", content)

    fun noscript(content: TextTemplate.() -> Unit) = tag("NOSCRIPT", content)

    fun hr() = tag("HR")

    fun p() = tag("P")

    fun br() = tag("BR")

    fun a(href: String, title: String? = null, content: TextTemplate.() -> Unit) {
        print("<A HREF='$href'") // TODO: escape
        if (title != null) {
            print("title='$title'") // TODO: escape
        }
        print(">")
        content()
        print("</A>")
        println()
    }

    fun a(href: String, title: String? = null, content: String) {
        a(href, title) {
            print(content)
        }
    }

    fun font(size: String, content: TextTemplate.() -> Unit) {
        print("<FONT SIZE='$size'>")
        content()
        print("</FONT>")
        println()
    }

    fun comment(comment: String) =
        println("<!-- $comment -->") // TODO: escape

    fun nbsp(count: Int = 1) {
        for (i in 0..count) {
            print("&nbsp")
        }
    }

    fun renderToText(): String {
        val buffer = StringWriter()
        renderTo(buffer)
        return buffer.toString().sure()
    }

    fun renderTo(writer: Writer): Unit {
        this.printer = WriterPrinter(writer)
        this.render()
    }

    fun renderTo(os: OutputStream): Unit {
        // TODO compiler error
        //OutputStreamWriter(os).forEach{ renderTo(it) }
        val s = OutputStreamWriter(os)
        renderTo(s)
        s.close()
    }

    fun renderTo(file: File): Unit {
        // TODO compiler error
        //FileWriter(file).forEach{ s -> renderTo(s) }
        val s = FileWriter(file)
        renderTo(s)
        s.close()
    }
}


/**
 * A Printer implementation which uses a Writer
 */
class WriterPrinter(val writer: Writer) : Printer {

    override fun print(value: Any) {
        // TODO should be using a formatter to do the conversion
        writer.write(value.toString())
    }
}
