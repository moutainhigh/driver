package com.easymi.common.adapter;

import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.CityLine;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.mvp.order.OrderActivity;
import com.easymi.common.util.DJStatus2Str;
import com.easymi.component.Config;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;

import java.util.List;

/**
 *
 * @author hiwhitley
 * @date 2016/10/17
 */

public class CityLineAdapter extends BaseMultiItemQuickAdapter<CityLine, BaseViewHolder> {
    private Context context;

    public CityLineAdapter(List<CityLine> data, Context context) {
        super(data);
        this.context = context;
        addItemType(MultipleOrder.ITEM_HEADER, R.layout.order_pinned_layout);
        addItemType(MultipleOrder.ITEM_POSTER, R.layout.order_item);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, CityLine cityLine) {
        if (cityLine.getItemType() == CityLine.ITEM_HEADER) {
            baseViewHolder.setText(R.id.pinned_text, "订单信息");
            baseViewHolder.itemView.setOnClickListener(v -> {
                context.startActivity(new Intent(context,OrderActivity.class));
            });
        } else if (cityLine.getItemType() == CityLine.ITEM_POSTER) {
            baseViewHolder.setText(R.id.order_time, TimeUtil.getTime("MM月dd日 HH:mm",TimeUtil.parseTime(TimeUtil.YMD_HM,cityLine.day+" "+cityLine.hour)));
            baseViewHolder.setText(R.id.order_start_place, "" + cityLine.startAddress);
            baseViewHolder.setText(R.id.order_end_place, cityLine.endAddress);
            baseViewHolder.setText(R.id.order_status, cityLine.getStatus()+" >");
            baseViewHolder.setText(R.id.order_type, "城际专线");
            baseViewHolder.itemView.setOnClickListener(v -> {
                //todo 跳转到专线模块
            });
        }
    }
}
