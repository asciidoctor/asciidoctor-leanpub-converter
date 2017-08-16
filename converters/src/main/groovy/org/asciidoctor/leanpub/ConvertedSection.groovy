package org.asciidoctor.leanpub

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.asciidoctor.converter.LeanpubConverter

/** A converted section is any chapter, preface, appendix etc.
 *
 * <p> It contains information on the following:
 * <ul>
 *     <li> Converted content,
 *     <li> The kind of section,
 *     <li> Whether the section should be included in a sample publication,
 *     <li> Any attached footnotes.
 *  </ul>
 */
@TupleConstructor
@CompileStatic
class ConvertedSection {

    enum SectionType {
        CHAPTER,
        PART,
        DEDICATION,
        PREFACE,
        PREAMBLE,
        BACKMATTER
    }

    LeanpubDocument document
    def content
    SectionType type
    boolean sample = false
    List<Integer> footnotes = []

    Writer write( Writer writer ) {
        writer << content

        for ( Integer index in footnotes) {
            writer << LeanpubConverter.LINESEP << document.footnotes.getMarkedUpFootnoteAt(index) << LeanpubConverter.LINESEP
        }
    }
}
