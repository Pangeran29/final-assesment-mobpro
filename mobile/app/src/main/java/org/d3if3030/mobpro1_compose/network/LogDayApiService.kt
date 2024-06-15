package org.d3if3030.mobpro1_compose.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.d3if3030.mobpro1_compose.model.LogDay
import org.d3if3030.mobpro1_compose.model.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

private const val BASE_URL = "http://20.2.250.135/"

val logging = HttpLoggingInterceptor().apply {
    setLevel(HttpLoggingInterceptor.Level.BODY)
}

val httpClient = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val moshi = Moshi
    .Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit
    .Builder()
    .client(httpClient)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface LogDayApiService {
    @GET("file-upload/{upload_by}")
    suspend fun getLogDay(@Path("upload_by") uploadBy: String): Response<List<LogDay>>

    @Multipart
    @POST("file-upload")
    suspend fun postLogDay(
        @Part file: MultipartBody.Part,
        @Part("upload_by") uploadBy: RequestBody,
        @Part("description") description: RequestBody,
    ): Response<LogDay>

    @DELETE("file-upload/{id}")
    suspend fun deleteLogDay(@Path("id") id: Int): Response<LogDay>
}

object LogDayApi {
    val service: LogDayApiService by lazy {
        retrofit.create(LogDayApiService::class.java)
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }