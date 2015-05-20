package org.ysb33r.asciidoctor

import org.ysb33r.asciidoctor.internal.LeanpubSpecification
import spock.lang.Issue
import spock.lang.Specification


/**
 * Created by schalkc on 15/12/14.
 */
class StylingSpec extends LeanpubSpecification {

    // TODO: http://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#formatted-text
    // subscript, bold/italic/etc. letters

    // TODO: Can paragraphs be centered?

    // TODO: Code blocks in lists
    // TODO: Images in lists
    // TODO: Asciidoctor checkboxes (Supported?)

    @Issue('https://leanpub.com/help/manual#leanpub-auto-styling-text')
    def "Basic styling should translate correctly"() {
        setup:
            File chapter = new File(manuscriptDir,'chapter_2.txt')

        when:
            generateOutput('simple-book.adoc')

        then:
            chapter.readLines() == '''# Chapter With Basic Styling

**Bold text**

*Italic text*

***Bold italic text***

____Underlined text____

Super^script^ text

`Monospace text`

Non&nbsp;breaking&nbsp;spaces
'''

    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-numberedordered-lists')
    def "Basic ordered and unordered lists"() {
        setup:
        File chapter = new File(manuscriptDir,'chapter_3.txt')

        when:
        generateOutput('simple-book.adoc')

        then:
        chapter.readLines() == '''# Chapter With Simple Lists

* List #1
* List #2
  * List #2 #1
  * List #2 #2
* List #3

1. List #1
1. List #2
  1. List #2 #1
  1. List #2 #2
1. List #3

'''

    }
}