package fr.oworld.stepbystep

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import fr.oworld.stepbystep.databinding.ActivityMainBinding
import fr.oworld.stepbystep.ui.home.section.SectionTaskFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
               val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
            val lastFragment = fragment!!.childFragmentManager.fragments.last()
            if(lastFragment is SectionTaskFragment) {
                navController.popBackStack()
            }

            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                }
                R.id.navigation_task -> {
                    navController.navigate(R.id.navigation_task)
                }
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_dashboard)
                }
                R.id.navigation_content -> {
                    navController.navigate(R.id.navigation_content)
                }
                else -> {
                    navController.navigate(item.itemId)
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    fun showNavBar(){
        nav_view.visibility = View.VISIBLE
    }

    fun hideNavBar(){
        nav_view.visibility = View.GONE
    }
}