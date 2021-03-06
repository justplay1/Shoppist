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

package com.justplay1.shoppist.entity;

import com.justplay1.shoppist.models.CategoryModel;
import com.justplay1.shoppist.models.CurrencyModel;
import com.justplay1.shoppist.models.ListItemModel;
import com.justplay1.shoppist.models.ListModel;
import com.justplay1.shoppist.models.ProductModel;
import com.justplay1.shoppist.models.UnitModel;

/**
 * Created by Mkhytar Mkhoian.
 */

public class DAOUtil {

    public static final String FAKE_CATEGORY_ID = "category_id";
    public static final String FAKE_UNIT_ID = "unit_id";
    public static final String FAKE_CURRENCY_ID = "currency_id";

    public static final String FAKE_ID = "id";
    public static final String FAKE_PARENT_LIST_ID = "parent_list_id";
    public static final String FAKE_NAME = "name";
    public static final double FAKE_PRICE = 5.56;
    public static final double FAKE_QUANTITY = 2.333;
    public static final String FAKE_NOTE = "note";
    public static final int FAKE_PRIORITY = 2;
    public static final long FAKE_TIME_CREATED = 55555L;
    public static final boolean FAKE_STATUS = true;

    public static final int FAKE_COLOR = 22222;
    public static final boolean FAKE_CREATE_BY_USER = true;

    public static final String FAKE_SHORT_NAME = "short_name";

    public static final int FAKE_BOUGHT_COUNT = 5;
    public static final int FAKE_SIZE = 10;

    public static ListItemDAO createFakeListItemDAO(CategoryDAO categoryDAO,
                                                    UnitDAO unitDAO,
                                                    CurrencyDAO currencyDAO) {
        return new ListItemDAO(FAKE_ID, FAKE_NAME,
                FAKE_PARENT_LIST_ID,
                FAKE_NOTE, FAKE_STATUS,
                categoryDAO,
                FAKE_PRIORITY,
                FAKE_PRICE,
                FAKE_QUANTITY,
                unitDAO,
                FAKE_TIME_CREATED,
                currencyDAO);

    }

    public static CategoryDAO createFakeCategoryDAO() {
        return new CategoryDAO(FAKE_ID, FAKE_NAME, FAKE_COLOR, FAKE_CREATE_BY_USER);
    }

    public static CurrencyDAO createFakeCurrencyDAO() {
        return new CurrencyDAO(FAKE_ID, FAKE_NAME);
    }

    public static ListDAO createFakeListDAO() {
        return new ListDAO(FAKE_ID, FAKE_NAME, FAKE_BOUGHT_COUNT, FAKE_TIME_CREATED, FAKE_PRIORITY, FAKE_COLOR, FAKE_SIZE);
    }

    public static UnitDAO createFakeUnitDAO() {
        return new UnitDAO(FAKE_ID, FAKE_NAME, FAKE_SHORT_NAME);
    }

    public static ProductDAO createFakeProductDAO(UnitDAO unitDAO, CategoryDAO categoryDAO) {
        return new ProductDAO(FAKE_ID, FAKE_NAME, categoryDAO, FAKE_CREATE_BY_USER, FAKE_TIME_CREATED, unitDAO);
    }

    public static CategoryModel createFakeCategoryModel() {
        return new CategoryModel(FAKE_ID, FAKE_NAME, FAKE_COLOR, FAKE_CREATE_BY_USER);
    }

    public static CurrencyModel createFakeCurrencyModel() {
        return new CurrencyModel(FAKE_ID, FAKE_NAME);
    }

    public static ListModel createFakeListModel() {
        return new ListModel(FAKE_ID, FAKE_NAME, FAKE_BOUGHT_COUNT, FAKE_TIME_CREATED, FAKE_PRIORITY, FAKE_COLOR, FAKE_SIZE);
    }

    public static UnitModel createFakeUnitModel() {
        return new UnitModel(FAKE_ID, FAKE_NAME, FAKE_SHORT_NAME);
    }

    public static ProductModel createFakeProductModel(UnitModel unitModel, CategoryModel categoryModel) {
        return new ProductModel(FAKE_ID, FAKE_NAME, categoryModel, FAKE_CREATE_BY_USER, FAKE_TIME_CREATED, unitModel);
    }

    public static ListItemModel createFakeListItemModel(CategoryModel categoryModel,
                                                        UnitModel unitModel,
                                                        CurrencyModel currencyModel) {
        return new ListItemModel(FAKE_ID, FAKE_NAME,
                FAKE_PARENT_LIST_ID,
                FAKE_NOTE, FAKE_STATUS,
                categoryModel,
                FAKE_PRIORITY,
                FAKE_PRICE,
                FAKE_QUANTITY,
                unitModel,
                FAKE_TIME_CREATED,
                currencyModel);

    }
}
