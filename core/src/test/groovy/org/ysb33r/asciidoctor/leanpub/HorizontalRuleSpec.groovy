package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class HorizontalRuleSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/29')
    def "Horizontal Rule"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_1.txt')
        generateOutput('hr.adoc')

        expect:
        chapter.text=='''# HR

This line is separated from the next

---

by a horizontal rule.
'''
    }

}





