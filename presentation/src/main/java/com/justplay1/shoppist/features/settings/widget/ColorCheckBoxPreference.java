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

package com.justplay1.shoppist.features.settings.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;

import com.jenzz.materialpreference.CheckBoxPreference;
import com.justplay1.shoppist.R;
import com.justplay1.shoppist.utils.DrawableUtils;

/**
 * Created by Mkhytar Mkhoian.
 */
public class ColorCheckBoxPreference extends CheckBoxPreference {

    @ColorInt private int color;

    public ColorCheckBoxPreference(Context context, @ColorInt int color) {
        super(context);
        this.color = color;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        CheckBox checkboxView = (CheckBox) view.findViewById(R.id.checkbox);
        StateListDrawable states = new StateListDrawable();

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.btn_check_to_on_000);
        Drawable checked = ContextCompat.getDrawable(getContext(), R.drawable.btn_check_to_on_015);

        if (isEnabled()) {
            states.setColorFilter(DrawableUtils.getColorFilter(color));
        } else {
            states.setColorFilter(DrawableUtils.getColorFilter(Color.GRAY));
        }

        states.addState(new int[]{android.R.attr.stateNotNeeded}, drawable);
        states.addState(new int[]{android.R.attr.state_checked}, checked);
        states.addState(new int[]{android.R.attr.state_enabled}, drawable);
        states.addState(new int[]{-android.R.attr.state_enabled, android.R.attr.state_checked}, checked);
        states.addState(new int[]{-android.R.attr.state_enabled}, drawable);

        checkboxView.setButtonDrawable(states);
    }
}
