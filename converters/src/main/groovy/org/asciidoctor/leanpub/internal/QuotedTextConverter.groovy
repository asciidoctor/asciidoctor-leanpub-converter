package org.asciidoctor.leanpub.internal

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.asciidoctor.markdown.internal.InlineQuotedTextFormatter

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class QuotedTextConverter extends InlineQuotedTextFormatter {
    static String latexmath(final String text) {
        '{$$}' + text + '{/$$}'
    }

    @CompileDynamic
    static String byMethod(final String name, final String text) {
        QuotedTextConverter."${name}"(text)
    }
}
