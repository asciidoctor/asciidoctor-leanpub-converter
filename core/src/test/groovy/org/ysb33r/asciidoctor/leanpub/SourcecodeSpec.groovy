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
            File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_2.txt')

        when:
            generateOutput('sourcecode.adoc')

        then:
            chapter.readLines() == '''# Chapter A

{lang=groovy}
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
    def "Source code shoudl add title if it is available"() {
        setup:
        File chapter = new File(LeanpubSpecification.manuscriptDir,'chapter_2.txt')

        when:
        generateOutput('sourcecode.adoc')

        then:
        chapter.readLines() == '''# Chapter A

{title="Source code with title", lang=groovy}
~~~~~~~~
@InputFiles
FileCollection getDocuments() {
    project.files(this.documents)
}
~~~~~~~~
'''

    }

    // TODO: Source code with call outs

}