package com.example.shopy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.shopy.R;
import com.example.shopy.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryAdapter extends BaseAdapter {

    // Declare Variables

    private Context cxt;
    private LayoutInflater inflater;
    private final List<Product> productNamesList;
    private final ArrayList<Product> arraylist;

    public CategoryAdapter(Context context, List<Product> productNamesList) {
        cxt = context;
        this.productNamesList = productNamesList;
        inflater = LayoutInflater.from(cxt);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(productNamesList);
    }

    public static class ViewHolder {
        TextView name;
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

    private String getTranslation(String text)
    {
        switch (text) {
            case "Books":
                text = cxt.getString(R.string.books);
                break;
            case "Clothing":
                text = cxt.getString(R.string.clothing);
                break;
            case "Computers":
                text = cxt.getString(R.string.computer);
                break;
            case "Electronic":
                text = cxt.getString(R.string.electronics);
                break;
            case "Games":
                text = cxt.getString(R.string.games);
                break;
            case "Home Appliances":
                text = cxt.getString(R.string.home_appliance);
                break;
            case "Phones":
                text = cxt.getString(R.string.phones);
                break;
        }
        return text;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            // Locate the TextViews in listview_item.xml
            holder.name = view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(getTranslation(productNamesList.get(position).getCategory()));
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        productNamesList.clear();
        if (charText.length() == 0) {
            productNamesList.addAll(arraylist);
        } else {
            for (Product wp : arraylist) {
                if (wp.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
                    productNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}