package org.asciidoctor.leanpub

import groovy.transform.CompileStatic

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class LeanpubDocument {

    ConvertedSection preamble
    ConvertedSection dedication
    ConvertedSection preface
    final List<ConvertedPart> parts = []
    final List<ConvertedSection> backmatter = []

    boolean hasFrontMatter() {
        dedication != null || preface != null || preamble != null
    }

    boolean hasBackMatter() {
        backmatter?.size() > 0
    }
    boolean isMultiPart() {
        parts?.size() > 1
    }

    ConvertedPart getCurrentPart() {
        parts.empty ? null : parts[-1]
    }

    ConvertedPart addPart() {
        parts.add new ConvertedPart()
        currentPart
    }

    void addChapterToPart(ConvertedSection chapter) {
        if(currentPart == null) {
            parts.add new ConvertedPart()
        }

        currentPart.chapters+= chapter
    }

    ConvertedSection addBackmatter(ConvertedSection bm) {
        backmatter.add bm
        backmatter[-1]
    }
}
