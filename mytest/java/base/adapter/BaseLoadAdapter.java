package com.example.aidltest.base.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.aidltest.MyApplication;
import com.example.aidltest.R;
import com.example.aidltest.base.adapter.BaseAdapter;
import com.example.aidltest.base.adapter.SubViewHolder;

import java.util.List;

/**
 * @author csp
 * @date 2017/12/25
 */
public abstract class BaseLoadAdapter<T> extends BaseAdapter<T, SubViewHolder> {

    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;

    protected LoadMoreListener listener;

    public static final int TYPE_ITEM = 1;
    public static final int TYPE_BOTTOM = 2;

    public int loadState;
    public static final int STATE_LOADING = 101;
    public static final int STATE_LASTED = 102;
    public static final int STATE_ERROR = 103;
    public static final int STATE_LOADING_SUCCESS = 104;
    public static final int STATE_GONE = 105;

    protected Context mContext;

    public boolean hasMore = true;
    public boolean isLoading = false;
    private View.OnClickListener onClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() instanceof TagClick && mOnItemClickListener != null) {
                        int position = ((TagClick) v.getTag()).getPosition();
                        mOnItemClickListener.onItemClick(v, position);
                    }
                }
            };

    private View.OnLongClickListener onLongClickListener =
            new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (v.getTag() instanceof TagClick && mOnItemLongClickListener != null) {
                        int position = ((TagClick) v.getTag()).getPosition();
                        mOnItemLongClickListener.onItemLongClick(v, position);
                    }
                    return true;
                }
            };

    /** 每一页和后台说定的条数 */
    private int pageCount;

    public BaseLoadAdapter(Context context, LoadMoreListener listener, int pageCount) {
        mContext = context;
        this.pageCount = pageCount;
        this.listener = listener;
    }

    public BaseLoadAdapter(Context context, LoadMoreListener listener) {
        mContext = context;
        this.listener = listener;
    }

    public BaseLoadAdapter(Context context, int pageCount) {
        mContext = context;
        this.pageCount = pageCount;
    }

    public BaseLoadAdapter(Context context) {
        mContext = context;
        this.pageCount = 10;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            return new SubViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.footer_view, parent, false));
        } else {
            View view = LayoutInflater.from(mContext).inflate(getContentView(), parent, false);

            return createViewHolder(view);
        }
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
     * @param holder itemHolder
     * @param position 当前item在当前的相对位置
     */
    protected abstract void onBindItemViewHolder(SubViewHolder holder, int position);

    /**
     * Create view holder sub view holder.
     *
     * @param view the view
     * @return the sub view holder
     */
    public abstract SubViewHolder createViewHolder(View view);

    /**
     * 设置Viewtag，用于点击事件 Set view tag clic.
     *
     * @param view the view
     * @param position the position
     */
    protected void setViewTagClick(View view, int position) {
        view.setTag(new TagClick(position));
    }

    @Override
    public void onBindViewHolder(final SubViewHolder holder, int position) {
        if (TYPE_BOTTOM == getItemViewType(position)) {

            final View leftLine = holder.getView(R.id.left_line);
            final View rightLine = holder.getView(R.id.right_line);
            final ProgressBar progressBar = holder.getView(R.id.progress_bar);
            final TextView bottomTextView = holder.getView(R.id.progress_text);

            switch (loadState) {
                case STATE_LOADING_SUCCESS:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("继续滑动加载更多");
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATE_LOADING:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("加载中...");
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATE_LASTED:
                    leftLine.setVisibility(View.VISIBLE);
                    rightLine.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("我也是有底线的~");
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATE_ERROR:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("加载出错！");
                    holder.itemView.setOnClickListener(
                            v -> {
                                //
                                // progressBar.setVisibility(View.VISIBLE);
                                //                                bottomTextView.setText("加载中...");
                                listener.loadMoreData();
                            });
                    break;
                case STATE_GONE:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.GONE);
                default:
                    break;
            }
        } else {
            setViewTagClick(holder.itemView, position);
            holder.itemView.setOnClickListener(onClickListener);
            holder.itemView.setOnLongClickListener(onLongClickListener);
            onBindItemViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        /*if (mList.size() < pageCount) {
            return TYPE_ITEM;
        } else {*/
        if (position == mList.size()) {
            return TYPE_BOTTOM;
        } else {
            return TYPE_ITEM;
        }
        //        }
    }

    @Override
    public int getItemCount() {
        /*if (mList.size() < pageCount) {
            hasMore = false;
            return mList.size();
        } else {*/
        return mList.size() + 1;
        //        }
    }

    public void setErrorStatus() {
        loadState = STATE_ERROR;
        hasMore = true;
        asyncNotifyItemDate(getItemCount() - 1);
        setLoading(false);
    }

    public void setLastedStatus() {
        loadState = STATE_LASTED;
        hasMore = false;
        asyncNotifyItemDate(getItemCount() - 1);
    }

    public void setLoadingGoneStatus() {
        loadState = STATE_GONE;
        hasMore = true;
        asyncNotifyItemDate(getItemCount() - 1);
    }

    @Override
    public void appendData(List<T> dataSet) {
        super.appendData(dataSet);
        loadState = STATE_LOADING_SUCCESS;
        hasMore = true;
        setLoading(false);
    }

    public void refreshList(List<T> newList) {
        this.mList.clear();
        this.mList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface LoadMoreListener {
        void loadMoreData();
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        if (loading) {
            loadState = STATE_LOADING;
            hasMore = true;
            asyncNotifyItemDate(getItemCount() - 1);
        }
        isLoading = loading;
    }

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

    public LoadMoreListener getListener() {
        return listener;
    }

    public void setListener(LoadMoreListener listener) {
        this.listener = listener;
    }

    public static class TagClick {
        int position;

        public TagClick(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int postion) {
            this.position = postion;
        }
    }
}
