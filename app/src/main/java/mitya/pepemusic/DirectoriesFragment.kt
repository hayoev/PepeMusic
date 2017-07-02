package mitya.pepemusic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * Created by Mitya on 02.07.2017.
 */
class DirectoriesFragment : Fragment() {

    private lateinit var directoryList: ArrayList<String>
    private val adapter = DirectoriesAdapter { openDirectory(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        directoryList = arguments.getStringArrayList("directoryList")
        setupRecyclerView()
        adapter.addDirectories(directoryList)
    }

    fun setupRecyclerView() {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    fun openDirectory(path: String) {
        val bundle = Bundle()
        bundle.putString("currentDirectory", path)
        val fragment = TracksFragment()
        fragment.arguments = bundle
        this.fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, null)
                .addToBackStack(null)
                .commit()
    }
}