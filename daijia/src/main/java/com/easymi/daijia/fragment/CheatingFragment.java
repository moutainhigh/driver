package com.easymi.daijia.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.MathUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.LoadingButton;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.flowMvp.ActFraCommBridge;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class CheatingFragment extends RxBaseFragment {
    private DymOrder djOrder;

    private ActFraCommBridge bridge;

    private TextView currentMileageTxt;
    private TextView currentFeeTxt;
    private ImageButton mileageSubBtn;
    private ImageButton mileageAddBtn;
    private ImageButton feeSubBtn;
    private ImageButton feeAddBtn;
    private LoadingButton sureChangeBtn;
    private Button cancelChangeBtn;
    private EditText mileageEdit;
    private EditText feeEdit;
    private LinearLayout changeEndCon;

    private double addedMileage;
    private double addedFee;

    DecimalFormat decimalFormat = new DecimalFormat("#0.0");


    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        djOrder = (DymOrder) args.getSerializable("djOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.cheating_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {

        decimalFormat.setRoundingMode(RoundingMode.DOWN);

        currentMileageTxt = getActivity().findViewById(R.id.current_mileage);
        currentFeeTxt = getActivity().findViewById(R.id.current_fee);
        mileageSubBtn = getActivity().findViewById(R.id.mileage_sub);
        mileageAddBtn = getActivity().findViewById(R.id.mileage_add);
        feeSubBtn = getActivity().findViewById(R.id.fee_sub);
        feeAddBtn = getActivity().findViewById(R.id.fee_add);
        sureChangeBtn = getActivity().findViewById(R.id.sure_change);
        cancelChangeBtn = getActivity().findViewById(R.id.cancel_change);
        mileageEdit = getActivity().findViewById(R.id.mileage_edit);
        feeEdit = getActivity().findViewById(R.id.fee_edit);
        changeEndCon = getActivity().findViewById(R.id.change_end_con);
        getActivity().findViewById(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());

        currentMileageTxt.setText("" + djOrder.distance);
        currentFeeTxt.setText("" + djOrder.totalFee);
        feeEdit.setText("" + djOrder.addedFee);
        feeEdit.setSelection(feeEdit.getText().toString().length());
        mileageEdit.setText("" + djOrder.addedKm);
        mileageEdit.setSelection(mileageEdit.getText().toString().length());

        getAddedMileage();
        getAddedFee();

        initMileage();
        initFee();

        sureChangeBtn.setOnClickListener(view -> bridge.doUploadOrder(addedFee, (int) addedMileage));
        cancelChangeBtn.setOnClickListener(view -> bridge.showDrive());

        changeEndCon.setOnClickListener(view -> bridge.changeEnd());
    }

    private void initFee() {
        feeSubBtn.setOnClickListener(view -> {
            getAddedMileage();
            if (addedFee >= 1) {
                addedFee--;
                feeEdit.setText("" + addedFee);
                feeEdit.setSelection(feeEdit.getText().toString().length());
            }
        });
        feeAddBtn.setOnClickListener(view -> {
            getAddedMileage();
            if (addedFee <= 999) {
                addedFee++;
                feeEdit.setText("" + addedFee);
                feeEdit.setSelection(feeEdit.getText().toString().length());
            }
        });
        feeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (StringUtils.isNotBlank(str)) {
                    if (str.startsWith("0")) {
                        if (str.length() >= 2) {
                            if (!String.valueOf(str.charAt(1)).equals(".")) {
                                str = str.substring(1, str.length());
                                feeEdit.setText(str);
                                feeEdit.setSelection(str.length());
                            }
                        }
                    }
                    getAddedFee();

                    if (!MathUtil.isDoubleLegal(addedFee, 1)) {
                        feeEdit.setText(decimalFormat.format(addedFee));
                        feeEdit.setSelection(decimalFormat.format(addedFee).length());
                    }
                    if (addedFee > 1000) {
                        feeEdit.setText("" + 1000);
                        feeEdit.setSelection(4);
                    }
                }
            }
        });
    }

    private void initMileage() {
        mileageSubBtn.setOnClickListener(view -> {
            getAddedMileage();
            if (addedMileage >= 1) {
                addedMileage--;
                mileageEdit.setText("" + addedMileage);
                mileageEdit.setSelection(mileageEdit.getText().toString().length());
            }
        });
        mileageAddBtn.setOnClickListener(view -> {
            getAddedMileage();
            if (addedMileage <= 999) {
                addedMileage++;
                mileageEdit.setText("" + addedMileage);
                mileageEdit.setSelection(mileageEdit.getText().toString().length());
            }
        });
        mileageEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (StringUtils.isNotBlank(str)) {
                    if (str.startsWith("0")) {
                        if (str.length() >= 2) {
                            if (!String.valueOf(str.charAt(1)).equals(".")) {
                                str = str.substring(1, str.length());
                                mileageEdit.setText(str);
                                mileageEdit.setSelection(str.length());
                            }
                        }
                    }
                    getAddedMileage();

                    if (!MathUtil.isDoubleLegal(addedMileage, 1)) {
                        mileageEdit.setText(decimalFormat.format(addedMileage));
                        mileageEdit.setSelection(decimalFormat.format(addedMileage).length());
                    }
                    if (addedMileage > 1000) {
                        mileageEdit.setText("" + 1000);
                        mileageEdit.setSelection(4);
                    }
                }
            }
        });
    }

    /**
     * 加的公里数
     */
    private void getAddedMileage() {
        String s = mileageEdit.getText().toString();
        try {
            addedMileage = Double.parseDouble(s);
        } catch (Exception e) {
            addedMileage = 0;
        } finally {
            if (addedMileage <= 0) {
                mileageSubBtn.setEnabled(false);
            } else if (addedMileage >= 1000) {
                mileageAddBtn.setEnabled(false);
            } else {
                mileageSubBtn.setEnabled(true);
                mileageAddBtn.setEnabled(true);
            }
        }
    }

    /**
     * 加的费用
     */
    private void getAddedFee() {
        String s = feeEdit.getText().toString();
        try {
            addedFee = Double.parseDouble(s);
        } catch (Exception e) {
            addedFee = 0;
        } finally {
            if (addedFee <= 0) {
                feeSubBtn.setEnabled(false);
            } else if (addedFee >= 1000) {
                feeAddBtn.setEnabled(false);
            } else {
                feeSubBtn.setEnabled(true);
                feeAddBtn.setEnabled(true);
            }
        }
    }
}
