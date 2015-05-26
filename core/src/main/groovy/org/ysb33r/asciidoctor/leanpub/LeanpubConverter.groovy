package org.ysb33r.asciidoctor.leanpub

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.asciidoctor.ast.AbstractNode
import org.asciidoctor.ast.Block
import org.asciidoctor.ast.Inline
import org.asciidoctor.ast.ListItem
import org.asciidoctor.ast.Section
import org.ysb33r.asciidoctor.AbstractTextConverter

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
class LeanpubConverter extends AbstractTextConverter {

    static final String LINESEP = "\n"
    static final String BOOK = 'Book.txt'
    static final String DOCFOLDER = 'manuscript'
    static final String FRONTCOVER = 'title_page.png'
    static final Pattern INLINE_IMG_PATTERN = ~/^image:(.+?)\[(.*?)\]$/

    String encoding = 'utf-8'

    LeanpubConverter(final String backend,Map<Object, Object> opts) {
        super(backend, opts)
    }

    void setupDocument(AbstractNode node,Map<Object, Object> opts) {
        log.debug "Document options at start: ${node.document.options}"
        log.debug "Document attributes at start: ${node.document.attributes}"

        Map<String,Object> docOptions = [:]
        node.document.options.each { k,v ->
            docOptions["${k}".toString()] = v
        }

        destDir = new File(docOptions.to_dir ?: '.',DOCFOLDER).absoluteFile
        docDir = new File(node.document.attributes.docdir ?: '.').absoluteFile

        log.debug "Destination directory set to ${destDir}"
        log.debug "Document directory set to ${docDir}"

        setFrontCoverFromAttribute(imagesDir(node),node.document.attributes['front-cover-image'])
        node.attributes.put('nbsp','&nbsp;')
    }

    /** Writes all of the index file
     *
     */
    def closeDocument(def content) {
        File imagesToDir = new File(destDir,'images')
        imagesToDir.mkdirs()

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

        if(frontCoverImage) {
            FileUtils.copyFile(frontCoverImage,new File(imagesToDir,FRONTCOVER))
        }

    }

