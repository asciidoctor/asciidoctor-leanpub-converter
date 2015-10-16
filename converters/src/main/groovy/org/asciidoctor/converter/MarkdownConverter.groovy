package org.asciidoctor.converter

import org.asciidoctor.converter.ConverterFor
import org.asciidoctor.converter.markdown.AbstractMarkdownConverter

/** A converter for going from Asciidoctor to classic Markdown (as defined by {@link http://daringfireball.net/projects/markdown/syntax}.
 *
 * @author Schalk W. Cronjé
 */
//@ConverterFor(suffix=".md")
@ConverterFor(format="markdown",suffix=".md")
class MarkdownConverter extends AbstractMarkdownConverter {
    MarkdownConverter(final String backend,Map<String, Object> opts) {
        super(backend, opts)
    }
}
