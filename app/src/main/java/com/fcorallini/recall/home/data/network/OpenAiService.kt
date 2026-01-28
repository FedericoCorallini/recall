package com.fcorallini.recall.home.data.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * Retrofit interface for OpenAI API endpoints
 */
interface OpenAiService {

    @Multipart
    @POST("v1/files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("purpose") purpose: RequestBody
    ): FileUploadResponse

    @POST("v1/responses")
    suspend fun createResponse(@Body req: ResponsesRequest): ResponsesResponse

}
