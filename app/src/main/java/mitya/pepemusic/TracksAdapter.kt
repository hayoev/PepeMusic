package mitya.pepemusic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.android.synthetic.main.track_layout.view.*

/**
 * Created by Mitya on 28.06.2017.
 */
open class TracksAdapter(private val clickListener: (Int) -> Unit
                         , private val popUpClickListener: (Int) -> Unit = {}
                         , private val deleteClickListener: (Int) -> Unit = {}) : Adapter<TracksAdapter.TrackViewHolder>() {

    val items = arrayListOf<Track>()

    fun addTrack(track: Track) {
        items.add(track)
        this.notifyItemInserted(itemCount - 1)
    }

    fun addTrackList(trackList: ArrayList<Track>) {
        items.clear()
        items.addAll(trackList)
        this.notifyItemRangeInserted(0, items.size)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) =
            holder.bindTrack(items[position], clickListener, popUpClickListener, deleteClickListener)

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track_layout, parent, false)
        return TrackViewHolder(itemView)
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindTrack(track: Track, clickListener: (Int) -> Unit, popUpClickListener: (Int) -> Unit, deleteClickListener: (Int) -> Unit) {
            with(itemView) {
                trackTitle.text = track.title
                trackArtist.text = track.artist
                setOnClickListener { clickListener(this@TrackViewHolder.adapterPosition) }
                popUpButton.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val popup = PopupMenu(context, popUpButton)
                        popup.menuInflater.inflate(R.menu.popup_search_menu, popup.menu)
                        popup.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.itemSave -> {
                                    log("saving")
                                    popUpClickListener(this@TrackViewHolder.adapterPosition)
                                    true
                                }
                                R.id.itemDelete -> {
                                    log("deleting")
                                    deleteClickListener(this@TrackViewHolder.adapterPosition)
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                        popup.show()
                    }
                }
            }
        }
    }

}