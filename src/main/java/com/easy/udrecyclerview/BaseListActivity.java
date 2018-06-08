package com.easy.udrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangtian on 2018/6/7.
 */

public abstract class BaseListActivity<A extends BaseListAdapter, D> extends AppCompatActivity {

    public static final int PAGE_COUNT = 20;

    protected List<D> mList;
    protected A mAdapter;
    protected int lastVisibleItem = 0;
    protected int page;
    protected Context mContext;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;

    protected abstract SwipeRefreshLayout getSwipeRefreshLayout();

    protected abstract RecyclerView getRecyclerView();

    protected abstract void requestData(int p, int count);

    protected abstract int getSchemeColor();

    protected abstract int getDivideLineColor();

    protected abstract int getDivideHeight();

    protected abstract int getContentView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initModule();
        initView();
        initController();
    }

    protected void initModule() {
        mList = new ArrayList<>();
        mContext = BaseListActivity.this;
    }

    protected void initView() {
        setContentView(getContentView());
        mSwipeRefreshLayout = getSwipeRefreshLayout();
        mRecyclerView = getRecyclerView();
    }

    protected void initController() {
        initRecyclerView();
    }

    protected void initRecyclerView() {
        if(getSchemeColor() != 0) {
            mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mContext, getSchemeColor()));
        }
        mSwipeRefreshLayout
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshData();
                    }
                });

        if(getDivideLineColor() != 0 && getDivideHeight() != 0) {
            mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, DisplayUtil.dip2pxInt(getDivideHeight()), getDivideLineColor()));
        } else if(getDivideLineColor() != 0 && getDivideHeight() == 0) {
            mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, DisplayUtil.dip2pxInt(5), getDivideLineColor()));
        } else if(getDivideLineColor() == 0 && getDivideHeight() != 0) {
            mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, DisplayUtil.dip2pxInt(getDivideHeight()), R.color.colorPrimary));
        }

        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.HORIZONTAL, DisplayUtil.dip2pxInt(getDivideHeight()), getDivideLineColor()));
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!mAdapter.isFadeTips() && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                        updateRecyclerView(++page, PAGE_COUNT);
                    }

                    if (mAdapter.isFadeTips() && lastVisibleItem + 2 == mAdapter.getItemCount()) {
                        updateRecyclerView(++page, PAGE_COUNT);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    protected void updateRecyclerView(int fromIndex, int toIndex) {
        addData(fromIndex, toIndex);
    }

    protected void addData(final int p, int count) {
        mSwipeRefreshLayout.setRefreshing(true);
        requestData(p, count);
    }

    protected void refreshData() {
        page = 0;
        mAdapter.resetDatas();
        addData(page, PAGE_COUNT);
    }
}
