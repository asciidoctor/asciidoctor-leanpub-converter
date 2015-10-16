package org.asciidoctor.markdown

import org.asciidoctor.converter.ConverterFor
import org.asciidoctor.converter.markdown.core.AbstractMarkdownConverter

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
