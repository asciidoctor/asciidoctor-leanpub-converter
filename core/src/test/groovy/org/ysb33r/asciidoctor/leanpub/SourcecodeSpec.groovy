package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.AbstractTextConverter
import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.IgnoreRest
import spock.lang.Issue
import static org.ysb33r.asciidoctor.leanpub.LeanpubConverter.LINESEP

/**
 * Created by schalkc on 15/12/14.
 */
class SourcecodeSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#leanpub-auto-code')
    def "Source code should add the language term"() {
        setup:
            File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_1.txt')

        when:
            generateOutput('sourcecode.adoc')

        then:
            chapter.text == '''# Chapter Source Listing

{lang="groovy"}
~~~~~~~~
@InputFiles
FileCollection getDocuments() {
    project.files(this.documents)
}

void setDocuments(Object... docs) {
    this.documents.clear()
    this.documents.addAll(docs as List)
}

void documents(Object... docs) {
    this.documents.addAll(docs as List)
}

private List<Object> documents
~~~~~~~~
'''

    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-code')
    def "Source code should add title if it is available"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_2.txt')

        when:
        generateOutput('sourcecode.adoc')

        then:
        chapter.text == '''# Chapter Source Listing with Title

{lang="groovy", title="Source code with title"}
~~~~~~~~
@InputFiles
FileCollection getDocuments() {
    project.files(this.documents)
}
~~~~~~~~
'''

    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-code, https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/2')
    def "Source code with callouts adds linenumbers with a reference block below"() {
        setup:
        def whitespace = ' '.multiply(7)
        File chapter = new File(LeanpubSpecification.manuscriptDir, 'chapter_3.txt')

        when:
        generateOutput('sourcecode.adoc')

        then:
        chapter.text == '''# Chapter Source Listing with Callouts

{lang="groovy", linenos=yes}
~~~~~~~~
@InputFiles''' + ' '.multiply(3) + '''
FileCollection getDocuments() {
    project.files(this.documents)
}

void setDocuments(Object... docs) {
    this.documents.clear()
    this.documents.addAll(docs as List)
}

void documents(Object... docs) {
    this.documents.addAll(docs as List)
}

private List<Object> documents
~~~~~~~~

**Line #1:** Create a getter and annotate with `@InputFiles` or `@OutputFiles`. The purpose of the getter is to translate upon access
to a `FileCollection` object.

**Line #3:** Translate from the list of `Object` using the built-in `project.files` method. This handles a large variety of types
including files, strings and closures as well as lists and arrays thereof.

**Line #6:** Use a setter to allow for `setDocuments('foo','bar')` replacement of current content with a new set of content. This becomes
very useful should another plugin author decide to extend your task type.

**Line #11:** Use a method with the name of the property to allow for a expressive `document 'foo','bar'` style.

**Line #15:** The property is left private as appropriate access is already provided.

**Line #15:** This extra callout is just a test.


'''
    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-code, https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/2')
    def "XML with callouts adds linenumbers with a reference block below"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_4.txt')

        when:
        generateOutput('sourcecode.adoc')

        then:
        chapter.text == '''# Chapter XML Source with Callouts

{lang="xml", linenos=yes}
~~~~~~~~
<someXml>
  <parser/>
  <tree/>
</someXml>
~~~~~~~~

**Line #2:** One XML callout

**Line #3:** First XML callout on a line

**Line #3:** Second XML callout on a line


'''
    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-code, https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/2')
    def "Source code with aside style"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_5.txt')

        when:
        generateOutput('sourcecode.adoc')

        then:
        chapter.text == '''# Chapter XML Source with Aside

{lang="xml", linenos=yes}
~~~~~~~~
<someXml>
  <parser/>
  <tree/>
</someXml>
~~~~~~~~

A> **Line #2:** One XML callout
A>''' + ' '.multiply(7) + '''
A> **Line #3:** First XML callout on a line
A>''' + ' '.multiply(7) + '''
A> **Line #3:** Second XML callout on a line
A>''' + ' '.multiply(7) + LeanpubConverter.LINESEP.multiply(2)
    }

    @Issue('https://leanpub.com/help/manual#leanpub-auto-code, https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/2')
    def "Source code with discussion style"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_6.txt')

        when:
        generateOutput('sourcecode.adoc')

        then:
        chapter.text == '''# Chapter XML Source with Discussion

{lang="xml", linenos=yes}
~~~~~~~~
<someXml>
  <parser/>
  <tree/>
</someXml>
~~~~~~~~

D> **Line #2:** One XML callout
D>''' + ' '.multiply(7) + '''
D> **Line #3:** First XML callout on a line
D>''' + ' '.multiply(7) + '''
D> **Line #3:** Second XML callout on a line
D>''' + ' '.multiply(7) + LeanpubConverter.LINESEP.multiply(2)

    }

    // TODO: Support linenumbers via leanpub_xxx attributes or similar
    // {line-numbers=on,starting-line-number=32}
    // https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/3

}