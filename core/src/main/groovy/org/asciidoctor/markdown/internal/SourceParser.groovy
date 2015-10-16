package org.asciidoctor.markdown.internal

import org.asciidoctor.ast.Block

import java.util.regex.Pattern

/**
 * @author Schalk W. Cronjé
 */
class SourceParser {


    /** Callout pattern to match callouts in literal blocks and source listings
     *
     * Modified from Asciidoctor Ruby code base (lib/asciidoctor.rb)
     *
     * Matches a callout reference inside literal text.
     *
     * Examples
     *   <1> (optionally prefixed by //, //, -- or ;; line comment chars)
     *   <1> <2> (multiple callouts on one line)
     *   <!--1--> (for XML-based languages)
     *
     */
    public static final Pattern CALLOUT_PATTERN = ~/(.+?)(?:\s\/\/|\s#|\s--|\s;;)?((\s<\d+>)+|(\s<!--\d+-->)+)\s*$/

    /** Parses a line from a source code or literal block, looking for callouts.
     *
     * @param line Line to parse
     * @return Returns an object containing @{code line}, which is the parsed out liine and {@code callouts}, which is a
     * an array of callouts found on the line. If not macth was found, then {@code line} will be the original line and
     * {@code callouts} will be an empty array
     */
    static def parseSourceLineForCallouts(String line) {
        def matcher = line =~ CALLOUT_PATTERN
        if(matcher.matches()) {
            [  line : matcher[0][1],
                callouts : matcher[0][2].toString().findAll( matcher[0][3]!=null ? ~/<\d+>/  : ~/<!--\d+-->/).collect {it.replaceAll(~/[<>\-\!]+/,'')}
            ]

        } else {
            [ line : line, callouts : [] ]
        }
    }

    /** Parses a source block for callouts.
     *
     * @param block Source code block to process
     *
     * @return Returns a list where each element has three properties:
     * {@code lineno}, {@code line}, {@code callout}. The latter is an array with the callout numbers. it can be empty
     * indicating that there is no callout on the that line.
     */
    static def parseSourceForCallouts(Block block) {
        def parsedContent = []
        block.lines.eachWithIndex { line,index ->
            def parsedLine = parseSourceLineForCallouts(line)
            parsedContent.add ([ lineno : index+1, line : parsedLine.line, callouts : (parsedLine.callouts.empty ? null : parsedLine.callouts) ])
        }
        parsedContent
    }
}
