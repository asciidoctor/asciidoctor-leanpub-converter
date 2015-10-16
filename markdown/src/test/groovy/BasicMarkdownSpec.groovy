import org.asciidoctor.internal.MarkdownSpecification
import spock.lang.FailsWith
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Unroll


/**
 * @author Schalk W. Cronjé
 */
class BasicMarkdownSpec extends MarkdownSpecification {

//    @Ignore
    @Issue('https://leanpub.com/help/manual#leanpub-auto-numberedordered-lists, https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/1')
    @FailsWith(org.spockframework.runtime.ConditionNotSatisfiedError)
    @Unroll
    def "Converting #adocType (with issues)"() {

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

//    @Unroll
//    def "Converting #adocType"() {
//
//        given:
//        def results = generateOutput( adocType+'.adoc' )
//
//        expect:
//        results.outputFile.exists()
//        results.outputFile.text == results.expectedFile.text
//
//        where:
//        adocType << [
//            'simple-book'
//        ]
//
//    }

}