package com.aferrari.kotlinpoetexample

import Deeplink
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aferrari.kotlinpoetexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start deepLink for Test Fragment
        binding.testFragment.setOnClickListener {
            val fragmentIntent = Intent(Intent.ACTION_VIEW, Uri.parse("test://test_fragment"))
            startActivity(fragmentIntent)
        }

        // Start deepLink for Test Fragment Java
        binding.testFragmentJava.setOnClickListener {
            val fragmentIntent = Intent(Intent.ACTION_VIEW, Uri.parse("test://test_fragment_java"))
            startActivity(fragmentIntent)
        }

    }

    /**
     * MainActivity set launchMode="singleTask"
     */
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (canHandleDeepLink(intent?.data)) {
            startDeepLinkFragment(intent?.data)
        }
    }

    private fun canHandleDeepLink(data: Uri?): Boolean = Deeplink.annotationDeeplinks.containsKey(data.toString())

    /**
     * Create fragment with reflexion and commit fragment
     */
    private fun startDeepLinkFragment(data: Uri?) {
        val fragment = Deeplink.annotationDeeplinks[data.toString()]?.newInstance()
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragment::class.simpleName)
                .commit()
        }
    }
}