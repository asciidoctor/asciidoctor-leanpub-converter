package org.asciidoctor.markdown

import org.asciidoctor.markdown.internal.MarkdownSpecification
import spock.lang.FailsWith
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Unroll


/**
 * @author Schalk W. Cronjé
 */
class BasicMarkdownSpec extends MarkdownSpecification {

    @Unroll
    def "Converting #adocType"() {

        given:
        def results = generateOutput( adocType+'.adoc' )

        expect:
        results.outputFile.exists()
        results.outputFile.text == results.expectedFile.text

        where:
        adocType << [
            'simple-book'
        ]

    }

    @Ignore
    @Unroll
    def "Converting #adocType (with issues)"() {

        given:
        def results = generateOutput( adocType+'.adoc' )

        expect:
        results.outputFile.exists()
        results.outputFile.text == results.expectedFile.text

        where:
        adocType << [
//            'simple-book'
        ]

    }

}