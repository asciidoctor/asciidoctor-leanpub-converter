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
import org.asciidoctor.converter.LeanpubConverter
import org.asciidoctor.markdown.internal.FileUtils
import spock.lang.Specification


/**
 * Created by schalkc on 11/12/14.
 */
class LeanpubSpecification extends Specification {


    static final File OUTPUT_DIR     = new File('./build/test/leanpub')
    static final File SOURCE_DIR     = new File( OUTPUT_DIR, 'src')
    static final File MANUSCRIPT_DIR = new File(OUTPUT_DIR,'manuscript')
    static final File IMAGES_DIR     = new File(MANUSCRIPT_DIR,'images')
    static final File IMAGES_OUT_DIR = new File(OUTPUT_DIR,'generated-images')
    static final File BOOK_1         = new File(MANUSCRIPT_DIR,LeanpubConverter.BOOK)
    static final File SAMPLE_1       = new File(MANUSCRIPT_DIR,LeanpubConverter.SAMPLE)
    static final File RESOURCE_DIR   = new File('./src/test/resources/test-documents')
    static final File GEM_PATH       = new File('./build/gems')

    Asciidoctor asciidoctor
    String documentName

    void setup() {
        asciidoctor = Asciidoctor.Factory.create(GEM_PATH.absolutePath)
        asciidoctor.javaConverterRegistry().register(LeanpubConverter,'leanpub')

        if(OUTPUT_DIR.exists()) {
            OUTPUT_DIR.deleteDir()
        }
        OUTPUT_DIR.mkdirs()

    }

    void generateOutput(final String documentFileName,boolean safeMode=true) {
        File targetFile = new File(SOURCE_DIR,documentFileName)
        FileUtils.copyFile(new File(RESOURCE_DIR,documentFileName),targetFile)
        def options = [
            to_dir : OUTPUT_DIR.absolutePath,
            mkdirs : true,
            backend : 'leanpub',
            sourcemap : true,
            safe : safeMode ? 1 : 0,
            attributes : ['imagesoutdir': IMAGES_OUT_DIR.absolutePath, 'a-test-value': '1.2.3.4' ]
        ]
        asciidoctor.convertFile(targetFile,options )

    }

    File chapterFromDocument(final String docName,int numero) {
        File chapter = new File(LeanpubSpecification.MANUSCRIPT_DIR,"chapter_${numero}.txt")
        generateOutput("${docName}.adoc")
        chapter
    }

    File chapterFromDocument(int numero) {
        chapterFromDocument(documentName,numero)
    }
}