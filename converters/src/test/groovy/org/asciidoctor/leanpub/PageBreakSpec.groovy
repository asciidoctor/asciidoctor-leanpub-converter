package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * @author Schalk W. Cronjé
 */
class PageBreakSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/29')
    def "Horizontal Rule"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_1.txt')
        generateOutput('pagebreak.adoc')

        expect:
        chapter.text=='''# Page Break

This line is separated from the next

{pagebreak}

by a page break.
'''
    }

}





