package org.asciidoctor.leanpub.internal

import org.asciidoctor.leanpub.LeanpubConverter

/**
 * @author Schalk W. Cronjé
 */
class QuotedTextConverter {
    static String strong(final String text) {
        "**${text}**"
    }

    static String emphasis(final String text) {
        "*${text}*"
    }

    static String underline(final String text) {
        "___${text}___"
    }

    static String superscript(final String text) {
        "^${text}^"
    }

    static String monospaced(final String text) {
        "`${text}`"
    }

    static String literal(final String text) {
        "```${text}```"
    }

    static String verse(final String text) {
        text
    }
}

// subscript
// double curved quotes
// single curved quotes