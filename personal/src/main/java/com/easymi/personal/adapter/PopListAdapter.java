package com.easymi.personal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.easymi.personal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/2.
 */

public class PopListAdapter extends BaseAdapter {

    private Context context;

    private List<String> strList;

    public PopListAdapter(Context context) {
        this.context = context;
        strList = new ArrayList<>();
    }

    public void setStrList(List<String> strList) {
        this.strList = strList;
    }

    @Override
    public int getCount() {
        return strList.size();
    }

    @Override
    public Object getItem(int i) {
        return strList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            holder.textView = (TextView) view.findViewById(R.id.text1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final String str = strList.get(i);
        holder.textView.setText(str);
        return view;
    }

    private class ViewHolder {
        private TextView textView;
    }

}
