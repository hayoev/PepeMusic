package mitya.pepemusic

import android.util.Log

/**
 * Created by Mitya on 02.07.2017.
 */
fun getParent(resourcePath: String) = resourcePath.substring(0, resourcePath.lastIndexOf('/'))

fun log(message: String) = Log.e("TEST", message)