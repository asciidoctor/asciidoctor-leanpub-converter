package org.asciidoctor.markdown.internal

import org.asciidoctor.Asciidoctor
//import org.asciidoctor.markdown.MarkdownConverter

/**
 * @author Schalk W. Cronjé
 */
class ConverterRegistry implements org.asciidoctor.converter.spi.ConverterRegistry {
    @Override
    void register(Asciidoctor asciidoctor) {
//        asciidoctor.javaConverterRegistry().register(MarkdownConverter,'markdown')
    }
}
