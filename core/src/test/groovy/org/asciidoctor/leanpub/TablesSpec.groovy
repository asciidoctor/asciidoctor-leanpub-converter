package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Ignore

/**
 * @author Schalk W. Cronjé
 */
class TablesSpec extends LeanpubSpecification {

    @Ignore
    def "Simple Table"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_1.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Columns on consecutive lines"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_2.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''

    }

    @Ignore
    def "Text centered in columns"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_3.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''

    }

    @Ignore
    def "Text right-aligned in columns"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_4.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Last column right-aligned"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_5.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Various vertical alignments"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_6.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Setting widths"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_7.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Column Styles"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_8.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Columns that span"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_9.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Columns with vbars"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_10.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Table with header row"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_11.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Table with footer row"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_12.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Ignore
    def "Controlling overall column width"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_13.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

}