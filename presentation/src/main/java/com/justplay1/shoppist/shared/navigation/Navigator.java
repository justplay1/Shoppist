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

package com.justplay1.shoppist.shared.navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import com.justplay1.shoppist.R;
import com.justplay1.shoppist.features.category.CategoriesActivity;
import com.justplay1.shoppist.features.currency.CurrencyActivity;
import com.justplay1.shoppist.features.goods.GoodsActivity;
import com.justplay1.shoppist.features.lists.items.ListItemActivity;
import com.justplay1.shoppist.features.search.SearchActivity;
import com.justplay1.shoppist.features.settings.SettingsActivity;
import com.justplay1.shoppist.features.units.UnitsActivity;
import com.justplay1.shoppist.models.AddElementType;
import com.justplay1.shoppist.models.CategoryViewModel;
import com.justplay1.shoppist.models.ListItemViewModel;
import com.justplay1.shoppist.models.ListViewModel;
import com.justplay1.shoppist.shared.base.activities.AddElementActivity;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class used to navigate through the application.
 */
@Singleton
public class Navigator {

    @Inject
    Navigator() {
        //empty
    }

    public void navigateToCategoriesScreen(Activity activity) {
        if (activity != null) {
            Intent intentToLaunch = CategoriesActivity.getCallingIntent(activity);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToCurrencyScreen(Activity activity) {
        if (activity != null) {
            Intent intentToLaunch = CurrencyActivity.getCallingIntent(activity);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToGoodsScreen(Activity activity) {
        if (activity != null) {
            Intent intentToLaunch = GoodsActivity.getCallingIntent(activity);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToUnitsScreen(Activity activity) {
        if (activity != null) {
            Intent intentToLaunch = UnitsActivity.getCallingIntent(activity);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToListItemsScreen(Activity activity, ListViewModel parentList) {
        if (activity != null) {
            Intent intentToLaunch = ListItemActivity.getCallingIntent(activity, parentList);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToSettingScreen(Activity activity, int settingId) {
        if (activity != null) {
            Intent intentToLaunch = SettingsActivity.getCallingIntent(activity, settingId);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToAddCategoryScreen(Activity activity, CategoryViewModel category) {
        if (activity != null) {
            Intent intentToLaunch = AddElementActivity.getCallingIntent(activity,
                    AddElementType.CATEGORY, category, null, null);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToAddListScreen(Activity activity, ListViewModel list) {
        if (activity != null) {
            Intent intentToLaunch = AddElementActivity.getCallingIntent(activity,
                    AddElementType.LIST, null, list, null);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToAddListItemScreen(Activity activity, ListViewModel list, ListItemViewModel listItem) {
        if (activity != null) {
            Intent intentToLaunch = AddElementActivity.getCallingIntent(activity,
                    AddElementType.LIST_ITEM, null, list, listItem);
            activity.startActivity(intentToLaunch);
            startActivityAnimation(activity);
        }
    }

    public void navigateToSearchScreen(Activity activity, int contextType, String parentListId) {
        if (activity != null) {
            Intent intentToLaunch = SearchActivity.getCallingIntent(activity, contextType, parentListId);
            startActivityWithFadeAnim(activity, intentToLaunch);
        }
    }

    private void startActivityWithFadeAnim(Activity activity, Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private static void startActivityAnimation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.overridePendingTransition(R.anim.activity_open_enter_v21, R.anim.activity_open_exit_v21);
        } else {
            activity.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
        }
    }
}
