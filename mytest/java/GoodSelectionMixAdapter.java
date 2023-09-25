/*
package com.example.aidltest;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.InitData;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.bumptech.glide.load.DecodeFormat;
import com.example.aidltest.CustomView.MaskableImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.blankj.utilcode.util.ColorUtils.getColor;

public class GoodSelectionMixAdapter extends BaseLoadAdapter<GoodListBean, SubViewHolder> {

    public static final int TYPE_BANNER = 3;
    public static final int TYPE_DAILY_RUSH = 4;
    public static final int TYPE_ACTIVITY = 5;
    public static final int TYPE_TOP_CHEAP_TITLE = 6;
    public static final int TYPE_TOP_CHEAP_ITEM = 7;
    public static final int TYPE_SELECTION_TITLE = 8;

    private RecyclerView rvMix;

    private GoodBannerBean bannerBean;
    private List<String> bannerSource = new ArrayList<>();
    private List<String> bannerUpdateImages = new ArrayList<>();
    private List<GoodListBean> dailyRushList = new ArrayList<>();
    private List<GoodListBean> topCheapList = new ArrayList<>();

    private int selectionTitleHeight = 0;

    private GoodDailyRushAdapter goodDailyRushAdapter;
    private GoodTopCheapAdapter goodTopCheapAdapter;

    private OnSelectionGoodsClickListener selectionGoodsClickListener;
    private OnActivityClickListener onActivityClickListener;
    private View.OnClickListener itemClickListener;

    public GoodDailyRushAdapter getGoodDailyRushAdapter() {
        return goodDailyRushAdapter;
    }

    public void setGoodDailyRushAdapter(GoodDailyRushAdapter goodDailyRushAdapter) {
        this.goodDailyRushAdapter = goodDailyRushAdapter;
    }

    public GoodTopCheapAdapter getGoodTopCheapAdapter() {
        return goodTopCheapAdapter;
    }

    public void setGoodTopCheapAdapter(GoodTopCheapAdapter goodTopCheapAdapter) {
        this.goodTopCheapAdapter = goodTopCheapAdapter;
    }

    public OnSelectionGoodsClickListener getSelectionGoodsClickListener() {
        return selectionGoodsClickListener;
    }

    public void setSelectionGoodsClickListener(
            OnSelectionGoodsClickListener selectionGoodsClickListener) {
        this.selectionGoodsClickListener = selectionGoodsClickListener;
    }

    public int getSelectionTitleHeight() {
        return selectionTitleHeight;
    }

    public void setSelectionTitleHeight(int selectionTitleHeight) {
        this.selectionTitleHeight = selectionTitleHeight;
    }

    public GoodBannerBean getBannerBean() {
        return bannerBean;
    }

    public void setBannerBean(GoodBannerBean bannerBean) {
        if (bannerBean != null
                && bannerBean.getKltzkbannerlist() != null
                && bannerBean.getKltzkbannerlist().size() > 0) {
            for (int i = 0, l = bannerBean.getKltzkbannerlist().size(); i < l; i++) {
                bannerSource.add(bannerBean.getKltzkbannerlist().get(i).getImg());
            }
            bannerUpdateImages.addAll(bannerSource);
        }
        if (bannerBean != null) {
            this.bannerBean = bannerBean;
            if (bannerBean.getKltzkbannerlist() != null
                    && bannerBean.getKltzkbannerlist().size() > 0) {
                notifyItemChanged(0);
            }
            if (bannerBean.getKltzkadvertlist() != null
                    && bannerBean.getKltzkadvertlist().size() > 0) {
                notifyItemChanged(2);
            }
        }
    }

    public List<String> getBannerSource() {
        return bannerSource == null ? new ArrayList<>() : bannerSource;
    }

    public void setBannerSource(List<String> bannerSource) {
        this.bannerSource = bannerSource;
    }

    public List<String> getBannerUpdateImages() {
        return bannerUpdateImages;
    }

    public void setBannerUpdateImages(List<String> bannerUpdateImages) {
        this.bannerUpdateImages = bannerUpdateImages;
    }

    public List<GoodListBean> getDailyRushList() {
        return dailyRushList;
    }

    public void setDailyRushList(List<GoodListBean> dailyRushList) {
        if (dailyRushList != null && dailyRushList.size() > 4) {
            this.dailyRushList.addAll(dailyRushList);
        }
        notifyItemChanged(1);
        if (goodDailyRushAdapter != null) {
            goodDailyRushAdapter.notifyDataSetChanged();
        }
    }

    public List<GoodListBean> getTopCheapList() {
        return topCheapList;
    }

    public void setTopCheapList(List<GoodListBean> topCheapList) {
        if (topCheapList != null && topCheapList.size() > 0) {
            this.topCheapList.addAll(topCheapList);
        }
        if (goodTopCheapAdapter != null) {
            goodTopCheapAdapter.notifyDataSetChanged();
        }
    }

    public GoodSelectionMixAdapter(
            Context context, LoadMoreListener listener, int pageCount, RecyclerView recyclerView) {
        super(context, listener, pageCount);
        rvMix = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BANNER;
        } else if (position == 1) {
            return TYPE_DAILY_RUSH;
        } else if (position == 2) {
            return TYPE_ACTIVITY;
        } else if (position == 3) {
            return TYPE_TOP_CHEAP_TITLE;
        } else if (position == 4) {
            return TYPE_TOP_CHEAP_ITEM;
        } else if (position == 5) {
            return TYPE_SELECTION_TITLE;
        } else if (position == (6 + mList.size())) {
            return TYPE_BOTTOM;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 7;
    }

    @Override
    public int getContentView() {
        return R.layout.item_good_selection;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            return new SubViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.footer_view, parent, false));
        } else if (viewType == TYPE_BANNER) {
            return new BannerHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_free_banner, parent, false));
        } else if (viewType == TYPE_DAILY_RUSH) {
            return new DailyRushHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_good_selection_daily_rush, parent, false));
        } else if (viewType == TYPE_ACTIVITY) {
            return new ActivityImgHolder(new MaskableImageView(mContext));
            */
