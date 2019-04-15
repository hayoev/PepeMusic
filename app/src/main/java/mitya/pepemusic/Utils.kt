package mitya.pepemusic

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

/**
 * Created by Mitya on 02.07.2017.
 */
fun getParent(resourcePath: String) = resourcePath.substring(0, resourcePath.lastIndexOf('/'))

fun log(message: String) = Log.e("TEST", message)

fun toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(App.instance.applicationContext, message, duration).show()

fun getSavedData(context: Context, key: String): String =
        PreferenceManager.getDefaultSharedPreferences(context).getString(key, "")


fun saveData(context: Context, key: String, value: String) =
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(key, value).apply()
