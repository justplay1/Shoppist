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

package com.justplay1.shoppist.repository.datasource.local.database;

import android.content.ContentValues;

import com.justplay1.shoppist.entity.ListDAO;
import com.justplay1.shoppist.entity.ListItemDAO;
import com.justplay1.shoppist.repository.datasource.local.LocalListDataStore;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Mkhytar Mkhoian.
 */
@Singleton
public class LocalListDataStoreImpl extends BaseLocalDataStore<ListDAO> implements LocalListDataStore {

    private static String LIST_QUERY(String selection) {
        String query = "SELECT *, COUNT(i." + ListItemDAO.STATUS + ") " + ListDAO.SIZE
                + ", SUM(i." + ListItemDAO.STATUS + ") " + ListDAO.BOUGHT_COUNT +
                " FROM " + ListDAO.TABLE + " s" +
                " LEFT OUTER JOIN " + ListItemDAO.TABLE + " i" +
                " ON s." + ListDAO.LIST_ID + " = i." + ListItemDAO.PARENT_LIST_ID;
        if (selection == null) {
            return query + " GROUP BY s." + ListDAO.LIST_ID;
        }
        return query + " WHERE " + selection +
                " GROUP BY s." + ListDAO.LIST_ID;
    }

    @Inject
    public LocalListDataStoreImpl(BriteDatabase db) {
        super(db);
    }

    @Override
    public Observable<List<ListDAO>> getItems() {
        return getAllLists();
    }

    @Override
    public Observable<ListDAO> getItem(String id) {
        return db.createQuery(ListDAO.TABLE, LIST_QUERY(ListDAO.WHERE_STRING), id)
                .mapToOne(ListDAO.MAPPER);
    }

    @Override
    public void save(Collection<ListDAO> data) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (ListDAO list : data) {
                db.insert(ListDAO.TABLE, getValue(list));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void save(ListDAO data) {
        save(Collections.singletonList(data));
    }

    @Override
    public void delete(Collection<ListDAO> data) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (ListDAO list : data) {
                db.delete(ListDAO.TABLE, ListDAO.WHERE_STRING, list.getId());
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void delete(ListDAO data) {
        delete(Collections.singletonList(data));
    }

    @Override
    public void update(Collection<ListDAO> data) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (ListDAO list : data) {
                db.update(ListDAO.TABLE, getValue(list), ListDAO.WHERE_STRING, list.getId());
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    @Override
    public void update(ListDAO data) {
        update(Collections.singletonList(data));
    }

    @Override
    public int clear() {
        return clear(ListDAO.TABLE);
    }

    @Override
    public void deleteListItems(String id) {
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            String selection = ListItemDAO.PARENT_LIST_ID + "=?";
            String[] selectionArgs = new String[]{id, ""};
            db.delete(ListItemDAO.TABLE, selection, selectionArgs);
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }

    private Observable<List<ListDAO>> getAllLists() {
        return db.createQuery(ListDAO.TABLE, LIST_QUERY(null), new String[]{})
                .mapToList(ListDAO.MAPPER);
    }

    @Override
    protected ContentValues getValue(ListDAO list) {
        ListDAO.Builder builder = new ListDAO.Builder();
        builder.id(list.getId());
        builder.name(list.getName());
        builder.color(list.getColor());
        builder.priority(list.getPriority());
        builder.timeCreated(list.getTimeCreated());
        return builder.build();
    }
}

