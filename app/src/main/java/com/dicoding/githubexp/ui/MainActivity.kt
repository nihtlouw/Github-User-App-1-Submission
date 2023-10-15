package com.dicoding.githubexp.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.githubexp.adapter.UserAdapter
import com.dicoding.githubexp.api.ApiConfig
import com.dicoding.githubexp.databinding.ActivityMainBinding
import com.dicoding.githubexp.model.ItemsItem
import com.dicoding.githubexp.model.ResponseSearch
import com.dicoding.githubexp.viewmodel.MainViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val adapter = UserAdapter()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE
        getRandomGitHubUsers()
        showViewModel()
        showRecyclerView()
        setupSearchView()
        viewModel.getIsLoading.observe(this, this::showLoading)
    }

    private fun getRandomGitHubUsers() {

        val apiService = ApiConfig.getApiService()
        val call = apiService.search("Arif")

        call.enqueue(object : Callback<ResponseSearch> {
            override fun onResponse(call: Call<ResponseSearch>, response: Response<ResponseSearch>) {
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val searchResults = response.body()!!.items
                    updateRecyclerView(searchResults)
                }
            }

            override fun onFailure(call: Call<ResponseSearch>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }

    private fun updateRecyclerView(usersList: List<ItemsItem>) {
        Log.d("GitHubUsers", "Total Users: ${usersList.size}")
        val arrayListUsers = ArrayList<ItemsItem>(usersList)
        adapter.setData(arrayListUsers)
    }

    private fun showViewModel() {
        viewModel.getSearchList.observe(this) { searchList ->
            if (searchList.size != 0) {
                binding.rvUser.visibility = View.VISIBLE
                adapter.setData(searchList)
            } else {
                binding.rvUser.visibility = View.GONE
                Toast.makeText(this, " User Not Found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRecyclerView() {
        binding.rvUser.layoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }

        binding.rvUser.setHasFixedSize(true)
        binding.rvUser.adapter = adapter

        adapter.setOnItemClickCallback { data -> selectedUser(data) }
    }

    private fun selectedUser(user: ItemsItem) {
        Toast.makeText(this, "You choose ${user.login}", Toast.LENGTH_SHORT).show()

        val i = Intent(this, UserDetailActivity::class.java)
        i.putExtra(UserDetailActivity.EXTRA_USER, user.login)
        startActivity(i)
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        val linearLayout = binding.bgSearch // Ganti dengan ID LinearLayout Anda

        // Memantulkan event saat pengguna menekan tombol "Search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchUser(query!!)
                }
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle changes in the search query text if needed
                // This method is called when the query text is changed (e.g., real-time search)
                return true
            }
        })

        // Menanggapi peristiwa ketika pengguna menekan tombol "Enter" di keyboard
        searchView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val query = searchView.query.toString()
                if (!query.isNullOrBlank()) {
                    viewModel.searchUser(query)
                }
                searchView.clearFocus()
                true
            } else {
                false
            }
        }

        // Menanggapi klik pada elemen dengan ID searchLinearLayout (LinearLayout)
        linearLayout.setOnClickListener {
            searchView.isIconified = false // Membuka SearchView
        }
    }



    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
