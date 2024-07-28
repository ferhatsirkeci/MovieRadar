package com.movieradarapp.movieradar

import MovieAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.movieradarapp.movieradar.databinding.FragmentHomeBinding
import com.movieradarapp.movieradar.model.MovieResponse
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val client = OkHttpClient()
    private lateinit var movieAdapter: MovieAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the RecyclerView to use a horizontal LinearLayoutManager
        binding.recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fetchTrendingMovies()
        binding.recyclerView1.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fetchTopRatedMovies()
    }

    private fun fetchTrendingMovies() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/trending/movie/week")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YjEyN2I1NWIzMDlhYmViOWU2ODhlZmU4ZWUzNzU1YSIsInN1YiI6IjY2NTQ2ZGJhOWU1ZWQxMDY4NzAzZjU2OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.xw-hEHjOpaQwnjw1HkgpAQpoS8nCJzoYByBMOnDGLPQ")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val movieResponse = Gson().fromJson(responseData, MovieResponse::class.java)

                    withContext(Dispatchers.Main) {
                        _binding?.let {
                            movieAdapter = MovieAdapter(movieResponse.results)
                            it.recyclerView.adapter = movieAdapter
                        }
                    }
                } else {
                    // Handle error case
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error case
            }
        }
    }



    private fun fetchTopRatedMovies() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/top_rated")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YjEyN2I1NWIzMDlhYmViOWU2ODhlZmU4ZWUzNzU1YSIsInN1YiI6IjY2NTQ2ZGJhOWU1ZWQxMDY4NzAzZjU2OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.xw-hEHjOpaQwnjw1HkgpAQpoS8nCJzoYByBMOnDGLPQ")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val movieResponse = Gson().fromJson(responseData, MovieResponse::class.java)

                    withContext(Dispatchers.Main) {
                        _binding?.let {
                            movieAdapter = MovieAdapter(movieResponse.results)
                            it.recyclerView1.adapter = movieAdapter
                        }
                    }
                } else {
                    // Handle error case
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle error case
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
