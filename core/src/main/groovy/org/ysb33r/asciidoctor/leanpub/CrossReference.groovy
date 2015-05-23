package org.ysb33r.asciidoctor.leanpub

/**
 * @author Schalk W. Cronjé
 */
class CrossReference {
    /** Converts an ASciidoctor cross-reference identifier to a safe Leanpub equivalent.
     *
     * @param id Asciidoctor refid
     * @return Leanpub refid
     */
    static String safeId(final String id) {
        id.replaceAll(' ','-')
    }
}
