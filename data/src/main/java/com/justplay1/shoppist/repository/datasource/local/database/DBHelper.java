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

import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.RemoteException;
import android.util.Log;

import com.justplay1.shoppist.data.R;
import com.justplay1.shoppist.entity.CategoryDAO;
import com.justplay1.shoppist.entity.CurrencyDAO;
import com.justplay1.shoppist.entity.ListDAO;
import com.justplay1.shoppist.entity.ListItemDAO;
import com.justplay1.shoppist.entity.ProductDAO;
import com.justplay1.shoppist.entity.UnitDAO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Mkhytar Mkhoian.
 */
@Singleton
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "shopping_list_db";
    private static final int DB_VERSION = 7;

    private static final String COLUMN_ID = "_id";

    static final String CREATE_PRODUCTS_TABLE = "create table " + ProductDAO.TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            ProductDAO.COL_ID + " text, " +
            ProductDAO.COL_NAME + " text, " +
            ProductDAO.COL_IS_CREATE_BY_USER + " integer DEFAULT 0, " +
            ProductDAO.COL_TIME_CREATED + " integer, " +
            ProductDAO.COL_CATEGORY_ID + " text, " +
            ProductDAO.COL_UNIT_ID + " text DEFAULT " + UnitDAO.NO_UNIT_ID +
            ");";

    static final String CREATE_CATEGORIES_TABLE = "create table " + CategoryDAO.TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            CategoryDAO.COL_NAME + " text, " +
            CategoryDAO.COL_COLOR + " integer, " +
            CategoryDAO.COL_CREATE_BY_USER + " integer DEFAULT 0, " +
            CategoryDAO.COL_ID + " text " +
            ");";

    static final String CREATE_UNITS_TABLE = "create table " + UnitDAO.TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            UnitDAO.COL_FULL_NAME + " text, " +
            UnitDAO.COL_SHORT_NAME + " text, " +
            UnitDAO.COL_ID + " text " +
            ");";

    static final String CREATE_CURRENCIES_TABLE = "create table " + CurrencyDAO.TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            CurrencyDAO.COL_NAME + " text, " +
            CurrencyDAO.COL_ID + " text " +
            ");";

    static final String CREATE_LIST_ITEMS_TABLE = "create table " + ListItemDAO.TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            ListItemDAO.COL_ID + " text, " +
            ListItemDAO.COL_PARENT_LIST_ID + " text, " +
            ListItemDAO.COL_NAME + " text, " +
            ListItemDAO.COL_NOTE + " text, " +
            ListItemDAO.COL_STATUS + " integer, " +
            ListItemDAO.COL_PRIORITY + " integer, " +
            ListItemDAO.COL_PRICE + " real, " +
            ListItemDAO.COL_QUANTITY + " real, " +
            ListItemDAO.COL_UNIT_ID + " text, " +
            ListItemDAO.COL_CURRENCY_ID + " text, " +
            ListItemDAO.COL_CATEGORY_ID + " text, " +
            ListItemDAO.COL_TIME_CREATED + " integer " +
            ");";

    static final String CREATE_LISTS_TABLE = "create table " + ListDAO.TABLE + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            ListDAO.COL_ID + " text, " +
            ListDAO.COL_NAME + " text, " +
            ListDAO.COL_PRIORITY + " integer, " +
            ListDAO.COL_COLOR + " integer, " +
            ListDAO.COL_TIME_CREATED + " integer " +
            ");";

    private Context context;

    @Inject
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createTables(db);
            fillDatabase(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDatabase(SQLiteDatabase db) {
        String[] categories = context.getResources().getStringArray(R.array.categories);
        int[] categoriesColors = context.getResources().getIntArray(R.array.categories_colors);

        for (int i = 0; i < categories.length; i++) {
            String[] categoriesName = categories[i].split(" ! ");
            db.insert(CategoryDAO.TABLE, null, new CategoryDAO.Builder()
                    .id(categoriesName[1])
                    .color(categoriesColors[i])
                    .name(categoriesName[0])
                    .build());
        }

        String[] units = context.getResources().getStringArray(R.array.units);
        for (String item : units) {
            String[] unit = item.split("/");
            String[] unitId = unit[1].split(" ! ");
            db.insert(UnitDAO.TABLE, null, new UnitDAO.Builder()
                    .id(unitId[1])
                    .fullName(unit[0])
                    .shortName(unitId[0])
                    .build());
        }

        String[] currency = context.getResources().getStringArray(R.array.currency);
        for (String c : currency) {
            String[] currencyId = c.split(" ! ");
            db.insert(CurrencyDAO.TABLE, null, new CurrencyDAO.Builder()
                    .id(currencyId[1])
                    .name(currencyId[0])
                    .build());
        }

        String[] products = context.getResources().getStringArray(R.array.products);
        for (String product : products) {
            String[] productsName = product.split(" ! ");
            db.insert(ProductDAO.TABLE, null, new ProductDAO.Builder()
                    .id(UUID.nameUUIDFromBytes((System.nanoTime() + "").getBytes()).toString())
                    .name(productsName[0])
                    .categoryId(productsName[1])
                    .isCreateByUser(false)
                    .timeCreated(System.currentTimeMillis())
                    .unitId(UnitDAO.NO_UNIT_ID)
                    .build());
        }
    }

    private void createTables(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL(CREATE_PRODUCTS_TABLE);
            db.execSQL(CREATE_CATEGORIES_TABLE);
            db.execSQL(CREATE_CURRENCIES_TABLE);
            db.execSQL(CREATE_UNITS_TABLE);
            db.execSQL(CREATE_LISTS_TABLE);
            db.execSQL(CREATE_LIST_ITEMS_TABLE);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("SQL", e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    try {
                        db.beginTransaction();
                        db.execSQL("ALTER TABLE " + ProductDAO.TABLE + " ADD COLUMN "
                                + ProductDAO.COL_IS_CREATE_BY_USER + " integer DEFAULT 0");
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
                case 3:
                    try {
                        db.beginTransaction();
                        long time = System.currentTimeMillis();
                        db.execSQL("ALTER TABLE " + ProductDAO.TABLE + " ADD COLUMN "
                                + ProductDAO.COL_TIME_CREATED + " integer DEFAULT " + time);
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
                case 4:
                    break;
                case 5:
                    try {
                        db.execSQL("DROP TABLE IF EXISTS to_do_lists");
                        db.execSQL("DROP TABLE IF EXISTS to_do_list_items");

                        updateCategoriesToVersion5(db);
                        updateCurrenciesToVersion5(db);
                        updateGoodsToVersion5(db);
                        updateUnitsToVersion5(db);

                        Map<String, String> shoppingLists = updateShoppingListToVersion5(db);
                        updateShoppingListItemsToVersion5(db, shoppingLists);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    try {
                        db.beginTransaction();
                        if (!isColumnExist(db, ProductDAO.TABLE, ProductDAO.COL_UNIT_ID)) {
                            db.execSQL("ALTER TABLE " + ProductDAO.TABLE + " ADD COLUMN "
                                    + ProductDAO.COL_UNIT_ID + " text DEFAULT " + UnitDAO.NO_UNIT_ID);
                        } else {
                            Log.d("ALTER TABLE", ProductDAO.COL_UNIT_ID + " column already exists");
                        }

                        String[] units = context.getResources().getStringArray(R.array.units);
                        String[] unit = units[0].split("/");
                        String[] unitId = unit[1].split(" ! ");
                        db.insert(UnitDAO.TABLE, null, new UnitDAO.Builder()
                                .id(unitId[1])
                                .fullName(unit[0])
                                .shortName(unitId[0])
                                .build());

                        String[] currency = context.getResources().getStringArray(R.array.currency);
                        String[] currencyId = currency[0].split(" ! ");
                        db.insert(CurrencyDAO.TABLE, null, new CurrencyDAO.Builder()
                                .id(currencyId[1])
                                .name(currencyId[0])
                                .build());

                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
                case 7:
                    try {
                        db.beginTransaction();
                        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                        builder.setTables("SELECT * FROM " + ProductDAO.TABLE);
                        Cursor cursor = builder.query(db, null, null, null, null, null, null);

                        try {
                            if (!cursor.isClosed() && cursor.moveToFirst()) {
                                do {
                                    String oldId = DbUtil.getString(cursor, ProductDAO.COL_ID);
                                    db.update(ProductDAO.TABLE,
                                            new ProductDAO.Builder()
                                                    .id(UUID.nameUUIDFromBytes((DbUtil.getString(cursor, ProductDAO.COL_NAME)).getBytes()).toString())
                                                    .build(),
                                            ProductDAO.COL_ID + "=?", new String[]{oldId});
                                } while (cursor.moveToNext());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    break;
            }
            upgradeTo++;
        }
    }

    private boolean isColumnExist(SQLiteDatabase db, String table, String column) {
        Cursor res = db.rawQuery("SELECT * FROM " + table + " LIMIT 1", null);
        try {
            if (res != null && !res.isClosed() && res.moveToFirst()) {
                int value = res.getColumnIndex(column);
                if (value == -1) {
                    return false;
                }
            }
        } finally {
            if (res != null) {
                res.close();
            }
        }
        return true;
    }

    private void updateCategoriesToVersion5(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + CategoryDAO.TABLE + " RENAME TO " + CategoryDAO.TABLE + "_backup");
            db.execSQL(CREATE_CATEGORIES_TABLE);
            db.execSQL("INSERT INTO " + CategoryDAO.TABLE + " SELECT " +
                    COLUMN_ID + "," +
                    CategoryDAO.COL_NAME + "," +
                    CategoryDAO.COL_COLOR + "," +
                    CategoryDAO.COL_CREATE_BY_USER + "," +
                    "CAST(" + CategoryDAO.COL_ID + " AS TEXT)" +
                    " FROM " + CategoryDAO.TABLE + "_backup;");
            db.execSQL("DROP TABLE " + CategoryDAO.TABLE + "_backup;");
            db.setTransactionSuccessful();
            Log.d("upCategoriesToVersion5", "OK");
        } finally {
            db.endTransaction();
        }
    }

    private void updateUnitsToVersion5(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + UnitDAO.TABLE + " RENAME TO " + UnitDAO.TABLE + "_backup");
            db.execSQL(CREATE_UNITS_TABLE);
            db.execSQL("INSERT INTO " + UnitDAO.TABLE + " SELECT " +
                    COLUMN_ID + "," +
                    UnitDAO.COL_FULL_NAME + "," +
                    UnitDAO.COL_SHORT_NAME + "," +
                    "CAST(" + UnitDAO.COL_ID + " AS TEXT)" +
                    " FROM " + UnitDAO.TABLE + "_backup;");
            db.execSQL("DROP TABLE " + UnitDAO.TABLE + "_backup;");
            db.setTransactionSuccessful();
            Log.d("updateUnitsToVersion5", "OK");
        } finally {
            db.endTransaction();
        }
    }

    private void updateCurrenciesToVersion5(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + CurrencyDAO.TABLE + " RENAME TO " + CurrencyDAO.TABLE + "_backup");
            db.execSQL(CREATE_CURRENCIES_TABLE);
            db.execSQL("INSERT INTO " + CurrencyDAO.TABLE + " SELECT " +
                    COLUMN_ID + "," +
                    CurrencyDAO.COL_NAME + "," +
                    "CAST(" + CurrencyDAO.COL_ID + " AS TEXT)" +
                    " FROM " + CurrencyDAO.TABLE + "_backup;");
            db.execSQL("DROP TABLE " + CurrencyDAO.TABLE + "_backup;");
            db.setTransactionSuccessful();
            Log.d("upCurrenciesToVersion5", "OK");
        } finally {
            db.endTransaction();
        }
    }

    private void updateGoodsToVersion5(SQLiteDatabase db) throws RemoteException, OperationApplicationException {
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + ProductDAO.TABLE + " RENAME TO " + ProductDAO.TABLE + "_backup");
            db.execSQL(CREATE_PRODUCTS_TABLE);

            String insert = String.format("INSERT INTO %s (%s, %s, %s, %s) ",
                    ProductDAO.TABLE,
                    ProductDAO.COL_NAME,
                    ProductDAO.COL_IS_CREATE_BY_USER,
                    ProductDAO.COL_TIME_CREATED,
                    ProductDAO.COL_CATEGORY_ID);

            db.execSQL(insert + "SELECT " +
                    ProductDAO.COL_NAME + "," +
                    ProductDAO.COL_IS_CREATE_BY_USER + "," +
                    ProductDAO.COL_TIME_CREATED + "," +
                    "CAST(" + ProductDAO.COL_CATEGORY_ID + " AS TEXT)" +
                    " FROM " + ProductDAO.TABLE + "_backup;");

            db.execSQL("DROP TABLE " + ProductDAO.TABLE + "_backup;");

            final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
            builder.setTables("SELECT * FROM " + ProductDAO.TABLE);
            Cursor cursor = builder.query(db, null, null, null, null, null, null);

            try {
                if (!cursor.isClosed() && cursor.moveToFirst()) {
                    do {
                        String name = DbUtil.getString(cursor, ProductDAO.COL_NAME);
                        db.update(ProductDAO.TABLE,
                                new ProductDAO.Builder()
                                        .id(UUID.nameUUIDFromBytes((name).getBytes()).toString())
                                        .build(),
                                ProductDAO.COL_NAME + "=?", new String[]{name});
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            db.setTransactionSuccessful();
            Log.d("updateGoodsToVersion5", "OK");
        } finally {
            db.endTransaction();
        }
    }

    private Map<String, String> updateShoppingListToVersion5(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + ListDAO.TABLE + " ADD COLUMN "
                    + ListDAO.COL_ID + " text");

            String request = "SELECT *, COUNT(i." + ListItemDAO.COL_STATUS + ") " + ListDAO.COL_SIZE +
                    ", SUM(i." + ListItemDAO.COL_STATUS + ") " + ListDAO.COL_BOUGHT_COUNT +
                    " FROM " + ListDAO.TABLE + " s" +
                    " LEFT JOIN " + ListItemDAO.TABLE + " i" +
                    " ON s." + ListDAO.COL_NAME + " = i.shopping_list_item_parent_list_name" +
                    " GROUP BY s." + ListDAO.COL_NAME;
            Cursor cursor = db.rawQuery(request, null);

            Map<String, String> shoppingLists = new HashMap<>();
            try {
                if (!cursor.isClosed() && cursor.moveToFirst()) {
                    do {
                        String id = DbUtil.getString(cursor, ListDAO.COL_ID);
                        String name = DbUtil.getString(cursor, ListDAO.COL_NAME);
                        shoppingLists.put(id, name);
                        db.update(ListDAO.TABLE, new ListDAO.Builder()
                                        .id(UUID.nameUUIDFromBytes((name + UUID.randomUUID()).getBytes()).toString())
                                        .build(),
                                ListDAO.COL_NAME + "=?", new String[]{name});
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            db.setTransactionSuccessful();
            Log.d("ShoppingListToVersion5", "OK");
            return shoppingLists;
        } finally {
            db.endTransaction();
        }
    }

    private void updateShoppingListItemsToVersion5(SQLiteDatabase db, Map<String, String> shoppingLists) {
        try {
            db.beginTransaction();
            db.execSQL("ALTER TABLE " + ListItemDAO.TABLE + " RENAME TO " + ListItemDAO.TABLE + "_backup");
            db.execSQL(CREATE_LIST_ITEMS_TABLE);

            final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
            builder.setTables(ListItemDAO.TABLE + "_backup");
            Cursor cursor = builder.query(db, null, null, null, null, null, null);
            try {
                if (!cursor.isClosed() && cursor.moveToFirst()) {
                    do {
                        String parentListName = cursor.getString(cursor.getColumnIndex("shopping_list_item_parent_list_name"));
                        String name = cursor.getString(cursor.getColumnIndex(ListItemDAO.COL_NAME));

                        ListItemDAO.Builder cv = new ListItemDAO.Builder();

                        for (Map.Entry<String, String> parentList : shoppingLists.entrySet()) {
                            if (parentListName.equals(parentList.getValue())) {
                                cv.parentId(parentList.getKey());
                            }
                        }
                        cv.id(UUID.nameUUIDFromBytes((name + UUID.randomUUID()).getBytes()).toString());
                        cv.name(name);
                        cv.note(DbUtil.getString(cursor, ListItemDAO.COL_NOTE));
                        cv.price(DbUtil.getDouble(cursor, ListItemDAO.COL_PRICE));
                        cv.timeCreated(DbUtil.getLong(cursor, ListItemDAO.COL_TIME_CREATED));
                        cv.quantity(DbUtil.getDouble(cursor, ListItemDAO.COL_QUANTITY));
                        cv.status(DbUtil.getBoolean(cursor, ListItemDAO.COL_STATUS));
                        cv.priority(DbUtil.getInt(cursor, ListItemDAO.COL_PRIORITY));
                        cv.categoryId(DbUtil.getString(cursor, ListItemDAO.COL_CATEGORY_ID));
                        cv.unitId(DbUtil.getString(cursor, ListItemDAO.COL_UNIT_ID));
                        cv.currencyId(DbUtil.getString(cursor, ListItemDAO.COL_CURRENCY_ID));

                        db.insert(ListItemDAO.TABLE, null, cv.build());
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            db.execSQL("DROP TABLE " + ListItemDAO.TABLE + "_backup;");
            db.setTransactionSuccessful();
            Log.d("ShoppingListItems5", "OK");
        } finally {
            db.endTransaction();
        }
    }
}
