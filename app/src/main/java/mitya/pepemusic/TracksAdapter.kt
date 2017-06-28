package mitya.pepemusic

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.track_layout.view.*

/**
 * Created by Mitya on 28.06.2017.
 */
class TracksAdapter(val listener: (Track) -> Unit) : Adapter<TracksAdapter.ViewHolder>() {

    var items: MutableList<Track> = mutableListOf()

    fun addTrack(track: Track) {
        items.add(track)
        this.notifyItemInserted(itemCount - 1)
    }

    fun addTrackList(trackList: MutableList<Track>) = items.addAll(trackList)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTrack(items[position], listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return ViewHolder(itemView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTrack(track: Track, listener: (Track) -> Unit) {
            itemView.trackTitle.text = track.title
            itemView.trackArtist.text = track.artist
            itemView.setOnClickListener { listener(track) }
        }

    }

}