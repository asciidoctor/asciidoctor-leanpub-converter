package org.ysb33r.asciidoctor.leanpub

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.asciidoctor.ast.AbstractNode
import org.asciidoctor.ast.Block
import org.asciidoctor.ast.Inline
import org.asciidoctor.ast.ListItem
import org.asciidoctor.ast.ListNode
import org.asciidoctor.ast.Section
import org.ysb33r.asciidoctor.AbstractTextConverter
import org.ysb33r.asciidoctor.internal.SourceParser
import org.ysb33r.asciidoctor.leanpub.internal.CrossReference
import org.ysb33r.asciidoctor.leanpub.internal.QuotedTextConverter
import static org.ysb33r.asciidoctor.leanpub.ConvertedSection.SectionType.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
class LeanpubConverter extends AbstractTextConverter {

    static final String LINESEP = "\n"
    static final String BOOK = 'Book.txt'
    static final String SAMPLE = 'Sample.txt'
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

        if(!node.attributes.get('leanpub-colist-style')) {
            node.attributes.put('leanpub-colist-style','paragraph')
        }
        if(!node.attributes.get('leanpub-colist-prefix')) {
            node.attributes.put('leanpub-colist-prefix','Line')
        }
    }

    /** Writes all of the index file
     *
     */
    def closeDocument(def content) {
        File imagesToDir = new File(destDir,'images')
        imagesToDir.mkdirs()

        def chapterNames = []
        def chaptersInSample = []
        boolean hasBackmatter = false
        boolean hasFrontmatter = preface != null

        leanpubSections.findAll { ConvertedSection it ->
            it.type == CHAPTER || it.type == PART || it.type == BACKMATTER
        }.eachWithIndex { ConvertedSection section, int index ->
            String prefix
            switch(section.type) {
                case PART:
                    prefix= 'part'
                    break
                case BACKMATTER:
                    prefix = 'backmatter'

                    if (!hasBackmatter) {
                        hasBackmatter= true
                        chapterNames+='backmatter.txt'
                        chaptersInSample+='backmatter.txt'
                    }

                    break
                default:
                    prefix = 'chapter'
            }

            File dest = new File(destDir,"${prefix}_${index+1}.txt" )
            dest.withWriter { w -> section.write(w) }
            chapterNames+= dest.name
            if(section.sample) {
                chaptersInSample+= dest.name
            }
        }

        new File(destDir,BOOK).withWriter { w ->

            if( hasFrontmatter) {
                w.println 'frontmatter.txt'
            }

            if(preface) {
                w.println 'preface.txt'
            }

            if( hasFrontmatter || hasBackmatter ) {
                w.println 'mainmatter.txt'
            }

            chapterNames.each {
                w.println it
            }

        }

        new File(destDir,SAMPLE).withWriter { w ->
            if(preface?.sample) {
                w.println 'frontmatter.txt'
                w.println 'preface.txt'
                w.println 'mainmatter.txt'
            }

            chaptersInSample.each { w.println it }
        }

        if(preface) {
            new File(destDir,'preface.txt').text = preface.content.toString()
        }

        if(hasFrontmatter || hasBackmatter) {
            new File(destDir, 'mainmatter.txt').text = '{mainmatter}'
        }

        if(hasFrontmatter) {
            new File(destDir,'frontmatter.txt').text = '{frontmatter}'
        }
        if(hasBackmatter) {
            new File(destDir,'backmatter.txt').text = '{backmatter}'
        }

        if(frontCoverImage) {
            FileUtils.copyFile(frontCoverImage,new File(imagesToDir,FRONTCOVER))
        }

        images.each { img ->
            FileUtils.copyFile(img,new File(imagesToDir,img.name))
        }

        null // Currently return null as AsciidoctorJ has no way of calling us back to spool the object
    }

    def convertSection(AbstractNode node, Map<String, Object> opts) {
        Section section = node as Section
        int sectionIndex = -1
        boolean  inSample = false

        log.debug "Transforming section: name=${section.sectname()}, level=${section.level} title=${section.title}"
        if(section.attributes.leanpub) {
            Set keywords = section.attributes.leanpub.split(',') as Set
            inSample= keywords.contains('sample')
        }

        if(section.level==0) {
            leanpubSections+= new ConvertedSection( content :null, type : PART )
            sectionIndex= leanpubSections.size() - 1
        } else if(section.sectname()=='preface') {
            preface = new ConvertedSection(content: null, type: PREFACE, sample : inSample)
        } else if(section.sectname()=='chapter') {
            leanpubSections += new ConvertedSection(content: null, type: CHAPTER, sample : inSample)
            sectionIndex = leanpubSections.size() - 1
        } else if(section.sectname() =~ /appendix|bibliography|index|glossary/ ) {
            leanpubSections += new ConvertedSection(content: null, type: BACKMATTER, sample: inSample)
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
            preface = new ConvertedSection( content : content, type : ConvertedSection.SectionType.PREFACE, sample : inSample )
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

    @Override
    def convertColist(AbstractNode node,Map<String, Object> opts) {
        if(lastSrcBlock) {
            def lookup = [:]
            lastSrcBlock.each { calloutLine ->
                if(calloutLine.callouts?.size()) {
                    calloutLine.callouts.each { callout ->
                        lookup[callout.toString()] = calloutLine.lineno
                    }
                }
            }
            lastSrcBlock = null
            String content = ''
            String prefix = node.document.attributes.get('leanpub-colist-prefix') ?: ''
            String style = node.document.attributes.get('leanpub-colist-style')

            ListNode listNode = node as ListNode
            listNode.items.eachWithIndex { ListItem item,int index ->
                String lineno = lookup[(index+1).toString()]
                if(lineno == null) {
                    log.error "More items in colist than in preceding source block. Ignoring item."
                } else {

                    content+= formatColistItemLines(style,prefix,lineno,(item.text + LINESEP + item.content + LINESEP).readLines())
                }
            }

            content + LINESEP
        } else {
            super.convertColist(node,opts)
        }
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

    def convertPass(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block
        block.content
    }

    def convertListingTypeSource(Block block,Map<String, Object> opts) {
        Map annotations = [lang: "\"${block.attributes.language}\""]
        if (block.title) {
            annotations['title'] = "\"${block.title}\""
        }

        def parsedContent = SourceParser.parseSourceForCallouts(block)
        String src
        if (parsedContent.find ({ it.callouts != null }) != null) {
            annotations['linenos'] = 'yes'
            lastSrcBlock = parsedContent
            src= parsedContent.collect { it.line }.join(LINESEP)
        } else {
            src=block.source()
        }

        '{' + annotations.collect{ k,v -> "${k}=${v}"}.join(', ') + '}' + LINESEP +
            '~'.multiply(8) + LINESEP +
            src  + LINESEP +
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

    def convertImage(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block

        def imageAttrs = []
        String prefix = ''

        if(block.getRole()) {
            def roles = block.getRole().split( ' ' )
            // Find anything in roles that is in validImageFloats?, then set role to that
            def foundFloat = roles.find { validImageFloats.contains(it) }

            if(foundFloat) {
                imageAttrs+= "float=\"${foundFloat}\""
            }
        } else if (block.attributes.float) {
            if(validImageFloats.contains(block.attributes.float)) {
                imageAttrs+= "float=\"${block.attributes.float}\""
            } else {
                log.error "'${block.attributes.float}' is not a valid image float string for Leanpub. Will ignore and continue."
            }

        } else if (block.attributes.align && block.attributes.align == 'center') {
            prefix = 'C> '
        }

        if(block.attributes.leanpub) {
            block.attributes.leanpub.split(',').each { item ->
                def kv = item.split('=')

                switch(kv[0]) {
                    case 'width':
                        imageAttrs+= "width=${kv[1]}"
                        break
                }
            }
        }

        images.add new File(imagesDir(block,docDir),block.attributes.target)
        (imageAttrs.size() ? "{${imageAttrs.join(',')}}${LINESEP}" : '')  +
             "${prefix}![${block.title?:''}](images/${block.attributes.target} \"${block.attributes.alt}\")" +
            LINESEP
    }

    def convertInlineImage(AbstractNode node,Map<String, Object> opts) {
        Inline inline = node as Inline
        images.add new File(imagesDir(inline,docDir),inline.target)
        "![](images/${inline.target} \"${inline.attributes.alt}\")"
    }

    /** Takes a bundle of lines from a Colist item and formats them in a way suitable to work with callouts in
     * Leanpub.
     *
     * @param style Colist callout style to apply.
     * @param prefix Prefix text for source code line.
     * @param lineno Source code line number to which this applies
     * @param lines All the lines from a specific item
     * @return A Leanpub-formatted Colist item.
     */
    private String formatColistItemLines(final String style,final String prefix,final String lineno,final List<String> lines) {
        switch(style) {
            case 'discussion':
                return wrapColistLineText(
                    "D> **${prefix} #${lineno}:** ",
                    'D>' + ' '.multiply(prefix.size()+3),
                    lines
                )
            case 'aside':
                return wrapColistLineText(
                    "A> **${prefix} #${lineno}:** ",
                    'A>' + ' '.multiply(prefix.size()+3),
                    lines
                )
            case 'paragraph':
            default:
                return wrapColistLineText(
                    "**${prefix} #${lineno}:** ",
                    '',
                    lines
                )
        }
    }

    /** Breaks up multi-line coList lines so that it can be indented and look better aligned
     *
     * @param prefix A prefix to ad to each line
     * @param text The text to be formatted.
     * @return
     */
    private String wrapColistLineText( final String firstPrefix, final String otherPrefix,final List<String> lines) {
        String formatted = ''
        lines.eachWithIndex { String line,int idx ->
            if(!idx) {
                formatted+= firstPrefix + line + LINESEP
            } else {
                formatted+= otherPrefix + line + LINESEP
            }
        }
        return formatted
    }

    /** Resolves the location of the front cover image.
     *
     * @param imgDir Directory where images are expected to be located. Can be null.
     * @param frontCover Text from the front-cover-image attribute.
     */
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
    private ConvertedSection preface
    private Object lastSrcBlock
    private List<File> images = []

    private static final def styleMap = [
        'warning'   : 'W',
        'tip'       : 'T',
        'note'      : 'I',
//        'caution'   : '',
//        'important' : ''
    ]

    private static final def validImageFloats = [ 'left', 'right', 'inside', 'outside']
}