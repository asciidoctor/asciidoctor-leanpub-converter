package org.asciidoctor.leanpub.internal

/**
 * @author Schalk W. Cronjé
 */
class QuotedTextConverter extends org.asciidoctor.markdown.internal.QuotedTextConverter {
    static String latexmath(final String text) {
        '{$$}' + text + '{/$$}'
    }
}
