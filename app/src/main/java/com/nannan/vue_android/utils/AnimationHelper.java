package com.nannan.vue_android.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * @author zxl on 2018/8/23.
 *         discription:
 */

public class AnimationHelper {

    public static AnimationHelper getInstance(){
        return AnimateHelperHolder.M_INSTANCE;
    }

    private AnimationHelper(){

    }

    private static class AnimateHelperHolder{
        private static final AnimationHelper M_INSTANCE  = new AnimationHelper();
    }


    /**
     * 展示或者隐藏某个特定的View
     * 样例: AnimationHelper.getInstance().hideOrShowDetailView(mTvTest,mTvHeight,false);
     */
    public void  hideOrShowDetailView(final View view, int height, boolean isShow){
        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        ValueAnimator valueAnimator;
        ObjectAnimator alpha;
        if(!isShow){
            valueAnimator = ValueAnimator.ofInt(height, 0);
            alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
        }else{
            valueAnimator = ValueAnimator.ofInt(0, height);
            alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.height= (int) valueAnimator.getAnimatedValue();
                view.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(valueAnimator,alpha);
        animatorSet.start();
    }

    public  void antilockWise(View view,long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -360).setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        //解决循环动画不流畅
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 顺时针
     */
    public  void lockWise(View view,long duration) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 360).setDuration(duration);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

}
