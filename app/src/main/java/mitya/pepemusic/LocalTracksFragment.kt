package mitya.pepemusic

import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.exoplayer2.util.Util


class LocalTracksFragment : TracksFragment() {

    private lateinit var currentDirectory: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        currentDirectory = arguments!!.getString("currentDirectory")
        super.onActivityCreated(savedInstanceState)
    }

    override fun loadTrackList(): ArrayList<Track> {
        val tracks = arrayListOf<Track>()
        val cursor = requireActivity().contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null)
        cursor.run {
            if (moveToFirst()) {
                val titleColumn = getColumnIndex(MediaStore.Audio.Media.TITLE)
                val idColumn = getColumnIndex(MediaStore.Audio.Media._ID)
                val artistColumn = getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val path = getColumnIndex(MediaStore.Audio.Media.DATA)
                val isMusic = getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)
                do {
                    val thisIsMusic = getInt(isMusic)
                    val thisPath = getString(path)
                    if (thisIsMusic != 0 && getParent(thisPath) == currentDirectory) {
                        val thisId = getLong(idColumn)
                        val thisTitle = getString(titleColumn)
                        val thisArtist = getString(artistColumn)
                        tracks.add(Track(thisTitle, thisArtist,
                                ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId)
                        ))
                    }
                } while (moveToNext())
            }
        }
        cursor.close()
        return tracks
    }

    override fun playTrack(currentTrack: Int) {
        val tmpList = arrayListOf<Track>().apply { addAll(adapter.items) }
        val playlist = Playlist(currentTrack, tmpList)
        Intent(context, AudioPlayerService::class.java).apply {
            putExtra("playlist", playlist)
            putExtra("directory", currentDirectory)
        }.also {
            Util.startForegroundService(context, it)
        }
    }
}