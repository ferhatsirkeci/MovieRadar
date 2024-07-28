package com.movieradarapp.movieradar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.movieradarapp.movieradar.databinding.FragmentProfileBinding
import androidx.appcompat.app.AlertDialog

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (email != null) {
              binding.tvEmail.text=email
            } else {
                Log.d("UserEmail", "Email is null")
            }
        } else {
            Log.d("UserEmail", "No user is currently signed in")
        }

        binding.logoutButton.setOnClickListener {
            logoutOnClick()
        }

        binding.btnWatchedMovies.setOnClickListener {
            watchedMoviesOnClick()
        }

        binding.btnWatchlists.setOnClickListener {
            watchlistOnClick()
        }
    }

    private fun logoutOnClick() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to Logout?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            auth.signOut()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun watchedMoviesOnClick() {
        val intent = Intent(requireActivity(), WatchedMoviesActivity::class.java)
        intent.putExtra("IS_WATCHLIST", false)
        startActivity(intent)
    }

    private fun watchlistOnClick() {
        val intent = Intent(requireActivity(), WatchedMoviesActivity::class.java)
        intent.putExtra("IS_WATCHLIST", true)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
