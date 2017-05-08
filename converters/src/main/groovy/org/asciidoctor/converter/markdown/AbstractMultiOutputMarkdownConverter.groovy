package org.asciidoctor.converter.markdown

import groovy.util.logging.Slf4j
import org.asciidoctor.ast.*

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
abstract class AbstractMultiOutputMarkdownConverter extends AbstractMarkdownConverter {

    AbstractMultiOutputMarkdownConverter(final String backend,Map<String, Object> opts) {
        super(backend, opts)
    }

//    /** Constructs a method name by capitalizing first character and removal of subsequent
//     * underscores, replacing the latter and the following character by a capitalized version.
//     * @param prefix A prefix to add to the name
//     * @param type The name to be adjusted.
//     * @return Method name
//     */
//    static String itemMethodName(final String prefix,final String type) {
//        String name = type.capitalize().replaceAll( ~/_\p{Alnum}/ ) {
//            it[1..-1].capitalize()
//        }
//        prefix + name
//    }
//
//    /** Created a method name out of a transform type or a node name.
//     *
//     * @param transform Transform name. If this is null, then nodeName will be used
//     * @param nodeName The name of the node
//     * @return Method name
//     */
//    static String methodName(final String transform,final String nodeName) {
//        itemMethodName('convert',transform?:nodeName)
//    }
//
//    /** If a method starts with {@code convert} it will be reported as missing, but processing will be continued
//     * Any other missing method will throw a MissingMethodException.
//     *
//     * @param name
//     * @param args
//     * @return
//     */
//    def methodMissing(String name, args) {
//
//        if(name.startsWith('convert') && args.size() == 2 && args[0] instanceof ContentNode) {
//            if(name.startsWith('convertAnchorType')) {
//                Inline inline = args[0] as Inline
//                log.error logMessageWithSourceTrace(
//                    "Anchor type '${inline.type}' is not defined. Will not transform this node, but will try to carry on.",
//                    inline
//                )
//            } else if(name.startsWith('convertListingType')) {
//                Block inline = args[0] as Block
//                log.error logMessageWithSourceTrace(
//                    "Listing type '${block.attributes.style}' is not defined. Will not transform this node, but will try to carry on.",
//                    inline
//                )
//            } else if(name.startsWith('convertListItemType')) {
//                ListItem item = args[0] as ListItem
//                log.error logMessageWithSourceTrace(
//                    "List item type '${item.parent.context}' is not defined. Will not transform this node, but will try to carry on",
//                    item
//                )
//            } else {
//                ContentNode node = args[0] as ContentNode
//                log.error logMessageWithSourceTrace(
//                    "${name} (node:${node.class.name}) is not defined. Will not transform this node, but will try to carry on.",
//                    node
//                )
//            }
//            return null
//        } else {
//            throw new MissingMethodException(name,this.class,args)
//        }
//    }

    /**
     * Converts an {@link org.asciidoctor.ast.ContentNode} using the specified transform along
     * with additional options. If a transform is not specified, implementations
     * typically derive one from the {@link org.asciidoctor.ast.ContentNode#getNodeName()} property.
     *
     * <p>For a document node, setupDocument is called at the start of processing and closeDocument at
     * the end of processing. IN between all other ndoes are processed. This allow for streaming output to
     * any media.
     *
     * @param node The concrete instance of ContentNode to convert
     * @param transform An optional String transform that hints at which transformation
     *             should be applied to this node. If a transform is not specified,
     *             the transform is typically derived from the value of the
     *             node's node_name property. (optional, default: null)
     * @param opts An optional map of options that provide additional hints about
     *             how to convert the node. (optional, default: empty map)
     * @return the converted result
     */
    @Override
    String convert(ContentNode node, String transform, Map<Object, Object> opts) {
        if (node instanceof Document) {
            if(setupComplete) {
                return node.content
            } else {
                setupDocument(node,opts)
                setupComplete = true
                def result =  closeDocument(node.content)
                setupComplete = false
                return result
            }
        } else {
            return "${methodName(transform,node.nodeName)}"(node,opts)
        }
    }

    /** Try to resolve an image directory, by looking for {@code imagesdir}.
     *
     * @param node
     * @return Returns a File instance of resovled, otherwise null
     */
    File imagesDir(ContentNode node) {
        node.document.attributes['imagesdir'] ? new File(node.document.attributes['imagesdir']) : null
    }

