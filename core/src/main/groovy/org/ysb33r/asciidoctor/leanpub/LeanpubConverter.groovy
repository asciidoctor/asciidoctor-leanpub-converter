package org.ysb33r.asciidoctor.leanpub

import groovy.util.logging.Slf4j
import org.asciidoctor.ast.AbstractNode
import org.asciidoctor.ast.Block
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.ast.Inline
import org.asciidoctor.ast.ListItem
import org.asciidoctor.ast.ListNode
import org.asciidoctor.ast.Section
import org.asciidoctor.converter.AbstractConverter

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
class LeanpubConverter extends AbstractConverter {

    static final String LINESEP = "\n"
    static final String BOOK = 'Book.txt'

    LeanpubConverter(final String backend,Map<Object, Object> opts) {
        super(backend, opts)
    }

    /**
     * Converts an {@link org.asciidoctor.ast.AbstractNode} using the specified transform along
     * with additional options. If a transform is not specified, implementations
     * typically derive one from the {@link org.asciidoctor.ast.AbstractNode#getNodeName()} property.
     *
     * <p>Implementations are free to decide how to carry out the conversion. In
     * the case of the built-in converters, the tranform value is used to
     * dispatch to a handler method. The TemplateConverter uses the value of
     * the transform to select a template to render.
     *
     * @param node The concrete instance of AbstractNode to convert
     * @param transform An optional String transform that hints at which transformation
     *             should be applied to this node. If a transform is not specified,
     *             the transform is typically derived from the value of the
     *             node's node_name property. (optional, default: null)
     * @param opts An optional map of options that provide additional hints about
     *             how to convert the node. (optional, default: empty map)
     * @return the converted result
     */
    @Override
    Object convert(AbstractNode node, String transform, Map<Object, Object> opts) {
        if (node instanceof DocumentRuby) {
            setup(node,opts )
            def result =  node.content
            closeout()
            this
        } else {
            def method = "convert${(transform?:node.nodeName).capitalize()}"

            if(this.class.metaClass.respondsTo(this,method,AbstractNode,Map)) {
                return "${method}" (node,  opts)
            } else {
                log.error "${method} (node:${node.class.name}) is not defined. Will not transform this node, but will try to carry on"
                return null
            }
        }
    }

    private void setup(AbstractNode node,Map<Object, Object> opts) {
        Map<String,Object> docOptions = [:]
        node.document.options.each { k,v -> docOptions["${k}".toString()] = v}
        destDir = new File(docOptions.to_dir ?: '.','manuscript').absoluteFile
        node.attributes.put('nbsp','&nbsp;')
    }

    /** Writes all of the index file
     *
     */
    private void closeout() {
        destDir.mkdirs()

        def chapterNames = []
        leanpubSections.findAll { ConvertedSection it ->
            it.type == ConvertedSection.SectionType.CHAPTER || it.type == ConvertedSection.SectionType.PART
        }.eachWithIndex { ConvertedSection section, int index ->
            File dest = new File(destDir, (section.type == ConvertedSection.SectionType.PART ? 'part' : 'chapter') + "_${index+1}.txt" )
            dest.withWriter { w -> section.write(w) }
            chapterNames+= dest.name
        }

        new File(destDir,BOOK).withWriter { w ->
            if(preface) {
                w.println 'frontmatter.txt'
                w.println 'preface.txt'
                w.println 'mainmatter.txt'
            }

            chapterNames.each { w.println it }

            // TODO: Add backmatter file name
        }

        if(preface) {
            new File(destDir,'frontmatter.txt').text = '{frontmatter}'
            new File(destDir,'preface.txt').text = preface.toString()
            new File(destDir,'mainmatter.txt').text = '{mainmatter}'
        }

        // TODO: Add backmatter content

    }

    private def convertSection(AbstractNode node, Map<String, Object> opts) {
        Section section = node as Section
        int sectionIndex = -1
        log.debug "Transforming section: name=${section.sectname()}, level=${section.level} title=${section.title}"

        if(section.level==0) {
            leanpubSections+= new ConvertedSection( content :null, type : ConvertedSection.SectionType.PART)
            sectionIndex= leanpubSections.size() - 1
        } else if(section.sectname()=='chapter') {
            leanpubSections += new ConvertedSection(content: null, type: ConvertedSection.SectionType.CHAPTER)
            sectionIndex = leanpubSections.size() - 1
        }

        String content = (section.level==0 ? '-#' : '#'.multiply(section.level)) +
            ' ' +
            section.title +
            LINESEP + LINESEP +
            section.content

        if(sectionIndex>=0) {
            leanpubSections[sectionIndex].content = content
        }  else if (section.sectname()=='preface') {
            preface = content
        }

        content
    }

    /** Paragraph conversion just passes the content back.
     *
     * @return
     */
    private def convertParagraph(AbstractNode node, Map<String, Object> opts) {
        Block block = node as Block
        block.content + LINESEP
    }

    private def convertInline_quoted(AbstractNode node, Map<String, Object> opts) {
        Inline inline = node as Inline
        QuotedTextConverter."${inline.type}"(inline.text)
    }

    private def convertUlist(AbstractNode node,Map<String, Object> opts) {
        ListNode listNode = node as ListNode

        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    private def convertList_item(AbstractNode node,Map<String, Object> opts) {
        ListItem item = node as ListItem
        ' '.multiply(item.level*2-2) + '* ' + item.text + LINESEP
    }


    private File destDir
    private List<ConvertedSection> leanpubSections = []
    private Object preface

}

/*
attributes:
encoding:UTF-8, sectids:, toc-placement:auto, stylesheet:, webfonts:, prewrap:, attribute-undefined:drop-line, attribute-missing:skip, iconfont-remote:, caution-caption:Caution, important-caption:Important, note-caption:Note, tip-caption:Tip, warning-caption:Warning, appendix-caption:Appendix, example-caption:Example, figure-caption:Figure, table-caption:Table, toc-title:Table of Contents, manname-title:NAME, untitled-label:Untitled, version-label:Version, last-update-label:Last updated, docfile:simple-book.adoc, docdir:, docname:simple-book, docdate:2015-05-19, doctime:22:42:17 BST, docdatetime:2015-05-19 22:42:17 BST, asciidoctor:, asciidoctor-version:1.5.2, safe-mode-name:secure, safe-mode-secure:, safe-mode-level:20, max-include-depth:64, user-home:., backend:leanpub, linkcss:, doctype:article, doctype-article:, backend-leanpub-doctype-article:, backend-leanpub:, outfilesuffix:.html, filetype:html, filetype-html:, basebackend:leanpub, basebackend-leanpub:, basebackend-leanpub-doctype-article:, localdate:2015-05-20, localtime:10:49:26 BST, localdatetime:2015-05-20 10:49:26 BST, stylesdir:., iconsdir:./images/icons, doctitle:Title, firstname:Asciidoctor, author:Asciidoctor Team, authorinitials:AT, lastname:Team, email:foo@bar.example, authorcount:1, authors:Asciidoctor Team

document.options
[backend:leanpub, destination_dir:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/./build/test/leanpub, header_footer:true, to_dir:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/build/resources/test/test-documents, attributes:[docfile:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/build/resources/test/test-documents/simple-book.adoc, docdir:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/build/resources/test/test-documents, docname:simple-book, docdate:2015-05-19, doctime:22:42:17 BST, docdatetime:2015-05-19 22:42:17 BST]]
 */