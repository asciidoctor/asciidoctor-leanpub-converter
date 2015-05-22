package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Issue

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

    // TODO: Source code with call outs
    // https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/2

    // TODO: Support linenumbers via leanpub_xxx attributes or similar
    // {line-numbers=on,starting-line-number=32}
    // https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/3

}