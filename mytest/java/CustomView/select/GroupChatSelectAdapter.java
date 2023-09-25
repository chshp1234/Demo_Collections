package com.example.aidltest.CustomView.select;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.base.adapter.BaseLoadAdapter;
import com.example.aidltest.base.adapter.SubViewHolder;
import com.example.aidltest.base.adapter.click.OnItemCheckedListener;
import com.example.aidltest.db.groupchat.GroupChatEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class GroupChatSelectAdapter extends BaseLoadAdapter<GroupChatSelectAdapter.GroupChatBean> {

    private SparseBooleanArray selectCache;

    private OnItemCheckedListener onItemCheckedListener;

    private View.OnClickListener onClickListener;

    public OnItemCheckedListener getOnItemCheckedListener() {
        return onItemCheckedListener;
    }

    public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
        this.onItemCheckedListener = onItemCheckedListener;
    }

    public GroupChatSelectAdapter(Context context, LoadMoreListener loadMoreListener) {
        super(context, loadMoreListener);

        onClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.getTag() instanceof TagClick && onItemCheckedListener != null) {

                            boolean isChecked = !isSelect(((TagClick) v.getTag()).getPosition());

                            mList.get(((TagClick) v.getTag()).getPosition()).isChecked = isChecked;
                            selectCache.put(((TagClick) v.getTag()).getPosition(), isChecked);
                            onItemCheckedListener.checked(
                                    isChecked, ((TagClick) v.getTag()).getPosition());

                            setSelect(((TagClick) v.getTag()).getPosition(), isChecked);
                        }
                    }
                };

        selectCache = new SparseBooleanArray();
    }

    @Override
    public int getContentView() {
        return 0;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            return super.onCreateViewHolder(parent, viewType);
        } else {
            View view = new SelectItem(mContext);

            return createViewHolder(view);
        }
    }

    @Override
    protected void onBindItemViewHolder(SubViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;

        SelectItem itemView = (SelectItem) itemHolder.itemView;

        itemView.setTag(new TagClick(position));
        if (selectCache.get(position)) {
            itemView.setChecked(selectCache.get(position));
        } else {
            selectCache.put(position, mList.get(position).isChecked);
            itemView.setChecked(mList.get(position).isChecked);
        }
        itemView.setOnClickListener(onClickListener);
    }

    @Override
    public ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public ArrayList<Integer> selectAll(List<String> names) {
        ArrayList<Integer> list = new ArrayList<>();
        for (GroupChatSelectAdapter.GroupChatBean groupChatBean : mList) {
            groupChatBean.isChecked = true;
            list.add(groupChatBean.groupChatEntity.get_id());
            names.add(groupChatBean.groupChatEntity.getName());
        }
        asyncNotifyAllDate();
        return list;
    }

    public void inverseAll() {
        for (GroupChatSelectAdapter.GroupChatBean groupChatBean : mList) {
            selectCache.clear();
            groupChatBean.isChecked = false;
        }
        asyncNotifyAllDate();
    }

    public void setSelect(int position, boolean select) {
        mList.get(position).isChecked = select;
        selectCache.put(position, select);
        if (onItemCheckedListener != null) {
            onItemCheckedListener.checked(select, position);
        }
        asyncNotifyItemDate(position);
        LogUtils.i("setSelect: position=" + position + " select" + select);
    }

    public boolean isSelect(int position) {
        return selectCache.get(position);
    }

    static class ViewHolder extends SubViewHolder {

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class GroupChatBean {
        public GroupChatEntity groupChatEntity;
        public boolean isChecked;

        public static List<GroupChatBean> getGroupChatBeanList(
                List<GroupChatEntity> groupChatEntityList,
                List<Integer> checkedId,
                List<String> names) {
            List<GroupChatBean> groupChatBeanList = new ArrayList<>();
            if (groupChatEntityList == null || groupChatEntityList.size() == 0) {
                return groupChatBeanList;
            }
            GroupChatBean groupChatBean;
            for (GroupChatEntity groupChatEntity : groupChatEntityList) {
                groupChatBean = new GroupChatBean();
                groupChatBean.groupChatEntity = groupChatEntity;
                if (checkedId != null && checkedId.contains(groupChatEntity.get_id())) {
                    groupChatBean.isChecked = true;
                    names.add(groupChatEntity.getName());
                } else {
                    groupChatBean.isChecked = false;
                }

                groupChatBeanList.add(groupChatBean);
            }
            return groupChatBeanList;
        }
    }
}
