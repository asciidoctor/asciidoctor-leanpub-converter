package org.asciidoctor.leanpub.internal

import groovy.transform.CompileStatic
import org.asciidoctor.converter.LeanpubConverter
import org.asciidoctor.leanpub.LeanpubDocument

/**
 * @since 1.6.0
 */
@CompileStatic
class Footnote {
    /** Prefix that is applied to all footnotes.
     *
     */
    static final String PREFIX = 'foot'

    /** For multi-line footnotes, use the following indent.
     *
     */
    static final String FOOTNOTE_INDENT = "${LeanpubConverter.LINESEP}${' '.multiply(4)}"

    final TreeMap<Integer,String> footnotes = [:] as TreeMap
    final TreeMap<String,Integer> footnoteReferences = [:] as TreeMap

    /** Returns a Leanpub-Markdown tag for a footnote of a specific index.
     *
     * @param idx
     * @return
     */
    String tag(Integer idx) {
        "[^${PREFIX}${idx}]"
    }

    /** A correctly formatted footnote.
     *
     * @param idx Global index of footnote
     * @return A string with the correct prefix and the footnote text
     */
    String getMarkedUpFootnoteAt(Integer idx) {
        String content = footnotes[idx]
        "${tag(idx)}: ${content.readLines().join(FOOTNOTE_INDENT)}"
    }

    /** Adds a new footnote
     *
     * @param content Markdown content
     * @return Global index for the footnote
     */
    Integer addNewFootnote(final String content) {
        Integer newIndex= footnotes.size()+1
        footnotes[newIndex] = content
        newIndex
    }

    /** Index for a specific reference.
     *
     * @param reference
     * @return Global index, or {@code -1} if not found.
     */
    Integer getIndexForReference(final String reference) {
        footnoteReferences.getOrDefault(reference,-1)
    }

    /** Adds a new footnote reference.
     *
     * @param reference The Asciidoc reference
     * @param content Markdown content
     * @return Global index for the footnote
     */
    Integer addNewFootnoteWithReference(final String reference,final String content) {
        Integer newIndex = addNewFootnote(content)
        footnoteReferences[reference] = newIndex
        newIndex
    }
}
