import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.movieradarapp.movieradar.MovieDetailsActivity
import com.movieradarapp.movieradar.databinding.MovieRecyclerRowBinding
import com.movieradarapp.movieradar.model.MovieModel
import com.squareup.picasso.Picasso

class MovieAdapter(private val movies: List<MovieModel>) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(val binding: MovieRecyclerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        Log.d("MovieAdapter", "Title: ${movie.name}, Poster Path: ${movie.imageUrl}")
        holder.binding.movieTitle.text = movie.name
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
    }

    override fun getItemCount() = movies.size
}
