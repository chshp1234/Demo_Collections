package com.csp.mynew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.LogUtils
import com.csp.mynew.net.CustomHttp
import com.csp.mynew.net.bean.QA
import com.csp.network.data.Err
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * created by dongdaqing 2021/9/23 5:12 下午
 */
class MainViewModel : ViewModel() {

    private val onDataSuccess: MutableLiveData<QA> = MutableLiveData()
    private val onDataErr: MutableLiveData<Err> = MutableLiveData()

    val successLiveData: LiveData<QA> = onDataSuccess
    val errLiveData: LiveData<Err> = onDataErr

    fun getData() {
        viewModelScope.launch {

            //三种方式加载网络
            val result = CustomHttp.getQABySuspendResult()
//
//            result.catch { onDataErr.value = it }
//                .onResult { onDataSuccess.value = it }

            result.onResult({
                onDataErr.value = it
            }) {
                onDataSuccess.value = it
            }

            /*CustomHttp.getQA(object : SimpleCallback<QA> {
                override fun onSuccess(data: QA) {
                    ToastUtils.showShort(data.toString())
                }

                override fun onErr(code: String, msg: String?) {
                    ToastUtils.showShort(msg)
                }
            })*/
        }
    }

    fun test() {
        LogUtils.d("test start")
        viewModelScope.launch {
            LogUtils.d("coroutine start")
//            launch {
//                block()
//            }
            launch {
                changeContext()
            }
            delay(1000)
            LogUtils.d("coroutine end")
        }
        LogUtils.d("test end")
    }

    suspend fun block(): Int {
        LogUtils.d("block start")
        //模拟阻塞
        Thread.sleep(3000)
        LogUtils.d("block end")
        return 3
    }

    suspend fun changeContext() {
        withContext(Dispatchers.IO) {
            LogUtils.d("current Thread=${Thread.currentThread()}")
            val result = async {
                withContext(Dispatchers.IO) {
                    LogUtils.d("current Thread=${Thread.currentThread()}")
                    block()
                }
            }

            launch {
                withContext(Dispatchers.IO) {
                    LogUtils.d("current Thread=${Thread.currentThread()}")
                }
            }

            LogUtils.d("current Thread=${Thread.currentThread()} compute result")
            LogUtils.d("current Thread=${Thread.currentThread()} result=${result.await()}")

            flow<Int> {

            }.catch { }
                .collect { }
        }
    }
}