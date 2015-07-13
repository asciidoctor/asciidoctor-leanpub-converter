import org.asciidoctor.internal.MarkdownSpecification
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll


/**
 * @author Schalk W. Cronjé
 */
class BasicMarkdownSpec extends MarkdownSpecification {

//    @Ignore
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

}