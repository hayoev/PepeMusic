package mitya.pepemusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val directoryList = hashSetOf<String>()

    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
        } else {
            loadDirectoryList()
            val bundle = Bundle()
            if (intent.getStringExtra("directory") != null) {
                bundle.putString("currentDirectory", intent.getStringExtra("directory"))
                replaceCurrentFragment(TracksFragment(), bundle)
            } else {
                bundle.putStringArrayList("directoryList", ArrayList(directoryList.toList()))
                replaceCurrentFragment(DirectoriesFragment(), bundle)
            }
        }
    }

    private fun replaceCurrentFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
    }

    private fun loadDirectoryList() {
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor.run {
            if (moveToFirst()) {
                val path = getColumnIndex(MediaStore.Audio.Media.DATA)
                val isMusic = getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)
                do {
                    val thisIsMusic = getInt(isMusic)
                    if (thisIsMusic != 0) {
                        val thisPath = getString(path)
                        directoryList.add(getParent(thisPath))
                    }
                } while (moveToNext())
            }
        }
        cursor.close()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            EXTERNAL_STORAGE_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadDirectoryList()
                    val bundle = Bundle()
                    bundle.putStringArrayList("directoryList", ArrayList(directoryList.toList()))
                    replaceCurrentFragment(DirectoriesFragment(), bundle)
                }
                return
            }
            else -> {

            }
        }
    }
}
