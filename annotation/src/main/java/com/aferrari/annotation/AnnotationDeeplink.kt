package com.aferrari.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AnnotationDeeplink(val deeplink: String)