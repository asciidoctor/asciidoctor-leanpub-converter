package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Ignore
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class BibliographySpec extends LeanpubSpecification {

    def "Bibliography"() {
        setup:
        File backmatter2 = new File(manuscriptDir,'backmatter_2.txt')
        File backmatter3 = new File(manuscriptDir,'backmatter_3.txt')
        generateOutput('bibliography.adoc')

        expect:
        verifyAll {


            backmatter3.text == '''# Chapter with References

{#prag2}
[**prag2**] Andy Hunt + Dave Thomas. The Pragmatic Programmer: From Journeyman to Master. Addison-Wesley. 1999.

{#seam2}
[**seam2**] Dan Allen. Seam in Action. Manning Publications. 2008.

'''
            backmatter2.text == '''# Fake Appendix

## Section with References

{#prag1}
[**prag1**] Andy Hunt + Dave Thomas. The Pragmatic Programmer: From Journeyman to Master. Addison-Wesley. 1999.

{#seam1}
[**seam1**] Dan Allen. Seam in Action. Manning Publications. 2008.

'''
        }
    }

    @Ignore
    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/31')
    def "Bibliography with ampersand"() {
        setup:
        File chapter3 = new File(manuscriptDir,'backmatter_3.txt')
        generateOutput('bibliography.adoc')

        expect:
        chapter3.text == '''# Chapter with Escaped Characters

{#prag3}
[**prag3**] Andy Hunt & Dave Thomas. The Pragmatic Programmer: From Journeyman to Master. Addison-Wesley. 1999.

{#seam3}
[**seam3**] Dan Allen. Seam in Action. Manning Publications. 2008.'''
    }

//    def "Bibliography with hyperlinks"() {
//        setup:
//        File chapter4 = new File(manuscriptDir, 'backmatter_4.txt')
//        generateOutput('bibliography.adoc')
//
//        expect:
//        chapter4.text == '''# Chapter with Escaped Characters
//'''
//    }
}