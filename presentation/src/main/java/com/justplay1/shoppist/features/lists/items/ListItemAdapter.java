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

package com.justplay1.shoppist.features.lists.items;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.justplay1.shoppist.R;
import com.justplay1.shoppist.models.BaseViewModel;
import com.justplay1.shoppist.models.CurrencyViewModel;
import com.justplay1.shoppist.models.HeaderViewModel;
import com.justplay1.shoppist.models.ItemType;
import com.justplay1.shoppist.models.ListItemViewModel;
import com.justplay1.shoppist.models.Priority;
import com.justplay1.shoppist.models.SortType;
import com.justplay1.shoppist.models.UnitViewModel;
import com.justplay1.shoppist.preferences.AppPreferences;
import com.justplay1.shoppist.shared.base.adapters.BaseGroupSwipeableItemAdapter;
import com.justplay1.shoppist.shared.widget.actionmode.ActionModeInteractionListener;
import com.justplay1.shoppist.shared.widget.recyclerview.holders.BaseHeaderHolder;
import com.justplay1.shoppist.shared.widget.recyclerview.holders.BaseSwipeableItemViewHolder;
import com.justplay1.shoppist.utils.ExpandUtils;
import com.justplay1.shoppist.utils.ShoppistUtils;
import com.justplay1.shoppist.utils.ViewUtils;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Mkhytar Mkhoian.
 */
