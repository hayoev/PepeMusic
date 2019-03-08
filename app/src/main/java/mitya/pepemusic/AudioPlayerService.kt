package mitya.pepemusic

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class AudioPlayerService : Service() {
    private val binder = AudioPLayerBinder()

    lateinit var playlist: Playlist
    lateinit var currentDirectory: String
    val player: SimpleExoPlayer by lazy { ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector()) }
    private val mediaSession: MediaSessionCompat by lazy { MediaSessionCompat(this, getString(R.string.app_name)) }
    private val mediaSessionConnector: MediaSessionConnector by lazy { MediaSessionConnector(mediaSession) }
    private val playerNotificationManager by lazy {
        PlayerNotificationManager.createWithNotificationChannel(this,
                PLAYBACK_CHANNEL_ID, R.string.playback_channel_name, PLAYBACK_NOTIFICATION_ID,
                object : MediaDescriptionAdapter {
                    override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                        val intent = Intent(this@AudioPlayerService, MainActivity::class.java).apply {
                            putExtra("directory", currentDirectory)
                        }
                        return PendingIntent.getActivity(this@AudioPlayerService, 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    }

                    override fun getCurrentContentText(player: Player) = playlist.list[player.currentWindowIndex].artist

                    override fun getCurrentContentTitle(player: Player) = playlist.list[player.currentWindowIndex].title

                    override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback?): Bitmap? {
                        return null
                    }
                })
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        playlist = intent.getParcelableExtra("playlist")
        currentDirectory = intent.getStringExtra("directory")
        val dataSourceFactory = DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)))
        val concatenatingMediaSource = ConcatenatingMediaSource()
        for (track in playlist.list) {
            concatenatingMediaSource.addMediaSource(ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(track.contentUri))
        }
        player.prepare(concatenatingMediaSource)
        player.playWhenReady = true

        playerNotificationManager.setNotificationListener(object : NotificationListener {

            override fun onNotificationStarted(notificationId: Int, notification: Notification?) = startForeground(notificationId, notification)

            override fun onNotificationCancelled(notificationId: Int) = stopSelf()

        })
        playerNotificationManager.setPlayer(player)

        mediaSession.isActive = true
        playerNotificationManager.setMediaSessionToken(mediaSession.sessionToken)
        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(player: Player?, windowIndex: Int) =
                    MediaDescriptionCompat.Builder()
                            .setTitle(playlist.list[windowIndex].title)
                            .setMediaUri(playlist.list[windowIndex].contentUri)
                            .build()
        })
        mediaSessionConnector.setPlayer(player, null)

        return START_STICKY
    }

    override fun onDestroy() {
        player.release()
        playerNotificationManager.setPlayer(null)
        mediaSession.release()
        mediaSessionConnector.setPlayer(null, null)
        super.onDestroy()
    }

    inner class AudioPLayerBinder : Binder() {
        fun getService() = this@AudioPlayerService
    }

    override fun onBind(intent: Intent) = binder

}