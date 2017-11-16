package com.easymi.common.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.component.base.RxLazyFragment;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.TimePickerView;

/**
 * Created by liuzihao on 2017/11/16.
 */

public class CreateDJFragment extends RxLazyFragment {

    TextView timeText;
    EditText nameText;
    EditText phoneText;
    TextView startPlace;
    TextView endPlace;
    TextView esMoney;

    Button createOrder;

    @Override
    public int getLayoutResId() {
        return R.layout.daijia_create_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initView();
        isPrepared = false;
    }

    private void initView() {
        timeText = getActivity().findViewById(R.id.time_text);
        nameText = getActivity().findViewById(R.id.name_text);
        phoneText = getActivity().findViewById(R.id.phone_text);
        startPlace = getActivity().findViewById(R.id.start_place);
        endPlace = getActivity().findViewById(R.id.end_place);
        esMoney = getActivity().findViewById(R.id.es_money_text);
        createOrder = getActivity().findViewById(R.id.create_order);
        initClick();
    }

    private void initClick() {
        timeText.setOnClickListener(view -> showTimeDialog(timeText));
        startPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        endPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    CusBottomSheetDialog bottomSheetDialog;

    private void showTimeDialog(TextView tv) {
        bottomSheetDialog = new CusBottomSheetDialog(getActivity());
        TimePickerView timePickerView = new TimePickerView(getActivity());

        timePickerView.setPositiveButton(getString(R.string.confirm), (View v) -> {
            String day = timePickerView.getDayStr();
            String hourStr = timePickerView.getHourStr();
            String minStr = timePickerView.getMinStr();
            tv.setText(day + hourStr + minStr);
            bottomSheetDialog.dismiss();
        }).setNegativeButton(getString(R.string.cancel), v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(timePickerView);
        bottomSheetDialog.show();
    }

}
