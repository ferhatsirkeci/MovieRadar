package com.movieradarapp.movieradar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.movieradarapp.movieradar.databinding.ActivityMovieDetailsBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding
    private val client = OkHttpClient()
    private val TAG = "MovieDetailsActivity"
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Get the movie ID from the intent
        val movieId = intent.getStringExtra("MOVIE_ID")
        Log.d(TAG, "Received Movie ID: $movieId")

        // Fetch movie details from API
        if (movieId != null) {
            fetchMovieDetails(movieId)
        }

        // Set up the "Watched" button click listener
        binding.watchedButton.setOnClickListener {
            if (movieId != null) {
                watchedButtonClicked(movieId)
            }
        }

        // Set up the "Watchlist" button click listener
        binding.watchlistButton.setOnClickListener {
            if (movieId != null) {
                watchlistButtonClicked(movieId)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun watchedButtonClicked(movieId: String) {
        val user = auth.currentUser
        if (user != null) {
            val email = user.email
            if (email != null) {
                // Query the Firestore to check if the movie is already in the watched collection
                firestore.collection("watched")
                    .whereEqualTo("email", email)
                    .whereEqualTo("movieId", movieId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // If no documents are found, add the movie to the watched collection
                            val watchData = hashMapOf(
                                "email" to email,
                                "movieId" to movieId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("watched")
                                .add(watchData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Movie added to watched collection")
                                    Toast.makeText(this, "Movie added to watched list", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error adding movie to watched collection", e)
                                    Toast.makeText(this, "Error adding movie to watched list", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // The movie is already in the watched collection for this user
                            Log.d(TAG, "Movie is already in the watched collection")
                            Toast.makeText(this, "Movie is already in your watched list", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error querying watched collection", e)
                        Toast.makeText(this, "Error querying watched collection", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // Handle the case where the user is not logged in
            Log.e(TAG, "User is not logged in")
            Toast.makeText(this, "You need to log in first", Toast.LENGTH_SHORT).show()
        }
    }


    private fun watchlistButtonClicked(movieId: String) {
        val user = auth.currentUser
        if (user != null) {
            val email = user.email
            if (email != null) {
                // Query the Firestore to check if the movie is already in the watchlist collection
                firestore.collection("watchlist")
                    .whereEqualTo("email", email)
                    .whereEqualTo("movieId", movieId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // If no documents are found, add the movie to the watchlist collection
                            val watchlistData = hashMapOf(
                                "email" to email,
                                "movieId" to movieId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("watchlist")
                                .add(watchlistData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Movie added to watchlist collection")
                                    Toast.makeText(this, "Movie added to watchlist", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error adding movie to watchlist collection", e)
                                    Toast.makeText(this, "Error adding movie to watchlist", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // The movie is already in the watchlist collection for this user
                            Log.d(TAG, "Movie is already in the watchlist collection")
                            Toast.makeText(this, "Movie is already in your watchlist", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error querying watchlist collection", e)
                        Toast.makeText(this, "Error querying watchlist collection", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // Handle the case where the user is not logged in
            Log.e(TAG, "User is not logged in")
            Toast.makeText(this, "You need to log in first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchMovieDetails(movieId: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/${movieId}")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YjEyN2I1NWIzMDlhYmViOWU2ODhlZmU4ZWUzNzU1YSIsInN1YiI6IjY2NTQ2ZGJhOWU1ZWQxMDY4NzAzZjU2OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.xw-hEHjOpaQwnjw1HkgpAQpoS8nCJzoYByBMOnDGLPQ")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    responseData?.let {
                        Log.d(TAG, "Response data: $it")
                        val jsonObject = JSONObject(it)

                        withContext(Dispatchers.Main) {
                            binding.nameText.text = jsonObject.getString("title")
                            binding.overviewText.text = jsonObject.getString("overview")

                            // Handle genres JSONArray
                            val genresArray = jsonObject.getJSONArray("genres")
                            val genresList = mutableListOf<String>()
                            for (i in 0 until genresArray.length()) {
                                val genre = genresArray.getJSONObject(i)
                                genresList.add(genre.getString("name"))
                            }
                            binding.genreText.text = "Genres: " + genresList.joinToString(", ")

                            // Handle production countries JSONArray
                            val countriesArray = jsonObject.getJSONArray("production_countries")
                            val countriesList = mutableListOf<String>()
                            for (i in 0 until countriesArray.length()) {
                                val country = countriesArray.getJSONObject(i)
                                countriesList.add(country.getString("name"))
                            }
                            binding.originCountryText.text = "Country: " + countriesList.joinToString(",")

                            binding.taglineText.text = jsonObject.getString("tagline")
                            binding.durationText.text = "Duration: " + jsonObject.getInt("runtime").toString() + " mins"
                            binding.releaseText.text = "Release Year: "+ jsonObject.getString("release_date").take(4)

                            // Load image using Picasso
                            val imageUrl = "https://image.tmdb.org/t/p/original" + jsonObject.getString("poster_path")
                            Picasso.get().load(imageUrl).into(binding.logoImage)
                        }
                    }
                } else {
                    // Handle error case
                    Log.e(TAG, "Response not successful: ${response.code}")
                    withContext(Dispatchers.Main) {
                        // Update UI to show error message
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(TAG, "IOException: ${e.message}")
                // Handle error case
                withContext(Dispatchers.Main) {
                    // Update UI to show error message
                }
            }
        }
    }
}