    def convertSection(AbstractNode node, Map<String, Object> opts) {
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

    def convertInlineQuoted(AbstractNode node, Map<String, Object> opts) {
        Inline inline = node as Inline
        QuotedTextConverter."${inline.type}"(inline.text)
    }

    def convertAnchorTypeXref(AbstractNode node, Map<String, Object> opts) {
        Inline inline = node as Inline
        "[${inline.text ?: inline.attributes.fragment}](#${CrossReference.safeId(inline.attributes.refid)})"
    }

    def convertAnchorTypeLink(AbstractNode node, Map<String, Object> opts) {
        Inline inline = node as Inline
        return "[${inline.text}](${inline.target})"
    }

    def convertListItemTypeColist(ListItem item, Map<String, Object> opts) {
        ' '.multiply(item.level*2-2) + '1. ' + item.text + LINESEP + item.content
    }

    def convertListItemTypeOlist(ListItem item, Map<String, Object> opts) {
        ' '.multiply(item.level*2-2) + '1. ' + item.text + LINESEP + item.content
    }

    def convertListItemTypeUlist(ListItem item, Map<String, Object> opts) {
        return ' '.multiply(item.level*2-2) + '* ' + item.text + LINESEP + item.content
    }

    def convertThematicBreak(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block
        '---' + LINESEP
    }

    def convertLiteral(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block

        // If a method referencing the role exists use that, otherwise fall back to default
        if(block.attributes.role ) {
            def method = itemMethodName('convertLiteral',block.attributes.role)
            if (this.metaClass.respondsTo(this,methodName,block,opts)) {
                return "${methodName}"(block,opts)
            }
        }

        '{linenos=off}' + LINESEP +
            block.lines().collect {
                ' '.multiply(4) + it
            }.join(LINESEP) + LINESEP
    }

    def convertVerse(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block

        // If a method referencing the role exists use that, otherwise fall back to default
        if(block.attributes.role ) {
            def method = itemMethodName('convertVerse',block.attributes.role)
            if (this.metaClass.respondsTo(this,method,block,opts)) {
                return "${method}"(block,opts)
            }
        }

        (block.title ? ("A> ## ${block.title}" + LINESEP) : '') +
            block.lines().collect {
                'A> ' + it
            }.join(LINESEP) + LINESEP +
            (block.attributes.attribution ? "A> ${LINESEP}A> -- **${block.attributes.attribution}**${LINESEP}" : '') +
            (block.attributes.citetitle ? "A> *${block.attributes.citetitle}*${LINESEP}" : '')
    }

    def convertVersePoem(Block block,Map<String, Object> opts) {
        '{style="poem"}' + LINESEP +
            '~'.multiply(6)  + LINESEP +
            block.lines().join(LINESEP) + LINESEP +
            '~'.multiply(6)  + LINESEP
    }

    def convertListingTypeSource(Block block,Map<String, Object> opts) {
        List<String> annotations = [ "lang=\"${block.attributes.language}\"" ]
        if(block.title) {
            annotations+="title=\"${block.title}\""
        }

        '{' + annotations.join(', ') + '}' + LINESEP +
            '~'.multiply(8) + LINESEP +
            block.source()  + LINESEP +
            '~'.multiply(8) + LINESEP
    }

    def convertAdmonition(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block

        String prefix = styleMap[block.attributes.name]
        if(!prefix) {
            log.warn "${block.attributes.name} not recognised as a Leanpub admonition. Will render as normal text"
            block.attributes.style + ': ' + block.lines().join(LINESEP)
        } else {
            String content = ''
            if(block.title) {
                content+= prefix + '> ## ' + block.title + LINESEP
            }
            block.lines().each {
                content+= prefix + '> ' + it + LINESEP
            }
            content + LINESEP
        }
    }

    private void setFrontCoverFromAttribute(final File imgDir,final String frontCover) {
        frontCoverImage = null
        if(frontCover) {
            Matcher matcher = frontCover =~ INLINE_IMG_PATTERN
            if(matcher.matches()) {
                File tmpImage = new File(imgDir ?: new File(docDir,'images'),matcher[0][1])
                if(!tmpImage.name.endsWith('.png')) {
                    log.warn "Front cover image '${tmpImage.name}' does not have a PNG extension. Ignoring front cover."
                } else if(!tmpImage.exists()) {
                    log.warn "Front cover image '${tmpImage}' not found. Ignoring front cover."
                } else {
                    frontCoverImage = tmpImage
                }
            } else {
                log.warn "front-cover-image is not a valid pattern. Ignoring front cove"
            }

        }

    }

    private File docDir
    private File destDir
    private File frontCoverImage
    private List<ConvertedSection> leanpubSections = []
    private Object preface

    private static final def styleMap = [
        'warning'   : 'W',
        'tip'       : 'T',
        'note'      : 'I',
//        'caution'   : '',
//        'important' : ''
    ]




}

/*
attributes:
encoding:UTF-8, sectids:, toc-placement:auto, stylesheet:, webfonts:, prewrap:, attribute-undefined:drop-line, attribute-missing:skip, iconfont-remote:, caution-caption:Caution, important-caption:Important, note-caption:Note, tip-caption:Tip, warning-caption:Warning, appendix-caption:Appendix, example-caption:Example, figure-caption:Figure, table-caption:Table, toc-title:Table of Contents, manname-title:NAME, untitled-label:Untitled, version-label:Version, last-update-label:Last updated, docfile:simple-book.adoc, docdir:, docname:simple-book, docdate:2015-05-19, doctime:22:42:17 BST, docdatetime:2015-05-19 22:42:17 BST, asciidoctor:, asciidoctor-version:1.5.2, safe-mode-name:secure, safe-mode-secure:, safe-mode-level:20, max-include-depth:64, user-home:., backend:leanpub, linkcss:, doctype:article, doctype-article:, backend-leanpub-doctype-article:, backend-leanpub:, outfilesuffix:.html, filetype:html, filetype-html:, basebackend:leanpub, basebackend-leanpub:, basebackend-leanpub-doctype-article:, localdate:2015-05-20, localtime:10:49:26 BST, localdatetime:2015-05-20 10:49:26 BST, stylesdir:., iconsdir:./images/icons, doctitle:Title, firstname:Asciidoctor, author:Asciidoctor Team, authorinitials:AT, lastname:Team, email:foo@bar.example, authorcount:1, authors:Asciidoctor Team

document.options
[backend:leanpub, destination_dir:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/./build/test/leanpub, header_footer:true, to_dir:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/build/resources/test/test-documents, attributes:[docfile:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/build/resources/test/test-documents/simple-book.adoc, docdir:/Users/schalkc/Projects/asciidoctor-dev/asciidoctor-leanpub/core/build/resources/test/test-documents, docname:simple-book, docdate:2015-05-19, doctime:22:42:17 BST, docdatetime:2015-05-19 22:42:17 BST]]
 */