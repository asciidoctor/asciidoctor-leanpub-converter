// ============================================================================
/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// ============================================================================
package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue


/**
 * Created by schalkc on 11/12/14.
 */
class FileLayoutSpec extends LeanpubSpecification {

    def "Each chapter should be written to a separate file and Book.txt updated"() {

        when: 'Generating a simple book with three chapters'
            generateOutput('simple-book.adoc')
            def index= book1.readLines()

        then: 'The same number of chapter files should be created'
            manuscriptDir.listFiles(
                new FilenameFilter() {
                    @Override
                    boolean accept(File dir, String name) {
                        name.startsWith('chapter_')
                    }
                }
            ).size() == 4

        and: 'Book.txt should contain the same number of entries'
            index.size() == 4
            index.every { it.startsWith('chapter_') }
    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-front-matter-main-matter-and-back-matter')
    def "If a preface is found then frontmatter should be generated"() {

        when: 'Generating a simple book with four chapters'
        generateOutput('book-with-matter.adoc')
        //def index= book1.readLines()

        then: 'The same number of chapter files should be created'
        manuscriptDir.listFiles(
                new FilenameFilter() {
                    @Override
                    boolean accept(File dir, String name) {
                        name.startsWith('chapter_')
                    }
                }
        ).size() == 4

        and: 'A preface should exist'
            new File(manuscriptDir,'preface.txt').exists()

        and: 'frontmatter should contain a single line'
            new File(manuscriptDir,'frontmatter.txt').text == '''{frontmatter}'''

        and: 'mainmatter should contain a single line'
            new File(manuscriptDir,'mainmatter.txt').text == '''{mainmatter}'''

        and: 'Book.txt should contain preface, chapter entries + frontmatter, mainmatter files'
            book1.text == '''frontmatter.txt
preface.txt
mainmatter.txt
chapter_1.txt
chapter_2.txt
chapter_3.txt
chapter_4.txt
'''
    }

    // TODO: Deal with backmatter.txt
    // bibliography, appendix (back)
    //
}