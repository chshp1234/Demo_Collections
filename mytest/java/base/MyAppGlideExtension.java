package com.example.aidltest.base;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.annotation.GlideType;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;

import static com.bumptech.glide.request.RequestOptions.decodeTypeOf;

/**
 * @author csp
 * @date 2017/10/20
 */
@GlideExtension
public class MyAppGlideExtension {

    private static RequestOptions DECODE_TYPE_GIF = decodeTypeOf(GifDrawable.class).lock();
    private static final int DEFAULT_MINI_THUMB_SIZE = 100;

    private MyAppGlideExtension() {}

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> miniThumb(@NonNull BaseRequestOptions<?> options) {
        return miniThumb(options, DEFAULT_MINI_THUMB_SIZE);
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> miniThumb(
            @NonNull BaseRequestOptions<?> options, int size) {
        return options.fitCenter().override(size);
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> roundCorner(@NonNull BaseRequestOptions<?> options) {
        return options.transform(new RoundedCorners(SizeUtils.dp2px(5)));
    }

    @GlideOption
    @NonNull
    public static BaseRequestOptions<?> roundCorner(
            @NonNull BaseRequestOptions<?> options, float size) {
        return options.transform(new RoundedCorners(SizeUtils.dp2px(size)));
    }

    @GlideType(GifDrawable.class)
    @NonNull
    public static RequestBuilder<GifDrawable> asMyGif(
            @NonNull RequestBuilder<GifDrawable> requestBuilder) {
        return requestBuilder.transition(new DrawableTransitionOptions()).apply(DECODE_TYPE_GIF);
    }
}
