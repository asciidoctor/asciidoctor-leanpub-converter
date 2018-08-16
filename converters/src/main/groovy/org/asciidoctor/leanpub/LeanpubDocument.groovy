package org.asciidoctor.leanpub

import groovy.transform.CompileStatic
import org.asciidoctor.leanpub.internal.Footnote

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
    final Footnote footnotes = new Footnote()

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

    /** Adds a chapter to a part
     *
     * @param chapter Chapter to add
     * @return CHapter that was added
     */
    ConvertedSection addChapterToPart(ConvertedSection chapter) {
        if(currentPart == null) {
            parts.add new ConvertedPart()
        }

        currentPart.chapters+= chapter
        chapter
    }

    ConvertedSection addBackmatter(ConvertedSection bm) {
        backmatter.add bm
        backmatter[-1]
    }
}
