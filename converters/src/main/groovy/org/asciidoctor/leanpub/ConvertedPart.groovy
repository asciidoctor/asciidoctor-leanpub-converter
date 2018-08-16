package org.asciidoctor.leanpub

/**
 *
 */
class ConvertedPart {
    final ConvertedSection.SectionType type = ConvertedSection.SectionType.PART

    boolean sample = false
    String title = ''
    def partIntro
    List<ConvertedSection> chapters = []

    Writer write(Writer writer) {
        writer << title
        if(partIntro) {
            writer << partIntro
        }

        writer
    }
}
