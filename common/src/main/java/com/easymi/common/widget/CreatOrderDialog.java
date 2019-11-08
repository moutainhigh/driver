package com.easymi.common.widget;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.entity.Employ;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.dialog.BaseBottomDialog;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CreatOrderDialog
 * @Author: hufeng
 * @Date: 2019/5/21 上午10:11
 * @Description:
 * @History:
 */
public class CreatOrderDialog extends BaseBottomDialog {

    ImageView iv_cancel;
    TextView tv_server_type;

    public CreatOrderDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_dialog_creat_order);

        iv_cancel = findViewById(R.id.iv_cancel);
        tv_server_type = findViewById(R.id.tv_server_type);

        iv_cancel.setOnClickListener(view -> {
            dismiss();
        });

        Employ employ = EmUtil.getEmployInfo();

        if (employ.serviceType.equals(Config.ZHUANCHE)) {
            if (employ.status == 2) {
                tv_server_type.setText(context.getResources().getString(R.string.com_zhuanche));
                tv_server_type.setTextColor(context.getResources().getColor(R.color.color_333333));
            } else if (employ.status == 3) {
                tv_server_type.setText(context.getResources().getString(R.string.com_zhuanche) + "(上线状态无法补单)");
                tv_server_type.setTextColor(context.getResources().getColor(R.color.color_999999));
            }
        } else if (employ.serviceType.equals(Config.CITY_LINE)) {
            tv_server_type.setText(context.getResources().getString(R.string.create_zhuanxian));
        } else if (employ.serviceType.equals(Config.CARPOOL)) {
            tv_server_type.setText(context.getResources().getString(R.string.create_carpool));
        } else if (employ.serviceType.equals(Config.COUNTRY)) {
            tv_server_type.setText(context.getResources().getString(R.string.create_bus_country));
        }

        tv_server_type.setOnClickListener(view -> {
            if ((employ.serviceType.equals(Config.ZHUANCHE) && employ.status == 2) ||
                    employ.serviceType.equals(Config.CITY_LINE) ||
                    employ.serviceType.equals(Config.CARPOOL) ||
                    employ.serviceType.equals(Config.COUNTRY)) {
                if (onMyClickListener != null) {
                    onMyClickListener.onItemClick(view);
                }
            }
        });
    }
}
