package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
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

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/28')
    def "Caution and Important"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_3.txt')

        when:
        generateOutput('admonitions.adoc')

        then:
        chapter.text == '''# Caution and Important

{icon=fire}
G> This is a caution


Some text in between

{icon=university}
G> ## Yip, really important
G> This is important text


Some text afterwards
'''
    }

}