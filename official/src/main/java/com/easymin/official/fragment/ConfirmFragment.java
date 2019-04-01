package com.easymin.official.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.component.widget.LoadingButton;
import com.easymi.component.widget.switchButton.SwitchButton;
import com.easymin.official.R;
import com.easymin.official.activity.FeeDetailActivity;
import com.easymin.official.adapter.ConfirmAdapter;
import com.easymin.official.dialog.ChooseImageDialog;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.flowMvp.ActFraCommBridge;
import com.easymin.official.flowMvp.FlowActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ConfirmFragment
 * @Author: hufeng
 * @Date: 2019/3/26 上午11:52
 * @Description:
 * @History:
 */
public class ConfirmFragment extends RxBaseFragment {

    TextView tv_name;
    TextView tv_booktime;
    TextView tv_start_addrrs;
    TextView tv_end_addrrs;
    TextView tv_remark;
    SwitchButton switch_btn;
    RecyclerView recyclerView;
    TextView tv_money;
    TextView tv_detail;
    LoadingButton confirm_button;

    private ConfirmAdapter adapter;
    private ArrayList<String> images = new ArrayList<>();

    /**
     * 订单信息
     */
    private GovOrder govOrder;

    /**
     * activity和fragment的通信接口
     */
    private ActFraCommBridge bridge;

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge){
        this.bridge = bridge;
    }

    /**
     * 动态数据
     */
    private DymOrder dymOrder;
    /**
     * 金额数据格式化
     */
    DecimalFormat df = new DecimalFormat("#0.00");

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        govOrder = (GovOrder) args.getSerializable("govOrder");
        dymOrder = DymOrder.findByIDType(govOrder.orderId, govOrder.orderType);
        if (null == dymOrder) {
            if (govOrder.orderFee != null) {
                dymOrder = govOrder.orderFee;
            }
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.gw_fragment_confirm;
    }

    @Override
    public void finishCreateView(Bundle state) {
        findId();
        initAdapter();
        initListenner();

        tv_name.setText(govOrder.passengerName+" "+govOrder.passengerPhone);
        tv_booktime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", govOrder.bookTime*1000));
        tv_start_addrrs.setText(govOrder.getStartSite().addr);
        tv_end_addrrs.setText(govOrder.getEndSite().addr);
        tv_remark.setText(govOrder.remark);

        tv_money.setText("¥ "+df.format(dymOrder.totalFee));
    }

    public void findId() {
        tv_name = $(R.id.tv_name);
        tv_booktime = $(R.id.tv_booktime);
        tv_start_addrrs = $(R.id.tv_start_addrrs);
        tv_end_addrrs = $(R.id.tv_end_addrrs);
        tv_remark = $(R.id.tv_remark);
        switch_btn = $(R.id.switch_btn);
        recyclerView = $(R.id.recyclerView);
        tv_money = $(R.id.tv_money);
        tv_detail = $(R.id.tv_detail);
        confirm_button = $(R.id.confirm_button);
    }

    public void initAdapter() {
        adapter = new ConfirmAdapter(getContext());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setBaseOrders(images);
    }

    public void initListenner(){
        adapter.setOnItemClickListener((view, position) -> {
            if (view.getId() == R.id.iv_delete){
                images.remove(position);
                adapter.setBaseOrders(images);
            }else {
                ChooseImageDialog dialog = new ChooseImageDialog(getContext());
                dialog.setOnMyClickListener(view1 -> {
                    dialog.dismiss();
                    if (view1.getId() == R.id.tv_camera) {
                        ((FlowActivity)getContext()).takePictures(1,1);
                    } else if (view1.getId() == R.id.tv_photo) {
                        ((FlowActivity)getContext()).choicePic(1,1,9-images.size());
                    }
                });
                dialog.show();
            }
        });

        tv_detail.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FeeDetailActivity.class);
            intent.putExtra("dymOrder", dymOrder);
            intent.putExtra("govOrder", govOrder);
            getContext().startActivity(intent);
        });

        confirm_button.setOnClickListener(view -> {
            if (images.size() != 0){
                bridge.doConfirm(images);
            }else {
                ToastUtil.showMessage(getContext(),getContext().getResources().getString(R.string.gw_up_iamges));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> image = PictureSelector.obtainMultipleResult(data);
                if (image != null && image.size() > 0) {
                    for (LocalMedia localMedia : image) {
                        images.add(localMedia.getCutPath());
                    }
                    adapter.setBaseOrders(images);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}