    /** Try to resolve an image directory, by looking for {@code imagesdir}.
     *
     * @param node
     * @param docDir Document directory
     * @return Returns a File instance of resovled, otherwise docDir
     */
    File imagesDir(ContentNode node,final File docDir) {
        node.document.attributes['imagesdir'] ? new File(node.document.attributes['imagesdir']) : docDir
    }

    /** Called before any processing on the document node starts. Use this to set up any appropriate
     * properties based upon document options and backend attributes.
     *
     * @param node
     * @param opts
     */
    abstract void setupDocument(ContentNode node,Map<Object,Object> opts)

    /** Called after document node processing has been completed. The completed the content is passed from
     * final post-processing before returning the object.
     *
     * @param content
     * @return An object representing completed content
     */
    abstract def closeDocument(def content)

    abstract def convertSection(ContentNode node, Map<String, Object> opts)
    abstract def convertInlineQuoted(ContentNode node, Map<String, Object> opts)
    abstract def convertThematicBreak(ContentNode node,Map<String, Object> opts)
    abstract def convertAdmonition(ContentNode node,Map<String, Object> opts)
    abstract def convertLiteral(ContentNode node,Map<String, Object> opts)
    abstract def convertVerse(ContentNode node,Map<String, Object> opts)
    abstract def convertSidebar(ContentNode node,Map<String, Object> opts)
    abstract def convertQuote(ContentNode node,Map<String, Object> opts)
    abstract def convertPass(ContentNode node,Map<String, Object> opts)
    abstract def convertImage(ContentNode node,Map<String, Object> opts)
    abstract def convertInlineImage(ContentNode node,Map<String, Object> opts)
    abstract def convertTable(ContentNode node,Map<String, Object> opts)
    abstract def convertStem(ContentNode node,Map<String, Object> opts)
    abstract def convertOpen(ContentNode node,Map<String, Object> opts)
    abstract def convertPreamble(ContentNode node,Map<String, Object> opts)
    abstract def convertAnchorTypeRef(ContentNode node,Map<String, Object> opts)

//    /** Paragraph conversion just passes the content back.
//     *
//     * @return Content plus an additional line separator
//     */
//    def convertParagraph(ContentNode node, Map<String, Object> opts) {
//        Block block = node as Block
//        block.content + LINESEP
//    }

//    def convertUlist(ContentNode node,Map<String, Object> opts) {
//        ListNode listNode = node as ListNode
//        listNode.items.collect { ListItem item -> item.convert() }.join('')
//    }
//
//    def convertOlist(ContentNode node,Map<String, Object> opts) {
//        ListNode listNode = node as ListNode
//        listNode.items.collect { ListItem item -> item.convert() }.join('')
//    }
//
//    def convertColist(ContentNode node,Map<String, Object> opts) {
//        ListNode listNode = node as ListNode
//        listNode.items.collect { ListItem item -> item.convert() }.join('')
//    }

    @Override
    def convertListItem(ContentNode node,Map<String, Object> opts) {
        ListItem item = node as ListItem

        if(item.parent.attributes?.style == 'bibliography' || item.parent.parent.attributes?.style == 'bibliography') {
            convertListItemTypeBibreflist(node,opts)
        }
        else {
            "${itemMethodName('convertListItemType', node.parent.context)}"(node, opts)
        }
    }

    abstract def convertListItemTypeColist(ListItem node, Map<String, Object> opts)
    abstract def convertListItemTypeBibreflist(ListItem node, Map<String, Object> opts)

    /** Redirects an anchor to the appropriate anchor type converter
     *
     * @param node
     * @param opts
     * @return
     */
    def convertInlineAnchor(ContentNode node, Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        "${itemMethodName('convertAnchorType',inline.type)}"(node,opts)
    }

    abstract def convertAnchorTypeXref(ContentNode node, Map<String, Object> opts)
    abstract def convertAnchorTypeLink(ContentNode node, Map<String, Object> opts)
    abstract def convertAnchorTypeBibref(ContentNode node, Map<String, Object> opts)

    def convertListing(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        if(block.attributes.style == null) {
            log.warn "Attributes for ${block.nodeName} has no  associated style. Skipping conversion."
            return null
        }
        "${itemMethodName('convertListingType',block.attributes.style)}"(block,opts)
    }

    abstract def convertListingTypeSource(Block block,Map<String, Object> opts)


    private boolean setupComplete = false
}
