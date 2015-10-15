package org.asciidoctor.leanpub.internal

import groovy.transform.TupleConstructor
import org.asciidoctor.ast.Table

/**
 * @author Schalk W. Cronjé
 */
@TupleConstructor
class LeanpubCell {
    List<String> content
    long getTextSize() {
        content ? content.size() : 0
    }

    /** Horizontal alignment of a cell. This does not have to be set, which means it will
     * follow the alignment of other cells in the same column.
     *
     */
    Table.HorizontalAlignment halign

//    int width

}
