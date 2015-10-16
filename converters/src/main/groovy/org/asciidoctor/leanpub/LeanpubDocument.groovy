package org.asciidoctor.leanpub

/**
 * @author Schalk W. Cronjé
 */
class LeanpubDocument {

    ConvertedSection preamble
    ConvertedSection dedication
    ConvertedSection preface
    List<ConvertedPart> parts
    List<ConvertedSection> backmatter

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
        parts ? parts[-1] : null
    }

    ConvertedPart addPart() {
        if(parts==null) {
            parts = [ new ConvertedPart() ]
        } else {
            parts+= new ConvertedPart()
        }

        currentPart
    }

    void addChapterToPart(ConvertedSection chapter) {
        if(currentPart == null) {
            parts = [ new ConvertedPart() ]
        }

        currentPart.chapters+= chapter
    }

    ConvertedSection addBackmatter(ConvertedSection bm) {
        if(backmatter==null) {
            backmatter = [ bm ]
        } else {
            backmatter+= bm
        }
        backmatter[-1]
    }
}
