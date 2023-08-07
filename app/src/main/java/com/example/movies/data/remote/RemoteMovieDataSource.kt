package com.example.movies.data.remote

import com.example.movies.application.AppConstants
import com.example.movies.data.model.MovieList
import com.example.movies.repository.WebService

class RemoteMovieDataSource(private val webService: WebService){

    suspend fun getUpcomingMovies(): MovieList = webService.getUpcomingMovies(AppConstants.API_KEY)

    suspend fun getTopRatedMovies(): MovieList = webService.getTopRatedMovies(AppConstants.API_KEY)

    suspend fun getPopularMovies(): MovieList = webService.getPopularMovies(AppConstants.API_KEY)
}