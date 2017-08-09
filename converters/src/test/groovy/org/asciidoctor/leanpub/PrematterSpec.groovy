package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification

/**
 * Created by schalkc on 15/12/14.
 */
class PrematterSpec extends LeanpubSpecification {

    def "Title image"() {
        setup:
            File chapter = new File(MANUSCRIPT_DIR,'chapter_1.txt')

        when:
            generateOutput('book-with-prematter.adoc')

        then:
            new File(IMAGES_DIR,'title_page.png').exists()
    }

    def "Preamble"() {
        setup:
            File chapter = new File(MANUSCRIPT_DIR,'preamble.txt')

        when:
            generateOutput('book-with-prematter.adoc')

        then:
            chapter.text == '''##### &nbsp;

First **paragraph** of preamble

Second paragrah of preamble

{pagebreak}
'''
    }


}