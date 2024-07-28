package com.movieradarapp.movieradar

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.movieradarapp.movieradar.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeOnClick(binding.root)
    }

    fun homeOnClick(view: View) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val homefragment = HomeFragment()
        fragmentTransaction.replace(R.id.nav_host_fragment,homefragment).commit()
    }

    fun profileOnClick(view: View) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val profilefragment = ProfileFragment()
        fragmentTransaction.replace(R.id.nav_host_fragment,profilefragment).commit()
    }

    fun searchOnClick(view: View) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val searchfragment = SearchFragment()
        fragmentTransaction.replace(R.id.nav_host_fragment,searchfragment).commit()
    }
}