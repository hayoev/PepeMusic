package mitya.pepemusic

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.directory_layout.view.*

/**
 * Created by Mitya on 02.07.2017.
 */
class DirectoriesAdapter(private val listener: (String) -> Unit) : Adapter<DirectoriesAdapter.DirectoryViewHolder>() {

    private val items = arrayListOf<String>()

    fun addDirectories(directories: ArrayList<String>) {
        items.clear()
        items.addAll(directories)
        this.notifyItemRangeInserted(0, items.size)
    }

    override fun onBindViewHolder(holder: DirectoryViewHolder, position: Int) = holder.bindDirectory(items[position], listener)

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoriesAdapter.DirectoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.directory_layout, parent, false)
        return DirectoryViewHolder(itemView)
    }

    class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindDirectory(directory: String, listener: (String) -> Unit) {
            with(itemView) {
                directoryTitle.text = directory
                setOnClickListener { listener(directory) }
            }
        }
    }

}