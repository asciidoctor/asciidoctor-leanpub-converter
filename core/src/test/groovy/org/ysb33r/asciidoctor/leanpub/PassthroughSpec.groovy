package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Ignore
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class PassthroughSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/22')
    def "Passthrough inline macro"() {
        setup:
            File chapter = new File(manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('passthrough.adoc')

        then:
            chapter.text == '''# Passthrough inline macro

This line **must** *go* `asis` on a single line.
'''

    }

    @Ignore
    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/27')
    def "Passthrough block macro"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_2.txt')

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
        File chapter = new File(manuscriptDir,'chapter_3.txt')

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
        File chapter = new File(manuscriptDir,'chapter_4.txt')

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
        File chapter = new File(manuscriptDir,'chapter_5.txt')

        when:
        generateOutput('passthrough.adoc')

        then:
        chapter.text == '''# Passthrough block multi-line with substitution

This line **must** *go* `asis` on a foo line.
This is an extra line.
'''

    }

}
