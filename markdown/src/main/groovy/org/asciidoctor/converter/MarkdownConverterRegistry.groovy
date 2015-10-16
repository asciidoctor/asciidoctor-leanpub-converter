package org.asciidoctor.converter

import org.asciidoctor.Asciidoctor
import org.asciidoctor.converter.MarkdownConverter

/**
 * @author Schalk W. Cronjé
 */
class MarkdownConverterRegistry implements org.asciidoctor.converter.spi.ConverterRegistry {
    @Override
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaConverterRegistry().register(MarkdownConverter,'markdown')
    }
}
