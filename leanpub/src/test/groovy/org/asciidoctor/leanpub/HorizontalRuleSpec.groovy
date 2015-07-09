package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * @author Schalk W. Cronjé
 */
class HorizontalRuleSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/29')
    def "Horizontal Rule"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_1.txt')
        generateOutput('hr.adoc')

        expect:
        chapter.text=='''# HR

This line is separated from the next

---

by a horizontal rule.
'''
    }

}





