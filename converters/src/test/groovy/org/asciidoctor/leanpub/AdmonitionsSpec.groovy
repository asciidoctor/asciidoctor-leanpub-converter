package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class AdmonitionsSpec extends LeanpubSpecification {

    void setup() {
        documentName = 'admonitions'
    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/4, https://leanpub.com/help/manual#leanpub-auto-warning')
    def "Warning"() {
        setup:
        setOutputRelativePath('Warning')
        File chapter = chapterFromDocument(1)

        expect:
            chapter.text == '''# Chapter with Warning

Some text here, then

W> This is a warning admonition.
W>   And some more text.

'''
    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-warning')
    def "Warning with title"() {
        setup:
        setOutputRelativePath('WarningWithTitle')
        File chapter = chapterFromDocument(2)

        expect:
        chapter.text == '''# Another Chapter with Warning

With some more text in between

W> ## Warning with title
W> This is a warning with title

'''
    }

    @Issue('https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/28')
    def "Caution and Important"() {
        setup:
        setOutputRelativePath('Caution-Important')
        File chapter = chapterFromDocument(3)

        expect:
        chapter.text == '''# Caution and Important

{icon=fire}
G> This is a caution


Some text in between

{icon=university}
G> ## Yip, really important
G> This is important text


Some text afterwards
'''
    }

    @Issue('https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/76')
    void 'Links inside admonitions'() {
        setup:
        setOutputRelativePath('Links')
        File chapter = chapterFromDocument(4)

        expect:
        chapter.text == '''# Admonitions Containing Links

I> If you do have a JDK installed, use the package manager for your operating system to install it, or download one from [Oracle Java site](http://www.oracle.com/technetwork/java/javase/downloads/index.html). JDK7 is the minimum required, but JDK8 is recommended. If you use [SDKMAN!](http://sdkman.io), it is also possible to use it to install the JDK.

'''

    }
}