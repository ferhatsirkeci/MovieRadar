package com.movieradarapp.movieradar.model

import com.google.gson.annotations.SerializedName

data class MovieModel(
    @SerializedName("poster_path") val imageUrl: String,

    @SerializedName("title") val name:String,
    @SerializedName("id") val id:String,
    @SerializedName("release_year") val releaseYear:String
 )

