package com.easymi.common.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.easymi.common.entity.Pic;
import com.easymi.common.entity.RegisterRes;
//import com.easymi.component.Glide4Engine;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.RxProgressHUD;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yalantis.ucrop.UCrop;
//import com.zhihu.matisse.Matisse;
//import com.zhihu.matisse.MimeType;
//import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class AbsRegisterFragment extends Fragment {

    protected RxManager mRxManager = new RxManager();
    private RxPermissions rxPermissions;
    private float mX = 1;
    private float mY = 1;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rxPermissions = new RxPermissions(this);
    }

    protected void openCropActivity(Uri uri, int requestCode, float x, float y) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_"+timeStamp+".jpg";
        UCrop.Options op = new UCrop.Options();
        op.setCompressionQuality(20);
        op.setToolbarColor(Color.parseColor("#176EB9"));
        op.setStatusBarColor(Color.parseColor("#176EB9"));

        UCrop.of(uri, Uri.fromFile(new File(getActivity().getCacheDir(), imageFileName)))
                .withAspectRatio(x, y)
                .withOptions(op)
                .start(getActivity(), this, requestCode);
    }

    protected void choicePic(int requestCode, float x, float y) {
//        if (requestCode >= 100) {
//            throw new IllegalArgumentException("request code must less than 100");
//        }
//
//        this.mX = x;
//        this.mY = y;
//
//        Disposable d = rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA).subscribe(granted -> {
//            if (granted) {
//                Matisse.from(this)
//                        .choose(MimeType.allOf())
//                        .capture(true)
//                        //适配7.0权限
//                        .captureStrategy(new CaptureStrategy(false, getActivity().getPackageName() + ".fileProvider"))
//                        .countable(false)
//                        .maxSelectable(1)
//                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                        .thumbnailScale(0.85f)
//                        .imageEngine(new Glide4Engine())
//                        .forResult(requestCode);
//            } else {
//                Toast.makeText(getActivity(), "请开启应用权限", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    protected void handlePic(int requestCode, int resultCode, Intent data, OnSelectPicListener listener) {
        if (data == null || resultCode != Activity.RESULT_OK || listener == null) {
            return;
        }
        if (requestCode >= 100) {
            int originCode = requestCode / 100;
            listener.onSelectPicResult(originCode, UCrop.getOutput(data));
        } else {
//            Uri uri = Matisse.obtainResult(data).get(0);
//            openCropActivity(uri, requestCode * 100, mX, mY);
        }
    }

    public interface OnSelectPicListener {
        void onSelectPicResult(int requestCode, Uri picUri);
    }

    private RxProgressHUD progressHUD;
    protected void showDialog() {
        Context context = getContext();
        if (context == null) {
            return;
        }

        if (progressHUD == null) {
            progressHUD = new RxProgressHUD.Builder(context)
                    .setTitle("")
                    .setMessage(context.getString(com.easymi.component.R.string.wait))
                    .setCancelable(false)
                    .create();
        }
        if (!progressHUD.isShowing()) {
            if (context instanceof Activity){
                if (((Activity) context).isFinishing()) {
                    return;
                }
            }
            progressHUD.show();
        }
    }

    protected void dismissDialog() {
        if (getActivity() != null && !getActivity().isFinishing() && progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    /**
     * 先上传图片然后在处理，图片上传后的hash值存储在集合中.
     * 保存上传图片后的哈希值,存储值与{@link RegisterModel#uploadPics}顺序相同.
     */
    protected void uploadAllPicsAndCommit(RegisterRequest request) {
        showDialog();
        List<String> picHash = new ArrayList<>();
        Observable<Pic> pics = RegisterModel.uploadPics(request);
        Subscription d = pics.subscribe(new Observer<Pic>() {
            @Override
            public void onCompleted() {
                Observable<RegisterRes> observable = RegisterModel.register(request, picHash);
                Subscription rd = observable.subscribe(new MySubscriber<>(getContext(), false, false, registerRes -> {
                    dismissDialog();
                    ToastUtil.showMessage(getContext(), "资料提交成功");
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }));
                mRxManager.add(rd);
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showMessage(getContext(), "图片上传失败，请重试");
                mRxManager.clear();
                dismissDialog();
            }

            @Override
            public void onNext(Pic pic) {
                picHash.add(pic.hashCode);
            }
        });
        mRxManager.add(d);
    }

    @Override
    public void onDetach() {
        dismissDialog();
        mRxManager.clear();
        super.onDetach();
    }

}
