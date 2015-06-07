package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification

/**
 * Created by schalkc on 14/12/14.
 */
class SectionsSpec extends LeanpubSpecification {

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
        File dedication = new File(LeanpubSpecification.manuscriptDir,'dedication.txt')
        File part1 = new File(LeanpubSpecification.manuscriptDir,'part_1.txt')
        File chapterA = new File(LeanpubSpecification.manuscriptDir,'chapter_2.txt')
        File chapterB = new File(LeanpubSpecification.manuscriptDir,'chapter_3.txt')
        File part2 = new File(LeanpubSpecification.manuscriptDir,'part_4.txt')
        File chapterC = new File(LeanpubSpecification.manuscriptDir,'chapter_5.txt')
        File chapterD = new File(LeanpubSpecification.manuscriptDir,'chapter_6.txt')

        when:
        generateOutput('book-with-parts.adoc')

        then: 'Part 1 is generated'
        part1.text == '''-# Part One

'''
        and: 'Chapter A is generated with subsections'
        chapterA.text == '''# Chapter A

## Level 3

Plain text at level 3

### Level 4

Plain text at level 4

#### Level 5

Plain text at level 5
'''
        and: 'Chapter B is generated with subsection'
        chapterB.text == '''# Chapter B

With no text
'''
        and: 'Part 2 is generated'
        part2.text == '''-# Part Two

'''
        and: 'Chapter C is generated after Part 2'
        chapterC.text == '''# Chapter C

Text
'''
        and: 'Chapter D is generated after Part 2'
        chapterD.text == '''# Chapter D

More text
'''
        and: 'Dedication is in Book.txt file'
        LeanpubSpecification.book1.readLines()[1] == 'dedication.txt'

        and: 'Dedication is generated'
        dedication.text == '-#' + '''
||
||
||
||
||
||
||
||
||
||
C> This file is dedicated to all those that has made the Asciidoctor chain possible.
C> ''' + '''
C> Happy hacking people!
'''
    }
}