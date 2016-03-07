package com.dingoapp.dingo.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.etiennelawlor.quickreturn.library.utils.QuickReturnUtils;

/**
 * Created by guestguest on 28/02/16.
 */
public class QuickNoticeScrollListener extends RecyclerView.OnScrollListener {

    private View mNoticeView;
    private boolean mIsSnappable;
    private int mMaxTranslation;
    private int mHeaderDiffTotal = 0;
    private int mPrevScrollY = 0;
    private final int mColumnCount;

    public QuickNoticeScrollListener(Builder builder) {
        mNoticeView = builder.mNoticeView;
        mIsSnappable = builder.mIsSnappable;
        mMaxTranslation = builder.mMaxTranslation;
        mColumnCount = builder.mColumnCount;
    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

    /*    int midHeader = -mMaxTranslation / 2;

        if (newState == RecyclerView.SCROLL_STATE_IDLE && mIsSnappable) {
            if (-mHeaderDiffTotal > 0 && -mHeaderDiffTotal < midHeader) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(mNoticeView, "translationY", mNoticeView.getTranslationY(), 0);
                anim.setDuration(100);
                anim.start();
                mHeaderDiffTotal = 0;
            } else if (-mHeaderDiffTotal < -mMaxTranslation && -mHeaderDiffTotal >= midHeader) {
                ObjectAnimator anim = ObjectAnimator.ofFloat(mNoticeView, "translationY", mNoticeView.getTranslationY(), mMaxTranslation);
                anim.setDuration(100);
                anim.start();
                mHeaderDiffTotal = mMaxTranslation;
            }
        }*/
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int scrollY = QuickReturnUtils.getScrollY(recyclerView, mColumnCount);
//        Log.d("", "onScrolled() : scrollY - "+scrollY);
        int diff = mPrevScrollY - scrollY;
//        Log.d("", "onScrolled() : diff - "+diff);

        if (diff != 0) {
            if (diff < 0) { // scrolling down
                mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, 0);
               // mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMaxTranslation);
            } else { // scrolling up
                mHeaderDiffTotal = Math.min(mHeaderDiffTotal + diff, mMaxTranslation);
               // mHeaderDiffTotal = Math.min(Math.max(mHeaderDiffTotal + diff, mMaxTranslation), 0);
            }

            mNoticeView.setTranslationY(mHeaderDiffTotal);
        }

        mPrevScrollY = scrollY;
    }

    public static class Builder {

        private View mNoticeView = null;
        private int mMaxTranslation = 0;
        private int mColumnCount = 1;
        private boolean mIsSnappable = false;

        public Builder(View noticeView) {
            mNoticeView = noticeView;
        }

        public Builder maxTranslation(int maxTranslation) {
            mMaxTranslation = maxTranslation;
            return this;
        }

        public Builder columnCount(int columnCount) {
            mColumnCount = columnCount;
            return this;
        }

        public Builder isSnappable(boolean isSnappable) {
            mIsSnappable = isSnappable;
            return this;
        }

        public QuickNoticeScrollListener build() {
            return new QuickNoticeScrollListener(this);
        }
    }
}
