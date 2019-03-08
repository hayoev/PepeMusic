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
open class TracksAdapter(private val clickListener: (Int) -> Unit) : Adapter<TracksAdapter.TrackViewHolder>() {

    private val items = arrayListOf<Track>()

    fun addTrack(track: Track) {
        items.add(track)
        this.notifyItemInserted(itemCount - 1)
    }

    fun addTrackList(trackList: ArrayList<Track>) {
        val startPosition = itemCount
        items.addAll(trackList)
        this.notifyItemRangeInserted(startPosition, items.size)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) = holder.bindTrack(items[position], clickListener)

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(itemView)
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindTrack(track: Track, clickListener: (Int) -> Unit) {
            with(itemView) {
                trackTitle.text = track.title
                trackArtist.text = track.artist
                setOnClickListener { clickListener(this@TrackViewHolder.adapterPosition) }
            }
        }
    }

}