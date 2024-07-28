package com.movieradarapp.movieradar.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.movieradarapp.movieradar.MovieDetailsActivity
import com.movieradarapp.movieradar.databinding.MovieListRecyclerRowBinding
import com.movieradarapp.movieradar.model.MovieModel
import com.squareup.picasso.Picasso

class SearchAdapter(
    private val context: Context,
    private val movieList: MutableList<MovieModel>,
    private val isWatchlist: Boolean,
    private val fromWatchedMoviesActivity: Boolean
) : RecyclerView.Adapter<SearchAdapter.MovieViewHolder>() {

    inner class MovieViewHolder(val binding: MovieListRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieListRecyclerRowBinding.inflate(LayoutInflater.from(context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]
        holder.binding.movieTitle.text = movie.name
        holder.binding.movieYear.text = movie.releaseYear.take(4)
        val posterUrl = "https://image.tmdb.org/t/p/original${movie.imageUrl}"
        Picasso.get().load(posterUrl).into(holder.binding.moviePoster)

        holder.binding.moviePoster.setOnClickListener {
            val context = holder.itemView.context
            Log.d("MovieAdapter", "Movie ID: ${movie.id}")
            val intent = Intent(context, MovieDetailsActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
            }
            context.startActivity(intent)
        }
        // Set button visibility based on the activity and list type
        if (fromWatchedMoviesActivity ) {
            holder.binding.deleteButton.visibility = View.VISIBLE
        } else {
            holder.binding.deleteButton.visibility = View.GONE
        }

        holder.binding.deleteButton.setOnClickListener {
            removeMovieFromFirestore(movie, holder.adapterPosition)
        }
    }

    private fun removeMovieFromFirestore(movie: MovieModel, position: Int) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val collectionPath = if (isWatchlist) "watchlist" else "watched"
            db.collection(collectionPath)
                .whereEqualTo("email", FirebaseAuth.getInstance().currentUser?.email)
                .whereEqualTo("movieId", movie.id)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection(collectionPath).document(document.id).delete()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Check if position is still valid and remove the item from the local list
                                    if (position >= 0 && position < movieList.size) {
                                        movieList.removeAt(position)
                                        notifyItemRemoved(position)
                                        notifyItemRangeChanged(position, movieList.size)
                                    }
                                } else {
                                    // Handle the error, if necessary
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error, if necessary
                }
        }
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}
