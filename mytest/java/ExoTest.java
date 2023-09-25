package com.example.aidltest;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ReflectUtils;
import com.example.aidltest.base.BaseActivity;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;

import static com.blankj.utilcode.util.ColorUtils.getColor;

public class ExoTest extends BaseActivity {
    @BindView(R.id.video_view)
    PlayerView playerView;

    private ExoPlayer player;
    private ImageButton full;

    @Override
    protected void initData() {}

    @Override
    protected void initView() {
        BarUtils.setStatusBarColor(this, getColor(R.color.transparent));
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        player.setPlayWhenReady(false);
        player.seekTo(0, 0);

        playerView.setControlDispatcher(
                new DefaultControlDispatcher() {
                    @Override
                    public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
                        LogUtils.i("dispatchSetPlayWhenReady: playWhenReady=" + playWhenReady);
                        return super.dispatchSetPlayWhenReady(player, playWhenReady);
                    }

                    @Override
                    public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
                        LogUtils.i(
                                "dispatchSeekTo: windowIndex="
                                        + windowIndex
                                        + "\npositionMs="
                                        + positionMs);
                        return super.dispatchSeekTo(player, windowIndex, positionMs);
                    }

                    @Override
                    public boolean dispatchSetRepeatMode(Player player, int repeatMode) {
                        LogUtils.i("dispatchSetRepeatMode: repeatMode=" + repeatMode);
                        return super.dispatchSetRepeatMode(player, repeatMode);
                    }

                    @Override
                    public boolean dispatchSetShuffleModeEnabled(
                            Player player, boolean shuffleModeEnabled) {
                        LogUtils.i(
                                "dispatchSetShuffleModeEnabled: shuffleModeEnabled="
                                        + shuffleModeEnabled);
                        return super.dispatchSetShuffleModeEnabled(player, shuffleModeEnabled);
                    }

                    @Override
                    public boolean dispatchStop(Player player, boolean reset) {
                        LogUtils.i("dispatchStop: reset=" + reset);
                        return super.dispatchStop(player, reset);
                    }
                });
        playerView.setAspectRatioListener(
                new AspectRatioFrameLayout.AspectRatioListener() {
                    @Override
                    public void onAspectRatioUpdated(
                            float targetAspectRatio,
                            float naturalAspectRatio,
                            boolean aspectRatioMismatch) {
                        LogUtils.i(
                                "onAspectRatioUpdated: targetAspectRatio="
                                        + targetAspectRatio
                                        + "\nnaturalAspectRatio="
                                        + naturalAspectRatio
                                        + "\naspectRatioMismatch="
                                        + aspectRatioMismatch);
                    }
                });
        playerView.setControllerVisibilityListener(
                new PlayerControlView.VisibilityListener() {
                    @Override
                    public void onVisibilityChange(int visibility) {
                        LogUtils.i("onVisibilityChange: visibility=" + visibility);
                    }
                });
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        initializePlayer();

        getFullBtn(playerView);
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this);

            playerView.setPlayer(player);

            player.setPlayWhenReady(true);
            player.seekTo(0, 0);
        }

        // 创建一个mp4媒体文件
        Uri uri =
                Uri.parse(
                        "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
    }

    private void getFullBtn(PlayerView playerView) {
        /*View control = ReflectUtils.reflect(playerView).field("controller").get();
        full = control.findViewById(R.id.exo_full);
        full.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPortrait) {
                            changeToLandscape();
                        } else {
                            changeToPortrait();
                        }
                    }
                });*/
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(
                        new DefaultHttpDataSourceFactory("exoplayer-codelab"))
                .createMediaSource(uri);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.test_exo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    /*    private synchronized void changeOrientation(
            @OnOrientationChangedListener.SensorOrientationType int orientation) {

        if (orientationListener == null) {
            return;
        }
        // 执行回调
        orientationListener.onOrientationChanged(orientation);

        switch (orientation) {
            case SENSOR_PORTRAIT:
                // 竖屏
                setPortrait(true);
                showSystemStatusUi();
                break;
            case SENSOR_LANDSCAPE:
                // 横屏
                setPortrait(false);
                showSystemStatusUi();
                break;
            case SENSOR_UNKNOWN:
            default:
                break;
        }
    }*/

    boolean isPortrait = true;
    long currentSeek;

    // 切换竖屏
    private void changeToPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        Window window = getWindow();
        window.setAttributes(attr);
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        isPortrait = true;
        currentSeek = player.getCurrentPosition();
        player.seekTo(currentSeek);
        //        rlTitle.setVisibility(View.VISIBLE);
        //        llOthersAlbumPlay.setVisibility(View.VISIBLE);
    }

    // 切换横屏
    private void changeToLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        Window window = getWindow();
        window.setAttributes(lp);
        // 隐藏显示状态栏时不改变原有布局
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        isPortrait = false;
        currentSeek = player.getCurrentPosition();
        player.seekTo(currentSeek);
        //        rlTitle.setVisibility(View.GONE);
        //        llOthersAlbumPlay.setVisibility(View.GONE);
    }

    public interface OnOrientationChangedListener {
        int SENSOR_UNKNOWN = -1;
        int SENSOR_PORTRAIT = SENSOR_UNKNOWN + 1;
        int SENSOR_LANDSCAPE = SENSOR_PORTRAIT + 1;

        @IntDef({SENSOR_UNKNOWN, SENSOR_PORTRAIT, SENSOR_LANDSCAPE})
        @Retention(RetentionPolicy.SOURCE)
        @interface SensorOrientationType {}

        void onOrientationChanged(@SensorOrientationType int orientation);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isPortrait) {
                finish();
            } else {
                changeToPortrait();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
