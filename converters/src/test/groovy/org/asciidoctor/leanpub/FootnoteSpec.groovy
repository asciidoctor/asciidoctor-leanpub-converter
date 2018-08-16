package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

class FootnoteSpec extends LeanpubSpecification {

    void setup() {
        documentName = 'footnotes'
    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/77")
    void "Footnote reference in preface"() {
        setup:
        File chapter = prefaceFromDocument()

        expect:
        chapter.text == '''# Preface

This is a preface.[^foot1]

[^foot1]: This is a footnote used in a preface and a chapter
'''

    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/77")
    void "Multiple footnotes in a chapter"() {
        setup:
        File chapter = chapterFromDocument(1)

        expect:
        chapter.text == '''# Multiple footnotes in a chapter

## Level 3

Full stop, space, then footnote. [^foot2]

### Level 4

Space, footnote, then full stop [^foot3].

#### Level 5

Full stop, no space, then footnote.[^foot4]

[^foot2]: FS-SP-FN this is footnote.

[^foot3]: SP-FN-FS this is footnote.

[^foot4]: FS-FN this is footnote.
'''
    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/77")
    void "Chapter twice re-using a footnote from preface"() {
        setup:
        File chapter = chapterFromDocument(2)

        expect:
        chapter.text == '''# Chapter re-using a footnote from a preface

With no text.[^foot1]

Some more nothing text.[^foot1]
'''
    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/77")
    void "Multi-line footnote in an appendix"() {
        setup:
        File chapter = backmatterFromDocument(3)

        String expectedText = '''# Appendix with multi-line footnote

Has nothing to do with a bodily part.[^foot5]

[^foot5]: Footnote in an appendix ''' + LINESEP +
            ' '.multiply(5) + LINESEP +
            ' '.multiply(5) + '''with multiple lines.
'''

        expect:
        chapter.text == expectedText
    }


}