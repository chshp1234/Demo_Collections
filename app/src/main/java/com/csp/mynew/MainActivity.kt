package com.csp.mynew

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.csp.compose.mediapipe.MediaPipeActivity
import com.csp.compose.ndk.NdkSampleActivity
import com.csp.compose.qalist.QAItem
import com.csp.compose.qalist.QAListActivity
import com.csp.mylocation.MyLocationUtils
import com.csp.mynew.mqtt.PahoExampleActivity
import com.csp.nativelibtest.NativeLib
import com.csp.qrcode.CaptureActivity
import com.example.aidltest.FloatWindowService
import com.jieba.jieba_android.JiebaSegmenter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel


class MainActivity : AppCompatActivity() {

    private val scope = MainScope()

    private val REQUEST_CODE_QRCODE = 10000

    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // onVsync信号执行绘制
        // onCreate -> sendMsg -> onVsync -> sendFrameDisplayEventReceiverMsg -> onResume
        // -> scheduleTraversals ->mChoreographer.postCallback(Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
        // 最后一步中将CALLBACK_TRAVERSAL类型的消息加入内部的mCallbackQueues中，使得之前垂直同步信号的消息可以提前执行到绘制流程
        Handler(mainLooper).post {
            LogUtils.d(findViewById<TextView>(R.id.jiebaa).width)
            LogUtils.d(findViewById<TextView>(R.id.jiebaa).height)
        }

        // performTraversals -> dispatchAttachedToWindow -> executeActions -> run
        findViewById<TextView>(R.id.jiebaa).post {
            LogUtils.d(findViewById<TextView>(R.id.jiebaa).width)
            LogUtils.d(findViewById<TextView>(R.id.jiebaa).height)
        }

//        val resource = resources
//        LogUtils.d(resource)
//
//        val background= BitmapFactory.decodeResource(resource, R.drawable.ic_launcher_background)
//        LogUtils.d(background)
        mainViewModel = ViewModelProvider(this).get()

        mainViewModel.successLiveData.observe(this) { qa ->
            val array = Array(qa.datas.size) {
                QAItem(qa.datas[it].title, qa.datas[it].link)
            }
            val intent = Intent(this, QAListActivity::class.java)
            intent.putExtra("qalist", array)
            startActivity(intent)
            LogUtils.d(qa.datas.toString())
        }

        mainViewModel.errLiveData.observe(this) {
            findViewById<TextView>(R.id.net).text = it.toString()
            LogUtils.d(it.toString())
        }

        findViewById<TextView>(R.id.jiebaa).setOnClickListener {
            val start = System.currentTimeMillis()
            JiebaSegmenter.init(MyApplication.application)

            val sentence = findViewById<EditText>(R.id.jieba_text).text.toString()
            JiebaSegmenter.getDividedStringAsync(sentence) {
                println("耗时{${System.currentTimeMillis() - start}}")
                ToastUtils.showShort(it.toString())
                it?.let {

                    findViewById<EditText>(R.id.jieba_text).post {
                        findViewById<EditText>(R.id.jieba_text).setText(it.toString())
                    }
                }
            }

        }

        findViewById<TextView>(R.id.net).setOnClickListener {
//            mainViewModel.test()
            mainViewModel.getData()
            val frida = checkFrida()
            LogUtils.d("frida=$frida")

//            ActivityUtils.startLauncherActivity("jp.pokemon.pokemonsleep")

            /*Repository.getTranslate("你好").observe(this) {

                it.onResult({
                    findViewById<TextView>(R.id.net).text = it.msg
                    LogUtils.d(it.msg)

                }) {
                    findViewById<TextView>(R.id.net).text = it.result.toString()
                    LogUtils.d(it.result.toString())
                }
            }*/

            /*Repository.getTranslate("你好", scope, object : SimpleCallback<QA> {
                override fun onSuccess(data: QA) {
                    findViewById<TextView>(R.id.net).text = data.toString()
                    LogUtils.d(data)
                }

                override fun onErr(msg: String) {
                    findViewById<TextView>(R.id.net).text = msg
                    LogUtils.d(msg)
                }

            })*/
        }

        findViewById<TextView>(R.id.location).setOnClickListener {

            MyLocationUtils.startLocation {
                ToastUtils.showShort(it.toString())
                if (it.errorCode == 0) {
                    LogUtils.d(it.toString())
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    LogUtils.e("location Error, ErrCode:" + it.errorCode + ", errInfo:" + it.errorInfo)
                }

            }
        }


        findViewById<TextView>(R.id.qrcode).setOnClickListener {
            val intent = Intent(this@MainActivity, CaptureActivity::class.java)
            intent.putExtra("int", 1)
            intent.putExtra("long", 1L)
            intent.putExtra("string", "string")
            intent.putExtra("boolean", true)
            intent.putExtra("double", 1.2)
            LogUtils.d(intent.toString())
            startActivityForResult(intent, REQUEST_CODE_QRCODE)
        }

        findViewById<TextView>(R.id.my_jni).setOnClickListener {
            (it as TextView).text = NativeLib.stringFromJNI()
        }

        findViewById<TextView>(R.id.compose).setOnClickListener {
            val intent = Intent(this, MediaPipeActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.ndk_sample).setOnClickListener {
            ActivityUtils.startActivity(NdkSampleActivity::class.java)
        }

        findViewById<TextView>(R.id.mqtt).setOnClickListener {
            ActivityUtils.startActivity(PahoExampleActivity::class.java)
        }

        findViewById<TextView>(R.id.aidl).setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                ToastUtils.showShort("当前无权限，请授权")
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ),
                    1
                )
            } else {
                val intent = Intent(this, FloatWindowService::class.java)
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtils.d(name.toShortString() + "onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            LogUtils.d(name.toShortString() + "onServiceDisconnected")
        }

        override fun onBindingDied(name: ComponentName) {
            LogUtils.d(name.toShortString() + "onBindingDied")
        }

        override fun onNullBinding(name: ComponentName) {
            LogUtils.d(name.toShortString() + "onNullBinding")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_QRCODE && resultCode == RESULT_OK) {
            val result = data?.getStringExtra(CaptureActivity.KEY_DATA)
            ToastUtils.showLong("qrcode result is $result")
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        unbindService(serviceConnection)
    }

    fun checkFrida(): Int {
        return 1
    }

}