package com.example.movieapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        val path= filesDir
        if(!File(path,"LET").exists()){
            File(path,"LET").mkdir()
        }
        val letDirectory= File(path,"LET")
        if(!File(letDirectory,"/watchlist.txt").exists()){
            Log.d("readFile","file created")
            File(letDirectory,"/watchlist.txt").createNewFile()
        }
        val file=File(letDirectory,"watchlist.txt")
        val client = OkHttpClient()
        var name = findViewById<TextView>(R.id.gridData)
        var image =findViewById<ImageView>(R.id.gridImage)
        val id=intent.getStringExtra("id")
        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/movie/$id&language=en-US")
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

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    var jsondata = response.body!!.string()
                    var jObject = JSONObject(jsondata)
                    var bitmap = MainActivity.getBitmapFromUrl(
                        jObject.get("poster_path")
                            .toString()
                    )
                    runOnUiThread{
                        name.setText(jObject.get("title").toString())
                        image.setImageBitmap(bitmap)
                    }

                }
            }

        })
        findViewById<Button>(R.id.addBookmark).setOnClickListener{
            file.appendText(id.toString()+"\n")
            Toast.makeText(this, "Bookmarked", Toast.LENGTH_SHORT).show()
        }


    }
    fun getBitmapFromUrl(src:String): Bitmap? {
        try {
            Log.e("src",src)
            var url= "https://image.tmdb.org/t/p/original/$src"
            var connection= URL(url).openStream()
            var image= BitmapFactory.decodeStream(connection)
            return image
        }catch(e:IOException){
            return null
        }
    }
}