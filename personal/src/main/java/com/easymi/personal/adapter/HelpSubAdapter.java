package com.easymi.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.personal.R;
import com.easymi.personal.activity.ArticleActivity;
import com.easymi.personal.entity.HelpMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2018/3/1.
 */

public class HelpSubAdapter extends RecyclerView.Adapter<HelpSubAdapter.ViewHolder> {

    private Context context;
    private List<HelpMenu> menus;

    public HelpSubAdapter(Context context) {
        this.context = context;
        menus = new ArrayList<>();
    }

    public void setMenus(List<HelpMenu> menus) {
        this.menus = menus;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_help, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HelpMenu menu = menus.get(position);
        holder.menuNameText.setText(menu.menuName);
        holder.root.setOnClickListener(view -> {
            Intent intent = new Intent(context, ArticleActivity.class);
            intent.putExtra("articleId", menu.id);
            intent.putExtra("title", context.getString(R.string.set_help));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView menuNameText;
        private View root;

        public ViewHolder(View itemView) {
            super(itemView);
            menuNameText = itemView.findViewById(R.id.menu_name);
            root = itemView;
        }
    }
}
