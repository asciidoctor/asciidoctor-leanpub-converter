package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification

/**
 * @author Schalk W. Cronjé
 */
class SubstitutionSpec extends LeanpubSpecification {
    def "Source code should add the language term"() {
        setup:
        File chapter = new File(manuscriptDir, 'chapter_1.txt')

        when:
        generateOutput('substitutions.adoc')

        then:
        chapter.text == '''# Attribute Substitutions

The language is 'afrikaans'.

Now the language is 'english'.
'''
    }
}