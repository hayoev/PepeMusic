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
class DirectoriesAdapter(val listener: (String) -> Unit) : Adapter<DirectoriesAdapter.DirectoryViewHolder>() {

    private val items = arrayListOf<String>()

    fun addDirectories(directories: ArrayList<String>) {
        val startPosition = itemCount
        items.addAll(directories)
        this.notifyItemRangeInserted(startPosition, items.size)
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
                directoryPath.text = directory
                directoryTitle.text = directory
                setOnClickListener { listener(directory) }
            }
        }
    }

}