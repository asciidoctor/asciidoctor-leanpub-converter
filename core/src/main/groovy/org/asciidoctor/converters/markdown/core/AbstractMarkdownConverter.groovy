package org.asciidoctor.converters.markdown.core

import groovy.util.logging.Slf4j
import org.asciidoctor.ast.*
import org.asciidoctor.converter.StringConverter

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
abstract class AbstractMarkdownConverter extends StringConverter {

    static final String LINESEP = "\n"

    AbstractMarkdownConverter(final String backend,Map<String, Object> opts) {
        super(backend, opts)
    }

    /** Constructs a method name by capitalizing first character and removal of subsequent
     * underscores, replacing the latter and the following character by a capitalized version.
     * @param prefix A prefix to add to the name
     * @param type The name to be adjusted.
     * @return Method name
     */
    static String itemMethodName(final String prefix,final String type) {
        String name = type.capitalize().replaceAll( ~/_\p{Alnum}/ ) {
            it[1..-1].capitalize()
        }
        prefix + name
    }

    /** Created a method name out of a transform type or a node name.
     *
     * @param transform Transform name. If this is null, then nodeName will be used
     * @param nodeName The name of the node
     * @return Method name
     */
    static String methodName(final String transform,final String nodeName) {
        itemMethodName('convert',transform?:nodeName)
    }

    /** If a method starts with {@code convert} it will be reported as missing, but processing will be continued
     * Any other missing method will throw a MissingMethodException.
     *
     * @param name
     * @param args
     * @return
     */
    def methodMissing(String name, args) {

        if(name.startsWith('convert') && args.size() == 2 && args[0] instanceof StructuralNode) {
            if(name.startsWith('convertAnchorType')) {
                PhraseNode inline = args[0] as PhraseNode
                log.error logMessageWithSourceTrace(
                    "Anchor type '${inline.type}' is not defined. Will not transform this node, but will try to carry on.",
                    inline
                )
            } else if(name.startsWith('convertListingType')) {
                Block inline = args[0] as Block
                log.error logMessageWithSourceTrace(
                    "Listing type '${block.attributes.style}' is not defined. Will not transform this node, but will try to carry on.",
                    inline
                )
            } else if(name.startsWith('convertListItemType')) {
                ListItem item = args[0] as ListItem
                log.error logMessageWithSourceTrace(
                    "List item type '${item.parent.context}' is not defined. Will not transform this node, but will try to carry on",
                    item
                )
            } else {
                StructuralNode node = args[0] as StructuralNode
                log.error logMessageWithSourceTrace(
                    "${name} (node:${node.class.name}) is not defined. Will not transform this node, but will try to carry on.",
                    node
                )
            }
            return null
        } else {
            throw new MissingMethodException(name,this.class,args)
        }
    }

    /**
     * Converts an {@link org.asciidoctor.ast.StructuralNode} using the specified transform along
     * with additional options. If a transform is not specified, implementations
     * typically derive one from the {@link org.asciidoctor.ast.StructuralNode#getNodeName()} property.
     *
     * <p>For a document node, setupDocument is called at the start of processing and closeDocument at
     * the end of processing. IN between all other ndoes are processed. This allow for streaming output to
     * any media.
     *
     * @param node The concrete instance of StructuralNode to convert
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
                node.attributes.put('nbsp','&nbsp;')
                setupComplete = true
                return '# ' + node.doctitle + LINESEP + LINESEP + node.content
            }
        } else {
            return "${methodName(transform,node.nodeName)}"(node,opts)
        }
    }


    def convertSection(ContentNode node, Map<String, Object> opts) {
        Section section = node as Section
        '#'.multiply(section.level+1) + " ${section.title}${LINESEP}${LINESEP}" + section.content
    }

    /** Paragraph conversion just passes the content back.
     *
     * @return Content plus an additional line separator
     */
    def convertParagraph(ContentNode node, Map<String, Object> opts) {
        Block block = node as Block
        block.content + LINESEP
    }

    def convertUlist(ContentNode node,Map<String, Object> opts) {
        List listNode = node as List
        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    def convertOlist(ContentNode node,Map<String, Object> opts) {
        List listNode = node as List
        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    def convertColist(ContentNode node,Map<String, Object> opts) {
        List listNode = node as List
        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    def convertInlineQuoted(ContentNode node, Map<String, Object> opts) {
        PhraseNode inline = node as PhraseNode
        InlineQuotedTextFormatter."${inline.type}"(inline.text)
    }

    def convertThematicBreak(ContentNode node,Map<String, Object> opts) {
        Block block = node as Block
        '- - -' + LINESEP
    }

    /** Create an ordered list item.
     *
     * @param item AST from tree
     * @param opts
     * @return
     */
    def convertListItemTypeOlist(ListItem item, Map<String, Object> opts) {
        ' '.multiply(item.level*2-2) + '1. ' + item.text + LINESEP + item.content
    }

    /** Create an unordered list item.
     *
     * @param item
     * @param opts
     * @return
     */
    def convertListItemTypeUlist(ListItem item, Map<String, Object> opts) {
        return ' '.multiply(item.level*2-2) + '* ' + item.text + LINESEP + item.content
    }

    def convertListItem(StructuralNode node,Map<String, Object> opts) {
        "${itemMethodName('convertListItemType', node.parent.context)}"(node, opts)
    }

//    abstract def convertTable(StructuralNode node,Map<String, Object> opts)

//    /** Redirects an anchor to the appropriate anchor type converter
//     *
//     * @param node
//     * @param opts
//     * @return
//     */
//    def convertInlineAnchor(StructuralNode node, Map<String, Object> opts) {
//        Inline inline = node as Inline
//        "${itemMethodName('convertAnchorType',inline.type)}"(node,opts)
//    }

//    def convertListing(StructuralNode node,Map<String, Object> opts) {
//        Block block = node as Block
//        "${itemMethodName('convertListingType',block.attributes.style)}"(block,opts)
//    }

    /** Creates a message for logging which could include source tracing information
     *
     * @param msg Base message to log
     * @param node Node to interrogate for source information
     * @return The base string, with source information optionally appended.
     *
     * @sa {@link https://github.com/asciidoctor/asciidoctor-leanpub-converter/issues/48}
     *
     */
    String logMessageWithSourceTrace(final String msg,StructuralNode node) {
        String postfix = ''
        if(node.respondsTo('getSourceLocation')) {
            Cursor cursor = (node as StructuralNode).sourceLocation
            if(cursor!=null) {
                postfix = " (${cursor.file}:${cursor.lineNumber})."
            }
        }
        postfix.empty ? msg : "${msg} ${postfix}"
    }

    protected setupComplete = false
}
