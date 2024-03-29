package com.example.movieapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors


class trendingCard(val id:String,val name:String,val bitmap: String){
    companion object {
        fun createWatchlistList(id: ArrayList<String>,name:ArrayList<String>,bitmap:ArrayList<String>): ArrayList<trendingCard> {
            val trending = ArrayList<trendingCard>()
            for((index,value) in id.withIndex()) {
                trending.add(trendingCard(id.get(index),name.get(index),bitmap.get(index)))
            }
            return trending
        }
    }
}

class trendingAdapter(private val mTrending: List<trendingCard>, context:Context?) : BaseAdapter() {
    val view:LayoutInflater
    init {
        view= LayoutInflater.from(context)
    }
    override fun getCount(): Int {
        return mTrending.size
    }

    override fun getItem(position: Int): Any {
        return null!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewed=view.inflate(R.layout.trending_card,null)
        val image=viewed.findViewById<ImageView>(R.id.trendingImage)
        val text=viewed.findViewById<TextView>(R.id.trendingText)
        var service=Executors.newSingleThreadExecutor()
        var handler = Handler(Looper.getMainLooper())
        val title :trendingCard= mTrending.get(position)
        service.execute(Runnable {
            Log.d("view","getting image")
            var bitmap=getBitmapFromUrl(title.bitmap)
            handler.post(Runnable {
                text.setText(title.name)
                image.setImageBitmap(bitmap)
            })
        })

        Log.d("view","printed")
        return viewed
    }
    fun getBitmapFromUrl(src: String): Bitmap? {
        try {
            Log.d("get image",src)
            var url= "https://image.tmdb.org/t/p/original/$src"
            var connection= URL(url).openStream()
            var image= BitmapFactory.decodeStream(connection)
            return image
        }catch(e:IOException){
            return null
        }
    }

}

class MainMenu : Fragment(R.layout.fragment_main_menu) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var gridview=view.findViewById<GridView>(R.id.trendingGridView)
        var list=ArrayList<trendingCard>()
        list.add(trendingCard("test","test","/5zmiBoMzeeVdQ62no55JOJMY498.jpg"))
        getTrendingId(list)
        Log.d("view",list.get(0).toString())
        gridview.adapter=trendingAdapter(list,context)
        gridview.setOnItemClickListener{ adapterView: AdapterView<*>, view2: View, i: Int, l: Long ->
            val intent =Intent(activity, DetailsActivity::class.java)
            intent.putExtra("id", list.get(i).id)
            this.startActivity(intent)
        }

    }

    fun getTrendingId(list:ArrayList<trendingCard>){
            var service = Executors.newSingleThreadExecutor()
            var handler = Handler(Looper.getMainLooper())
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/trending/all/day?language=en-US")
                .get()
                .addHeader("accept", "application/json")
                .addHeader(
                    "Authorization",
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5ZmM3MWMxNzZjNzVjZTQzZDk2MWFiYTc1NWFiNWFjMSIsInN1YiI6IjY1ZTgwZTllMzQ0YThlMDE3ZDNlZmQyOCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.phSeMPz4gwQ-daRBwmo21fg-HCWDQcpZfd3IWX2VXuM"
                )
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                @SuppressLint("SuspiciousIndentation")
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        var jsondata = response.body!!.string()
                        var jObject = JSONObject(jsondata)
                        var count=jObject.getJSONArray("results").length()
                        for( i in 0..<count){
                            if(jObject.getJSONArray("results").getJSONObject(i).has("title")){
                                Log.d("test title",jObject.getJSONArray("results").getJSONObject(i).get("title").toString())
                                list.add(trendingCard(jObject.getJSONArray("results").getJSONObject(i).get("id").toString(),jObject.getJSONArray("results").getJSONObject(i).get("title").toString(), jObject.getJSONArray("results").getJSONObject(i).get("poster_path").toString()))
                            }else{
                                Log.d("test title",jObject.getJSONArray("results").getJSONObject(i).get("name").toString())
                                list.add(trendingCard(jObject.getJSONArray("results").getJSONObject(i).get("id").toString(),jObject.getJSONArray("results").getJSONObject(i).get("name").toString(), jObject.getJSONArray("results").getJSONObject(i).get("poster_path").toString()))
                            }

                        }
                    }
                }

            })
    }



}