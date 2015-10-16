package org.asciidoctor.leanpub.internal

import groovy.transform.CompileStatic
import org.asciidoctor.ast.Table
import org.asciidoctor.converter.LeanpubConverter

/**
 * @author Schalk W. Cronjé
 */
@CompileStatic
class LeanpubTable {

    static enum HorizontalAlignment  {
        LEFT,
        RIGHT,
        CENTER,
        VARIOUS
    }

    final LeanpubTableRow[] rows
    final LeanpubTableRow[]  header
    final LeanpubTableRow[]  footer
    final List<Long> columnWidths
    final HorizontalAlignment[] columnAlignment

    static HorizontalAlignment convertAlignment(Table.HorizontalAlignment ha) {
        switch(ha) {
            case Table.HorizontalAlignment.RIGHT: HorizontalAlignment.RIGHT; break;
            case Table.HorizontalAlignment.CENTER: HorizontalAlignment.CENTER; break;
            default: HorizontalAlignment.LEFT;
        }
    }

    LeanpubTable( int numRows, int numColumns, int headerRows, int footerRows ) {
        columnWidths = new Long[numColumns] as List
        columnAlignment = new HorizontalAlignment[numColumns]
        rows = createTableRows(numRows,numColumns)
        if(headerRows>0) {
            header = createTableRows(headerRows,numColumns)
        }
        if(footerRows>0) {
            footer = createTableRows(footerRows,numColumns)
        }
    }

    void postProcessTables() {
        calculateColumnWidths()
        analyseColumnAlignment()
        checkForMultilineCells()
    }

    boolean getHeterogeneousColumnAlignment() {
        heterogeneousColumnAlignment
    }

    boolean getIsDefaultAlignment() {
        !heterogeneousColumnAlignment && columnAlignment.every { it == null || it == HorizontalAlignment.LEFT }
    }

    /** Returns {@code true} if any normal cell has a multiple of lines.
     */
    boolean getHasMultilineCells() {
        hasMultilineCells
    }

    List<LeanpubTableRow> getAllRows() {
        List<LeanpubTableRow> tmp = rows as List
        if(header) { tmp+= header as List }
        if(footer) { tmp+= footer as List }
        tmp
    }

    private void checkForMultilineCells() {
        hasMultilineCells = rows.find { LeanpubTableRow ltr ->
            ltr.cells.any { LeanpubCell cell ->
                cell.content.size() > 1 || cell.content[0].find(LeanpubConverter.LINESEP)
            }
        }
    }

    private void calculateColumnWidths() {
        allRows.each { LeanpubTableRow ltr ->
            ltr.cells.eachWithIndex { LeanpubCell c, int index ->
                long sz = c.textSize ?:0
                if(sz > columnWidths[index]) {
                    columnWidths[index] = sz
                }
            }
        }
    }

    private void analyseColumnAlignment() {
        (0..columnWidths.size()-1).each { int colIndex ->
            allRows.each { LeanpubTableRow ltr ->
                if(columnAlignment[colIndex] == null && ltr.cells[colIndex].halign != null) {
                    columnAlignment[colIndex] = convertAlignment(ltr.cells[colIndex].halign)
                } else if(columnAlignment[colIndex] != HorizontalAlignment.VARIOUS && ltr.cells[colIndex].halign != null) {
                    HorizontalAlignment ha = convertAlignment(ltr.cells[colIndex].halign)
                    if(ha != columnAlignment[colIndex]) {
                        columnAlignment[colIndex]= HorizontalAlignment.VARIOUS
                        heterogeneousColumnAlignment = true
                    }
                }
            }
        }
    }

    private static LeanpubTableRow[] createTableRows(int numRows,int numColumns) {
        LeanpubTableRow[] newRows = new LeanpubTableRow[numRows]
        for( int i=0 ; i < numRows ; ++ i) {
            newRows[i] = new LeanpubTableRow( numColumns )
        }
        newRows
    }


    private boolean heterogeneousColumnAlignment = false
    private boolean hasMultilineCells = false
}
