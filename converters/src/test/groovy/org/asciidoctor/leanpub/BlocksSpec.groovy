package org.asciidoctor.leanpub

import org.asciidoctor.converter.LeanpubConverter
import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue
import spock.lang.PendingFeature

/**
 * Created by schalkc on 15/12/14.
 */
class BlocksSpec extends LeanpubSpecification {

    void setup() {
        documentName = 'blocks'
    }

    @Issue('https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Literal Block"() {
        setup:
        setOutputRelativePath('Literal')
        File chapter = chapterFromDocument(1)

        expect:
        chapter.text == '''# A Literal Block

{linenos=off}
    Kommetjie, Kommegas, wees my gas
    Hier in my houthuis
'''

    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/10, https://leanpub.com/help/manual#crosslink_from_endnotes')
    def "Poetry"() {
        setup:
        setOutputRelativePath('Poetry')
        File chapter = chapterFromDocument(2)

        expect:
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
        setOutputRelativePath('Verse')
        File chapter = chapterFromDocument(3)

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
        setOutputRelativePath('Sidebar')
        File chapter = chapterFromDocument(4)

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
        setOutputRelativePath('MD-blockquote')
        File chapter = chapterFromDocument(5)

        expect:
        chapter.text == '''# Simple MD-style blockquote

> First line,
> Second line.
'''
    }

    def "Simple MD-style blockquote with reference"() {
        setup:
        setOutputRelativePath('MD-blockqupte-referenced')
        File chapter = chapterFromDocument(6)

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
        setOutputRelativePath('Multi-level-MD-blockquote')
        File chapter = chapterFromDocument(7)

        expect:

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

    @PendingFeature
    def "Air quotes"() {
        setup:
        setOutputRelativePath('Air')
        File chapter = chapterFromDocument(8)

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
        setOutputRelativePath('QuotedParagraph')
        File chapter = chapterFromDocument(9)

        expect:

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
        setOutputRelativePath('QuotedBlocks')
        File chapter = chapterFromDocument(10)

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