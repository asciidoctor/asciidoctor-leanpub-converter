package org.asciidoctor.leanpub

import org.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.FailsWith
import spock.lang.Issue


/**
 * @author Schalk W. Cronjé
 */
class LabeledListsSpec extends LeanpubSpecification {

    @FailsWith(Exception)
    @Issue('https://github.com/asciidoctor/asciidoctorj/issues/404')
    def "Labeled lists should process examples form Asciidoctor user guide"() {
        setup:
        File single = new File(LeanpubSpecification.manuscriptDir, 'chapter_1.txt')
        File multi  = new File(LeanpubSpecification.manuscriptDir, 'chapter_2.txt')
        File qanda  = new File(LeanpubSpecification.manuscriptDir, 'chapter_3.txt')
        File mixed  = new File(LeanpubSpecification.manuscriptDir, 'chapter_4.txt')

        when:
        generateOutput('labeled-lists.adoc')

        then:
        single.text == '''# Single line

first term

: definition of first term

second term

: definition of second term

'''

        multi.text == '''# Multi-line

first term

: definition of first term
on two lines

second term

: definition of second term
can be on multiple lines
'''

        qanda.text == '''# Q & A
'''

        mixed.text == '''# Multi
'''
    }

}

/*
[chapter]
== Single line

first term:: definition of first term
section term:: definition of second term

[chapter]
== Multi-line

first term::
definition of first term
section term::
definition of second term

[chapter]
== Q & A

[qanda]
What is Asciidoctor?::
  An implementation of the AsciiDoc processor in Ruby.
What is the answer to the Ultimate Question?:: 42

[chapter]
== Mixed

Operating Systems::
  Linux:::
    . Fedora
      * Desktop
    . Ubuntu
      * Desktop
      * Server
  BSD:::
    . FreeBSD
    . NetBSD

Cloud Providers::
  PaaS:::
    . OpenShift
    . CloudBees
  IaaS:::
    . Amazon EC2
    . Rackspace

 */