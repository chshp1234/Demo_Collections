package com.example.aidltest.CustomView.exo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;

import com.example.aidltest.R;
import com.google.android.exoplayer2.ui.PlayerControlView;

public class ExoVideoPlayBackControlView extends PlayerControlView {

    private ImageButton full;

    public ExoVideoPlayBackControlView(Context context) {
        this(context,null);
    }

    public ExoVideoPlayBackControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ExoVideoPlayBackControlView(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,attrs);
    }

    public ExoVideoPlayBackControlView(
            Context context,
            @Nullable AttributeSet attrs,
            int defStyleAttr,
            @Nullable AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr, playbackAttrs);
        full = findViewById(R.id.exo_full);
    }


}
