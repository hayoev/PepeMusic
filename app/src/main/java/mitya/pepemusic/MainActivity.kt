package mitya.pepemusic

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigationView()
        setupToolbar()
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
        } else {
            if (intent.getStringExtra("directory") != null) {
                val bundle = Bundle()
                bundle.putString("currentDirectory", intent.getStringExtra("directory"))
                replaceCurrentFragment(LocalTracksFragment(), bundle)
            } else {
                replaceCurrentFragment(DirectoriesFragment())
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.menuLocal -> replaceCurrentFragment(DirectoriesFragment())
                R.id.menuVk -> {
                    val bundle = Bundle()
                    bundle.putString("currentDirectory"
                            , Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/" + getString(R.string.app_name))
                    replaceCurrentFragment(LocalTracksFragment(), bundle)
                }
            }
            true
        }
    }

    private fun replaceCurrentFragment(fragment: Fragment, bundle: Bundle = Bundle()
                                       , addToBackStack: Boolean = false) {
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .apply { if (addToBackStack) addToBackStack(null) }
                .commit()
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            val bundle = Bundle()
            bundle.putString("query", intent.getStringExtra(SearchManager.QUERY))
            replaceCurrentFragment(VkTracksFragment(), bundle, true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            EXTERNAL_STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    replaceCurrentFragment(DirectoriesFragment())
                }
                return
            }
            else -> {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
