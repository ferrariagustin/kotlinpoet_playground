package com.aferrari.kotlinpoetexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aferrari.annotation.AnnotationDeeplink

@AnnotationDeeplink("test://test_fragment2")
class TestFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.test_fragment,
            container,
            false
        )
    }
}