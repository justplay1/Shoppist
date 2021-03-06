/*
 * Copyright (C) 2016 Mkhytar Mkhoian
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.justplay1.shoppist.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mkhytar Mkhoian.
 */
public class ProductViewModel extends BaseViewModel {

    private CategoryViewModel category;
    private boolean isCreateByUser;
    private boolean isChecked;
    private long timeCreated;
    private UnitViewModel unit;

    public ProductViewModel() {
    }

    public ProductViewModel(Parcel parcel) {
        this();
        id = parcel.readString();
        name = parcel.readString();
        category = parcel.readParcelable(ProductViewModel.class.getClassLoader());
        isCreateByUser = parcel.readByte() != 0;
        isChecked = parcel.readByte() != 0;
        timeCreated = parcel.readLong();
        unit = parcel.readParcelable(ProductViewModel.class.getClassLoader());
    }

    public UnitViewModel getUnit() {
        return unit;
    }

    public void setUnit(UnitViewModel unit) {
        this.unit = unit;
    }

    @Override
    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isCreateByUser() {
        return isCreateByUser;
    }

    public void setCreateByUser(boolean isCreateByUser) {
        this.isCreateByUser = isCreateByUser;
    }

    @Override
    public CategoryViewModel getCategory() {
        return category;
    }

    public void setCategory(CategoryViewModel category) {
        this.category = category;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ProductViewModel)) return false;
        ProductViewModel item = (ProductViewModel) o;

        return item.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(category, flags);
        dest.writeByte((byte) (isCreateByUser ? 1 : 0));
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeLong(timeCreated);
        dest.writeParcelable(unit, flags);
    }

    public boolean isCategoryEmpty() {
        return category == null || category.getId() == null || category.getName() == null;
    }

    public boolean isUnitEmpty() {
        return unit == null || unit.getId() == null || unit.getName() == null;
    }

    public static final Parcelable.Creator<ProductViewModel> CREATOR = new Creator<ProductViewModel>() {

        @Override
        public ProductViewModel[] newArray(int size) {
            return new ProductViewModel[size];
        }

        @Override
        public ProductViewModel createFromParcel(Parcel source) {
            return new ProductViewModel(source);
        }
    };
}
