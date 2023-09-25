package com.example.aidltest;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class TestBannerAdapter<T> extends BannerAdapter<T, TestBannerAdapter.TestBannerViewHolder> {

    public TestBannerAdapter(List<T> datas) {
        // 设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(datas);
    }

    // 创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public TestBannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        // 注意，必须设置为match_parent，这个是viewpager2强制要求的
        imageView.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new TestBannerViewHolder(imageView);
    }

    @Override
    public void onBindView(
            TestBannerAdapter.TestBannerViewHolder holder, T data, int position, int size) {}

    public static class TestBannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public TestBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}
