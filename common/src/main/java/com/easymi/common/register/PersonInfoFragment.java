package com.easymi.common.register;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.common.R;
import com.easymi.common.widget.ImvDialog;
import com.easymi.component.utils.ToastUtil;

/**
 * @author hufeng
 * 已废弃
 */
public class PersonInfoFragment extends AbsRegisterFragment {

    private static final int REQUEST_CARD1 = 1;
    private static final int REQUEST_CARD2 = 2;
    private static final int REQUEST_DRIVER = 3;
    private static final int REQUEST_BODY = 4;

    private RequestOptions options = new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    private RegisterRequest registerRequest;

    public static PersonInfoFragment newInstance(RegisterRequest registerRequest) {
        PersonInfoFragment fragment = new PersonInfoFragment();
        Bundle arguments = new Bundle();
        arguments.putParcelable("registerRequest", registerRequest);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.com_fragment_person_info, container, false);
        bindViews(view);
        Bundle arguments = getArguments();
        if (arguments != null) {
            registerRequest = arguments.getParcelable("registerRequest");
            if (registerRequest != null && registerRequest.needCarInfo) {
                btnNext.setText(R.string.com_next);
            } else {
                btnNext.setText(R.string.com_commit);
            }
        }
        return view;
    }

    /**
     * 图片选择回调。
     */
    private OnSelectPicListener onSelectPicListener = new OnSelectPicListener() {
        @Override
        public void onSelectPicResult(int requestCode, Uri picUri) {
            if (registerRequest == null) {
                return;
            }
            ImageView imv;
            switch (requestCode) {
                case REQUEST_CARD1:
                    imv = imvCard1;
                    registerRequest.idCardPath = picUri.getPath();
                    break;
                case REQUEST_CARD2:
                    imv = imvCard2;
                    registerRequest.idCardBackPath = picUri.getPath();
                    break;
                case REQUEST_DRIVER:
                    imv = driverPic;
                    registerRequest.driveLicensePath = picUri.getPath();
                    break;
                case REQUEST_BODY:
                    imv = bodyPic;
                    registerRequest.fullBodyPath = picUri.getPath();
                    break;
                default:
                    return;
            }
            Glide.with(PersonInfoFragment.this)
                    .load(picUri)
                    .apply(options)
                    .into(imv);
        }
    };

    private ImageView imvCard1;
    private ImageView imvCard2;
    private ImageView driverPic;
    private ImageView bodyPic;
    private Button btnNext;

    private void bindViews(View view) {
        imvCard1 = view.findViewById(R.id.imvCard1);
        imvCard2 = view.findViewById(R.id.imvCard2);
        driverPic = view.findViewById(R.id.driverPic);
        bodyPic = view.findViewById(R.id.bodyPic);
        btnNext = view.findViewById(R.id.next);

        imvCard1.setOnClickListener(v -> choicePic(REQUEST_CARD1, 1.6f, 1));
        imvCard2.setOnClickListener(v -> choicePic(REQUEST_CARD2, 1.6f, 1));
        driverPic.setOnClickListener(v -> choicePic(REQUEST_DRIVER, 2.5f, 1));
        bodyPic.setOnClickListener(v -> choicePic(REQUEST_BODY, 0.8f, 1));
        btnNext.setOnClickListener(v -> next());

        if (getFragmentManager() == null) {
            return;
        }
        view.findViewById(R.id.tvIdCard2).setOnClickListener(v -> ImvDialog.newInstance(R.mipmap.com_idcard2).show(getFragmentManager(), ""));
        view.findViewById(R.id.tvIdCard).setOnClickListener(v -> ImvDialog.newInstance(R.mipmap.com_idcard).show(getFragmentManager(), ""));
        view.findViewById(R.id.tvDriver).setOnClickListener(v -> ImvDialog.newInstance(R.mipmap.com_driver).show(getFragmentManager(), ""));
        view.findViewById(R.id.tvBody).setOnClickListener(v -> ImvDialog.newInstance(R.mipmap.com_pic_body).show(getFragmentManager(), ""));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || resultCode != Activity.RESULT_OK) {
            return;
        }
        handlePic(requestCode, resultCode, data, onSelectPicListener);
    }

    private void next() {
        //check
        if (registerRequest == null) {
            ToastUtil.showMessage(getActivity(), "参数异常");
            return;
        }
        if (registerRequest.idCardPath == null) {
            ToastUtil.showMessage(getActivity(), "未上传身份证正面");
            return;
        }
        if (registerRequest.idCardBackPath == null) {
            ToastUtil.showMessage(getActivity(), "未上传身份证反面");
            return;
        }
        if (registerRequest.driveLicensePath == null) {
            ToastUtil.showMessage(getActivity(), "未上传驾驶证");
            return;
        }
        if (registerRequest.fullBodyPath == null) {
            ToastUtil.showMessage(getActivity(), "未上传全身照");
            return;
        }

        if (!registerRequest.needCarInfo) {
            uploadAllPicsAndCommit(registerRequest);
        } else {
            //填写车辆信息
            FragmentManager fm = getFragmentManager();
            if (fm != null) {
                CarInfoFragment fragment = CarInfoFragment.newInstance(registerRequest);
                FragmentTransaction ts = fm.beginTransaction();
                ts.hide(PersonInfoFragment.this).add(R.id.registerContainer, fragment, fragment.getClass().getName());
                ts.addToBackStack(null);
                ts.commitAllowingStateLoss();
            }
        }
    }

}
