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

import org.junit.Test;

import static com.justplay1.shoppist.ModelUtil.FAKE_BOUGHT_COUNT;
import static com.justplay1.shoppist.ModelUtil.FAKE_COLOR;
import static com.justplay1.shoppist.ModelUtil.FAKE_ID;
import static com.justplay1.shoppist.ModelUtil.FAKE_NAME;
import static com.justplay1.shoppist.ModelUtil.FAKE_PRIORITY;
import static com.justplay1.shoppist.ModelUtil.FAKE_SIZE;
import static com.justplay1.shoppist.ModelUtil.FAKE_TIME_CREATED;
import static com.justplay1.shoppist.ModelUtil.createFakeListModel;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by Mkhytar Mkhoian.
 */

public class ListModelTest {

    @Test
    public void listConstructor_HappyCase() {
        ListModel model = createFakeListModel();

        String id = model.getId();
        String name = model.getName();
        int color = model.getColor();
        int boughtCount = model.getBoughtCount();
        int priority = model.getPriority();
        int size = model.getSize();
        long timeCreated = model.getTimeCreated();

        assertThat(id, is(FAKE_ID));
        assertThat(color, is(FAKE_COLOR));
        assertThat(name, is(FAKE_NAME));
        assertThat(boughtCount, is(FAKE_BOUGHT_COUNT));
        assertThat(priority, is(FAKE_PRIORITY));
        assertThat(timeCreated, is(FAKE_TIME_CREATED));
        assertThat(size, is(FAKE_SIZE));
    }

    @Test
    public void listHashCode_HappyCase() {
        ListModel model = createFakeListModel();
        int hashCode = model.hashCode();

        assertThat(hashCode, is(FAKE_ID.hashCode()));
    }

    @Test
    public void listEquals_HappyCase() {
        ListModel x = createFakeListModel();
        ListModel y = createFakeListModel();
        ListModel z = createFakeListModel();

        // reflection rule
        assertEquals(x, x);

        // symmetry rule
        assertEquals(x, y);
        assertEquals(y, x);

        // transitivity rule
        assertEquals(x, y);
        assertEquals(y, z);
        assertEquals(x, z);

        assertNotEquals(x, null);
    }
}
