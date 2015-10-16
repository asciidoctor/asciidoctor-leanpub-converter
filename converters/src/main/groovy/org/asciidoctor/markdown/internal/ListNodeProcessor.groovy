package org.asciidoctor.markdown.internal

import org.asciidoctor.ast.List
import org.asciidoctor.ast.ListItem
import static org.asciidoctor.markdown.internal.Constants.LINESEP

/** Generic routines for dealing with Lists
 *
 * @author Schalk W. Cronjé
 */
class ListNodeProcessor {
    /** Helper function to process any ulist, colist, or olist. Also checks for end of
     * complete list and inserts additional new line.
     *
     * @param listNode To process
     * @return
     */
    static def processListItems(List listNode) {
        listNode.items.collect { ListItem item -> item.convert() }.join('')  +
            (listNode.parent.nodeName.endsWith('list') ? '' : LINESEP)
    }

}
