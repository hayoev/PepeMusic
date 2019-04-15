package mitya.pepemusic

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.exoplayer2.util.Util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import okio.Okio
import org.jsoup.Jsoup
import java.io.File

class VkTracksFragment : TracksFragment() {

    private lateinit var searchQuery: String
    private val compositeDisposable = CompositeDisposable()
    override val adapter = TracksAdapter({ playTrack(it) }, { saveTrack(it) })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        searchQuery = arguments!!.getString("query")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun loadTrackList(): ArrayList<Track> {
        val tracks = arrayListOf<Track>()

        /*compositeDisposable.add(VkService.getInstance().getLoginHash()
                .subscribeOn(Schedulers.io())
                .map { handleResponse(it) }
                .flatMap { VkService.getInstance().login("login", "password", it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleLogin, this::handleError))
                */

        compositeDisposable.add(VkService.getInstance().searchMusic(searchQuery, 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    adapter.addTrackList(handleSearch(response))
                }, {
                    handleError(it)
                }))
        return tracks
    }

    private fun handleResponse(response: ResponseBody): String {
        var result = ""
        response.charStream().useLines { lines ->
            for (line in lines) {
                result += "name=\"lg_h\" value=\"([a-z0-9]+)\"".toRegex().find(line)?.value
                        ?.split("name=\"lg_h\" value=\"", "\"")?.get(1) ?: ""
                if (result != "") break
            }
        }
        return result
    }

    private fun handleSearch(response: ResponseBody): ArrayList<Track> {
        val tracks = arrayListOf<Track>()
        val document = Jsoup.parse(response.byteStream(), "UTF-8", "https://vk.com/")
        val elements = document.select("div.ai_info")
        for (element in elements) {
            val title = element.select("span.ai_title").text()
            val artist = element.select("span.ai_artist").text()
            val thumbnailLink = element.select("div.ai_play").attr("style").run {
                if (this.isNotEmpty()) substring(indexOf("(") + 1, indexOf(")"))
                else ""
            }
            val audioLink = decodeAudioUrl(element.getElementsByTag("input").`val`(),
                    getSavedData(context!!, "userId").toInt())
            tracks.add(Track(title, artist, Uri.parse(audioLink), Uri.parse(thumbnailLink)))
        }
        return tracks
    }

    private fun handleLogin(response: ResponseBody) {
        response.charStream().forEachLine {
            "/id([0-9]++)".toRegex().find(it)?.value?.split("/id")?.let { list ->
                saveData(context!!, "userId", list[1])
            }
        }
    }

    override fun playTrack(trackIndex: Int) {
        val tmpList = arrayListOf<Track>().apply { addAll(adapter.items) }
        val playlist = Playlist(trackIndex, tmpList)
        Intent(context, AudioPlayerService::class.java).apply {
            putExtra("playlist", playlist)
        }.also {
            Util.startForegroundService(context, it)
        }
    }

    private fun saveTrack(trackIndex: Int): Boolean {
        toast("Saving track ${adapter.items[trackIndex].artist} - ${adapter.items[trackIndex].title}", Toast.LENGTH_LONG)
        compositeDisposable.add(VkService.getInstance().downloadTrack(adapter.items[trackIndex].contentUri.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath + "/" + getString(R.string.app_name)
                    if (!File(path).exists()) File(path).mkdirs()
                    val file = File(path, "${adapter.items[trackIndex].artist} - ${adapter.items[trackIndex].title}.mp3")
                    Okio.buffer(Okio.sink(file)).apply {
                        writeAll(it.source())
                        close()
                    }
                    context!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                    toast("Track saved")
                }, { handleError(it) }))
        return true
    }

    private fun handleError(error: Throwable) {
        log(error.localizedMessage)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }
}