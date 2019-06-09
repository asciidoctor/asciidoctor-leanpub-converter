package org.asciidoctor.converter

import org.asciidoctor.Asciidoctor
import org.asciidoctor.converter.MarkdownConverter
import org.asciidoctor.jruby.converter.spi.ConverterRegistry

/**
 * @author Schalk W. Cronjé
 */
class MarkdownConverterRegistry implements ConverterRegistry {
    @Override
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaConverterRegistry().register(MarkdownConverter,'markdown')
    }
}
