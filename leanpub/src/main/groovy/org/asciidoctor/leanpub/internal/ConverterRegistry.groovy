package org.asciidoctor.leanpub.internal

import org.asciidoctor.Asciidoctor
import org.asciidoctor.leanpub.LeanpubConverter

/**
 * @author Schalk W. Cronjé
 */
class ConverterRegistry implements org.asciidoctor.converter.spi.ConverterRegistry {
    @Override
    void register(Asciidoctor asciidoctor) {
        asciidoctor.javaConverterRegistry().register(LeanpubConverter,'leanpub')
    }
}
