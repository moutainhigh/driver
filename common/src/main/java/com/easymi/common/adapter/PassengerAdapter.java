package com.easymi.common.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.PassengerBean;

public class PassengerAdapter extends BaseQuickAdapter<PassengerBean, BaseViewHolder> {

    boolean isSelect;

    public PassengerAdapter(boolean isSelect) {
        super(R.layout.item_passenger_select_content);
        this.isSelect = isSelect;
    }

    @Override
    protected void convert(BaseViewHolder helper, PassengerBean item) {
        ImageView itemPassengerSelectContentIv = helper.getView(R.id.itemPassengerSelectContentIv);
        if (isSelect) {
            itemPassengerSelectContentIv.setSelected(item.isSelect);
            helper.addOnClickListener(R.id.itemPassengerSelectContentRl);
        } else {
            itemPassengerSelectContentIv.setVisibility(View.GONE);
        }

        helper.setText(R.id.itemPassengerSelectContentTvName, item.riderName)
                .setGone(R.id.itemPassengerSelectContentV, helper.getAdapterPosition() != 0)
                .setGone(R.id.itemPassengerSelectContentTvGuardianName, item.riderType != 1)
                .setText(R.id.itemPassengerSelectContentTvGuardianName, "监护人姓名: " + item.guardianName)
                .setText(R.id.itemPassengerSelectContentTvDesc, item.riderType == 1 ? "成人" : "儿童")
                .setText(R.id.itemPassengerSelectContentTvPhone, item.riderType == 1 ? "乘车人电话: " + item.riderPhone : "监护人电话: " + item.guardianPhone)
                .setText(R.id.itemPassengerSelectContentTvId, item.riderType == 1 ? "乘车人身份证号: " + item.riderCard : "监护人身份证号: " + item.guardianCard)
                .addOnClickListener(R.id.itemPassengerSelectContentTvDelete);
    }
}
