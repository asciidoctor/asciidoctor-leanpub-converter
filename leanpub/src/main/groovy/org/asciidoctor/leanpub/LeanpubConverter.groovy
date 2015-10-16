package org.asciidoctor.leanpub

import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.asciidoctor.ast.*
import org.asciidoctor.converter.ConverterFor
import org.asciidoctor.converter.markdown.core.AbstractMultiOutputMarkdownConverter
import org.asciidoctor.markdown.internal.SourceParser
import org.asciidoctor.leanpub.internal.CrossReference
import org.asciidoctor.leanpub.internal.LeanpubCell
import org.asciidoctor.leanpub.internal.LeanpubTable
import org.asciidoctor.leanpub.internal.LeanpubTableRow
import org.asciidoctor.leanpub.internal.QuotedTextConverter
import static org.asciidoctor.leanpub.ConvertedSection.SectionType.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
@ConverterFor(format="leanpub",suffix="txt")
class LeanpubConverter extends AbstractMultiOutputMarkdownConverter {

    static final String LINESEP = "\n"
    static final String BOOK = 'Book.txt'
    static final String SAMPLE = 'Sample.txt'
    static final String DOCFOLDER = 'manuscript'
    static final String FRONTCOVER = 'title_page.png'
    static final Pattern INLINE_IMG_PATTERN = ~/^image:(.+?)\[(.*?)\]$/
    static final Pattern LISTITEM_BIBREF_PATTERN = ~/(?s)(.+\s+)?(\{#.+?\})(.+)/

    String encoding = 'utf-8'

    LeanpubConverter(final String backend,Map<String, Object> opts) {
        super(backend, opts)
    }

    void setupDocument(ContentNode node,Map<Object, Object> opts) {

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
        node.attributes.put('vbar','\\|')

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

        int index=1
        document.parts.each { part ->
            if(document.isMultiPart()) {
                File dest = new File(destDir,"part_${index}.txt" )
                dest.withWriter { part.write(it) }

                chapterNames += dest.name
                ++index

                if (part.sample) {
                    chaptersInSample += dest.name
                }
            }

            part.chapters.each { chapter ->
                File dest = new File(destDir,"chapter_${index}.txt" )
                dest.withWriter { chapter.write(it) }
                chapterNames+= dest.name
                ++index
                if(chapter.sample) {
                    chaptersInSample+= dest.name
                }
            }
        }

        if (document.hasBackMatter()) {
            chapterNames += 'backmatter.txt'
            chaptersInSample += 'backmatter.txt'
        }

        document.backmatter.each { bm ->
            File dest = new File(destDir,"backmatter_${index}.txt" )
            dest.withWriter { bm.write(it) }
            chapterNames+= dest.name
            if(bm.sample) {
                chaptersInSample+= dest.name
            }
            ++index
        }

        new File(destDir,BOOK).withWriter { w ->

            if( document.hasFrontMatter() ) {
                w.println 'frontmatter.txt'
            }

            if(document.preamble) {
                w.println 'preamble.txt'
            }

            if(document.dedication) {
                w.println 'dedication.txt'
            }

            if(document.preface) {
                w.println 'preface.txt'
            }

            if( document.hasFrontMatter() || document.hasBackMatter() ) {
                w.println 'mainmatter.txt'
            }

            chapterNames.each {
                w.println it
            }

        }

        new File(destDir,SAMPLE).withWriter { w ->
            if(document.preface?.sample) {
                w.println 'frontmatter.txt'
                w.println 'preface.txt'
                w.println 'mainmatter.txt'
            }

            chaptersInSample.each { w.println it }
        }

        if(document.preamble) {
            new File(destDir,'preamble.txt').text = document.preamble.content.toString()
        }

        if(document.dedication) {
            new File(destDir,'dedication.txt').text = document.dedication.content.toString()
        }

        if(document.preface) {
            new File(destDir,'preface.txt').text = document.preface.content.toString()
        }

        if(document.hasFrontMatter() || document.hasBackMatter() ) {
            new File(destDir, 'mainmatter.txt').text = '{mainmatter}'
        }

        if(document.hasFrontMatter() ) {
            new File(destDir,'frontmatter.txt').text = '{frontmatter}'
        }
        if(document.hasBackMatter() ) {
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

    def convertSection(ContentNode node, Map<String, Object> opts) {
        Section section = node as Section
        boolean  inSample = false

        log.debug "Transforming section: name=${section.sectname()}, level=${section.level} title=${section.title}"
        if(section.attributes.leanpub) {
            Set keywords = section.attributes.leanpub.split(',') as Set
            inSample= keywords.contains('sample')
        }

        if(section.level==0) {
            document.addPart()
            document.currentPart.title = "-# ${section.title}${LINESEP}${LINESEP}"
            return section.content
        } else if(section.level == 1) {

            switch(section.sectionName) {
                case 'preface':
                    if(document.preface == null) {
                        document.preface = new ConvertedSection(content: formatSection(section), type: PREFACE, sample: inSample)
                        return document.preface.content
                    } else {
                        log.warn "A [preface] level one section was processed previously. This one will be ignored."
                        return ''
                    }
                case 'dedication' :
                    if(document.dedication == null) {
                        document.dedication = new ConvertedSection(content: formatDedication(section), type: DEDICATION, sample: false)
                        return document.dedication.content
                    } else {
                        log.warn "A [dedication] level one section was processed previously. This one will be ignored."
                        return ''
                    }
                case 'chapter':
                    document.addChapterToPart(new ConvertedSection(content: formatSection(section), type: CHAPTER, sample: inSample))
                    return document.currentPart.chapters[-1].content
                case ~/appendix|bibliography|index|glossary/:
                    return document.addBackmatter(new ConvertedSection(
                        content: formatSection(section),
                        type: BACKMATTER,
                        sample: inSample
                    )).content
            }
        }

        return formatSection(section)
    }

    def convertInlineQuoted(ContentNode node, Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        // In table context it needs to get split otherwise formatting in Leanpub
        // will be messed up
        if(inline.parent.context == 'cell' || inline.parent.context=='column') {
            inline.text.split(LINESEP).collect {
                QuotedTextConverter."${inline.type}"(it)
            }.join(LINESEP)
        } else {
            QuotedTextConverter."${inline.type}"(inline.text)
        }
    }

    def convertAnchorTypeXref(ContentNode node, Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        "[${inline.text ?: inline.attributes.fragment}](#${CrossReference.safeId(inline.attributes.refid)})"
    }

    def convertAnchorTypeLink(ContentNode node, Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        return "[${inline.text}](${inline.target})"
    }

    def convertAnchorTypeBibref(ContentNode node, Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        "{#${inline.text}}${LINESEP}"
    }

    @Override
    def convertColist(ContentNode node,Map<String, Object> opts) {
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

            List listNode = node as List
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

//    def convertListItemTypeOlist(ListItem item, Map<String, Object> opts) {
//        ' '.multiply(item.level*2-2) + '1. ' + item.text + LINESEP + item.content
//    }
//
//    /** Create an unordered list item, but takes special care if the list item is part of bibliography
//     *
//     * @param item
//     * @param opts
//     * @return
//     */
//    def convertListItemTypeUlist(ListItem item, Map<String, Object> opts) {
//        return ' '.multiply(item.level*2-2) + '* ' + item.text + LINESEP + item.content
//    }


    /** Takes special care if the list item is part of bibliography
     *
     * @param item
     * @param opts
     * @return
     */
    def convertListItemTypeBibreflist(ListItem item, Map<String, Object> opts) {
        // Strip the anchor, write it on a separate line the write the rest

        def matcher = item.text.replaceAll(LINESEP,' ') =~ LISTITEM_BIBREF_PATTERN
        if(matcher.matches()) {
            return matcher[0][2] + LINESEP +
                (matcher[0][1] ?: '') +
                matcher[0][3].trim() + LINESEP + item.content + LINESEP
        }

        item.text.trim() + LINESEP + item.content + LINESEP

    }

    def convertThematicBreak(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        '---' + LINESEP
    }

    def convertLiteral(ContentNode node,Map<String, Object> opts) {
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

    def convertVerse(ContentNode node,Map<String, Object> opts) {
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

    def convertSidebar(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        (block.title ? ("A> ## ${block.title}" + LINESEP) : '') +
            block.content.readLines().collect {
                'A> ' + it
            }.join(LINESEP) + LINESEP
    }

    def convertQuote(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        String convertedBlock = (block.title ? ("> ## ${block.title}" + LINESEP) : '') +
            block.content.readLines().collect {
                '> ' + it
            }.join(LINESEP) + LINESEP +
            (block.attributes.attribution ? "> ${LINESEP}> -- **${block.attributes.attribution}**${LINESEP}" : '') +
            (block.attributes.citetitle ? "> *${block.attributes.citetitle}*${LINESEP}" : '')

        convertedBlock.replaceAll('&#8217;',"'") /* Set quotes as is, not fancy */
    }

    def convertPass(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        block.content
    }

    def convertListingTypeSource(Block block,Map<String, Object> opts) {
        Map annotations = [lang: "${block.attributes.language}"]
        if (block.title) {
            annotations['title'] = block.title
        }

        def parsedContent = SourceParser.parseSourceForCallouts(block)
        String src
        if (parsedContent.find ({ it.callouts != null }) != null) {
            annotations['linenos'] = 'yes'
            lastSrcBlock = parsedContent
            src= parsedContent.collect { it.line }.join(LINESEP)
        } else {
            src=block.source
        }

        renderLeanpubAttributes(annotations) +
            '~'.multiply(8) + LINESEP +
            src  + LINESEP +
            '~'.multiply(8) + LINESEP
    }

    def convertAdmonition(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block

        def style = styleMap[block.attributes.name]
        if(!style) {
            log.warn "${block.attributes.name} not recognised as a Leanpub admonition. Will render as normal text"
            block.attributes.style + ': ' + block.lines.join(LINESEP)
        } else {
            String content = ''
            if(style['icon'] && style['icon'].size()) {
                content+= "{icon=${style.icon}}${LINESEP}"
            }
            if(block.title) {
                content+= style.prefix + '> ## ' + block.title + LINESEP
            }
            block.lines().each {
                content+= style.prefix + '> ' + it + LINESEP
            }
            content + LINESEP
        }
    }

    def convertImage(ContentNode node,Map<String, Object> opts) {
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

    def convertInlineImage(ContentNode node,Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        images.add new File(imagesDir(inline,docDir),inline.target)
        "![](images/${inline.target} \"${inline.attributes.alt}\")"
    }

    def convertTable(ContentNode node,Map<String, Object> opts) {
        Table table = node as Table
        LeanpubTable leanpubTable = new LeanpubTable(
            table.body.size(),
            table.columns.size(),
            table.header.size(),
            table.footer.size()
        )
        def tableAttrs = [:]

        switch(table.attributes.width) {
            case null:
                tableAttrs['width'] = 'default'
                break
            case 'narrow':
                log.warn "Leanpub width of 'narrow' is not currently supported. Will set table width to 'default'"
                tableAttrs['width'] = 'default'
                break
            case 'default':
            case 'full':
                tableAttrs['width'] = table.attributes.width
                break
            default:
                tableAttrs['width'] = table.attributes.width.endsWith('%') ? table.attributes.width : "${table.attributes.width}%"
        }

        if(table.title) {
            tableAttrs['title']= table.title
        }

        table.header.eachWithIndex { Row row,int rowIndex ->
            processOneAsciidocTableRow row,leanpubTable.header[rowIndex]
        }
\
        table.footer.eachWithIndex { Row row,int rowIndex ->
            processOneAsciidocTableRow row,leanpubTable.footer[rowIndex]
        }

        table.body.eachWithIndex { Row row,int rowIndex ->
            processOneAsciidocTableRow row,leanpubTable.rows[rowIndex]
        }

        leanpubTable.postProcessTables()

        // Leanpub Table Formatting for width != narrow
        String renderedTable = renderLeanpubAttributes(tableAttrs)


        if(leanpubTable.header) {
            for( ltr in leanpubTable.header ) {
                renderedTable+= renderOneLeanpubTableRow(ltr,leanpubTable.columnWidths,leanpubTable.columnAlignment) + LINESEP
            }
        } else if (!leanpubTable.isDefaultAlignment || leanpubTable.hasMultilineCells) {
            // If header does not exist, but multi-line cells or non-default alignment, we need tp create an empty header
            renderedTable+= '|' + ' |'.multiply(leanpubTable.columnWidths.size()) + LINESEP
        }

        // Wee need to generate a separator line if header exists or we don't have default alignment or we
        // have multi-line cells.
        Character charAbove = (leanpubTable.header || !leanpubTable.isDefaultAlignment || leanpubTable.hasMultilineCells )? '-' : null

        for( ltr in leanpubTable.rows ) {
            renderedTable+= renderOneLeanpubTableRow(ltr,leanpubTable.columnWidths,leanpubTable.columnAlignment,charAbove) + LINESEP
            if(!leanpubTable.heterogeneousColumnAlignment && !leanpubTable.hasMultilineCells ) {
                charAbove=null
            }
        }

        if(leanpubTable.footer) {
            charAbove='='
            for( ltr in leanpubTable.footer ) {
                renderedTable+= renderOneLeanpubTableRow(ltr,leanpubTable.columnWidths,leanpubTable.columnAlignment,charAbove) + LINESEP
                charAbove=null
            }
        }

        renderedTable
    }

    def convertStem(ContentNode node,Map<String, Object> opts) {
        Block stem = node as Block

        switch(stem.style) {
            case 'latexmath':
                (stem.title ? QuotedTextConverter.latexmath("${LINESEP}\\textbf{${stem.title}}${LINESEP}") + LINESEP + LINESEP : '') +
                    QuotedTextConverter.latexmath("${LINESEP}${stem.content}${LINESEP}") + LINESEP
                break
            default:
                log.warn "Stem block of style '${stem.style}' will be ignored, but processing will continue"
                null
        }
    }

    def convertOpen(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        if(block.style) {
            "convert${block.style.capitalize()}"(block,opts)
        } else {
            log.error logMessageWithSourceTrace(
                "Open block with no styling not yet implemented. Will not transform this node, but will try to carry on. " +
                "Please raise an issue at https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues with an example of your use case." ,
                block
            )
            null
        }
    }

    def convertPartintro(Block block,Map<String, Object> opts) {
        if(document.currentPart) {
            document.currentPart.partIntro = block.content + LINESEP
        } else {
            log.warn logMessageWithSourceTrace(
                "Using [partintro] outside of a part is not supported for leanpub backend. This block will be ignored.",
                block
            )
        }
    }

    def convertPreamble(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        document.preamble = new ConvertedSection(content: formatPreamble(block), type: PREAMBLE, sample: false)
        return document.preamble.content
    }

    private String leanpubTableAlignmentRow(Character divCharAbove,LeanpubTable.HorizontalAlignment ha) {

        if(divCharAbove==null) {
            return ''
        }

        switch(ha) {
            case null:
                "${divCharAbove}".multiply(5) + '|'
                break
            case LeanpubTable.HorizontalAlignment.LEFT:
                ':' + "${divCharAbove}".multiply(4) + '|'
                break
            case LeanpubTable.HorizontalAlignment.RIGHT:
                "${divCharAbove}".multiply(4) + ':|'
                break
            case LeanpubTable.HorizontalAlignment.CENTER:
                ':' + "${divCharAbove}".multiply(3) + ':|'
                break
            default:
                ''
        }
    }

    /** Processes one table rows as defined in Asciidoc syntax. Translates multiple rows found within a cell
     * to multiple Leanpub cell rows.
     *
     * @param asciiRow An instance of a table row from Asciidoc
     * @param lpRow An instance of a table tow for Leanpub
     */
    private void processOneAsciidocTableRow(Row asciiRow,LeanpubTableRow lpRow) {
        int cellIndex=0 // TODO: Maybe we can use cell.column.columnNumber instead to work out the position

        asciiRow.cells.each { Cell cell ->

            def content = cell.content instanceof Collection ? cell.content : [cell.content]

            lpRow.cells[cellIndex].content = content.collect{ it.split(LINESEP) }.flatten()
            lpRow.cells[cellIndex].halign = cell.horizontalAlignment

            int colspan = cell.colspan - 1
            while(colspan > 0 ) {
                log.warn "Colspan > 1 is not supported at present by Leanpub. An empty cell will be generated."
                ++cellIndex
                --colspan
                lpRow.cells[cellIndex].content = [' '.multiply(5)]
            }
            ++cellIndex
        }
    }


    private String renderOneLeanpubTableRow(LeanpubTableRow ltr,def columnWidths,def columnAlignment,Character divCharAbove=null) {

        String ret= ''
        if(divCharAbove) {
            ret+='|'
            columnAlignment.eachWithIndex { LeanpubTable.HorizontalAlignment ha,int index ->
                switch(ha) {
                    case null:
                    case LeanpubTable.HorizontalAlignment.LEFT:
                    case LeanpubTable.HorizontalAlignment.RIGHT:
                    case LeanpubTable.HorizontalAlignment.CENTER:
                        ret+= leanpubTableAlignmentRow(divCharAbove,ha)
                        break
                    case LeanpubTable.HorizontalAlignment.VARIOUS:
                        ret+= leanpubTableAlignmentRow(divCharAbove,LeanpubTable.convertAlignment(ltr.cells[index].halign))
                }
            }
            ret+= LINESEP
        }

        int textRowCount = ltr.textRows

        ret + (0..textRowCount-1).collect { int rowCounter ->
            String renderedRow = '|'
            ltr.cells.eachWithIndex { LeanpubCell cell, int index ->
                int width = columnWidths[index]
                if(rowCounter < cell.content?.size()) {
                    renderedRow+= cell.content[rowCounter]
                } else {
                    renderedRow+= ' '.multiply(5)
                }
                renderedRow+= '|'
            }
            renderedRow
        }.join(LINESEP)
    }

    /** Formats the content in a dedication.
     *
     * @param section
     * @return Formatted content
     */
    private String formatDedication(Section section) {

        "##### &nbsp;${LINESEP}${LINESEP}" +
            renderLeanpubAttributes( width : 'narrow' ) +
            "| |${LINESEP}".multiply(10) +
            LINESEP +
            section.content.toString().readLines().collect {
                "C> ${it}"
            }.join(LINESEP) + LINESEP
    }

    /** Formats the content in a prematter.
     *
     * @param section
     * @return Formatted content
     */
    private String formatPreamble(Block block) {

        "##### &nbsp;${LINESEP}${LINESEP}" +
            block.content + LINESEP +
            '{pagebreak}' + LINESEP
    }

    /** Formats the content in a section.
     *
     * @param section
     * @return Formatted content
     */
    private String formatSection(Section section) {
        (section.level==0 ? '-#' : '#'.multiply(section.level)) +
            ' ' +
            section.title +
            LINESEP + LINESEP +
            section.content
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
    private String formatColistItemLines(final String style,final String prefix,final String lineno,final java.util.List<String> lines) {
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
    private String wrapColistLineText( final String firstPrefix, final String otherPrefix,final java.util.List<String> lines) {
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

    private String renderLeanpubAttributes( Map attrs ) {
        if(attrs?.size()) {
            return '{' + attrs.collect { k,v ->
                "${k}=\"${v}\""
            }.join(',') + "}${LINESEP}"
        } else {
            return ''
        }
    }

    private LeanpubDocument document = new LeanpubDocument()

    private File docDir
    private File destDir
    private File frontCoverImage
    private Object lastSrcBlock
    private java.util.List<File> images = []

    private static final def styleMap = [
        'warning'   : [ prefix : 'W' ],
        'tip'       : [ prefix : 'T' ],
        'note'      : [ prefix : 'I' ],
        'caution'   : [ prefix : 'G', icon: 'fire' ],
        'important' : [ prefix : 'G', icon: 'university' ],
    ]

    private static final def validImageFloats = [ 'left', 'right', 'inside', 'outside']
}