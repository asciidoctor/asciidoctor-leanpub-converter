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
        PREFACE,
        BACKMATTER,
        OTHER
    }

    def content
    SectionType type
    boolean sample = false

    Writer write( Writer writer ) {
        if(type == SectionType.PART) {
            for (line in content.toString().readLines())  {
                if( line.startsWith('# ')) {
                    break
                }
                writer.println line
            }
        } else {
            writer << content
        }
        writer
    }
}
