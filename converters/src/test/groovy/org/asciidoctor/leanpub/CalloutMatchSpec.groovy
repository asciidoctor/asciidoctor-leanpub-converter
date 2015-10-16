package org.asciidoctor.leanpub

import org.asciidoctor.markdown.internal.SourceParser
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Pattern


/**
 * @author Schalk W. Cronjé
 */
@Unroll
class CalloutMatchSpec extends Specification {

    static final Pattern EXTRACTOR = SourceParser.CALLOUT_PATTERN

    def "Match lines with callouts"() {
        given:
        def matcher = source =~ EXTRACTOR


        expect:
        matcher.matches()
        matcher.size() == 1
        matcher[0].size() == 5
        matcher[0][1] == 'line of text'
        matcher[0][2] == callouts
        matcher[0][pos] != null
        matcher[0][2].toString().findAll( pos==3 ? ~/<\d+>/  : ~/<!--\d+-->/).collect {it.replaceAll(~/[<>\-\!]+/,'')} == items

        where:
        source                           || callouts            | pos | items
        'line of text // <1>'            || ' <1>'              | 3   | ['1']
        'line of text // <1> <2> <3>'    || ' <1> <2> <3>'      | 3   | ['1','2','3']
        'line of text # <1>'             || ' <1>'              | 3   | ['1']
        'line of text # <1> <2> <3>'     || ' <1> <2> <3>'      | 3   | ['1','2','3']
        'line of text -- <1>'            || ' <1>'              | 3   | ['1']
        'line of text -- <1> <2> <3>'    || ' <1> <2> <3>'      | 3   | ['1','2','3']
        'line of text ;; <1>'            || ' <1>'              | 3   | ['1']
        'line of text ;; <1> <2> <3>'    || ' <1> <2> <3>'      | 3   | ['1','2','3']
        'line of text <!--1-->'          || ' <!--1-->'         | 4   | ['1']
        'line of text <!--1--> <!--2-->' || ' <!--1--> <!--2-->'| 4   | ['1','2']

    }
}