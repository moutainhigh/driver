package com.easymi.personal.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.activity.WebActivity;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.personal.R;
import com.easymi.personal.activity.ArticleActivity;
import com.easymi.personal.activity.HelpCenterActivity;
import com.easymi.personal.activity.LoginActivity;
import com.easymi.personal.activity.NearWcActivity;
import com.easymi.personal.activity.ReliActivity;
import com.easymi.personal.activity.SetActivity;
import com.easymi.personal.activity.SysCheck2Activity;
import com.easymi.personal.activity.SysCheckActivity;
import com.easymi.personal.activity.WeatherActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/1.
 */
public class GridAdapter extends BaseAdapter {

    List<String> stringList;

    Activity context;

    public GridAdapter(Activity context) {
        this.context = context;
        stringList = new ArrayList<>();
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_more, parent, false);
            holder = new ViewHolder();
            holder.item_pic = convertView
                    .findViewById(R.id.item_pic);
            holder.item_txt = convertView.findViewById(R.id.item_txt);
            holder.item_root = convertView.findViewById(R.id.item_root);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String str = stringList.get(position);
        if (StringUtils.isBlank(str)) {
            return convertView;
        }
        holder.item_txt.setText(str);
        if (str.equals(context.getString(R.string.near_wc))) {
            holder.item_pic.setImageResource(R.mipmap.near_wc);
            holder.item_root.setOnClickListener(v -> {
                Intent intent = new Intent(context, NearWcActivity.class);
                context.startActivity(intent);
            });
        } else if (str.equals(context.getString(R.string.reli_pic))) {
            holder.item_pic.setImageResource(R.mipmap.hot_img);
//            holder.item_root.setOnClickListener(v -> {
//                Intent intent = new Intent(context, ReliActivity.class);
//                context.startActivity(intent);
//            });
        } else if (str.equals(context.getString(R.string.weather_forecast))) {
            holder.item_pic.setImageResource(R.mipmap.weather_report);
            holder.item_root.setOnClickListener(v -> {
                Intent intent = new Intent(context, WeatherActivity.class);
                context.startActivity(intent);
            });
        } else if (str.equals(context.getString(R.string.contract_service))) {
            holder.item_pic.setImageResource(R.mipmap.contract_service);
            holder.item_root.setOnClickListener(v ->
//                    PhoneUtil.call(context, EmUtil.getEmployInfo().company_phone));
            PhoneUtil.call(context, "1111111"));
        }else if (str.equals(context.getString(R.string.help_center))) {
            holder.item_pic.setImageResource(R.mipmap.p_help);
            holder.item_root.setOnClickListener(view -> {
//                Intent intent = new Intent(context, HelpCenterActivity.class);
//                context.startActivity(intent);
//                Intent intent = new Intent(context, ArticleActivity.class);
//                intent.putExtra("tag", "driverHelp");
//                intent.putExtra("title", context.getString(R.string.set_help));
//                context.startActivity(intent);
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", "http://h5.xiaokakj.com/#/protocol?articleName=driverHelp&appKey="+ Config.APP_KEY);
                intent.putExtra("title", context.getString(R.string.set_help));
                context.startActivity(intent);
            });
        }else if (str.equals(context.getString(R.string.sys_check))) {
            holder.item_pic.setImageResource(R.mipmap.p_sys_check);
            holder.item_root.setOnClickListener(view -> {
                Intent intent = new Intent(context, SysCheckActivity.class);
                context.startActivity(intent);
            });
        }
        return convertView;
    }

    final class ViewHolder {
        public ImageView item_pic;
        public TextView item_txt;
        public LinearLayout item_root;
    }

}
