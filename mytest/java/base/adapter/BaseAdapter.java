package com.example.aidltest.base.adapter;

import android.os.Handler;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author csp
 * @date 2017/12/25
 */
public abstract class BaseAdapter<T, K extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<K> {
    protected List<T> mList = new ArrayList<>();

    public void updateData(List<T> dataSet) {
        this.mList.clear();
        appendData(dataSet);
    }

    public void appendData(List<T> dataSet) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (dataSet != null && !dataSet.isEmpty()) {
            int startIndex = getItemCount() > 0 ? getItemCount() - 1 : 0;
            int oldCount = this.mList.size();
            this.mList.addAll(dataSet);
            //            notifyItemRangeChanged(startIndex, this.mList.size() - oldCount);
            asyncNotifyAllDate();
        }
    }

    public List<T> getDataSet() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void asyncNotifyItemDate(int position) {
        Handler handler = new Handler();
        handler.post(() -> notifyItemChanged(position));
    }

    public void asyncNotifyAllDate() {
        Handler handler = new Handler();
        final Runnable r = this::notifyDataSetChanged;
        handler.post(r);
    }

    public void asyncNotifyItemInsert(int position) {
        Handler handler = new Handler();
        final Runnable r =
                () -> {
                    notifyItemInserted(position);
                    notifyItemRangeChanged(position, mList.size() - position);
                };
        handler.post(r);
    }

    public void asyncNotifyItemRemove(int position) {
        Handler handler = new Handler();
        final Runnable r =
                () -> {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size() - position);
                };
        handler.post(r);
    }

    public void asyncSmoothScroll(RecyclerView recyclerView, int position) {
        Handler handler = new Handler();
        final Runnable r =
                () -> {
                    recyclerView.smoothScrollToPosition(position);
                };
        handler.post(r);
    }
}
