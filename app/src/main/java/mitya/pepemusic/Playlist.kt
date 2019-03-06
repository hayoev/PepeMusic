package mitya.pepemusic

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by mitya on 13.02.2018.
 */
@Parcelize
class Playlist(val list: ArrayList<Track>) : Parcelable {
    constructor(index: Int, playlist: ArrayList<Track>) : this(playlist) {
        Collections.rotate(playlist, -index)
    }
}