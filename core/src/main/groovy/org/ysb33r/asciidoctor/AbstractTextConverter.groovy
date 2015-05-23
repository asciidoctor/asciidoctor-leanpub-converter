package org.ysb33r.asciidoctor

import groovy.util.logging.Slf4j
import org.asciidoctor.ast.AbstractNode
import org.asciidoctor.ast.Block
import org.asciidoctor.ast.DocumentRuby
import org.asciidoctor.ast.Inline
import org.asciidoctor.ast.ListItem
import org.asciidoctor.ast.ListNode
import org.asciidoctor.converter.AbstractConverter
import org.ysb33r.asciidoctor.leanpub.CrossReference

/**
 * @author Schalk W. Cronjé
 */
@Slf4j
abstract class AbstractTextConverter extends AbstractConverter {

    AbstractTextConverter(final String backend,Map<Object, Object> opts) {
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

    def methodMissing(String name, args) {

        if(name.startsWith('convert') && args.size() == 2 && args[0] instanceof AbstractNode) {
            if(name.startsWith('convertAnchorType')) {
                Inline inline = args[0] as Inline
                log.error "Anchor type '${inline.type}' is not defined. Will not transform this node, but will try to carry on."
            } else if(name.startsWith('convertListingTyoe')) {
                Block inline = args[0] as Block
                log.error "Listing type '${block.attributes.style}' is not defined. Will not transform this node, but will try to carry on."
            } else if(name.startsWith('convertListItemType')) {
                ListItem item = args[0] as ListItem
                log.error "List item type '${item.parent.context}' is not defined. Will not transform this node, but will try to carry on"
            } else {
                AbstractNode node = args[0] as AbstractNode
                log.error "${name} (node:${node.class.name}) is not defined. Will not transform this node, but will try to carry on."
            }
            return null
        } else {
            throw new MissingMethodException(name,this.class,args)
        }
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
            setupDocument(node,opts)
            return closeDocument(node.content)
        } else {
            return "${methodName(transform,node.nodeName)}"(node,opts)
        }
    }

    abstract void setupDocument(AbstractNode node,Map<Object,Object> opts)
    abstract def closeDocument(def content)

    abstract def convertSection(AbstractNode node, Map<String, Object> opts)
    abstract def convertInlineQuoted(AbstractNode node, Map<String, Object> opts)
    abstract def convertThematicBreak(AbstractNode node,Map<String, Object> opts)
    abstract def convertAdmonition(AbstractNode node,Map<String, Object> opts)


    /** Paragraph conversion just passes the content back.
     *
     * @return
     */
    def convertParagraph(AbstractNode node, Map<String, Object> opts) {
        Block block = node as Block
        block.content + LINESEP
    }

    def convertUlist(AbstractNode node,Map<String, Object> opts) {
        ListNode listNode = node as ListNode
        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    def convertOlist(AbstractNode node,Map<String, Object> opts) {
        ListNode listNode = node as ListNode
        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    def convertColist(AbstractNode node,Map<String, Object> opts) {
        ListNode listNode = node as ListNode
        listNode.items.collect { ListItem item -> item.convert() }.join('')
    }

    def convertListItem(AbstractNode node,Map<String, Object> opts) {
        ListItem item = node as ListItem
        "${itemMethodName('convertListItemType',node.parent.context)}"(node,opts)
    }

    abstract def convertListItemTypeColist(ListItem node, Map<String, Object> opts)
    abstract def convertListItemTypeOlist(ListItem node, Map<String, Object> opts)
    abstract def convertListItemTypeUlist(ListItem node, Map<String, Object> opts)

    /** Redirects an anchor to the appropriate anchor type converter
     *
     * @param node
     * @param opts
     * @return
     */
    def convertInlineAnchor(AbstractNode node, Map<String, Object> opts) {
        Inline inline = node as Inline
        "${itemMethodName('convertAnchorType',inline.type)}"(node,opts)
    }

    abstract def convertAnchorTypeXref(AbstractNode node, Map<String, Object> opts)
    abstract def convertAnchorTypeLink(AbstractNode node, Map<String, Object> opts)

    def convertListing(AbstractNode node,Map<String, Object> opts) {
        Block block = node as Block
        "${itemMethodName('convertListingType',block.attributes.style)}"(block,opts)
    }

    abstract def convertListingTypeSource(Block block,Map<String, Object> opts)

}
