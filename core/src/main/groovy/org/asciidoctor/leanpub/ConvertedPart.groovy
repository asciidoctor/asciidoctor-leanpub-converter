package org.asciidoctor.leanpub

/**
 * @author Schalk W. Cronjé
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
            writer << LeanpubConverter.LINESEP
            writer << partIntro
        }

        writer
    }
}