/*LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_good_selection_activity, parent, false));*//*

        } else if (viewType == TYPE_TOP_CHEAP_TITLE) {
            return new TopCheapTitleHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_good_selection_top_cheap_title, parent, false));
        } else if (viewType == TYPE_TOP_CHEAP_ITEM) {
            return new TopCheapItemHolder(new RecyclerView(mContext));
            */
/*LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_top_cheap_goods, parent, false));*//*

        } else if (viewType == TYPE_SELECTION_TITLE) {
            return new SelectionTitleHolder(
                    new ImageView(mContext)
                    */
/*LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_good_selection_goods_list_title, parent, false)*//*
 );

        } else if (viewType == TYPE_ITEM) {
            itemClickListener =
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getTag() instanceof TagClick) {
                                int position = ((TagClick) v.getTag()).getPosition();
                                selectionGoodsClickListener.onClick(position);
                            }
                        }
                    };
            View view = LayoutInflater.from(mContext).inflate(getContentView(), parent, false);
            return createViewHolder(view);
        } else {
            return null;
        }
    }

    @Override
    protected void onBindItemViewHolder(SubViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case TYPE_BANNER:
                bindBanner(holder);
                break;
            case TYPE_DAILY_RUSH:
                bindDailyRush(holder);
                break;
            case TYPE_ACTIVITY:
                bindActivity(holder);
                break;
            case TYPE_TOP_CHEAP_TITLE:
                bindTopCheapTitle(holder);
                break;
            case TYPE_TOP_CHEAP_ITEM:
                bindTopCheapItem(holder, position);
                break;
            case TYPE_SELECTION_TITLE:
                bindSelectionTitle(holder);
                break;
            case TYPE_ITEM:
                bindSelectionItem(holder, position - 6);
                break;
        }
    }

    public Banner banner;

    public Banner getBanner() {
        return banner;
    }

    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    private void chooseBannerTarget(GoodBannerBean bannerBean, int from, int position) {
        if (bannerBean != null) {
            String[] contents = null;
            switch (from) {
                case FROM_BANNER:
                    if (ObjectUtils.isNotEmpty(bannerBean.getKltzkbannerlist())) {
                        contents =
                                bannerBean.getKltzkbannerlist().get(position).getUrl().split(",");
                    }
                    break;
                case FROM_ADVERT:
                    if (ObjectUtils.isNotEmpty(bannerBean.getKltzkadvertlist())) {
                        contents =
                                bannerBean.getKltzkadvertlist().get(position).getUrl().split(",");
                    }
                    break;
            }

            if (ObjectUtils.isNotEmpty(contents)) {
                try {
                    int type = Integer.valueOf(contents[0]);

                    switch (type) {
                        case TYPE_GOODS_CATEGORY:
                            EventBus.getDefault()
                                    .post(new GoodTabEvent(Integer.valueOf(contents[1])));
                            break;
                        case TYPE_GOODS_DETAIL:
                            Intent intentDetail =
                                    new Intent(mContext, GoodGoodsDetailActivity.class);
                            intentDetail.putExtra("productId", contents[1]);
                            ActivityUtils.startActivity(intentDetail);
                            break;
                        case TYPE_GOODS_WEB:
                            Intent intentWeb =
                                    new Intent(mContext, WebViewFullScreenActivity.class);
                            intentWeb.putExtra("url", contents[1]);
                            ActivityUtils.startActivity(intentWeb);
                            break;
                        case TYPE_GOODS_FREE:
                            EventBus.getDefault().post(new ChooseTabEvent(2));
                            break;
                        case TYPE_GOODS_FREE_DETAIL:
                            String trytoken = SPStaticUtils.getString(SPConfig.FREE_TOKEN);
                            int pid = Integer.valueOf(contents[1]);
                            String url =
                                    MyApplication.getContext()
                                            .getString(
                                                    R.string.url_free_goods_detail,
                                                    pid,
                                                    trytoken,
                                                    InitData.getInstance().getToken());
                            Intent intent = new Intent(mContext, WebViewFullScreenActivity.class);
                            intent.putExtra("url", url);
                            ActivityUtils.startActivity(intent);
                            break;
                    }
                } catch (Exception ignored) {

                }
            }
        }
    }

    private void bindBanner(SubViewHolder holder) {
        BannerHolder bannerHolder = (BannerHolder) holder;
        bannerHolder.banner.setIndicatorRes(
                R.drawable.bg_indicator_drawable_selected,
                R.drawable.bg_indicator_drawable_unselected);
        bannerHolder.banner.updateBannerStyle(BannerConfig.CUSTOM_INDICATOR);
        bannerHolder
                .banner
                .setAutoPlay(true)
                .setPages(bannerSource, new CustomViewHolder())
                .start();

        bannerHolder.banner.setOnBannerClickListener(
                new OnBannerClickListener() {
                    @Override
                    public void onBannerClick(List datas, int position) {
                        chooseBannerTarget(bannerBean, FROM_BANNER, position);
                    }
                });
        banner = bannerHolder.banner;
    }

    private void bindDailyRush(SubViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (dailyRushList.size() < 4) {
            holder.itemView.setVisibility(View.GONE);
            layoutParams.height = SizeUtils.dp2px(10);
            layoutParams.width = 0;
        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    private void bindActivity(SubViewHolder holder) {
        ActivityImgHolder rushHolder = (ActivityImgHolder) holder;
        if (bannerBean != null
                && bannerBean.getKltzkadvertlist() != null
                && bannerBean.getKltzkadvertlist().size() > 0) {
            GlideApp.with(mContext)
                    .load(bannerBean.getKltzkadvertlist().get(0).getImg())
                    .format(DecodeFormat.PREFER_RGB_565)
                    .placeholder(R.drawable.default_placeholde_banner)
                    .into(rushHolder.activityImg);
        }
        rushHolder.itemView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseBannerTarget(bannerBean, FROM_ADVERT, 0);
                    }
                });
    }

    private void bindTopCheapTitle(SubViewHolder holder) {
        TopCheapTitleHolder topCheapTitleHolder = (TopCheapTitleHolder) holder;
        topCheapTitleHolder.more.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WebViewFullScreenActivity.class);
                        intent.putExtra(
                                "url",
                                MyApplication.getContext()
                                        .getString(R.string.url_good_activity_nine));
                        ActivityUtils.startActivity(intent);
                    }
                });
    }

    private void bindTopCheapItem(SubViewHolder holder, int position) {}

    private void bindSelectionTitle(SubViewHolder holder) {
        LogUtils.d("bindSelectionTitle: ");
        selectionTitleHeight = holder.itemView.getTop();
    }

    private void bindSelectionItem(SubViewHolder holder, int position) {
        holder.itemView.setTag(new TagClick(position));
        if (selectionGoodsClickListener != null) {
            holder.itemView.setOnClickListener(itemClickListener);
        }
        if (mList.size() > 0) {
            GoodSelectionMixAdapter.MyHolder myHolder = (GoodSelectionMixAdapter.MyHolder) holder;
            GlideApp.with(mContext)
                    .load(mList.get(position).getPicturl())
                    .override(SizeUtils.dp2px(180))
                    .format(DecodeFormat.PREFER_RGB_565)
                    .placeholder(R.color.item_placeholder)
                    .into(myHolder.goodsImg);
            myHolder.goodsTitle.setText(mList.get(position).getTitle());
            myHolder.goodsCoupon.setText(mList.get(position).getCoupon());
            myHolder.goodsAddress.setText(mList.get(position).getProvcity());
            SpanUtils.with(myHolder.goodsPrice)
                    .append(mList.get(position).getCouponprice())
                    .setBold()
                    .setForegroundColor(getColor(R.color.red))
                    .setFontSize(15, true)
                    .appendSpace(SizeUtils.dp2px(5))
                    .append(mList.get(position).getPrice())
                    .setStrikethrough()
                    .setForegroundColor(getColor(R.color.text_light_gray))
                    .setFontSize(10, true)
                    .create();
            myHolder.goodsSellNum.setText("已售" + mList.get(position).getVolume());
            if (myHolder.goodsLabel.getChildCount() > 0) {
                myHolder.goodsLabel.removeAllViews();
            }
            if (ObjectUtils.isNotEmpty(mList.get(position).getTags())) {
                for (int i = 0, l = mList.get(position).getTags().size(); i < l; i++) {
                    TextView textView = new TextView(mContext);
                    textView.setBackground(
                            mContext.getResources()
                                    .getDrawable(R.drawable.bg_label_good_goods, null));
                    textView.setText("#" + mList.get(position).getTags().get(i) + "#");
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextColor(getColor(R.color.text_label_color));
                    textView.setPadding(
                            SizeUtils.dp2px(2),
                            SizeUtils.dp2px(2),
                            SizeUtils.dp2px(2),
                            SizeUtils.dp2px(2));
                    textView.setTextSize(10);

                    myHolder.goodsLabel.addView(textView);

                    ViewGroup.LayoutParams params = textView.getLayoutParams();
                    if (params instanceof FlexboxLayout.LayoutParams) {
                        FlexboxLayout.LayoutParams layoutParams =
                                (FlexboxLayout.LayoutParams) params;
                        layoutParams.rightMargin = SizeUtils.dp2px(10);
                        layoutParams.bottomMargin = SizeUtils.dp2px(10);
                    }
                }
            }
        }
    }

    @Override
    public GoodSelectionMixAdapter.MyHolder createViewHolder(View view) {
        return new GoodSelectionMixAdapter.MyHolder(view);
    }

    class BannerHolder extends SubViewHolder {
        Banner banner;

        public BannerHolder(View itemView) {
            super(itemView);
            banner = itemView.findViewById(R.id.banner);
        }
    }

    class DailyRushHolder extends SubViewHolder {

        RecyclerView recyclerView;

        public DailyRushHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.daily_rush_list);
            final LinearLayoutManager dailyRushManager =
                    new LinearLayoutManager(
                            itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(dailyRushManager);
            goodDailyRushAdapter = new GoodDailyRushAdapter(mContext, dailyRushList);
            recyclerView.setAdapter(goodDailyRushAdapter);
            goodDailyRushAdapter.setmOnItemClickListener(
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(View itemView, int position) {
                            Intent intent = new Intent(mContext, GoodGoodsDetailActivity.class);
                            intent.putExtra(
                                    "productId", dailyRushList.get(position).getProductid());
                            ActivityUtils.startActivity(intent);
                        }
                    });
        }
    }

    class ActivityImgHolder extends SubViewHolder {

        MaskableImageView activityImg;

        public ActivityImgHolder(View itemView) {
            super(itemView);
            activityImg = (MaskableImageView) itemView;
            ViewGroup.LayoutParams layoutParams =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            activityImg.setBackgroundColor(ColorUtils.getColor(R.color.white));
            activityImg.setAdjustViewBounds(true);
            activityImg.setScaleType(ImageView.ScaleType.FIT_XY);
            activityImg.setLayoutParams(layoutParams);
        }
    }

    class TopCheapTitleHolder extends SubViewHolder {

        TextView more;

        public TopCheapTitleHolder(View itemView) {
            super(itemView);
            more = itemView.findViewById(R.id.overbalance_more);
        }
    }

    class TopCheapItemHolder extends SubViewHolder {

        RecyclerView topCheapRV;

        public TopCheapItemHolder(View itemView) {
            super(itemView);
            topCheapRV = (RecyclerView) itemView;
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            final LinearLayoutManager topCheapManager = new LinearLayoutManager(mContext);
            topCheapRV.setLayoutManager(topCheapManager);
            topCheapRV.setLayoutParams(layoutParams);
            topCheapRV.setNestedScrollingEnabled(false);
            topCheapRV.setHasFixedSize(true);
            goodTopCheapAdapter = new GoodTopCheapAdapter(mContext, topCheapList);
            goodTopCheapAdapter.setmOnItemClickListener(
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(View itemView, int position) {
                            Intent intent = new Intent(mContext, GoodGoodsDetailActivity.class);
                            intent.putExtra("productId", topCheapList.get(position).getProductid());
                            ActivityUtils.startActivity(intent);
                        }
                    });
            topCheapRV.setAdapter(goodTopCheapAdapter);
            //            ButterKnife.bind(this, itemView);
        }
    }

    class SelectionTitleHolder extends SubViewHolder {

        ImageView imageView;

        public SelectionTitleHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
            ViewGroup.MarginLayoutParams layoutParams =
                    new ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(45));
            layoutParams.topMargin = SizeUtils.dp2px(10);
            imageView.setBackgroundColor(ColorUtils.getColor(R.color.white));
            imageView.setImageDrawable(mContext.getDrawable(R.drawable.title_select));
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setLayoutParams(layoutParams);
        }
    }

    class MyHolder extends SubViewHolder {
        @BindView(R.id.goods_img)
        MaskableImageView goodsImg;

        @BindView(R.id.goods_title)
        TextView goodsTitle;

        @BindView(R.id.goods_price)
        TextView goodsPrice;

        @BindView(R.id.goods_coupon)
        TextView goodsCoupon;

        @BindView(R.id.goods_sell_num)
        TextView goodsSellNum;

        @BindView(R.id.goods_address)
        TextView goodsAddress;

        @BindView(R.id.goods_label)
        FlexboxLayout goodsLabel;

        MyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class CustomViewHolder implements BannerViewHolder<Object> {

        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            // 返回mImageView页面布局
            mImageView = new ImageView(context);
            ViewGroup.LayoutParams params =
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
            mImageView.setLayoutParams(params);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return mImageView;
        }

        @Override
        public void onBind(Context context, int position, Object data) {
            // 数据绑定
            GlideApp.with(context)
                    .load(data)
                    .placeholder(R.drawable.default_placeholde_banner)
                    .fitCenter()
                    .into(mImageView);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull SubViewHolder holder) {
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        //        if (getItemViewType(position) == TYPE_TOP_CHEAP_ITEM) {}

        if (getItemViewType(position) != TYPE_ITEM) {
            StaggeredGridLayoutManager.LayoutParams p =
                    (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    public void updateBanner() {
        if (banner != null) {
            banner.update(bannerUpdateImages);
            notifyItemChanged(0);
        }
    }

    public void releaseBanner() {
        if (banner != null) {
            banner.releaseBanner();
        }
    }

    public void stopAutoPlay() {
        // 结束轮播
        if (banner != null && banner.isStart() && banner.isPrepare()) {
            banner.stopAutoPlay();
        }
    }

    public void startAutoPlay() {
        // 开始轮播
        if (banner != null && !banner.isStart() && banner.isPrepare()) {
            banner.startAutoPlay();
        }
    }

    public interface OnActivityClickListener {
        void onClick(int position);
    }

    public interface OnSelectionGoodsClickListener {
        void onClick(int position);
    }
}
*/
