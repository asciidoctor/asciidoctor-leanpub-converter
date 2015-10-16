package org.asciidoctor.leanpub.internal

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

/** A row for a Leanpub table.
 *
 * @author Schalk W. Cronjé
 */
@CompileStatic
class LeanpubTableRow {
    boolean horizontalLine = true
    final LeanpubCell[] cells

    LeanpubTableRow( int numColumns ) {
        cells = new LeanpubCell[numColumns]
        for( int i = 0 ; i < numColumns ; ++i ) {
            cells[i] = new LeanpubCell()
        }
    }

    /** Return the maximum number of text rows for a single table row
     *
     * @return
     */
    int getTextRows() {
        cells.collect { LeanpubCell c ->
            c.content?.size() ?: 0
        }.max()
    }
}
