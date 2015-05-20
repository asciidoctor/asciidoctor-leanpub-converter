package org.ysb33r.asciidoctor

import org.ysb33r.asciidoctor.internal.LeanpubSpecification
import spock.lang.Specification


/**
 * Created by schalkc on 14/12/14.
 */
class LevelsSpec extends LeanpubSpecification {

    def "Levels should translate to one less level"() {
        setup:
            File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('simple-book.adoc')

        then:
            chapter.readLines() == '''# Chapter A

## Level 3

Plain text at level 3

### Level 4

Plain text at level 4

#### Level 5

Plain text at level 5

'''
    }

    def "Parts are generated in the first chapter of a part"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_1.txt')

        when:
        generateOutput('book-with-parts.adoc')

        then:
        chapter.readLines() == '''-# Part Two

# Chapter C

Text

# Chapter D

More text

'''

    }
}