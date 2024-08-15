package com.example.fortunes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.fortunes.view.DrawListFragment
import com.example.fortunes.viewmodel.SharedViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {

            setContentView(R.layout.activity_main)

            // Initialize ViewModel
            sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]

            if (savedInstanceState == null) {
                val fragment = DrawListFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }

        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error initializing activity")
        }
    }

    override fun onStart() {
        super.onStart()
        // Set data using ViewModel
        try {
            sharedViewModel.fetchDrawList()
            sharedViewModel.setLoadingVisibilityState(true)
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error starting activity")
        }
    }



}
