package com.aes.myhome

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.aes.myhome.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object Intents {
        const val DISPLAY_FRAGMENT = "com.aes.myhome.intent_action.display_fragment"
    }

    private lateinit var _appBarConfiguration: AppBarConfiguration
    private lateinit var _navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        _navController = findNavController(R.id.nav_host_fragment_content_main)

        _appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_food,
                    R.id.nav_food_shopping,
                    R.id.nav_food_menu,
                    R.id.nav_food_recipes,
                    R.id.nav_food_diet,
                R.id.nav_tasks,
                    R.id.nav_tasks_schedule,
                    R.id.nav_tasks_cases,
                    R.id.nav_tasks_works,
                R.id.nav_other,
                    R.id.nav_other_budget,
                    R.id.nav_other_closet,
                    R.id.nav_other_fun
            ),
            drawerLayout
        )

        setupActionBarWithNavController(_navController, _appBarConfiguration)
        navView.setupWithNavController(_navController)

        setupAppTheme()

        when (intent.action) {
            DISPLAY_FRAGMENT -> {
                _navController.navigate(intent.data!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main, menu)

        menu.findItem(R.id.action_settings)
            .setIconToTitle(AppCompatResources.getDrawable(this, R.drawable.settings)!!)

        menu.findItem(R.id.action_help)
            .setIconToTitle(AppCompatResources.getDrawable(this, R.drawable.help)!!)

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        _navController = findNavController(R.id.nav_host_fragment_content_main)
        return _navController.navigateUp(_appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                _navController.navigate(R.id.action_open_settings)
                true
            }
            R.id.action_help -> {
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/ab0_obus69")))
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun setupAppTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this).all

        preferences["sync_theme"]?.let { sync ->
            if (sync == true) {
                preferences["attachment_theme"]?.let { value ->
                    AppCompatDelegate.setDefaultNightMode(
                        if (value == true) AppCompatDelegate.MODE_NIGHT_YES
                        else AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
            }
        }
    }

    private fun MenuItem.setIconToTitle(icon: Drawable): MenuItem {
        icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
        val str = SpannableString("   $title")
        val img = ImageSpan(icon, ImageSpan.ALIGN_BOTTOM)
        str.setSpan(img, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        this.setTitle(str)

        return this
    }

}