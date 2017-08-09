package org.asciidoctor.leanpub

import org.asciidoctor.converter.LeanpubConverter
import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class BlocksSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Literal Block"() {
        setup:
            File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_1.txt')

        when:
            generateOutput('blocks.adoc')

        then:
            chapter.text == '''# A Literal Block

{linenos=off}
    Kommetjie, Kommegas, wees my gas
    Hier in my houthuis
'''

    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/10, https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Poetry"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_2.txt')

        when:
        generateOutput('blocks.adoc')

        then:
        chapter.text == '''# A Poetry Block

{style="poem"}
~~~~~~
Kommetjie, Kommegas, wees my gas
Hier in my houthuis
~~~~~~
'''
    }

    def "Verse"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_3.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# A Verse Block

A> The fog comes
A> on little cat feet
A> ''' + LeanpubConverter.LINESEP +
'''A> -- **Carl Sandburg**
A> *Fog*
'''
    }

    def "Sidebar"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_4.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Two Sidebar Blocks

A> ## This one has a title
A> Foo
A> Bar

A> And this one does not
A>''' + ' ' + '''
A> mickey
A> minny
'''
    }

    def "Simple MD-style blockquote"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_5.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Simple MD-style blockquote

> First line,
> Second line.
'''
    }

    def "Simple MD-style blockquote with reference"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_6.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Simple MD-style blockquote with reference

> First line,
> Second line.
> ''' + '''
> -- **Asciidoctor Team**
> *Code of the Asciidoctors: Volume X*
'''
    }

    def "Multi-level MD-style blockquote"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_7.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Multi-level MD-style blockquote

> > What's new?
> '''+'''
> I've got Markdown in my AsciiDoc!
> '''+'''
> > Like what?
> '''+'''
> * Blockquotes
> * Headings
> * Fenced code blocks
> '''+'''
> > Is there more?
> '''+'''
> Yep. AsciiDoc and Markdown share a lot of common syntax already.
'''
    }

    def "Air quotes"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_8.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Air Quotes

> When you want to go fast, go alone.
> When you want to go far, go together.
>''' + ' ' + '''
> -- **African proverb**

> Simple air quotes, but not citation.
'''
    }

    def "Quoted paragraph"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_9.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Quoted Paragraph

> Quoted paragraph with citation.
>''' + ' ' + '''
> -- **Asciidoctor Team**
> *Code of the Asciidoctors: Volume X*
'''
    }

    def "Quoted blocks"() {
        setup:
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,'chapter_10.txt')
        generateOutput('blocks.adoc')

        expect:
        chapter.text == '''# Quote Blocks

> Quoted short block with citation.
> ''' + '''
> -- **Asciidoctor Team**
> *Code of the Asciidoctors: Volume X*

> ## Quoted short block
> Quoted short block with citation and title
> ''' + '''
> -- **Asciidoctor Team**
> *Code of the Asciidoctors: Volume X*

> Multi-line quoted block
> ''' + '''
> -- **Asciidoctor Team**
> *Code of the Asciidoctors: Volume X*
'''
    }
}