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
package org.asciidoctor.leanpub.internal

import org.asciidoctor.Asciidoctor
import org.asciidoctor.leanpub.LeanpubConverter
import spock.lang.Specification


/**
 * Created by schalkc on 11/12/14.
 */
class LeanpubSpecification extends Specification {

    Asciidoctor asciidoctor
    static final File outputDir = new File('./build/test/leanpub')
    static final File manuscriptDir = new File(outputDir,'manuscript')
    static final File imagesDir = new File(manuscriptDir,'images')
    static final File book1 = new File(manuscriptDir,LeanpubConverter.BOOK)
    static final File sample1 = new File(manuscriptDir,LeanpubConverter.SAMPLE)
    static final File resourceDir = new File('./build/resources/test/test-documents')
    static final File gemPath = new File('./build/gems')

    void setup() {
        asciidoctor = Asciidoctor.Factory.create(gemPath.absolutePath)
        asciidoctor.javaConverterRegistry().register(LeanpubConverter,'leanpub')

        if(outputDir.exists()) {
            outputDir.deleteDir()
        }
        outputDir.mkdirs()

    }

    void generateOutput(final String documentFileName) {
        def options = [
            to_dir : outputDir.absolutePath,
            mkdirs : true,
            backend : 'leanpub',
            safe : 1,
            sourcemap : 'true'
        ]
        asciidoctor.convertFile(new File(resourceDir,documentFileName),options )

    }
}