package com.fg.mdp.facemonitor.connections

import com.fg.mdp.facemonitor.config.MsgProperties
import com.fg.mdp.facemonitor.model.ImageBgModel
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CallAPIService {

    init {
        println("This ($this) is a singleton")

    }

    private object Holder {
        val INSTANCE = CallAPIService()
    }

    companion object {
//        lateinit var retrofit: Retrofit

        val instance: CallAPIService by lazy { Holder.INSTANCE }

        /**
        internal var OKHttp: Create? = null internal
        var with: client? = null internal
        var Interceptor: Logging? = null.
         *
        internal var production: On? = null internal
        var have: not? = null internal
        var interceptor: add? = null
         *
         * @ return OkHttpClient
         */
        private fun createClient(): OkHttpClient {
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
//        client.interceptors().add(AddCookiesInterceptor())
            return client.build()
        }

        private var retrofit = Retrofit.Builder().baseUrl(MsgProperties.URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient())
            .build()

        private var apiretrofit = retrofit.create(APIService::class.java)

    }

    private fun getService(): APIService {
        return apiretrofit
    }



    fun requesGetBg(): Observable<Response<ImageBgModel>> {
        var obb = getService()
            .APIgetBg()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())

        return obb
    }
}