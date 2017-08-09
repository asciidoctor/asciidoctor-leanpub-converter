package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class AnchorsSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Anchors"() {
        setup:
            File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_1.txt')

        when:
            generateOutput('anchors-and-references.adoc')

        then:
            chapter.text == '''# Chapter A

This is a cross-reference to [[Asciidoctor Project]](#Asciidoctor-Project).

This is a [reference with text](#RefText2).
'''

    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-links')
    def "Hyperlinks"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_2.txt')

        when:
        generateOutput('anchors-and-references.adoc')

        then:
        chapter.text == '''# Chapter B

With reference to the [Spock Framework](http://docs.spockframework.org/en/latest) used in testing.
'''

    }

    def "Embedded anchors with []"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_3.txt')

        when:
        generateOutput('anchors-and-references.adoc')

        then:
        chapter.text == '''# Chapter C

{#embeddedAnchor}
This chapter has an embedded anchor.
'''

    }

}