package com.csp.network

import android.text.TextUtils
import android.util.Log
import com.csp.network.data.Err
import com.csp.network.data.Result
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.ParameterizedType
import java.util.concurrent.TimeUnit

abstract class Repository<DATA, SERVICE> {

    val requestService: SERVICE by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        getRetrofit().create(getServiceClass())
    }

    private fun getServiceClass(): Class<SERVICE> {
        var serviceClass: Class<SERVICE>? = null
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            val actualTypeArguments = type.actualTypeArguments;
            if (actualTypeArguments.isNotEmpty()) {
                (actualTypeArguments[1] as? Class<SERVICE>)?.let {
                    serviceClass = it
                }

            }
        }

        return serviceClass?.run {
            return this
        } ?: throw ClassNotFoundException("service not found")

    }

    private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder().apply {
            connectTimeout(60, TimeUnit.SECONDS)//连接 超时时间
            writeTimeout(60, TimeUnit.SECONDS)//写操作 超时时间
            readTimeout(60, TimeUnit.SECONDS)//读操作 超时时间
            retryOnConnectionFailure(true)//错误重连
//            val cacheFile = File(context.cacheDir,"cache")
//            val cache = Cache(cacheFile,1024 *1024 *100)//100Mb
//            cache(cache)

            customHttpInterceptor()?.forEach {
                addInterceptor(it)
            }

            customApplicationInterceptor()?.forEach {
                addNetworkInterceptor(it)
            }
        }

        return builder
    }

    private fun getRetrofit(): Retrofit {
        val builder = getOkHttpClientBuilder()
        // 创建Retrofit
        val client = Retrofit.Builder().client(builder.build())

        customConverterFactory()?.forEach {
            client.addConverterFactory(it)
        }

        customCallAdapter()?.forEach {
            client.addCallAdapterFactory(it)
        }

        return client
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl())
            .build()
    }

    abstract fun baseUrl(): String

    abstract fun serviceClass(): Class<SERVICE>

    abstract fun <T> validateData(data: DATA?): Result<T>

    abstract fun catchException(e: Throwable): Err

    open fun customCallAdapter(): List<CallAdapter.Factory>? {
        return null
    }

    open fun customConverterFactory(): List<Converter.Factory>? {
        return null
    }

    open fun customHttpInterceptor(): List<Interceptor>? {
        return null
    }

    open fun customApplicationInterceptor(): List<Interceptor>? {
        return null
    }

    suspend fun <T> fetchDataBySuspend(call: suspend () -> DATA): Result<T> {
        val response: DATA
        try {
            response = call()
        } catch (e: Throwable) {
            return catchException(e)
        }
        return validateData(response)
    }

    fun <T> fetchDataByCall(
        callback: SimpleCallback<T>,
        call: SERVICE.() -> Call<*>
    ) {
        (call(requestService) as? Call<DATA>)?.enqueue(object : Callback<DATA> {
            override fun onResponse(call: Call<DATA>, response: Response<DATA>) {

                validateData<T>(response.body()).catch {
                    callback.onErr(it.code, it.msg)
                }.onResult {
                    callback.onSuccess(it)
                }
            }

            override fun onFailure(call: Call<DATA>, t: Throwable) {
                val err = catchException(t)
                callback.onErr(err.code, err.msg)
            }
        }) ?: throw Throwable("call type not match")
    }
}