//package com.example.aidltest.utils;
//
//import java.io.File;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.OkHttpClient;
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//import retrofit2.Retrofit;
//import retrofit2.http.GET;
//import retrofit2.http.Header;
//import retrofit2.http.Streaming;
//import retrofit2.http.Url;
//
//class NetDownloadUtils private constructor() {
//
//    companion object {
//        private const val TAG = "NetDownload"
//
//        // 下载的时候保存为临时文件，下载成功后取消这个后缀
//        private const val DEFAULT_DOWNLOAD_SUFFIX = ".download"
//        private var mDownloadApi = NetworkHelper().createNetwork()
//    }
//
//    private lateinit var url: String
//    private lateinit var filePath: String
//    private var onProgressUpdateCallback: ((Int) -> Unit)? = null
//
//    private var call:Call<ResponseBody>? = null
//
//    private var isCancel = false
//
//    /**
//     * 开始下载
//     */
//    fun start(): Boolean {
//        Log.i(TAG, "start download: $url")
//        val downloadFilePath = filePath + DEFAULT_DOWNLOAD_SUFFIX
//        val file = File(downloadFilePath)
//        // 断点下载，获取从什么地方开始下载
//        val startLength = if (file.exists()) {
//            file.length()
//        } else {
//            0L
//        }
//        Log.i(TAG, "download start length: $startLength")
//        mDownloadApi = NetworkHelper().createNetwork()
//        call = mDownloadApi.download("bytes=$startLength-", url)
//        val response = call?.execute()
//        val body = response?.body()
//        // 如果返回不为空，则保存文件，保存完成后返回下载结果。
//        // 因为如果取消下载，也会走到这里，所有返回结果如果没有 cancel，即为成功
//        return body?.let {
//            saveFile(body, filePath, downloadFilePath)
//            Log.i(TAG, "download save file, finish. result: ${!isCancel}")
//            !isCancel
//        } ?: let {
//            Log.w(TAG, "download failed! Error body: ${response?.errorBody() ?: "unknown"}")
//            false
//        }
//    }
//
//    /**
//     * 取消下载
//     */
//    fun cancel() {
//        isCancel = true
//        call?.cancel()
//    }
//
//    private fun saveFile(body: ResponseBody, filePath: String, downloadFilePath: String) {
//        val inputStream = body.byteStream()
//        val buffer = ByteArray(2048)
//
//        // 创建下载的文件的 File 对象
//        val downloadFile = createDownloadFile(downloadFilePath)
//
//        val fos = FileOutputStream(downloadFile, true)
//        // 总共要下载的长度
//        val downloadFileSize = body.contentLength() + downloadFile.length()
//        var lastProgress = -1
//        var read: Int
//        while (inputStream.read(buffer).also { read = it } != -1) {
//            if (!isCancel) {
//                fos.write(buffer, 0, read)
//                lastProgress = notifyProgressUpdated(downloadFile, downloadFileSize, lastProgress)
//            } else {
//                break
//            }
//        }
//        fos.flush()
//        fos.close()
//        inputStream.close()
//        // 如果不是因为取消完成下载的，才会重命名
//        if (!isCancel) {
//            rename(downloadFile, filePath)
//        }
//    }
//
//    private fun notifyProgressUpdated(downloadFile: File, downloadFileSize: Long, lastProgress: Int): Int {
//        val currentLength = downloadFile.length().toFloat()
//        val progress = ((currentLength / downloadFileSize) * 100).toInt()
//        if (progress != lastProgress && !isCancel) {
//            onProgressUpdateCallback?.invoke(progress)
//        }
//        return progress
//    }
//
//    private fun createDownloadFile(downloadFilePath: String):File {
//        val file = File(downloadFilePath)
//        val dir = File(file.parent)
//        if (!dir.exists()) {
//            dir.mkdirs()
//        }
//        return file
//    }
//
//    class Builder {
//        private val netDownload = NetDownload()
//        fun url(url: String): Builder = apply {
//            netDownload.url = url
//        }
//
//        fun filePath(filePath: String): Builder = apply {
//            netDownload.filePath = filePath
//        }
//
//        fun onProgressUpdate(callback: (Int) -> Unit) = apply {
//            netDownload.onProgressUpdateCallback = callback
//        }
//
//        fun create(): NetDownload = netDownload
//    }
//
//    class NetworkHelper {
//
//        fun createNetwork(): ApiService {
//            val retrofit = Retrofit.Builder()
//                    .client(createOkHttp())
//                    .baseUrl("http://baidu.com")
//                    .build()
//            return retrofit.create(ApiService::class.java)
//        }
//
//        private fun createOkHttp(): OkHttpClient
//
//        {
//            return OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .callTimeout(1, TimeUnit.MINUTES)
//                    .readTimeout(1, TimeUnit.MINUTES)
//                    .writeTimeout(1, TimeUnit.MINUTES)
//                    .build()
//        }
//
//        interface ApiService {
//            @GET
//            @Streaming
//            fun download(@Header("RANGE")startPosition: String, @Url url: String): Call<ResponseBody>
//        }
//    }
//}