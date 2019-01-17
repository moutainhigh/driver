package com.easymi.common.register;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.easymi.common.R;
import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;

import java.util.List;

/**
 * @author hufeng
 * 已废弃
 */
public class StringPicker extends BottomSheetDialog {

    private View mView;
    private WheelView wheelView;
    private OnSelectListener onSelectListener;
    private List<String> stringList;

    public StringPicker(@NonNull Context context, List<String> stringList) {
        super(context);
        this.stringList = stringList;
        initViews(context, stringList);
    }

    @Override
    public void show() {
        View v = (View) mView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        //禁止下滑手势
        behavior.setHideable(false);
        super.show();
    }

    private void initViews(Context context, List<String> stringList) {
        mView = LayoutInflater.from(context).inflate(R.layout.com_layout_picker_item, null);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.btn_confirm).setOnClickListener(v -> ensure());
        wheelView = mView.findViewById(R.id.wheelView);

        TimeWheelAdapter adapter = new TimeWheelAdapter(stringList, wheelView.getContext());
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
        String content = null;
        if (index >= 0 && index < stringList.size()) {
            content = stringList.get(index);
        }
        if (onSelectListener != null && content != null) {
            onSelectListener.onSelect(content);
        }
    }

    public interface OnSelectListener {
        void onSelect(String name);
    }

    public StringPicker setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    private class TimeWheelAdapter extends AbstractWheelTextAdapter {

        private List<String> datas;

        TimeWheelAdapter(List<String> datas, Context context) {
            super(context);
            this.datas = datas;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return datas != null ? datas.get(index) : "";
        }

        @Override
        public int getItemsCount() {
            return datas != null ? datas.size() : 0;
        }
    }

}
