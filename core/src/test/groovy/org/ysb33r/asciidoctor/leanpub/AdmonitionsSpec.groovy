package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.FailsWith
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class AdmonitionsSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/4, https://leanpub.com/help/manual#leanpub-auto-warning')
    def "Warning"() {
        setup:
            File chapter = new File(manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('admonitions.adoc')

        then:
            chapter.text == '''# Chapter with Warning

Some text here, then

W> This is a warning admonition.
W>   And some more text.

'''

    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-warning')
    def "Warning with title"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_2.txt')

        when:
        generateOutput('admonitions.adoc')

        then:
        chapter.text == '''# Another Chapter with Warning

With some more text in between

W> ## Warning with title
W> This is a warning with title

'''

    }

}