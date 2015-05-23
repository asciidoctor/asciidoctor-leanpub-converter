package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.FailsWith
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class AnchorsSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Anchors"() {
        setup:
            File chapter = new File(manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('anchors-and-references.adoc')

        then:
            chapter.text == '''# Chapter A

This is a reference to [Asciidoctor Project](#Asciidoctor-Project).

This is a [reference with text](#RefText2).
'''

    }

}