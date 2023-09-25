package com.example.aidltest;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.example.aidltest.base.BaseActivity;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NotificationActivity extends BaseActivity {

    Uri uri;

    @Override
    protected void initData() {
        uri = getIntent().getData();
        if (uri != null) {
            // 完整的url信息
            String url = uri.toString();
            Log.i(TAG, "url:" + uri);

            // scheme部分
            String scheme = uri.getScheme();
            Log.i(TAG, "scheme:" + scheme);

            // host部分
            String host = uri.getHost();
            Log.i(TAG, "host:" + host);

            // port部分
            int port = uri.getPort();
            Log.i(TAG, "port:" + port);

            // 访问路劲
            String path = uri.getPath();
            Log.i(TAG, "path:" + path);

            List<String> pathSegments = uri.getPathSegments();

            // Query部分
            String query = uri.getQuery();
            Log.i(TAG, "query:" + query);

            // 获取指定参数值
            String success = uri.getQueryParameter("query2");
            Log.i(TAG, "query2:" + success);

            if (!StringUtils.isTrimEmpty(query)) {

                String[] split = query.split("&");
                for (String q : split) {
                    Log.i(TAG, "q:" + q);
                }
            }
        } else {
            Log.w(TAG, "uri is null");
        }
    }

    @Override
    protected void initView() {
        initMedia();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification;
    }

    @BindView(R.id.test_media)
    SurfaceView testMedia;

    MediaPlayer mediaPlayer = new MediaPlayer();
    SurfaceHolder surfaceHolder;

    boolean isPrepare;
    boolean isPlay;
    boolean isBuffer;
    boolean isEnd;

    private void initMedia() {
        mediaPlayer.setOnPreparedListener(
                new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        LogUtils.d("onPrepared: a");
                        mp.start();
                        isPrepare = true;
                        isPlay = true;
                    }
                });
        mediaPlayer.setOnSeekCompleteListener(
                new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        LogUtils.d("onSeekComplete: a");
                    }
                });
        mediaPlayer.setOnBufferingUpdateListener(
                new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if (percent == 100) {
                            isBuffer = true;
                        }
                        LogUtils.d("onBufferingUpdate: percent=" + percent);
                    }
                });
        mediaPlayer.setOnCompletionListener(
                new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        isEnd = true;
                        LogUtils.d("onCompletion: a");
                    }
                });
        mediaPlayer.setOnVideoSizeChangedListener(
                new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        LogUtils.d("onVideoSizeChanged: width=" + width + " height=" + height);
                    }
                });
        mediaPlayer.setOnErrorListener(
                new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        LogUtils.e("onError: what=" + what + " extra=" + extra);
                        return false;
                    }
                });
        surfaceHolder = testMedia.getHolder();
        surfaceHolder.addCallback(
                new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {
                        if (mediaPlayer != null) {
                            mediaPlayer.setDisplay(holder);
                        }
                    }

                    @Override
                    public void surfaceChanged(
                            SurfaceHolder holder, int format, int width, int height) {}

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        if (null != mediaPlayer) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                    }
                });
    }

    @OnClick(R.id.test_media)
    public void testMedia(View view) {
        try {
            if (isBuffer && isEnd) {
//                mediaPlayer.seekTo(0);
                mediaPlayer.start();
                isPlay = true;
                isEnd = false;
                return;
            }
            if (!isPrepare) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(
                        "http://v3-dy-x.ixigua.com/4503e300ebefe091f689159ffe743ded/5d809dc9/video/m/220e00fdfdec57b4e68b51a3289faa0145811637ec380000492fc250f0ee/?a=1128&br=1704&cr=0&cs=0&dr=0&ds=3&er=&l=20190917154754010010064080333AD6&lr=aweme&rc=anc8OG5md3Q2bzMzPGkzM0ApaDVkODQ5OjtkN2c7ZWg5NWdsY3BlMnFwZHNfLS1iLS9zc2NhMS0uYGBgNS8xNl4uLjE6Yw%3D%3D");

                mediaPlayer.prepareAsync();
            } else if (isPlay) {
                isPlay = false;
                mediaPlayer.pause();
            } else {
                isPlay = true;
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @OnClick({R.id.sendChatMsg, R.id.sendSubscribeMsg})
    public void onClick(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        switch (view.getId()) {
            case R.id.sendChatMsg:
                Notification notificationChat =
                        new NotificationCompat.Builder(this, "chat")
                                .setContentTitle("收到一条聊天消息")
                                .setContentText("今天中午吃什么？")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setLargeIcon(
                                        BitmapFactory.decodeResource(
                                                getResources(), R.mipmap.ic_launcher_round))
                                .setAutoCancel(true)
                                .build();
                manager.notify(1, notificationChat);
                break;
            case R.id.sendSubscribeMsg:
                Notification notificationSubscribe =
                        new NotificationCompat.Builder(this, "subscribe")
                                .setContentTitle("收到一条订阅消息")
                                .setContentText("地铁沿线30万商铺抢购中！")
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.mipmap.ic_launcher_round)
                                .setLargeIcon(
                                        BitmapFactory.decodeResource(
                                                getResources(), R.mipmap.ic_launcher_round))
                                .setAutoCancel(true)
                                .build();
                manager.notify(2, notificationSubscribe);
                break;
        }
    }
}
