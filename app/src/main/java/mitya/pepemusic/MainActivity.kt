package mitya.pepemusic

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.content.ContentUris


class MainActivity : AppCompatActivity() {

    val adapter: TracksAdapter = TracksAdapter({ track -> playTrack(track) })

    val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        setupMediaPlayer()
        loadTrackList()

    }

    fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    fun setupMediaPlayer() = mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

    fun loadTrackList() {
        val contentResolver = contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            // query failed, handle error.
        } else if (!cursor.moveToFirst()) {
            // no media on the device
        } else {
            val titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            do {
                val thisId = cursor.getLong(idColumn)
                val thisTitle = cursor.getString(titleColumn)
                val thisArtist = cursor.getString(artistColumn)
                adapter.addTrack(Track(thisTitle, thisArtist, thisId))
            } while (cursor.moveToNext())
        }
    }

    fun playTrack(track: Track): Unit {
        val contentUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, track.id)
        mediaPlayer.setDataSource(applicationContext, contentUri)
        mediaPlayer.start()
    }
}
