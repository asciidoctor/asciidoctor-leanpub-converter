package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class PassthroughSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/22')
    def "Passthrough inline macro"() {
        setup:
            File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_1.txt')

        when:
            generateOutput('passthrough.adoc')

        then:
            chapter.text == '''# Passthrough inline macro

This line **must** *go* `asis` on a single line.
'''

    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/22')
    def "Passthrough block single line"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_2.txt')

        when:
        generateOutput('passthrough.adoc')

        then:
        chapter.text == '''# Passthrough block single line

<u>underline me</u> renders as underlined text.
This is an **extra** line.
'''

    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/22')
    def "Passthrough block multi-line"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_3.txt')

        when:
        generateOutput('passthrough.adoc')

        then:
        chapter.text == '''# Passthrough block multi-line

This line **must** *go* `asis` onn a single line.
This is an extra line.
'''

    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/22')
    def "Passthrough block multi-line with substitution"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_4.txt')

        when:
        generateOutput('passthrough.adoc')

        then:
        chapter.text == '''# Passthrough block multi-line with substitution

This line **must** *go* `asis` on a foo line.
This is an extra line.
'''

    }

}