public class ListItemAdapter extends
    BaseGroupSwipeableItemAdapter<ListItemViewModel, BaseHeaderHolder, BaseSwipeableItemViewHolder> {

    private NoteClickListener noteClickListener;

    public ListItemAdapter(Context context, ActionModeInteractionListener listener,
                           RecyclerView recyclerView, AppPreferences preferences) {
        super(context, listener, recyclerView, preferences);
    }

    public void setNoteClickListener(NoteClickListener noteClickListener) {
        this.noteClickListener = noteClickListener;
    }

    @Override
    protected int getLeftSwipeActionType() {
        return preferences.getLeftShoppingListItemSwipeAction();
    }

    @Override
    protected int getRightSwipeActionType() {
        return preferences.getRightShoppingListItemSwipeAction();
    }

    @Override
    protected int getMoveToStatusNotDoneIcon() {
        return R.drawable.bg_swipe_item_move_from_cart_action;
    }

    @Override
    protected int getMoveToStatusDoneIcon() {
        return R.drawable.bg_swipe_item_move_to_cart_action;
    }

    @Override
    public BaseHeaderHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ItemType.HEADER_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
                return new HeaderViewHolder(view);
            case ItemType.CART_HEADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_header, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                holder.setColor(preferences.getColorPrimary());
                return holder;
        }
        return null;
    }

    @Override
    public BaseSwipeableItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_list_item, parent, false);
        ListItemViewHolder holder = new ListItemViewHolder(view);
        holder.setClickListener(itemClickListener);
        return holder;
    }

    @Override
    public void onBindGroupViewHolder(BaseHeaderHolder viewHolder, int groupPosition, int viewType) {
        switch (viewType) {
            case ItemType.HEADER_ITEM:
                BaseViewModel model = getGroupItem(groupPosition);
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                if (preferences.getSortForShoppingListItems() == SortType.SORT_BY_PRIORITY) {
                    if (model.getPriority() == Priority.NO_PRIORITY) {
                        headerViewHolder.name.setTextColor(ContextCompat.getColor(context, R.color.action_mode_toolbar_color));
                    } else {
                        setPriorityTextColor(model.getPriority(), headerViewHolder.name);
                    }
                } else {
                    headerViewHolder.name.setTextColor(preferences.getColorPrimary());
                }
                headerViewHolder.indicator.setBackgroundResource(R.drawable.ic_expand);
                headerViewHolder.name.setText(model.getName());
                break;
            case ItemType.CART_HEADER:
                HeaderViewModel header = getGroupItem(groupPosition);
                CartViewHolder cartViewHolder = (CartViewHolder) viewHolder;

                if (header.isShowExpandIndicator()) {
                    cartViewHolder.indicator.setVisibility(View.VISIBLE);
                    cartViewHolder.indicator.setBackgroundResource(R.drawable.ic_expand_white);
                } else {
                    cartViewHolder.indicator.setVisibility(View.INVISIBLE);
                }

                cartViewHolder.itemView.setBackgroundColor(preferences.getColorPrimary());
                cartViewHolder.cart.setTextColor(Color.WHITE);
                cartViewHolder.cart.setText(header.getName().toUpperCase(Locale.getDefault()));
                break;
        }
        ExpandUtils.toggleIndicator(viewHolder);
    }

    @Override
    public void onBindChildViewHolder(BaseSwipeableItemViewHolder viewHolder, int groupPosition, int childPosition, int viewType) {
        ListItemViewHolder holder = (ListItemViewHolder) viewHolder;
        holder.childPosition = childPosition;
        holder.groupPosition = groupPosition;

        final ListItemViewModel item = getChildItem(groupPosition, childPosition);
        if (!item.isChecked()) {
            item.setChecked(isItemChecked(item.getId()));
        }

        holder.name.setText(item.getName());
        if (item.getStatus()) {
            holder.note.setVisibility(View.GONE);
            holder.info2.setVisibility(View.INVISIBLE);
        } else {
            holder.info2.setVisibility(View.VISIBLE);

            if (!item.getNote().isEmpty()) {
                View.OnClickListener listener = v -> {
                    if (noteClickListener != null) {
                        noteClickListener.onNoteClick(item.getNote());
                    }
                };
                holder.note.setVisibility(View.VISIBLE);
                holder.note.setOnClickListener(listener);
                ViewUtils.setPaddingRight(holder.info2, 0);
            } else {
                ViewUtils.setPaddingRight(holder.info2, context.getResources().getDimensionPixelSize(R.dimen.content2x));
                holder.note.setVisibility(View.GONE);
            }
        }

        if (preferences.getSortForShoppingListItems() == SortType.SORT_BY_CATEGORIES) {
            holder.categoryName.setVisibility(View.GONE);
        } else {
            holder.categoryName.setVisibility(View.VISIBLE);
            holder.categoryName.setText(item.getCategory().getName());
        }

        if (item.getPrice() == 0 || item.getCurrency().getId().equals(CurrencyViewModel.NO_CURRENCY_ID)) {
            holder.priceAndCurrency.setVisibility(View.GONE);
        } else {
            holder.priceAndCurrency.setVisibility(View.VISIBLE);
            holder.priceAndCurrency.setText(String.format("%s %s", ShoppistUtils.roundDouble(item.getPrice() * item.getQuantity(), 2),
                    item.getCurrency().getName()));
        }

        if (item.getQuantity() == 0 || item.getUnit().getId().equals(UnitViewModel.NO_UNIT_ID)) {
            holder.quantityAndUnit.setVisibility(View.GONE);
        } else {
            holder.quantityAndUnit.setVisibility(View.VISIBLE);
            holder.quantityAndUnit.setText(String.format("%s %s", item.getQuantity(), item.getUnit().getShortName()));
        }

        setPriorityBackgroundColor(item.getPriority(), holder.priorityIndicator);

        int normalStateColor = item.getCategory().getColor();
        if (item.getStatus()) {
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.categoryName.setPaintFlags(holder.categoryName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.disable_text_color_black));
            normalStateColor = ContextCompat.getColor(context, R.color.grey_300);
            holder.selectBox.setInnerTextColor(ContextCompat.getColor(context, R.color.disable_text_color_black));
        } else {
            holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.categoryName.setPaintFlags(holder.categoryName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.text_color_black));
            holder.selectBox.setInnerTextColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.selectBox.setNormalStateColor(normalStateColor);
        holder.selectBox.setInnerText(ShoppistUtils.getFirstCharacter(item.getName()).toUpperCase(Locale.getDefault()));
        holder.selectBox.setEventListener(isChecked -> {
            onCheckItem(item, isChecked);
            holder.setActivated(isChecked);
        });
        holder.selectBox.setChecked(item.isChecked());
        holder.setActivated(item.isChecked());

        if (item.isPinned()) {
            if (holder.getSwipeResult() == SwipeableItemConstants.RESULT_SWIPED_RIGHT) {
                holder.setSwipeItemHorizontalSlideAmount(SwipeableItemConstants.OUTSIDE_OF_THE_WINDOW_RIGHT);
            } else {
                holder.setSwipeItemHorizontalSlideAmount(SwipeableItemConstants.OUTSIDE_OF_THE_WINDOW_LEFT);
            }
        } else {
            holder.setSwipeItemHorizontalSlideAmount(0);
        }
    }

    public interface NoteClickListener {

        void onNoteClick(String note);
    }

    static class CartViewHolder extends BaseHeaderHolder {
        @Bind(R.id.header_name)
        TextView cart;

        CartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.findViewById(R.id.line).setVisibility(View.GONE);
        }

        public void setColor(@ColorInt int color) {
            itemView.setBackgroundColor(color);
        }
    }

    static class HeaderViewHolder extends BaseHeaderHolder {
        @Bind(R.id.header_name)
        TextView name;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ListItemViewHolder extends BaseSwipeableItemViewHolder implements ExpandableItemViewHolder {
        @Bind(R.id.priority_indicator)
        ImageView priorityIndicator;
        @Bind(R.id.note_image)
        ImageView note;
        @Bind(R.id.item_name)
        TextView name;
        @Bind(R.id.quantity_unit)
        TextView quantityAndUnit;
        @Bind(R.id.price_currency)
        TextView priceAndCurrency;
        @Bind(R.id.category_name)
        TextView categoryName;
        @Bind(R.id.info2)
        LinearLayout info2;

        private int mExpandStateFlags;

        ListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setExpandStateFlags(int flags) {
            mExpandStateFlags = flags;
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public View getSwipeableContainerView() {
            return container;
        }
    }
}
