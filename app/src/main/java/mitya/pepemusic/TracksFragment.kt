package mitya.pepemusic

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.Player
import kotlinx.android.synthetic.main.exo_player_control_view.view.*
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by Mitya on 01.07.2017.
 */
abstract class TracksFragment : Fragment() {

    private lateinit var audioPlayerService: AudioPlayerService
    private var serviceBound = false

    protected open val adapter = TracksAdapter({ playTrack(it) })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        adapter.addTrackList(loadTrackList())
    }

    override fun onStart() {
        super.onStart()
        Intent(context, AudioPlayerService::class.java).also { intent ->
            activity!!.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        activity!!.unbindService(connection)
        serviceBound = false
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            audioPlayerService = (service as AudioPlayerService.AudioPLayerBinder).getService()
            audioPlayerService.player.addListener(object : Player.EventListener {

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    updatePlayerControlView()
                }

                override fun onPositionDiscontinuity(reason: Int) {
                    updatePlayerControlView()
                }
            })
            serviceBound = true
            if (audioPlayerService.player.playbackState == Player.STATE_READY) {
                updatePlayerControlView()
            }
            playerControlView.player = audioPlayerService.player
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
            playerControlView.visibility = View.GONE
        }

    }

    private fun updatePlayerControlView() {
        if (playerControlView != null) {
            playerControlView.visibility = View.VISIBLE
            val currentWindowIndex = audioPlayerService.player.currentWindowIndex
            playerControlView.title.text = audioPlayerService.playlist.list[currentWindowIndex].title
            playerControlView.artist.text = audioPlayerService.playlist.list[currentWindowIndex].artist
        }
    }

    protected abstract fun loadTrackList(): ArrayList<Track>
    protected abstract fun playTrack(currentTrack: Int)
}