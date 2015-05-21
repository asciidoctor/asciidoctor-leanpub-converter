package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.IgnoreRest

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
            chapter.text == '''# Chapter A

## Level 3

Plain text at level 3

### Level 4

Plain text at level 4

#### Level 5

Plain text at level 5
'''
    }

    def "Parts are generated on their own in between chapters"() {
        setup:
        File part1 = new File(LeanpubSpecification.manuscriptDir,'part_1.txt')
        File chapterA = new File(LeanpubSpecification.manuscriptDir,'chapter_2.txt')
        File chapterB = new File(LeanpubSpecification.manuscriptDir,'chapter_3.txt')
        File part2 = new File(LeanpubSpecification.manuscriptDir,'part_4.txt')
        File chapterC = new File(LeanpubSpecification.manuscriptDir,'chapter_5.txt')
        File chapterD = new File(LeanpubSpecification.manuscriptDir,'chapter_6.txt')

        when:
        generateOutput('book-with-parts.adoc')

        then:
        part1.text == '''-# Part One

'''
        chapterA.text == '''# Chapter A

## Level 3

Plain text at level 3

### Level 4

Plain text at level 4

#### Level 5

Plain text at level 5
'''
        chapterB.text == '''# Chapter B

With no text
'''
        part2.text == '''-# Part Two

'''
        chapterC.text == '''# Chapter C

Text
'''
        chapterD.text == '''# Chapter D

More text
'''

    }
}