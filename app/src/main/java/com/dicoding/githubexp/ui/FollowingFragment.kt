package com.dicoding.githubexp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubexp.adapter.FollowAdapter
import com.dicoding.githubexp.databinding.FragmentFollowingBinding
import com.dicoding.githubexp.model.GithubResponseFollow
import com.dicoding.githubexp.viewmodel.MainViewModel


class FollowingFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private val adapter = FollowAdapter()

    private lateinit var binding: FragmentFollowingBinding
    private val _binding get() = binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getIsLoading.observe(viewLifecycleOwner, this::showLoading)
        showViewModel()
        showRecyclerView()
    }

    private fun showViewModel() {
        viewModel.following(UserDetailActivity.username)
        viewModel.getFollowing.observe(viewLifecycleOwner) { following ->
            if (following.size != 0) {
                adapter.setData(following)
            } else {
                Toast.makeText(context, "Following Not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerView() {
        with(binding.rvFollowing) {
            layoutManager = LinearLayoutManager(requireActivity())
            setHasFixedSize(true)
            adapter = this@FollowingFragment.adapter
        }

        adapter.setOnItemClickCallback { data -> selectedUser(data) }
    }

    private fun selectedUser(user: GithubResponseFollow) {
        Toast.makeText(context, "You choose ${user.login}", Toast.LENGTH_SHORT).show()

        val i = Intent(activity, UserDetailActivity::class.java)
        i.putExtra(UserDetailActivity.EXTRA_USER, user.login)
        startActivity(i)
    }

    private fun showLoading(isLoading: Boolean) =
        binding.progressBar.visibility == if (isLoading) View.VISIBLE else View.GONE

    override fun onResume() {
        super.onResume()
        viewModel.following(UserDetailActivity.username)
    }
}