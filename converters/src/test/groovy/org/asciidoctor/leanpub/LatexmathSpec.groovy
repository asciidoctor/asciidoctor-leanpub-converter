package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class LatexmathSpec extends LeanpubSpecification {

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/41')
    def "Inline and block content"() {
        setup:
            File chapter = new File(manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('latexmath.adoc')

        then:
            chapter.text == '''# Basic Latex Maths

We can have an inline equation: {$$}C = \\alpha + \\beta Y^{\\gamma} + \\epsilon{/$$}, or we can have a block

{$$}
\\textbf{A latexmath block}
{/$$}

{$$}
C = \\alpha + \\beta Y^{\\gamma} + \\epsilon
{/$$}
'''
    }

}