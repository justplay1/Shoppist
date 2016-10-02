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

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Mkhytar Mkhoian.
 */

public class ProductModelTest {

    private static final String FAKE_ID = "id";
    private static final String FAKE_NAME = "name";
    private static final boolean FAKE_CREATE_BY_USER = true;

    private ProductModel model;
    private CategoryModel categoryModel;
    private UnitModel unitModel;

    @Before
    public void setUp() {
        unitModel = new UnitModel();
        categoryModel = new CategoryModel();

        model = new ProductModel();
        model.setId(FAKE_ID);
        model.setCreateByUser(FAKE_CREATE_BY_USER);
        model.setName(FAKE_NAME);
        model.setCategory(categoryModel);
        model.setUnit(unitModel);
    }

    @Test
    public void testUserConstructorHappyCase() {
        String id = model.getId();
        String name = model.getName();
        boolean isCreateByUser = model.isCreateByUser();
        CategoryModel category = model.getCategory();
        UnitModel unit = model.getUnit();

        assertThat(id, is(FAKE_ID));
        assertThat(name, is(FAKE_NAME));
        assertThat(isCreateByUser, is(FAKE_CREATE_BY_USER));

        assertEquals(categoryModel, category);
        assertEquals(unitModel, unit);
    }
}