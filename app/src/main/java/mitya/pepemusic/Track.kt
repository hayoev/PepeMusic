package mitya.pepemusic

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Mitya on 28.06.2017.
 */
@Parcelize
data class Track(val title: String, val artist: String,
                 val contentUri: Uri, val thumbnail: Uri = Uri.EMPTY) : Parcelable