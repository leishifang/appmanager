package com.example.giggle.appmanager.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.adapter.ProcessesAdapter;
import com.example.giggle.appmanager.bean.ProcessInfo;
import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by leishifang on 2017/4/19 16:45.
 */

public class ProcessActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = ProcessActivity.class.toString();
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.img_loading)
    AVLoadingIndicatorView mImgLoading;
    @BindView(R.id.tv_progress)
    TextView mTvProgress;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewTouchActionGuardManager mRecyclerViewTouchActionGuardManager;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerViewSwipeManager mRecyclerViewSwipeManager;

    private ActivityManager mActivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        ButterKnife.bind(this);

        initView();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        Observable.create(new ObservableOnSubscribe<List<ProcessInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ProcessInfo>> e) throws Exception {
                List<ProcessInfo> infos = getRunningProcessesInfo();
                e.onNext(infos);
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ProcessInfo>>() {
                    @Override
                    public void accept(List<ProcessInfo> processInfos) throws Exception {
                        fillAdapter(processInfos);
                    }
                });
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(this);
    }

    private List<ProcessInfo> getRunningProcessesInfo() {
        showProgress();
        PackageManager packageManager = this.getPackageManager();

        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        List<ProcessInfo> infos = new ArrayList<ProcessInfo>();
        int count = 0;
        for (AndroidAppProcess process : processes) {
            ProcessInfo info = new ProcessInfo();
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(process.getPackageName(),
                        0);
                info.setLable(applicationInfo.loadLabel(packageManager).toString());
                info.setIcon(applicationInfo.loadIcon(packageManager));
                info.setProcessName(process.name);
                info.setPid(process.pid);
                info.setPackageName(applicationInfo.packageName);
                infos.add(info);
                count++;
                updateProgress(count);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return infos;
    }

    private void fillAdapter(List<ProcessInfo> infos) {

        if (infos.size() <= 0) {
            mTvEmpty.setVisibility(View.VISIBLE);
        } else {
            mTvEmpty.setVisibility(View.GONE);
            mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            mRecyclerViewTouchActionGuardManager = new RecyclerViewTouchActionGuardManager();
            mRecyclerViewTouchActionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true);
            mRecyclerViewTouchActionGuardManager.setEnabled(true);

            mRecyclerViewSwipeManager = new RecyclerViewSwipeManager();

            ProcessesAdapter processesAdapter = new ProcessesAdapter(infos);
            processesAdapter.setEventListener(new ProcessesAdapter.EventListener() {
                @Override
                public void onLeftAndRighMoved(String pckName) {
                    mActivityManager.killBackgroundProcesses(pckName);
                }
            });
            mAdapter = processesAdapter;
            mWrappedAdapter = mRecyclerViewSwipeManager.createWrappedAdapter(mAdapter);

            final GeneralItemAnimator animator = new SwipeDismissItemAnimator();
            animator.setSupportsChangeAnimations(false);

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
            mRecyclerView.setItemAnimator(animator);

            mRecyclerViewTouchActionGuardManager.attachRecyclerView(mRecyclerView);
            mRecyclerViewSwipeManager.attachRecyclerView(mRecyclerView);
        }
        updateSubTitle(infos.size());
        hideProgress();
    }

    public void updateSubTitle(int count) {
        mToolbar.setSubtitle("进程数：" + count);
    }

    private void updateProgress(final int progress) {

        mTvProgress.post(new Runnable() {
            @Override
            public void run() {
                mTvProgress.setText(progress + "");
            }
        });
    }

    public void showProgress() {
        mImgLoading.post(new Runnable() {
            @Override
            public void run() {
                mImgLoading.show();
                mTvProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideProgress() {
        mImgLoading.post(new Runnable() {
            @Override
            public void run() {
                mImgLoading.hide();
                mTvProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_all_menu, menu);
        mToolbar.getMenu().getItem(0).setTitle("清理所有");
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                ((ProcessesAdapter) mAdapter).deleteAll();
                Observable.create(new ObservableOnSubscribe<List<ProcessInfo>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<ProcessInfo>> e) throws Exception {
                        List<ProcessInfo> infos = getRunningProcessesInfo();
                        e.onNext(infos);
                        e.onComplete();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<ProcessInfo>>() {
                            @Override
                            public void accept(List<ProcessInfo> processInfos) throws Exception {
                                ((ProcessesAdapter) mAdapter).setData(processInfos);
                                hideProgress();
                                updateSubTitle(processInfos.size());
                            }
                        });
                break;
        }
        return false;
    }
}
