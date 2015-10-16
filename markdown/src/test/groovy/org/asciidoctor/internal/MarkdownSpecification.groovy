// ============================================================================
/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// ============================================================================
package org.asciidoctor.internal

import org.asciidoctor.Asciidoctor
import org.asciidoctor.converter.MarkdownConverter
import spock.lang.Specification

/**
 * Created by schalkc on 11/12/14.
 */
class MarkdownSpecification extends Specification {

    Asciidoctor asciidoctor
    static final File outputDir = new File('./build/test/markdown')
    static final File resourceDir = new File('./build/resources/test/test-documents')
    static final File expectedDir = new File('./build/resources/test/expected-documents')
    static final File gemPath = new File('./build/gems')

    void setup() {
        asciidoctor = Asciidoctor.Factory.create(gemPath.absolutePath)
        asciidoctor.javaConverterRegistry().register(MarkdownConverter,'markdown')

        if(outputDir.exists()) {
            outputDir.deleteDir()
        }
        outputDir.mkdirs()

    }

    Map generateOutput(final String documentFileName) {
        String outputName = documentFileName.replaceAll(~/\.adoc$/,'.md')
        def options = [
            to_dir : outputDir.absolutePath,
            mkdirs : true,
            backend : 'markdown',
            sourcemap : true,
            safe : 1
        ]
        asciidoctor.convertFile(new File(resourceDir,documentFileName),options )

        [
            expectedFile : new File(expectedDir,outputName),
            // TODO: FIx this output extension once the asciidoctorj API allows setting output file name
            outputFile : new File(outputDir,documentFileName.replaceAll(~/\.adoc$/,'.md')),
//            outputFile : new File(outputDir,outputName),
        ]
    }
}