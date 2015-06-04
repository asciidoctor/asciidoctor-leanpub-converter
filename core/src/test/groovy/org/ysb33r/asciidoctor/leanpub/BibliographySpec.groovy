package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Ignore
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class BibliographySpec extends LeanpubSpecification {

    def "Bibliography"() {
        setup:
        File chapter1 = new File(manuscriptDir,'backmatter_1.txt')
        File chapter2 = new File(manuscriptDir,'backmatter_2.txt')
        generateOutput('bibliography.adoc')

        expect:
        chapter2.text == '''# Chapter with References

{#prag}
Andy Hunt + Dave Thomas. The Pragmatic Programmer: From Journeyman to Master. Addison-Wesley. 1999.

{#seam}
Dan Allen. Seam in Action. Manning Publications. 2008.

'''
        chapter1.text == '''# Fake Appendix

## Section with References

{#prag}
Andy Hunt + Dave Thomas. The Pragmatic Programmer: From Journeyman to Master. Addison-Wesley. 1999.

{#seam}
Dan Allen. Seam in Action. Manning Publications. 2008.

'''
    }

    @Ignore
    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/31')
    def "Bibliography with ampersand"() {
        setup:
        File chapter3 = new File(manuscriptDir,'backmatter_3.txt')
        generateOutput('bibliography.adoc')

        expect:
        chapter3.text == '''# Chapter with Escaped Characters

{#prag}
Andy Hunt & Dave Thomas. The Pragmatic Programmer: From Journeyman to Master. Addison-Wesley. 1999.

{#seam}
Dan Allen. Seam in Action. Manning Publications. 2008.'''
    }

}