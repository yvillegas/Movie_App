package com.example.movies.ui.movie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.movies.R
import com.example.movies.core.Resource
import com.example.movies.data.local.AppDatabase
import com.example.movies.data.local.LocalMovieDataSource
import com.example.movies.data.model.Movie
import com.example.movies.data.remote.RemoteMovieDataSource
import com.example.movies.databinding.FragmentMovieBinding
import com.example.movies.presentation.MovieViewModel
import com.example.movies.presentation.MovieViewModelFactory
import com.example.movies.repository.MovieRepositoryImpl
import com.example.movies.repository.RetrofitClient
import com.example.movies.ui.movie.adapters.MovieAdapter
import com.example.movies.ui.movie.adapters.concat.PopularConcatAdapter
import com.example.movies.ui.movie.adapters.concat.TopRatedConcatAdapter
import com.example.movies.ui.movie.adapters.concat.UpcomingConcatAdapter


class MovieFragment : Fragment(R.layout.fragment_movie), MovieAdapter.OnMovieClickListener {

    private lateinit var binding: FragmentMovieBinding
    private val viewModel by viewModels<MovieViewModel> {
        MovieViewModelFactory(
            MovieRepositoryImpl(
                RemoteMovieDataSource(RetrofitClient.webservice),
                LocalMovieDataSource(AppDatabase.getDatabase(requireContext()).movieDao())
            )
        )
    }

    private lateinit var concatAdapter: ConcatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieBinding.bind(view)

        concatAdapter = ConcatAdapter()

        viewModel.fetchMainScreenMovies().observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    concatAdapter.apply {
                        addAdapter(
                            0,
                            UpcomingConcatAdapter(
                                MovieAdapter(
                                    result.data.first.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            1,
                            TopRatedConcatAdapter(
                                MovieAdapter(
                                    result.data.second.results,
                                    this@MovieFragment
                                )
                            )
                        )
                        addAdapter(
                            2,
                            PopularConcatAdapter(
                                MovieAdapter(
                                    result.data.third.results,
                                    this@MovieFragment
                                )
                            )
                        )
                    }

                    binding.rvMovies.adapter = concatAdapter
                }
                is Resource.Failure -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d("LiveData", "${result.exception}")
                }
            }
        })

    }

    override fun onMovieClick(movie: Movie) {
        val action = MovieFragmentDirections .actionMovieFragmentToMovieDetailFragment(
            movie.poster_path,
            movie.backdrop_path,
            movie.vote_average.toFloat(),
            movie.vote_count,
            movie.overview,
            movie.original_language,
            movie.release_date,
            movie.original_title
        )
        findNavController().navigate(action)
    }
}