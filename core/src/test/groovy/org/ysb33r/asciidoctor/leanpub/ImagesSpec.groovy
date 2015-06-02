package org.ysb33r.asciidoctor.leanpub

import org.ysb33r.asciidoctor.leanpub.internal.LeanpubSpecification
import spock.lang.Ignore
import spock.lang.Issue

/**
 * Created by schalkc on 15/12/14.
 */
class ImagesSpec extends LeanpubSpecification {

    @Issue('https://leanpub.com/help/manual#leanpub-auto-how-to-insert-an-image, https://github.com/ysb33r/asciidoctor-leanpub-converter/issues/15')
    def "Processing images"() {
        setup:
        File chapter1 = new File(manuscriptDir,'chapter_1.txt')

        when:
        generateOutput('images.adoc')

        then:
        chapter1.text == '''# Images

## Block image

![Image One](images/Image1.png "Image1")

## Inline Image

This is an inline ![](images/Image2.png "This is image 2 alternative text") image.

This is an inline ![](images/Image1.png "Image1") image with no text.

## Block image without title

![](images/Image1.png "Image One as AltText")

## Block image with float

{float="right"}
![Image Two Right](images/Image2.png "Image2")

## Block image with role

{float="left"}
![Image One Left](images/Image1.png "Image1")

## Block image with center

C> ![Image One Center](images/Image1.png "Image1")

## Block image with everything

{float="right",width=60%}
![Image One Everything](images/Image1.png "This is my alternative text")
'''
        and:
        new File(imagesDir,'Image1.png').exists()
        new File(imagesDir,'Image2.png').exists()

    }

}

/*


=== Block image with center

.Image One Center
image::Image1.png[align="center"]
 */

/*
  |        # Images
    |
    |        ## Block image
    |
    |        ![Image One](images/Image1.png "Image1")
    |
    |        ## Inline Image
    |
    |        This is an inline ![](images/Image2.png "This is image 2 alternative text") image.
    |
    |        This is an inline ![](images/Image1.png "Image1") image with no text.
    |
    |        ## Block image without title
    |
    |        ![](images/Image1.png "Image One as AltText")
    |
    |        ## Block image with float
    |
    |        {float="right"}
    |        ![Image Two Right](images/Image2.png "Image2")
    |
    |        ## Block image with role
    |
    |        {float="left"}
    |        ![Image One Left](images/Image1.png "Image1")
    |
    |        ## Block image with center
    |
    |        C> ![Image One Center](images/Image1.png "Image1")
    |
    |        ## Block image with everything
    |
    |        {float="right",width=60%}
    |        ![Image One Everything](images/Image1.png "Image1")
 */