/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.giggle.appmanager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.giggle.appmanager.R;
import com.example.giggle.appmanager.bean.ProcessInfo;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProcessesAdapter
        extends RecyclerView.Adapter<ProcessesAdapter.MyViewHolder>
        implements SwipeableItemAdapter<ProcessesAdapter.MyViewHolder> {

    private static final String TAG = "MySwipeableItemAdapter";

    private EventListener mEventListener;
    private List<ProcessInfo> mInfos;

    public interface EventListener {

        void onLeftAndRighMoved(String pckName);
    }

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {

        public LinearLayout mContainer;

        @BindView(R.id.img_icon)
        ImageView mImgIcon;
        @BindView(R.id.tv_app_name)
        TextView mTvAppName;
        @BindView(R.id.tv_process_name)
        TextView mTvProcessName;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (LinearLayout) v.findViewById(R.id.container_p);
            ButterKnife.bind(this, v);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }
    }

    public ProcessesAdapter(List<ProcessInfo> data) {
        mInfos = data;
        setHasStableIds(true);
    }

    public void setEventListener(EventListener e) {
        mEventListener = e;
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void deleteAll() {
        if (mEventListener != null && mInfos != null && mInfos.size() >= 0) {
            for (ProcessInfo info : mInfos) {
                getEventListener().onLeftAndRighMoved(info.getPackageName());
            }
        }
    }

    public void setData(List<ProcessInfo> infos) {
        mInfos = infos;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return mInfos.get(position).getPid();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_process, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ProcessInfo item = mInfos.get(position);

        holder.mImgIcon.setImageDrawable(item.getIcon());
        holder.mTvProcessName.setText(item.getProcessName());
        holder.mTvAppName.setText(item.getLable());
        holder.setSwipeItemHorizontalSlideAmount(0);
    }

    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        return SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {

    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, final int position, int result) {
        if (result == SwipeableItemConstants.RESULT_SWIPED_LEFT || result == SwipeableItemConstants
                .RESULT_SWIPED_RIGHT) {
            return new SwipeLeftAndRightResultAction(this, position);
        }
        return null;
    }

    private class SwipeLeftAndRightResultAction extends SwipeResultActionMoveToSwipedDirection {

        private ProcessesAdapter mAdapter;
        private final int mPosition;

        SwipeLeftAndRightResultAction(ProcessesAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();
            if (mAdapter.mEventListener != null) {
                mAdapter.getEventListener().onLeftAndRighMoved(mAdapter.mInfos.get(mPosition)
                        .getPackageName());
                mAdapter.mInfos.remove(mPosition);
                mAdapter.notifyItemRemoved(mPosition);
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }
}
