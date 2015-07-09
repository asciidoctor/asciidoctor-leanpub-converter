package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import org.spockframework.runtime.ConditionNotSatisfiedError
import spock.lang.FailsWith
import spock.lang.Ignore
import spock.lang.IgnoreRest
import spock.lang.Issue

/**
 * @author Schalk W. Cronjé
 */
class TablesSpec extends LeanpubSpecification {

    def "Simple Table"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_1.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Simple Table

{width="default",title="Simple table with single text rows and no header"}
|Cell in column 1, row 1|Cell in column two, row 1|
|Cell in column one, row 2|Cell in column 2, row 2|
|Cell in column 1, row three|Cell in column 2, row three|

{width="default",title="Simple table with single text rows and a header"}
|Header 1|Header 2|
|:----|:----|
|Cell in column 1, row 1|Cell in column two, row 1|
|Cell in column one, row 2|Cell in column 2, row 2|
|Cell in column 1, row three|Cell in column 2, row three|

{width="default",title="Simple table with single text rows and a footer"}
|Cell in column 1, row 1|Cell in column two, row 1|
|Cell in column one, row 2|Cell in column 2, row 2|
|Cell in column 1, row three|Cell in column 2, row three|
|:====|:====|
|Footer 1|Footer 2|

{width="default",title="Simple table with single text rows and a header and footer"}
|Header 1|Header 2|
|:----|:----|
|Cell in column 1, row 1|Cell in column two, row 1|
|Cell in column one, row 2|Cell in column 2, row 2|
|Cell in column 1, row three|Cell in column 2, row three|
|:====|:====|
|Footer 1|Footer 2|
'''
    }

    def "Columns on consecutive lines"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_2.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Columns on consecutive lines

{width="default",title="Columns on consecutive lines"}
|Cell in column 1, row 1|Cell in column 2, row 1|Cell in column 3, row 1|
|Cell in column 1, row 2|Cell in column 2, row 2|Cell in column 3, row 2|
'''

    }

    def "Text centered in columns"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_3.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Text centered in columns

{width="default"}
| | | |
|:---:|:---:|:---:|
|Cell in column 1, row 1|Cell in column 2, row 1|Cell in column 3, row 1|
|Cell in column 1, row 2|Cell in column 2, row 2|Cell in column 3, row 2|
'''

    }

    def "Text right-aligned in columns"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_4.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Text right-aligned in columns

{width="default"}
| | | |
|----:|----:|----:|
|Cell in column 1, row 1|Cell in column 2, row 1|Cell in column 3, row 1|
|Cell in column 1, row 2|Cell in column 2, row 2|Cell in column 3, row 2|
'''
    }

    def "Last column right-aligned"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_5.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Last column right-aligned

{width="default"}
| | | |
|:----|:----|----:|
|Cell in column 1, row 1|Cell in column 2, row 1|Cell in column 3, row 1|
|Cell in column 1, row 2|Cell in column 2, row 2|Cell in column 3, row 2|
'''
    }

    @Issue('https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/37')
    @Ignore
    def "Various vertical alignments"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_6.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @Issue('https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/38')
    @Ignore
    def "Setting widths"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_7.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''
'''
    }

    @FailsWith(ConditionNotSatisfiedError)
    @Issue('https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/39')
    def "Columns that span"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_8.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Columns that span

{width="default"}
|A cell in column 1, row 1|A cell in column 2, row 1|A cell in column 3, row 1|
|Same cell content in columns 1, 2, and 3|     |     |
|Cell in column 1, row 3|Cell in column 2, row 3|Cell in column 3, row 3|
'''
    }

    def "Columns with vbars"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_9.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Columns with vbars

{width="default",title="This table also has a vbar"}
|Cell in column 1, row 1|Cell in column 2, row 1|
|Cell in column 2, row 2|Cell in column 3, row 2|
|Cell in column 2, row 3 with \\| embedded|Cell in column 3, row 3|
'''
    }

    def "Controlling overall table width"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_10.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Controlling overall table width

{width="65%",title="Setting overall width and use implicit header row"}
|Name of Column 1|Name of Column 2|Name of Column 3|
|:----|:----|:----|
|Cell in column 1, row 1|Cell in column 2, row 1|Cell in column 3, row 1|
|Cell in column 1, row 2|Cell in column 2, row 2|Cell in column 3, row 2|
'''
    }

    def "Column Styles"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_11.txt')
        generateOutput('tables.adoc')

        expect:
        chapter.text=='''# Column Styles

{width="default",title="Column styles will multiple rows"}
| | | |
|:----|:----|:----|
|Cell **in** column 1, row 1,|*Cell in column 2, row 1,*|Cell in column 3, row 1,|
|asciidoc|*emphasized*|header style|
|:----|:----|:----|
|```Cell **in** column 1, row 2,```|`Cell in column 2, row 2,`|**Cell in column 3, row 2,**|
|```literal```|`monospaced`|**bold**|
|:----|:----|:----|
|Cell in column 1, row 2,|Cell in column 2, row 2|Cell in column 3, row 2|
|verse|     |     |
'''
    }
}