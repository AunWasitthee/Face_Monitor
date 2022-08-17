package com.fg.mdp.facemonitor.connections

import com.fg.mdp.facemonitor.model.ImageBgModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {

    //GetBg
    @Headers("Content-Type: application/json")
    @POST("get_bg")
    fun APIgetBg(): Observable<Response<ImageBgModel>>
}