package com.example.huddle

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.huddle.viewModel.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var progress: ProgressBar
    lateinit var bottomNav: BottomNavigationView
    val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        progress = findViewById<ProgressBar>(R.id.main_progressBar)

        lifecycleScope.launch {
            viewModel.userId.collect { uid ->
                if (uid != null) {
                    progress.visibility = View.GONE

                    if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) == null) {
                        replaceFragment(FragmentTask())
                    }
                } else {
                    progress.visibility = View.VISIBLE
                }
            }
        }

        bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_task -> {
                    replaceFragment(FragmentTask())
                    true
                }

                R.id.navigation_add -> {
                    val dialog = FragmentNewTask()
                    dialog.show(supportFragmentManager, "MyFloatingLayout")
                    false
                }

                R.id.navigation_history -> {
                    replaceFragment(FragmentHistory())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager

        val currentFragment = fragmentManager.findFragmentById(R.id.fragmentContainerView)
        if (currentFragment?.javaClass == fragment.javaClass) {
            return
        }

        fragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fast_fade_in, R.anim.fast_fade_out) // Makes it look professional
            .replace(R.id.fragmentContainerView, fragment)
            .commit()
    }
}