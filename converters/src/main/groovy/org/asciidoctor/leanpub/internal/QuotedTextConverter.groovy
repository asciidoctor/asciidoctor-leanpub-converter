package org.asciidoctor.leanpub.internal

import org.asciidoctor.markdown.internal.InlineQuotedTextFormatter

/**
 * @author Schalk W. Cronjé
 */
class QuotedTextConverter extends InlineQuotedTextFormatter {
    static String latexmath(final String text) {
        '{$$}' + text + '{/$$}'
    }
}
