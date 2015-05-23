package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class BlocksSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Anchors"() {
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


}