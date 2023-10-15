package com.dicoding.githubexp.api

import com.dicoding.githubexp.model.GithubResponseDetailUser
import com.dicoding.githubexp.model.GithubResponseFollow
import com.dicoding.githubexp.model.ResponseSearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/users")
    fun search(
        @Query("q") username: String
    ): Call<ResponseSearch>

    @GET("users/{username}")
    fun detailUser(
        @Path("username") username: String
    ): Call<GithubResponseDetailUser>

    @GET("users/{username}/followers")
    fun followers(
        @Path("username") username: String
    ): Call<ArrayList<GithubResponseFollow>>

    @GET("users/{username}/following")
    fun following(
        @Path("username") username: String
    ): Call<ArrayList<GithubResponseFollow>>
}