package com.easymi.taxi.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.MathUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.LoadingButton;
import com.easymi.taxi.R;
import com.easymi.taxi.flowMvp.ActFraCommBridge;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by developerLzh on 2017/11/13 0013.
 */

public class CheatingFragment extends RxBaseFragment {
    private DymOrder zcOrder;

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

    private int addedKm;
    private double addedFee;

    DecimalFormat decimalFormat = new DecimalFormat("#0.0");


    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        zcOrder = (DymOrder) args.getSerializable("zcOrder");
    }

    @Override
    public int getLayoutResId() {
        return R.layout.taxi_cheating_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        initView();
    }

    private void initView() {

        decimalFormat.setRoundingMode(RoundingMode.DOWN);

        currentMileageTxt = $(R.id.current_mileage);
        currentFeeTxt = $(R.id.current_fee);
        mileageSubBtn = $(R.id.mileage_sub);
        mileageAddBtn = $(R.id.mileage_add);
        feeSubBtn = $(R.id.fee_sub);
        feeAddBtn = $(R.id.fee_add);
        sureChangeBtn = $(R.id.sure_change);
        cancelChangeBtn = $(R.id.cancel_change);
        mileageEdit = $(R.id.mileage_edit);
        feeEdit = $(R.id.fee_edit);
        changeEndCon = $(R.id.change_end_con);
        $(R.id.change_end_con).setOnClickListener(view -> bridge.changeEnd());

        DecimalFormat df1 = new DecimalFormat("#0.0");
        currentMileageTxt.setText("" + (zcOrder.distance - zcOrder.addedKm));
        currentFeeTxt.setText(df1.format(zcOrder.totalFee - zcOrder.addedFee));
        feeEdit.setText("" + zcOrder.addedFee);
        feeEdit.setSelection(feeEdit.getText().toString().length());
        mileageEdit.setText("" + zcOrder.addedKm);
        mileageEdit.setSelection(mileageEdit.getText().toString().length());

        getaddedKm();
        getAddedFee();

        initMileage();
        initFee();

        sureChangeBtn.setOnClickListener(view -> {
            zcOrder.addedKm = addedKm;
            zcOrder.addedFee = addedFee;
            zcOrder.updateCheating();
            PhoneUtil.hideKeyboard(getActivity());
            bridge.doUploadOrder();
        });
        cancelChangeBtn.setOnClickListener(view -> {
            PhoneUtil.hideKeyboard(getActivity());
            bridge.showDrive();
        });

        changeEndCon.setOnClickListener(view -> bridge.changeEnd());
    }

    private void initFee() {
        feeSubBtn.setOnClickListener(view -> {
            getaddedKm();
            if (addedFee >= 1) {
                addedFee--;
                feeEdit.setText("" + addedFee);
                feeEdit.setSelection(feeEdit.getText().toString().length());
            }
        });
        feeAddBtn.setOnClickListener(view -> {
            getaddedKm();
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
                } else {
                    addedFee = 0;
                }
            }
        });
    }

    private void initMileage() {
        mileageSubBtn.setOnClickListener(view -> {
            getaddedKm();
            if (addedKm >= 1) {
                addedKm--;
                mileageEdit.setText("" + addedKm);
                mileageEdit.setSelection(mileageEdit.getText().toString().length());
            }
        });
        mileageAddBtn.setOnClickListener(view -> {
            getaddedKm();
            if (addedKm <= 999) {
                addedKm++;
                mileageEdit.setText("" + addedKm);
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
                    getaddedKm();

                    if (!MathUtil.isDoubleLegal(addedKm, 1)) {
                        mileageEdit.setText(decimalFormat.format(addedKm));
                        mileageEdit.setSelection(decimalFormat.format(addedKm).length());
                    }
                    if (addedKm > 1000) {
                        mileageEdit.setText("" + 1000);
                        mileageEdit.setSelection(4);
                    }
                } else {
                    addedKm = 0;
                }
            }
        });
    }

    /**
     * 加的公里数
     */
    private void getaddedKm() {
        String s = mileageEdit.getText().toString();
        try {
            addedKm = Integer.parseInt(s);
        } catch (Exception e) {
            addedKm = 0;
        } finally {
            if (addedKm <= 0) {
                mileageSubBtn.setEnabled(false);
            } else if (addedKm >= 1000) {
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
