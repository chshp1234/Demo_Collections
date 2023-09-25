package com.example.aidltest;

import android.media.AudioManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.aidltest.base.BaseActivity;

import java.io.IOException;
import java.net.InetAddress;

import butterknife.BindView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class TestIjkActivity extends BaseActivity implements SurfaceHolder.Callback {
    @Override
    protected void initData() {}

    @BindView(R.id.test_media)
    SurfaceView testMedia;

    private IjkMediaPlayer ijkMediaPlayer;

    @Override
    protected void initView() {
        testMedia.getHolder().addCallback(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.test_ijk;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createPlayer();
        ijkMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (testMedia != null) {
            testMedia.getHolder().removeCallback(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }

    private void createPlayer() {
        if (ijkMediaPlayer == null) {
            ijkMediaPlayer = new IjkMediaPlayer();
            ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                ijkMediaPlayer.setDataSource("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ijkMediaPlayer.prepareAsync();
        }
    }

    private void release() {
        if (ijkMediaPlayer != null) {
            ijkMediaPlayer.stop();
            ijkMediaPlayer.release();
            ijkMediaPlayer = null;
        }
        IjkMediaPlayer.native_profileEnd();
    }
}
