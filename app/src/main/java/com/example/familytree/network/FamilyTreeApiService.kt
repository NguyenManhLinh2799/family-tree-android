package com.example.familytree.network

import com.example.familytree.domain.User
import com.example.familytree.network.auth.*
import com.example.familytree.network.contributor.ContributorRequest
import com.example.familytree.network.member.AddChildMemberRequest
import com.example.familytree.network.member.Member
import com.example.familytree.network.contributor.FilterUsersRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
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
    suspend fun getTrees(@Header("Authorization") token: String): NetworkTreeListContainer

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

    @POST("person-management/person/{personId}/parent")
    suspend fun addParent(@Path("personId") id: Int, @Header("Authorization") token: String, @Body parentMember: Member)

    @POST("person-management/person/{personId}/spouse")
    suspend fun addSpouse(@Path("personId") id: Int, @Header("Authorization") token: String, @Body spouseMember: Member)

    @POST("person-management/person/child")
    suspend fun addChild(@Header("Authorization") token: String, @Body newChildMember: AddChildMemberRequest)

    @PUT("person-management/person/{personId}")
    suspend fun editPerson(@Path("personId") id: Int, @Header("Authorization") token: String, @Body editedMember: Member)

    @DELETE("person-management/person/{personId}")
    suspend fun deletePerson(@Path("personId") id: Int, @Header("Authorization") token: String)



    // Tree members
    @GET("tree-management/tree/{treeId}")
    suspend fun getTreeMembers(@Path("treeId") id: Int?, @Header("Authorization") token: String): ApiResponse<TreeMembers>



    // Tree contributor
    @GET("tree-management/tree/{treeId}/editors")
    suspend fun getEditors(@Path("treeId") id: Int?, @Header("Authorization") token: String): NetworkContributorListContainer

    @POST("user-management/users")
    suspend fun filterUsers(@Body filterRequest: FilterUsersRequest?): ApiResponse<List<NetworkUser>>

    @POST("tree-management/tree/{treeId}/add-users-to-editor")
    suspend fun addEditors(@Path("treeId") treeID: Int?, @Header("Authorization") token: String, @Body contributorRequest: ContributorRequest)

    @POST("tree-management/tree/{treeId}/remove-users-from-editor")
    suspend fun removeEditors(@Path("treeId") treeID: Int?, @Header("Authorization") token: String, @Body contributorRequest: ContributorRequest)



    // Upload image
    @Multipart
    @POST("file-upload/image")
    suspend fun uploadImage(@Header("Authorization") token: String, @Part file: MultipartBody.Part): ApiResponse<String>



    // User
    @GET("user-management/user-by-token")
    suspend fun getProfile(@Header("Authorization") token: String): ApiResponse<NetworkUser>

    @PUT("user-management/user")
    suspend fun editProfile(@Header("Authorization") token: String, @Body editedProfile: EditProfileRequest)

}

object FamilyTreeApi {
    val retrofitService: FamilyTreeApiService by lazy { retrofit.create(FamilyTreeApiService::class.java) }
}