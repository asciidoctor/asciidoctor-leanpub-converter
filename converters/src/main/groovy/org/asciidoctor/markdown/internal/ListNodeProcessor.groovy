package org.asciidoctor.markdown.internal

import groovy.transform.CompileStatic
import org.asciidoctor.ast.List
import org.asciidoctor.ast.ListItem
import org.asciidoctor.ast.StructuralNode

import static org.asciidoctor.markdown.internal.Constants.LINESEP

/** Generic routines for dealing with Lists
 *
 * @author Schalk W. Cronjé
 */
@CompileStatic
class ListNodeProcessor {
    /** Helper function to process any ulist, colist, or olist. Also checks for end of
     * complete list and inserts additional new line.
     *
     * @param listNode To process
     * @return
     */
    static def processListItems(List listNode) {
        listNode.items.collect { StructuralNode item -> item.convert() }.join('')
    }

}
