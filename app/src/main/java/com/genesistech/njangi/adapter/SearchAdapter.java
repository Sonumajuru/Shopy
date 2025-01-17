package com.genesistech.njangi.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.genesistech.njangi.Controller;
import com.genesistech.njangi.R;
import com.genesistech.njangi.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private final Controller controller;
    private final List<Product> productNamesList;
    private final ArrayList<Product> arraylist;
    public SearchAdapter(Context context, List<Product> productNamesList) {
        mContext = context;
        controller = Controller.getInstance(context);
        this.productNamesList = productNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(productNamesList);
    }
    public static class ViewHolder {
        TextView title;
    }
    @Override
    public int getCount() {
        return productNamesList.size();
    }
    @Override
    public Product getItem(int position) {
        return productNamesList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            // Locate the TextViews in listview_item.xml
            holder.title = view.findViewById(R.id.name);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        controller.setTextLength(holder.title);
        holder.title.setText(productNamesList.get(position).getTitle());
        return view;
    }
    // Filter Class
    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        productNamesList.clear();
        if (charText.length() == 0) {
            productNamesList.addAll(arraylist);
        }
        else {
            for (Product wp : arraylist) {
                if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    productNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}