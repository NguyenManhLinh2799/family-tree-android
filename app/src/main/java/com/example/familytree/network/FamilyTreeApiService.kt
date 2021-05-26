package com.example.familytree.network

import com.example.familytree.network.auth.LoginRequest
import com.example.familytree.network.auth.NetworkAuthContainer
import com.example.familytree.network.auth.RegisterRequest
import com.example.familytree.network.member.AddChildMemberRequest
import com.example.familytree.network.member.Member
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
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

    // Auth
    @POST("authentication/register")
    suspend fun register(@Body registerRequestBody: RegisterRequest): NetworkAuthContainer

    @POST("authentication/login")
    suspend fun login(@Body loginRequestBody: LoginRequest): Response<NetworkAuthContainer>


    // Family tree
    @GET("tree-management/trees/list")
    suspend fun getTrees(@Header("Authorization") token: String): NetworkTreeContainer

    @Headers("Content-Type: application/json")
    @POST("tree-management/tree")
    suspend fun addTree(@Header("Authorization") token: String, @Body newTree: NetworkTree)

    @DELETE("tree-management/tree/{treeId}")
    suspend fun deleteTree(@Path("treeId") id: Int?, @Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @PUT("tree-management/tree/{treeId}")
    suspend fun editTree(@Path("treeId") id: Int?, @Header("Authorization") token: String, @Body editedTree: NetworkTree)


    // Member
    @GET("person-management/person/{personId}")
    suspend fun getPerson(@Path("personId") id: Int, @Header("Authorization") token: String): ApiResponse<Member>

    @POST("person-management/person/child")
    suspend fun addChild(@Header("Authorization") token: String, @Body newChildMember: AddChildMemberRequest)

    @PUT("person-management/person/{personId}")
    suspend fun editPerson(@Path("personId") id: Int, @Header("Authorization") token: String, @Body editedMember: Member)


    // Tree members
    @GET("tree-management/tree/{treeId}")
    suspend fun getTreeMembers(@Path("treeId") id: Int?, @Header("Authorization") token: String): ApiResponse<TreeMembers>
}

object FamilyTreeApi {
    val retrofitService: FamilyTreeApiService by lazy { retrofit.create(FamilyTreeApiService::class.java) }
}