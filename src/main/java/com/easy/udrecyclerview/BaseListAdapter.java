package com.easy.udrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhangtian on 2018/6/7.
 */

public abstract class BaseListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    protected List<T> mList;
    protected Context mContext;

    private boolean hasMore = true;
    private boolean fadeTips = false;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOT = 1;

    public boolean isFadeTips() {
        return fadeTips;
    }

    public void resetDatas() {
        mList.clear();
    }

    public abstract int getItemLayout();

    public abstract VH getViewHolder(View view);

    public abstract void bindData(RecyclerView.ViewHolder holder, T data);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(mContext).inflate(getItemLayout(), null);
            return getViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_footer, null);
            return new BaseListAdapter.FooterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof BaseListAdapter.FooterHolder) {
            setFootAdapter(holder);
        } else {
            bindData(holder, mList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOT;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    private void setFootAdapter(RecyclerView.ViewHolder holder) {
        // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
        ((FooterHolder) holder).tips.setVisibility(View.VISIBLE);
        // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
        if (hasMore) {
            // 不隐藏footView提示
            fadeTips = false;
            if (mList.size() > 0) {
                // 如果查询数据发现增加之后，就显示正在加载更多
                ((FooterHolder) holder).tips.setText(mContext.getString(R.string.msg_load_more));
            }
        } else {
            if (mList.size() > 0) {
                // 如果查询数据发现并没有增加时，就显示没有更多数据了
                ((FooterHolder) holder).tips.setText(mContext.getString(R.string.msg_load_no));

                // 隐藏提示条
                ((FooterHolder) holder).tips.setVisibility(View.GONE);
                // 将fadeTips设置true
                fadeTips = true;
                // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                hasMore = true;

            }
        }
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<T> newDatas, boolean hasMore) {
        // 在原有的数据之上增加新数据
        if (newDatas != null) {
            mList.addAll(newDatas);
        }
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    class FooterHolder extends RecyclerView.ViewHolder {

        protected TextView tips;

        public FooterHolder(View itemView) {
            super(itemView);

            tips = (TextView) itemView.findViewById(R.id.tips);
        }
    }
}
