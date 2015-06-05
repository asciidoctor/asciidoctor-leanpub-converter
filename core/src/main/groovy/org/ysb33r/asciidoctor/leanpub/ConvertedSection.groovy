package org.ysb33r.asciidoctor.leanpub

import groovy.transform.TupleConstructor

/**
 * @author Schalk W. Cronjé
 */
@TupleConstructor
class ConvertedSection {

    enum SectionType {
        CHAPTER,
        PART,
        DEDICATION,
        PREFACE,
        BACKMATTER
    }

    def content
    SectionType type
    boolean sample = false

    Writer write( Writer writer ) {
        writer << content
    }
}
