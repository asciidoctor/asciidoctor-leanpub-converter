package org.asciidoctor.converter

import org.asciidoctor.Asciidoctor
import org.asciidoctor.converter.LeanpubConverter
import org.asciidoctor.jruby.converter.spi.ConverterRegistry

/**
 * @author Schalk W. Cronjé
 */
class LeanpubConverterRegistry implements ConverterRegistry {
    @Override
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaConverterRegistry().register(LeanpubConverter,'leanpub')
    }
}
