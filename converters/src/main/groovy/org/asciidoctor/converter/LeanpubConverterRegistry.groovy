package org.asciidoctor.converter

import org.asciidoctor.Asciidoctor
import org.asciidoctor.converter.LeanpubConverter

/**
 * @author Schalk W. Cronjé
 */
class LeanpubConverterRegistry implements org.asciidoctor.converter.spi.ConverterRegistry {
    @Override
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaConverterRegistry().register(LeanpubConverter,'leanpub')
    }
}
