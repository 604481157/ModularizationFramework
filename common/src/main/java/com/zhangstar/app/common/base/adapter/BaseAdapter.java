package com.zhangstar.app.common.base.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangstar on 2016/11/10.
 */

public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_COMMON_VIEW = 100001;
    public static final int TYPE_FOOTER_VIEW = 100002;
    public static final int TYPE_EMPTY_VIEW = 100003;
    public static final int TYPE_DEFAULT_VIEW = 100004;
    protected Context mContext;

    public List<T> getDatas() {
        return mDatas;
    }


    protected List<T> mDatas;
    private View mLoadingView;
    private View mLoadFailedView;
    private View mLoadEndView;
    private View mEmptyView;
    private RelativeLayout mFooterLayout;
    private OnLoadMoreListener mLoadMoreListener;
    private OnItemClickListeners<T> mItemClickListener;

    public boolean isNeedSpan() {
        return needSpan;
    }

    public void setNeedSpan(boolean needSpan) {
        this.needSpan = needSpan;
    }

    private boolean needSpan;


    public void setItemLongClickListeners(OnItemLongClickListeners<T> itemLongClickListener) {
        this.mItemLongClickListeners = itemLongClickListener;
    }

    private OnItemLongClickListeners<T> mItemLongClickListeners;

    protected boolean mOpenLoadMore;
    private boolean isAutoLoadMore = true;

    protected abstract void convert(BaseViewHolder holder, T data);

    protected abstract int getItemLayoutId();

    public interface OnLoadMoreListener {
        void onLoadMore(boolean isReload);
    }

    public interface OnItemClickListeners<T> {
        void onItemClick(BaseViewHolder viewHolder, T data, int position);
    }

    public interface OnItemLongClickListeners<T> {
        void onItemLongClick(BaseViewHolder viewHolder, T data, int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_COMMON_VIEW:
                viewHolder = BaseViewHolder.create(mContext, getItemLayoutId(), parent);
                break;
            case TYPE_FOOTER_VIEW:
                if (mFooterLayout == null) {
                    mFooterLayout = new RelativeLayout(mContext);
                }
                viewHolder = BaseViewHolder.create(mFooterLayout);
                break;
            case TYPE_EMPTY_VIEW:
                viewHolder = BaseViewHolder.create(mEmptyView);
                break;
        }
        return viewHolder;

    }

    public BaseAdapter(Context context, List<T> datas, boolean isOpenLoadMore) {
        mContext = context;
        mDatas = datas == null ? new ArrayList<T>() : datas;
        mOpenLoadMore = isOpenLoadMore;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_COMMON_VIEW:
                bindCommonItem(holder, position);
                break;
        }
    }

    private void bindCommonItem(RecyclerView.ViewHolder holder, final int position) {
        final BaseViewHolder viewHolder = (BaseViewHolder) holder;
        convert(viewHolder, mDatas.get(position));
        if (mItemClickListener != null) {
            viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(viewHolder, mDatas.get(position), position);
                }
            });
        }
        if (mItemLongClickListeners != null) {
            viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mItemLongClickListeners.onItemLongClick(viewHolder, mDatas.get(position), position);
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return TYPE_FOOTER_VIEW;
        }
        return TYPE_COMMON_VIEW;
    }

    @Override
    public int getItemCount() {
        return mDatas.size() + getFootViewCount();
    }


    public T getItem(int position) {
        return mDatas.get(position);
    }

    private int getFootViewCount() {
        return mOpenLoadMore ? 1 : 0;
    }

    /**
     *
     */
    private void setEmptyView(View view) {
        mEmptyView = view;
    }

    public void setEmptyView(int emptyViewId) {
        setEmptyView(LayoutInflater.from(mContext).inflate(emptyViewId, null));
    }

    /**
     * 是否是FooterView
     *
     * @param position
     * @return
     */
    private boolean isFooterView(int position) {
        return mOpenLoadMore && getItemCount() > 1 && position >= getItemCount() - 1;
    }


    /**
     * StaggeredGridLayoutManager模式时，FooterView可占据一行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isFooterView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /*
     * GridLayoutManager模式时， FooterView可占据一行，判断RecyclerView是否到达底部
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager && needSpan) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    /**
                     * 这里返回并不是列数  而是Item的占比 类似于权重
                     */
                    if (isFooterView(position)) {
                        return gridManager.getSpanCount();
                    } else {
                        switch (position % 6) {
                            case 5:
                                return 3;
                            case 3:
                                return 2;
                            default:
                                return 1;
                        }
                    }
                }
            });
        }
        //为什么只有这一个方法开启加载更多
        startLoadMore(recyclerView, layoutManager);
    }

    //是否正在加载中
    private boolean isLoading;
    //请求数据源已经全部请求完毕
    private boolean isEnd;

    public void isEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public void isLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    /**
     * 判断列表是否滑动到底部
     *
     * @param recyclerView
     * @param layoutManager
     */
    private void startLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (!mOpenLoadMore || mLoadMoreListener == null) {
            return;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //滑动静止
                    if (findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        if (!isLoading && !isEnd) {
                            //避免重复多次请求
                            scrollLoadMore();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //滑动中
            }
        });
        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });
    }

    /**
     * 到达底部开始刷新
     */
    private void scrollLoadMore() {
        if (mFooterLayout.getChildAt(0) == mLoadingView) {
            mLoadMoreListener.onLoadMore(false);
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            return findMax(lastVisibleItemPositions);
        }
        return -1;
    }

    /**
     * 找到瀑布流布局中最后一个可见的子元素的position
     *
     * @param lastVisiblePositions
     * @return
     */
    private int findMax(int[] lastVisiblePositions) {
        int max = lastVisiblePositions[0];
        for (int value : lastVisiblePositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    /**
     * 移出底部布局中的所有View
     */
    private void removeFooterView() {
        mFooterLayout.removeAllViews();
    }

    /**
     * 添加View至底部布局
     *
     * @param footerView
     */
    private void addFooterView(View footerView) {
        if (footerView == null) {
            return;
        }

        if (mFooterLayout == null) {
            mFooterLayout = new RelativeLayout(mContext);
        }
        removeFooterView();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mFooterLayout.addView(footerView, params);
    }

    public void setLoadMoreData(List<T> datas) {
        int size = mDatas.size();
        mDatas.addAll(datas);
        notifyItemInserted(size);
    }

    public void setData(List<T> datas) {
        mDatas.addAll(0, datas);
        notifyDataSetChanged();
    }

    public void setNewData(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    /**
     * 初始化加载中布局
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        mLoadingView = loadingView;
        addFooterView(mLoadingView);
    }

    public void setLoadingView(int loadingId) {
        setLoadingView(LayoutInflater.from(mContext).inflate(loadingId, null));
    }

    /**
     * 初始化全部加载完成布局
     *
     * @param loadEndView
     */
    public void setLoadEndView(View loadEndView) {
        if (loadEndView == null) {
            return;
        }
        mLoadFailedView = loadEndView;
        addFooterView(mLoadFailedView);
        if (!isEnd) {
            mLoadFailedView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addFooterView(mLoadingView);
                    mLoadMoreListener.onLoadMore(true);
                }
            });
        }
    }

    public void setLoadEndView(int loadEndId) {
        setLoadEndView(LayoutInflater.from(mContext).inflate(loadEndId, null));
    }

    public int getFooterViewCount() {
        return mOpenLoadMore ? 1 : 0;
    }


    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setOnItemClickListener(OnItemClickListeners<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

}
