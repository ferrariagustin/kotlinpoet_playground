package com.aferrari.processor

import com.aferrari.processor.util.ProcessorUtils
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * Extend to AbstractProcessor to use partial implementation of annotation processor
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
class Processor : AbstractProcessor() {

    private val deepLinksMap: HashMap<String, Element> = HashMap()

    /**
     * Indicate with which annotation we are going to work
     */
    override fun getSupportedAnnotationTypes() = setOf(ProcessorUtils.ANNOTATION_DEEPLINK.canonicalName)

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(ProcessorUtils.ANNOTATION_DEEPLINK)
        loadAnnotationElements(annotatedElements)

        val fileSpec = generateAnnotationFileSpec()
        createBuildFile(fileSpec)
        return true
    }

    private fun createBuildFile(fileSpec: FileSpec) {
        val path = processingEnv.options["kapt.kotlin.generated"]
        fileSpec.writeTo(File(path, ProcessorUtils.FILE_NAME))
    }

    /**
     * Load all elements that has AnnotationDeeplink like annotation
     */
    private fun loadAnnotationElements(annotatedElements: Set<Element>) {
        for (element in annotatedElements) {
            if (ProcessorUtils.isClass(element)) {
                val deeplink = element.getAnnotation(ProcessorUtils.ANNOTATION_DEEPLINK).deeplink
                deepLinksMap[deeplink] = element
            }
        }

    }

    /**
     * return a FileSpec object that is kotlin file containing top level objects like
     * classes, objects, functions, properties and type
     */
    private fun generateAnnotationFileSpec(): FileSpec {
        return FileSpec.builder("", ProcessorUtils.CLASS_NAME)
            .addType(createClass())
            .build()
    }

    /**
     * return TypeSpec with a class, interface, or enum declaration.
     */
    private fun createClass(): TypeSpec {
        return TypeSpec
            .objectBuilder(ProcessorUtils.CLASS_NAME) // to simplify the example
            .addProperty(createHashMapProperty())
            .build()
    }

    private fun getDeepLinkInitializationCode(): CodeBlock {
        return CodeBlock.builder()
            .add("hashMapOf(")
            .add(ProcessorUtils.LINE_BREAK)
            .add(processDeeplinksMap())
            .add(ProcessorUtils.LINE_BREAK)
            .add(")")
            .build()
    }

    private fun processDeeplinksMap(): String {
        var codeBlocks = ""
        deepLinksMap.forEach {
            codeBlocks += """"${it.key}"·to·${ProcessorUtils.getPathFragment(it.value)},"""
            codeBlocks += ProcessorUtils.LINE_BREAK
        }

        return codeBlocks.removeSuffix(",\n")
    }


    /**
     * return property declaration like attributes
     */
    private fun createHashMapProperty(): PropertySpec {
        return PropertySpec.builder(
            ProcessorUtils.PROPERTY_HASHMAP_NAME,
            getHashMapType()
        )
            .initializer(
                getDeepLinkInitializationCode()
            )
            .build()
    }

    private fun getHashMapType(): ParameterizedTypeName {
        return HashMap::class.asClassName().parameterizedBy(
            String::class.asClassName(),
            parametrizedFragmentTypeName()
        )
    }

    /**
     * return parametrized type for fragment "Class<out Fragment>" of androidx.fragment.app
     */
    private fun parametrizedFragmentTypeName() = Class::class.asClassName().parameterizedBy(
        WildcardTypeName.producerOf(
            ClassName("androidx.fragment.app", "Fragment")
        )
    )
}
