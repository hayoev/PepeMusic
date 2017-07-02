package mitya.pepemusic

import android.content.ContentUris
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by Mitya on 01.07.2017.
 */
class TracksFragment : Fragment() {

    private lateinit var currentDirectory: String
    private val mediaPlayer = MediaPlayer()
    private val adapter = TracksAdapter { playTrack(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        setupMediaPlayer()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        currentDirectory = arguments.getString("currentDirectory")
        setupRecyclerView()
        loadTrackList()
    }

    fun setupMediaPlayer() = mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

    fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
    }

    fun playTrack(track: Track) {
        val contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, track.id)
        with(mediaPlayer) {
            if (isPlaying) {
                reset()
            } else {
                setDataSource(activity, contentUri)
                prepare()
                start()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    fun loadTrackList() {
        val contentResolver = activity.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
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
                        adapter.addTrack(Track(thisTitle, thisArtist, thisId))
                    }
                } while (moveToNext())
            }
        }
        cursor.close()
    }

}