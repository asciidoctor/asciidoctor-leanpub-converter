package org.asciidoctor.markdown.internal

import groovy.transform.CompileStatic

import java.nio.file.Files
import java.nio.file.StandardCopyOption

@CompileStatic
class FileUtils {

    /** Copy a file from one location to another.
     *
     * <p> Creates intermediate paths if required.
     *
     * @param srcFile Source file
     * @param destFile Destination file
     * @return destination file
     */
    static File copyFile(final File srcFile,final File destFile) {
        destFile.parentFile.mkdirs()
        Files.copy(srcFile.toPath(),destFile.toPath(),StandardCopyOption.REPLACE_EXISTING)
        destFile
    }
}
