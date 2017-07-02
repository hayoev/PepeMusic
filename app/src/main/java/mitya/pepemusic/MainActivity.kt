package mitya.pepemusic

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
        loadDirectoryList()
        replaceCurrentFragment(DirectoriesFragment())
    }

    fun replaceCurrentFragment(fragment: Fragment) {
        val bundle = Bundle()
        bundle.putStringArrayList("directoryList", ArrayList(directoryList.toList()))
        fragment.arguments = bundle
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
    }

    fun loadDirectoryList() {
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
}
