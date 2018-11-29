package com.easymi.taxi.fragment;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.GlideCircleTransform;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.R;
import com.easymi.taxi.entity.TaxiOrder;
import com.easymi.taxi.flowMvp.ActFraCommBridge;
import com.easymi.taxi.widget.CallPhoneDialog;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class ArriveStartFragment extends RxBaseFragment {
    private TaxiOrder taxiOrder;

    private ActFraCommBridge bridge;

    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    LoadingButton startDrive;
    LinearLayout startWait;
    TextView startPlaceText;
    TextView endPlaceText;
    FrameLayout callPhoneCon;

    ImageView customHead;
    TextView customName;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        taxiOrder = (TaxiOrder) args.getSerializable("taxiOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.taxi_arrive_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {
        startPlaceText = $(R.id.start_place);
        endPlaceText = $(R.id.end_place);
        startDrive = $(R.id.start_drive);
        startWait = $(R.id.start_wait);
        callPhoneCon = $(R.id.call_phone_con);

        customHead = $(R.id.iv_head);
        customName = $(R.id.tv_custom_name);
        //todo 差客户头像
        customName.setText(taxiOrder.passengerName);

        if (StringUtils.isNotBlank(taxiOrder.avatar)) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .transform(new GlideCircleTransform())
                    .placeholder(R.mipmap.ic_customer_head)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this)
                    .load(Config.IMG_SERVER + taxiOrder.avatar + Config.IMG_PATH)
                    .apply(options)
                    .into(customHead);
        }

        startPlaceText.setText(taxiOrder.getStartSite().address);
        endPlaceText.setText(taxiOrder.getEndSite().address);
        startDrive.setOnClickListener(view -> bridge.doStartDrive(startDrive));
        startWait.setOnClickListener(view -> bridge.doStartWait());
        callPhoneCon.setOnClickListener(view -> CallPhoneDialog.callDialog(getActivity(), taxiOrder));
        $(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());
    }
}
