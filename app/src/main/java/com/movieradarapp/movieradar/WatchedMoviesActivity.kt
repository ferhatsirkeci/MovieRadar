package com.movieradarapp.movieradar

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.movieradarapp.movieradar.adapter.SearchAdapter
import com.movieradarapp.movieradar.databinding.ActivityWatchedMoviesBinding
import com.movieradarapp.movieradar.model.MovieModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class WatchedMoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchedMoviesBinding
    private val client = OkHttpClient()
    private val TAG = "WatchedMoviesActivity"
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private val movieList = mutableListOf<MovieModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWatchedMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Set up RecyclerView
        binding.watchedMoviesRecyclerView.layoutManager = LinearLayoutManager(this)
        val isWatchlist = intent.getBooleanExtra("IS_WATCHLIST", false)
        val adapter = SearchAdapter(this@WatchedMoviesActivity, movieList, isWatchlist,true)
        binding.watchedMoviesRecyclerView.adapter = adapter

        // Fetch movies based on the list type
        if (isWatchlist) {
            fetchWatchlistMovies(adapter)
        } else {
            fetchWatchedMovies(adapter)
        }
    }

    private fun fetchWatchedMovies(adapter: SearchAdapter) {
        val user = auth.currentUser
        if (user != null) {
            val email = user.email
            if (email != null) {
                firestore.collection("watched")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        val sortedDocuments = documents.sortedByDescending { it.getLong("timestamp") }
                        for (document in sortedDocuments) {
                            val movieId = document.getString("movieId")
                            if (movieId != null) {
                                fetchMovieDetails(movieId, adapter)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error fetching watched movies", e)
                    }
            }
        } else {
            Log.e(TAG, "User is not logged in")
        }
    }

    private fun fetchWatchlistMovies(adapter: SearchAdapter) {
        val user = auth.currentUser
        if (user != null) {
            val email = user.email
            if (email != null) {
                firestore.collection("watchlist")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        val sortedDocuments = documents.sortedByDescending { it.getLong("timestamp") }
                        for (document in sortedDocuments) {
                            val movieId = document.getString("movieId")
                            if (movieId != null) {
                                fetchMovieDetails(movieId, adapter)
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error fetching watchlist movies", e)
                    }
            }
        } else {
            Log.e(TAG, "User is not logged in")
        }
    }

    private fun fetchMovieDetails(movieId: String, adapter: SearchAdapter) {
        lifecycleScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YjEyN2I1NWIzMDlhYmViOWU2ODhlZmU4ZWUzNzU1YSIsInN1YiI6IjY2NTQ2ZGJhOWU1ZWQxMDY4NzAzZjU2OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.xw-hEHjOpaQwnjw1HkgpAQpoS8nCJzoYByBMOnDGLPQ")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    responseData?.let {
                        val jsonObject = JSONObject(it)
                        val movie = MovieModel(
                            jsonObject.getString("poster_path"),
                            jsonObject.getString("title"),
                            jsonObject.getString("id"),
                            jsonObject.getString("release_date").take(4)
                        )
                        movieList.add(movie)

                        withContext(Dispatchers.Main) {
                            adapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    Log.e(TAG, "Response not successful: ${response.code}")
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "IOException: ${e.message}")
            }
        }
    }
}
