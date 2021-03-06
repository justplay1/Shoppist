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

package com.justplay1.shoppist.shared.widget.recyclerview.holders;

import android.view.View;
import android.widget.Checkable;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.justplay1.shoppist.R;
import com.justplay1.shoppist.shared.widget.animboxes.SelectBoxView;

/**
 * Created by Mkhytar Mkhoian.
 */
public abstract class BaseItemHolder extends BaseViewHolder implements Checkable {

    @Bind(R.id.select_box)
    public SelectBoxView selectBox;

    public int groupPosition = -1;
    public int childPosition = -1;

    public BaseItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void setChecked(boolean checked) {
        selectBox.setCheckedWithAnim(checked);
    }

    @Override
    public boolean isChecked() {
        return selectBox.isChecked();
    }

    @Override
    public void toggle() {
        selectBox.setCheckedWithAnim(!selectBox.isChecked());
    }

    public void setActivated(boolean isChecked) {
        itemView.setActivated(isChecked);
    }
}
