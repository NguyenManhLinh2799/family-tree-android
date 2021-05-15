package com.example.familytree.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://family-tree.azurewebsites.net/api/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface FamilyTreeApiService {

    // Family tree
    @GET("tree-management/tree")
    suspend fun getTrees(): NetworkTreeContainer

    @Headers("Content-Type: application/json")
    @POST("tree-management/tree")
    suspend fun addTree(@Body newTree: NetworkTree)

    @DELETE("tree-management/tree/{treeId}")
    suspend fun deleteTree(@Path("treeId") id: Int?)

    @Headers("Content-Type: application/json")
    @PUT("tree-management/tree/{treeId}")
    suspend fun editTree(@Path("treeId") id: Int?, @Body editedTree: NetworkTree)


    // Member
    @GET("person-management/person/{personId}")
    suspend fun getPerson(@Path("personId") id: Int): ApiResponse<Member>

    // Tree members
    @GET("tree-management/tree/{treeId}")
    suspend fun getTreeMembers(@Path("treeId") id: Int): ApiResponse<TreeMembers>
}

object FamilyTreeApi {
    val retrofitService: FamilyTreeApiService by lazy { retrofit.create(FamilyTreeApiService::class.java) }
}