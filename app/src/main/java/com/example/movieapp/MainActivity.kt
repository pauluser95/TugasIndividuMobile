package com.example.movieapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.ui.theme.MovieAppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(R.id.flFragment,MainMenu()).commit()
        binding.btnFragment1.setOnClickListener{
            replaceFragment(MainMenu())
        }
        binding.btnFragment2.setOnClickListener{
            replaceFragment(Watchlist())
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager =supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.flFragment,fragment)
        fragmentTransaction.commit()
    }

    companion object {
        fun getBitmapFromUrl(src: String): Bitmap? {
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

}
