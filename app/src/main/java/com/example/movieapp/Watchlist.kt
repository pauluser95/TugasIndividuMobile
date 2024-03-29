package com.example.movieapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [Watchlist.newInstance] factory method to
 * create an instance of this fragment.
 */
class Watchlisted(val id: String, val isWatched: Boolean) {
    companion object {
        fun createWatchlistList(id: ArrayList<String>): ArrayList<Watchlisted> {
            val watchlisted = ArrayList<Watchlisted>()
            for((index,value) in id.withIndex()) {
                watchlisted.add(Watchlisted(value,false))
            }
            return watchlisted
        }
    }

}
class WatchlistedAdapter(private val mWatchlisted: List<Watchlisted>) : RecyclerView.Adapter<WatchlistedAdapter.ViewHolder>() {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain and initialize a member variable
        // for any view that will be set as you render a row
        val nameTextView = itemView.findViewById<TextView>(R.id.watchlist_name)
        val watchButton = itemView.findViewById<Button>(R.id.watched_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistedAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.watchlist_recycle_item, parent, false)
        // Return a new holder instance
        return ViewHolder(contactView)
    }
    override fun onBindViewHolder(viewHolder: WatchlistedAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val movie: Watchlisted = mWatchlisted.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.nameTextView
        val id=movie.id
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/$id&language=en-US")
            .get()
            .addHeader("accept", "application/json")
            .addHeader(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZmM3MWMxNzZjNzVjZTQzZDk2MWFiYTc1NWFiNWFjMSIsInN1YiI6IjY1ZTgwZTllMzQ0YThlMDE3ZDNlZmQyOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.phSeMPz4gwQ-daRBwmo21fg-HCWDQcpZfd3IWX2VXuM"
            )
            .build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var jsondata = response.body!!.string()
                    var jObject = JSONObject(jsondata)
                    textView.setText(jObject.get("title").toString())
                }
            }

        })
//        textView.setText(movie.id)
        val button = viewHolder.watchButton
        button.text = if (movie.isWatched) "Watched" else "Watch now"
        button.isEnabled = movie.isWatched.not()
    }
    override fun getItemCount(): Int {
        return mWatchlisted.size
    }
}

class Watchlist : Fragment(R.layout.fragment_watchlist) {
    lateinit var watchlists: ArrayList<Watchlisted>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val watchlistRecycler = view.findViewById(R.id.watchlistRecycler) as RecyclerView
        val path= activity?.filesDir
        if(!File(path,"LET").exists()){
            File(path,"LET").mkdir()
        }
        val letDirectory= File(path,"LET")
        if(!File(letDirectory,"/watchlist.txt").exists()){
            Log.d("readFile","file created")
            File(letDirectory,"/watchlist.txt").createNewFile()
        }
        val file= File(letDirectory,"watchlist.txt")
        var watchlist=ArrayList<String>()
        if(file.exists()) {
            val read = FileInputStream(file).bufferedReader()
            var line = read.readLine()
            while (line != null) {
                Log.d("readFile", line.toString())
                watchlist.add(line.toString())
                line = read.readLine()
            }
        }
        watchlists = Watchlisted.createWatchlistList(watchlist)
        val adapter = WatchlistedAdapter(watchlists)
        watchlistRecycler.adapter = adapter
        watchlistRecycler.layoutManager = LinearLayoutManager(activity)
    }
}