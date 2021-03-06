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
package com.justplay1.shoppist.shared.base.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.justplay1.shoppist.App;
import com.justplay1.shoppist.R;
import com.justplay1.shoppist.di.HasInjector;
import com.justplay1.shoppist.di.components.AppComponent;
import com.justplay1.shoppist.preferences.AppPreferences;
import com.justplay1.shoppist.shared.navigation.Navigator;
import com.justplay1.shoppist.utils.ShoppistUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


/**
 * Created by Mkhytar Mkhoian.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements HasInjector {

    @Inject
    protected Navigator navigator;
    @Inject
    protected AppPreferences preferences;

    protected NonConfigurationInstance nonConfigurationInstance;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        nonConfigurationInstance = retrieveNonConfigurationInstanceOrCreateNew();
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
        setStatusBarColor();
    }

    private NonConfigurationInstance retrieveNonConfigurationInstanceOrCreateNew() {
        Object lastNonConfInstance = getLastCustomNonConfigurationInstance();
        if (lastNonConfInstance == null) {
            return new NonConfigurationInstance();
        } else {
            return (NonConfigurationInstance) lastNonConfInstance;
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return nonConfigurationInstance;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> C getInjector(String id) {
        return (C) nonConfigurationInstance.getInjector(id);
    }

    @Override
    public void putInjector(String id, Object component) {
        nonConfigurationInstance.putInjector(id, component);
    }

    protected AppComponent getAppComponent() {
        return ((App) getApplication()).getAppComponent();
    }

    public void replaceFragment(int id, Fragment frag, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(id, frag, tag);
        transaction.commit();
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public void setStatusBarColor() {
        setStatusBarColor(preferences.getColorPrimaryDark());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShoppistUtils.setKeepScreenOn(getWindow(), preferences.isLockScreen());
    }

    public void finishActivityWithResult(Activity activity, int resultCode, Intent data) {
        activity.setResult(resultCode, data);
        finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        activity.finish();
        finishActivityAnimation(activity);
    }

    public void finishActivity() {
        finish();
        finishActivityAnimation(this);
    }

    private static void finishActivityAnimation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.overridePendingTransition(R.anim.activity_close_enter_v21, R.anim.activity_close_exit_v21);
        } else {
            activity.overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
        }
    }

    private static class NonConfigurationInstance {
        private Map<String, Object> injectors;

        NonConfigurationInstance() {
            injectors = new HashMap<>();
        }

        public Object getInjector(String id) {
            return injectors.get(id);
        }

        public void putInjector(String id, Object injector) {
            injectors.put(id, injector);
        }
    }
}
