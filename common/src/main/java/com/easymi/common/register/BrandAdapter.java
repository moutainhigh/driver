package com.easymi.common.register;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.common.entity.Brands;
import com.easymi.component.decoration.Sticky;
import com.easymi.component.decoration.StickyAdapter;

import java.util.ArrayList;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> implements StickyAdapter {

    private List<Brands.Brand> brandList = new ArrayList<>();

    public void setDatas(List<Brands.Brand> brands) {
        brandList.clear();
        if (brands != null && !brands.isEmpty()) {
            brandList.addAll(brands);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.com_item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Brands.Brand brand = brandList.get(position);
        holder.tv.setText(brand.chinese);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    @Override
    public Sticky getSticky(int position) {
        if (brandList == null || position >= brandList.size() || position < 0) {
            return null;
        }
        return (Sticky) brandList.get(position);
    }

    class BrandViewHolder extends RecyclerView.ViewHolder {
        private int mPosition;

        private TextView tv;

        BrandViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onBrandClick(brandList.get(mPosition));
                    }
                }
            });
        }

        private void bind(int position) {
            this.mPosition = position;
        }
    }

    private OnBrandClickListener listener;

    public void setOnBrandClickListener(OnBrandClickListener listener) {
        this.listener = listener;
    }

    public interface OnBrandClickListener {
        void onBrandClick(Brands.Brand brand);
    }


}
