package org.asciidoctor.markdown

import org.asciidoctor.converters.markdown.core.AbstractMarkdownConverter

/** A converter for going from Asciidoctor to classic Markdown (as defined by {@link http://daringfireball.net/projects/markdown/syntax}.
 *
 * @author Schalk W. Cronjé
 */
//@ConverterFor(suffix=".md")
class MarkdownConverter extends AbstractMarkdownConverter {
    MarkdownConverter(final String backend,Map<String, Object> opts) {
        super(backend, opts)
    }
}
