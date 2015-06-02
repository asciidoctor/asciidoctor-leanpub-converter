package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class BlocksSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Literal Block"() {
        setup:
            File chapter = new File(manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('blocks.adoc')

        then:
            chapter.text == '''# A Literal Block

{linenos=off}
    Kommetjie, Kommegas, wees my gas
    Hier in my houthuis
'''

    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/10, https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Poetry"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_2.txt')

        when:
        generateOutput('blocks.adoc')

        then:
        chapter.text == '''# A Poetry Block

{style="poem"}
~~~~~~
Kommetjie, Kommegas, wees my gas
Hier in my houthuis
~~~~~~
'''
    }

    def "Verse"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_3.txt')

        when:
        generateOutput('blocks.adoc')

        then:
        chapter.text == '''# A Verse Block

A> The fog comes
A> on little cat feet
A> ''' + LeanpubConverter.LINESEP +
'''A> -- **Carl Sandburg**
A> *Fog*
'''
    }

}