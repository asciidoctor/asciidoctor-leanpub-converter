package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

class ListingSpec extends LeanpubSpecification {

    void setup() {
        documentName = 'listing'
    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/18")
    void "Listing block without substitution"() {
        setup:
        File chapter = chapterFromDocument(1)

        expect:
        chapter.text == '''# Listing block without substitution

~~~~~~~~
$ mkdir test-project
$ cd test-project
$ gradle wrapper --gradle-version={a-test-value}

:wrapper
~~~~~~~~
'''
    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/18")
    void "Listing block with substitution"() {
        setup:
        File chapter = chapterFromDocument(2)

        expect:
        chapter.text == '''# Listing terminal block with substitution

~~~~~~~~
$ mkdir test-project
$ cd test-project
$ gradle wrapper --gradle-version=1.2.3.4

:wrapper
~~~~~~~~
'''
    }

    @Issue("https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/18")
    void "Listing block with substitution and callout"() {
        setup:
        File chapter = chapterFromDocument(3)

        expect:
        chapter.text == '''# Listing terminal block with substitution and callout

{linenos="yes"}
~~~~~~~~
$ mkdir test-project
$ cd test-project
$ gradle wrapper --gradle-version=1.2.3.4

:wrapper
~~~~~~~~

**Line #3:** This value must be substituted.


'''
    }
}