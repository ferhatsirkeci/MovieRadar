package com.movieradarapp.movieradar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.movieradarapp.movieradar.adapter.SearchAdapter
import com.movieradarapp.movieradar.databinding.FragmentSearchBinding
import com.movieradarapp.movieradar.model.MovieModel
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Callback

import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchAdapter: SearchAdapter
    private val movieList = mutableListOf<MovieModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val view = binding.root

        // SearchView'i burada ayarlayın
        val searchView = binding.searchView
        searchView.queryHint = "Search for a movie"

        // Hint text view'ini bulup rengini beyaz yapın
        val hintTextViewId = searchView.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val hintTextView = searchView.findViewById<TextView>(hintTextViewId)
        hintTextView.setHintTextColor(Color.WHITE)
        hintTextView.setTextColor(Color.WHITE)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        searchAdapter = SearchAdapter(requireContext(),movieList,false,false)
        binding.recyclerView.adapter = searchAdapter

        setupSearchView()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    performSearch(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun performSearch(query: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.themoviedb.org/3/search/movie?query=${query}&include_adult=false")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YjEyN2I1NWIzMDlhYmViOWU2ODhlZmU4ZWUzNzU1YSIsInN1YiI6IjY2NTQ2ZGJhOWU1ZWQxMDY4NzAzZjU2OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.xw-hEHjOpaQwnjw1HkgpAQpoS8nCJzoYByBMOnDGLPQ")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val jsonResponse = JSONObject(it)
                    val results = jsonResponse.getJSONArray("results")
                    val movies = mutableListOf<MovieModel>()

                    for (i in 0 until results.length()) {
                        val movieJson = results.getJSONObject(i)
                        val movie = MovieModel(
                            id = movieJson.getString("id"),
                            imageUrl = movieJson.optString("poster_path", null),
                            name = movieJson.getString("title"),
                            releaseYear = movieJson.getString("release_date").take(4)
                        )
                        movies.add(movie)
                    }

                    activity?.runOnUiThread {
                        movieList.clear()
                        movieList.addAll(movies)
                        searchAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

