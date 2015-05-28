package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Specification


/**
 * @author Schalk W. Cronjé
 */
class SubstitutionSpec extends LeanpubSpecification {
    def "Source code should add the language term"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir, 'chapter_1.txt')

        when:
        generateOutput('substitutions.adoc')

        then:
        chapter.text == '''# Attribute Substitutions

The language is 'afrikaans'.

Now the language is 'english'.
'''
    }
}