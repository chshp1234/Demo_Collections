package com.example.aidltest.base.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * @author csp
 * @date 2017/10/18
 */
public abstract class MyAdapter<T, K extends SubViewHolder> extends BaseAdapter<T, K> {

    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;
    protected Context mContext;

    private View.OnClickListener onClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() instanceof BaseLoadAdapter.TagClick
                            && mOnItemClickListener != null) {
                        int position = ((BaseLoadAdapter.TagClick) v.getTag()).getPosition();
                        mOnItemClickListener.onItemClick(v, position);
                    }
                }
            };

    private View.OnLongClickListener onLongClickListener =
            new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v.getTag() instanceof BaseLoadAdapter.TagClick
                            && mOnItemLongClickListener != null) {
                        int position = ((BaseLoadAdapter.TagClick) v.getTag()).getPosition();
                        mOnItemLongClickListener.onItemLongClick(v, position);
                    }
                    return true;
                }
            };

    public MyAdapter(Context context) {
        this.mContext = context;
    }

    public MyAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    @NonNull
    @Override
    public K onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(getContentView(), parent, false);

        return createViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull K holder, int position) {

        setViewTagClick(holder.itemView, position);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(onLongClickListener);

        onBindItemViewHolder(holder, position);
    }

    /**
     * 设置Viewtag，用于点击事件 Set view tag clic.
     *
     * @param view the view
     * @param position the position
     */
    protected void setViewTagClick(View view, int position) {
        view.setTag(new BaseLoadAdapter.TagClick(position));
    }

    /**
     * item布局ID
     *
     * @return item布局ID
     */
    public abstract int getContentView();

    /**
     * 给view中的控件设置数据
     *
     * @param itemHolder itemHolder
     * @param position 当前item在当前的相对位置
     */
    protected abstract void onBindItemViewHolder(K itemHolder, int position);

    public abstract K createViewHolder(View view);

    public OnItemClickListener getmOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public OnItemLongClickListener getmOnItemLongClickListener() {
        return mOnItemLongClickListener;
    }

    public void setmOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }

    /** 使用handler类排队，等待recyclerview 更新结束之后再刷新。 */
    public void specialUpdate() {
        Handler handler = new Handler();
        final Runnable r = this::notifyDataSetChanged;
        handler.post(r);
    }
}
