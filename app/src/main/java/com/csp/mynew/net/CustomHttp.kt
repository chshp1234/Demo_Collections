package com.csp.mynew.net

import android.text.TextUtils
import android.util.Log
import com.csp.mynew.net.bean.QA
import com.csp.network.Repository
import com.csp.network.SimpleCallback
import com.csp.network.data.Err
import com.csp.network.data.Result
import com.csp.network.data.Success
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.converter.gson.GsonConverterFactory

/**
 * created by dongdaqing 2022/3/22 2:02 下午
 */
object CustomHttp : Repository<Base<*>, RequestService>() {

    suspend fun getQABySuspendResult(): Result<QA> {
        return fetchDataBySuspendResult {
            requestService.getQAResult()
        }
    }

    suspend fun getQABySuspend(): Result<QA> {
        return fetchDataBySuspend {
            requestService.getQA()
        }
    }

    fun getQA(
        callback: SimpleCallback<QA>
    ) {
        fetchDataByCall(callback) {
            getQACall()
        }
    }

    private suspend fun <T> fetchDataBySuspendResult(call: suspend () -> Result<T>): Result<T> {
        return call()
    }

    override fun baseUrl(): String {
        return "https://wanandroid.com/"
    }

    override fun serviceClass(): Class<RequestService> {
        return RequestService::class.java
    }

    override fun customCallAdapter(): List<CallAdapter.Factory> {
        return listOf(MyResultCallAdapterFactory())
    }

    override fun customHttpInterceptor(): List<Interceptor>? {
        return return listOf(HttpLoggingInterceptor {
            if (TextUtils.isEmpty(it)) {
                return@HttpLoggingInterceptor
            }
            val len: Int = it.length
            var readLen = 0
            while (readLen < len) {
                var end = readLen + 3500
                if (readLen + 3500 > len) {
                    end = len
                }
                Log.d("HttpLoggingInterceptor", it.substring(readLen, end))
                readLen = end
            }
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
    }

    override fun <T> validateData(data: Base<*>?): Result<T> {
        if (data == null) {
            return Err("0000", "body is null")
        }

        if (data.errorCode == 0) {
            (data.data as? T)?.let {
                return Success(it)
            }
        }

        return Err(data.errorCode.toString(), data.errorMsg)
    }

    override fun catchException(e: Throwable): Err {
        return Err("00002", e.message)
    }


    /*fun getTranslate(
        word: String,
    ): LiveData<Result<QA>> {

        return fetchData() {
            requestService.getTranslate(
                mapOf(
                    "word" to word,
                    "uid" to "U202101294007036503493",
                    "type" to "0",
                    "sourceType" to "0"
                )
            )
        }

    }*/

    /*private fun <T> fetchData(
        call: suspend () -> Base<T>
    ): LiveData<Result<T>> {

        return liveData(Dispatchers.IO) {
            latestValue?.let {
                emit(it)
                return@liveData
            }
            val response: Base<T>?

            try {
                response = call()
            } catch (e: Exception) {
                emit(Err("100", e.localizedMessage))
                return@liveData
            }

            response.apply {
                if (code == "1000") {
                    data?.apply {
                        emit(Success(this))
                    } ?: emit(Err("101", "data is null"))
                } else {
                    emit(Err("102", "code!=1000,msg=${response.message}"))
                }
            }
        }
    }*/
}