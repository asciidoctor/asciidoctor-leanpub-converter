package org.ysb33r.asciidoctor

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
}

