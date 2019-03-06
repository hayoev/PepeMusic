package mitya.pepemusic

import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by Mitya on 01.07.2017.
 */
class TracksFragment : Fragment() {

    private lateinit var currentDirectory: String
    private val trackList = arrayListOf<Track>()
    private val adapter = TracksAdapter { playTrack(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        currentDirectory = arguments!!.getString("currentDirectory")
        loadTrackList()
        setupRecyclerView()
    }

    fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter.addTrackList(trackList)
    }

    fun playTrack(currentTrack: Int) {
        val tmpList = arrayListOf<Track>().apply { addAll(trackList) }
        val playlist = Playlist(currentTrack, tmpList)
        Intent(context, AudioPlayerService::class.java).apply {
            putExtra("playlist", playlist)
        }.also {
            Util.startForegroundService(context, it)
        }
    }

    fun loadTrackList() {
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
                        trackList.add(Track(thisTitle, thisArtist,
                                ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId)
                        ))
                    }
                } while (moveToNext())
            }
        }
        cursor.close()
    }

}