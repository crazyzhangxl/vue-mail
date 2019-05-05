package com.nannan.vue_android.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.nannan.vue_android.R;


/**
 * @description 加载动画dialog
 * @author  zxl
 */
public class LoadingProgressDialog {
	public static Dialog createLoadingDialog(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// 得到加载view
		View v = inflater.inflate(R.layout.loading_dialog, null);
		ImageView bg_loading = (ImageView) v.findViewById(R.id.bg_loading);
		ImageView ivLoading_l = (ImageView) v.findViewById(R.id.ivLoading_l);
		ImageView ivLoading_b = (ImageView) v.findViewById(R.id.ivLoading_b);
		AnimationHelper.getInstance().lockWise(bg_loading,2500);
		AnimationHelper.getInstance().antilockWise(ivLoading_l, 2500);
		AnimationHelper.getInstance().antilockWise(ivLoading_b,2500);
		// 创建自定义样式dialog
		Dialog loadingDialog = new Dialog(context, R.style.dialog);
		// 不可以触摸取消
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setContentView(v);
		return loadingDialog;
	}
}
