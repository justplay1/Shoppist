/*
 * Copyright (C) 2016 Mkhytar Mkhoian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.justplay1.shoppist.features.lists.items.add;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.justplay1.shoppist.R;
import com.justplay1.shoppist.models.ProductViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mkhytar Mkhoian.
 */
public class AutoCompleteTextAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private OnAddButtonClickListener listener;
    private Map<String, ProductViewModel> products;
    private List<SpannableStringBuilder> productsToDisplay;

    private String currentText = "";
    private String notEqualText = "";

    public AutoCompleteTextAdapter(Context context) {
        this.context = context;
        products = new HashMap<>();
        productsToDisplay = new ArrayList<>();
    }

    public ProductViewModel getProduct(String name) {
        return products.get(name.toLowerCase());
    }

    public String getCurrentText() {
        return currentText;
    }

    public void setNotEqualText(String notEqual) {
        this.notEqualText = notEqual;
    }

    @Override
    public int getCount() {
        return productsToDisplay.size();
    }

    @Override
    public String getItem(int position) {
        return productsToDisplay.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return productsToDisplay.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_avto_complete_text, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (productsToDisplay.get(position).toString().equals(currentText)) {
            holder.addBtn.setVisibility(View.VISIBLE);
        } else {
            holder.addBtn.setVisibility(View.GONE);
        }
        holder.addBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.OnAddButtonClick(productsToDisplay.get(position).toString());
            }
        });
        holder.name.setText(productsToDisplay.get(position), TextView.BufferType.SPANNABLE);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = products;
                    results.count = products.size();
                } else {
                    String prefixString = constraint.toString().toLowerCase();
                    final List<SpannableStringBuilder> newValues = new ArrayList<>();

                    if (prefixString.equals(notEqualText.toLowerCase())) {
                        return results;
                    }

                    if (!products.containsKey(prefixString)) {
                        currentText = constraint.toString();
                        newValues.add(getSpannableStringBuilder(constraint.toString(), 0, constraint.length()));
                    }

                    for (final ProductViewModel value : products.values()) {
                        final String valueText = value.getName().toLowerCase();

                        if (valueText.startsWith(prefixString)) {
                            newValues.add(getSpannableStringBuilder(value.getName(), 0, prefixString.length()));
                        } else {
                            final String[] words = valueText.split(" ");

                            // Start at index 0, in case valueText starts with space(s)
                            for (String word : words) {
                                if (word.startsWith(prefixString)) {
                                    int start = valueText.length() - word.length();
                                    newValues.add(getSpannableStringBuilder(value.getName(), start, start + prefixString.length()));
                                    break;
                                }
                            }
                        }
                    }
                    results.values = newValues;
                    results.count = newValues.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (constraint == null) return;
                if (results.values == null) return;

                productsToDisplay = (List<SpannableStringBuilder>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }

    private SpannableStringBuilder getSpannableStringBuilder(String value, int start, int end) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        SpannableString redSpannable = new SpannableString(value);
        redSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        redSpannable.setSpan(new ForegroundColorSpan(Color.BLACK),
                start, end, 0);
        builder.append(redSpannable);
        return builder;
    }

    public void setData(Map<String, ProductViewModel> data) {
        products = data;
        notifyDataSetChanged();
    }

    public void setListener(OnAddButtonClickListener listener) {
        this.listener = listener;
    }

    public interface OnAddButtonClickListener {

        void OnAddButtonClick(String name);
    }

    public static class ViewHolder {
        @Bind(R.id.title)
        TextView name;
        @Bind(R.id.add_btn)
        ImageView addBtn;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
