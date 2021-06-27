package com.aferrari.processor.util

import com.aferrari.annotation.AnnotationDeeplink
import javax.lang.model.element.Element

object ProcessorUtils {
    val ANNOTATION_DEEPLINK = AnnotationDeeplink::class.java
    const val FILE_NAME = "GeneratedDeeplink"
    const val CLASS_NAME = "Deeplink"
    const val PROPERTY_HASHMAP_NAME = "annotationDeeplinks"
    const val LINE_BREAK = "\n"

    /**
     * Validate if element is a class
     */
    fun isClass(element: Element): Boolean = element.kind.isClass

    /**
     * Return complete path of fragment like java class
     */
    fun getPathFragment(element: Element): String = "$element::class.java"
}