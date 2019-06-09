package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue
import spock.lang.PendingFeature

/**
 * Created by schalkc on 15/12/14.
 */
class AnchorsSpec extends LeanpubSpecification {

    void setup() {
        documentName = 'anchors-and-references'
    }

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Anchors"() {
        setup:
        setOutputRelativePath('Anchors')
        File chapter = chapterFromDocument(1)

        expect:
        chapter.text == '''# Chapter A

This is a cross-reference to [[Asciidoctor Project]](#Asciidoctor-Project).

This is a [reference with text](#RefText2).
'''

    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-links')
    def "Hyperlinks"() {
        setup:
        setOutputRelativePath('Hyperlinks')
        File chapter = chapterFromDocument(2)

        expect:
        chapter.text == '''# Chapter B

With reference to the [Spock Framework](http://docs.spockframework.org/en/latest) used in testing.
'''

    }

    @PendingFeature
    def "Embedded anchors with []"() {
        setup:
        setOutputRelativePath('EmbeddedAnchors')
        File chapter = chapterFromDocument(3)

        expect:
        chapter.text == '''# Chapter C

{#embeddedAnchor}
This chapter has an embedded anchor.
'''

    }

}