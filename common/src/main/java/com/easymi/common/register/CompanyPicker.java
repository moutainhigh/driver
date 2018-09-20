package com.easymi.common.register;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.easymi.common.R;
import com.easymi.common.entity.CompanyList;
import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;

import java.util.List;

public class CompanyPicker extends BottomSheetDialog {

    private View mView;
    private WheelView wheelView;
    private OnSelectListener onSelectListener;
    private List<CompanyList.Company> mCompanies;

    public CompanyPicker(@NonNull Context context, @NonNull List<CompanyList.Company> companies) {
        super(context);
        this.mCompanies = companies;
        initViews(context, companies);
    }

    @Override
    public void show() {
        View v = (View) mView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        //禁止下滑手势
        behavior.setHideable(false);
        super.show();
    }

    private void initViews(Context context, List<CompanyList.Company> companies) {
        mView = LayoutInflater.from(context).inflate(R.layout.com_layout_picker_item, null);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.btn_confirm).setOnClickListener(v -> ensure());
        wheelView = mView.findViewById(R.id.wheelView);

        TimeWheelAdapter adapter = new TimeWheelAdapter(companies, wheelView.getContext());
        adapter.setItemResource(R.layout.com_item_picker);
        adapter.setItemTextResource(R.id.tvContent);
        wheelView.setCyclic(false);
        wheelView.setCurrentItem(0);
        wheelView.setViewAdapter(adapter);

        setCancelable(true);
        setContentView(mView);

    }

    private void ensure() {
        dismiss();
        int index = wheelView.getCurrentItem();
        CompanyList.Company company = null;
        if (index >= 0 && index < mCompanies.size()) {
            company = mCompanies.get(index);
        }
        if (onSelectListener != null && company != null) {
            onSelectListener.onSelect(company);
        }
    }

    public interface OnSelectListener {
        void onSelect(CompanyList.Company company);
    }

    public CompanyPicker setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    private class TimeWheelAdapter extends AbstractWheelTextAdapter {

        private List<CompanyList.Company> datas;

        TimeWheelAdapter(List<CompanyList.Company> datas, Context context) {
            super(context);
            this.datas = datas;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return datas != null ? datas.get(index).companyName : "";
        }

        @Override
        public int getItemsCount() {
            return datas != null ? datas.size() : 0;
        }
    }


}
