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

package com.justplay1.shoppist.features.lists;

import android.support.v4.util.Pair;

import com.justplay1.shoppist.eventbus.DataEventBus;
import com.justplay1.shoppist.eventbus.ListsDataUpdatedEvent;
import com.justplay1.shoppist.di.scope.NonConfigurationScope;
import com.justplay1.shoppist.interactor.DefaultSubscriber;
import com.justplay1.shoppist.interactor.listitems.GetListItems;
import com.justplay1.shoppist.interactor.lists.DeleteLists;
import com.justplay1.shoppist.interactor.lists.GetLists;
import com.justplay1.shoppist.models.HeaderViewModel;
import com.justplay1.shoppist.models.ListViewModel;
import com.justplay1.shoppist.models.SortType;
import com.justplay1.shoppist.models.mappers.ListItemsViewModelMapper;
import com.justplay1.shoppist.models.mappers.ListViewModelMapper;
import com.justplay1.shoppist.preferences.AppPreferences;
import com.justplay1.shoppist.shared.base.presenters.BaseSortablePresenter;
import com.justplay1.shoppist.utils.ShoppistUtils;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

/**
 * Created by Mkhytar Mkhoian.
 */
@NonConfigurationScope
public class ListPresenter extends BaseSortablePresenter<ListView, ListViewModel, ListRouter> {

    private final BehaviorSubject<List<Pair<HeaderViewModel, List<ListViewModel>>>> cache = BehaviorSubject.create();

    private final ListViewModelMapper dataMapper;
    private final ListItemsViewModelMapper listItemsViewModelMapper;
    private final GetLists getLists;
    private final DeleteLists deleteLists;
    private final GetListItems getListItems;
    private final AppPreferences preferences;

    private Subscription dataBusSubscription;

    @Inject
    ListPresenter(AppPreferences preferences,
                  GetLists getLists,
                  DeleteLists deleteLists,
                  GetListItems getListItems,
                  ListViewModelMapper listModelDataMapper,
                  ListItemsViewModelMapper listItemsViewModelMapper) {
        this.preferences = preferences;
        this.getLists = getLists;
        this.deleteLists = deleteLists;
        this.dataMapper = listModelDataMapper;
        this.listItemsViewModelMapper = listItemsViewModelMapper;
        this.getListItems = getListItems;

        loadData();
    }

    @Override
    public void attachView(ListView view) {
        super.attachView(view);

        DataEventBus.instanceOf().filteredObservable(ListsDataUpdatedEvent.class);
        dataBusSubscription = DataEventBus.instanceOf().observable().subscribe(new DefaultSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                loadData();
            }
        });

        addSubscription(cache.subscribe(new DefaultSubscriber<List<Pair<HeaderViewModel, List<ListViewModel>>>>() {
            @Override
            public void onNext(List<Pair<HeaderViewModel, List<ListViewModel>>> data) {
                showData(data);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public void detachView() {
        super.detachView();
        dataBusSubscription.unsubscribe();
    }

    @SuppressWarnings("ResourceType")
    private void loadData() {
        getLists.get()
                .map(dataMapper::transformToViewModel)
                .map(listViewModels -> sort(listViewModels, preferences.getSortForShoppingLists()))
                .subscribe(cache);
    }

    private void showLoading() {
        if (isViewAttached()) {
            getView().showLoading();
        }
    }

    private void hideLoading() {
        if (isViewAttached()) {
            getView().hideLoading();
        }
    }

    private void showLoadingDialog() {
        if (isViewAttached()) {
            getView().showLoadingDialog();
        }
    }

    private void hideLoadingDialog() {
        if (isViewAttached()) {
            getView().hideLoadingDialog();
        }
    }

    public void sortByName(final List<ListViewModel> data) {
        if (preferences.getSortForShoppingLists() != SortType.SORT_BY_NAME) {
            preferences.setSortForShoppingLists(SortType.SORT_BY_NAME);
            showData(sort(data, SortType.SORT_BY_NAME));
        }
    }

    public void sortByPriority(final List<ListViewModel> data) {
        if (preferences.getSortForShoppingLists() != SortType.SORT_BY_PRIORITY) {
            preferences.setSortForShoppingLists(SortType.SORT_BY_PRIORITY);
            showData(sort(data, SortType.SORT_BY_PRIORITY));
        }
    }

    public void sortByTimeCreated(final List<ListViewModel> data) {
        if (preferences.getSortForShoppingLists() != SortType.SORT_BY_TIME_CREATED) {
            preferences.setSortForShoppingLists(SortType.SORT_BY_TIME_CREATED);
            showData(sort(data, SortType.SORT_BY_TIME_CREATED));
        }
    }

    public void onAddButtonClick() {
        if (hasRouter()) {
            getRouter().openEditScreen(null);
        }
    }

    public void onEditItemClick(ListViewModel list) {
        if (hasRouter()) {
            getRouter().openEditScreen(list);
        }
    }

    public void onItemClick(ListViewModel list) {
        if (hasRouter()) {
            getRouter().openListDetailScreen(list);
        }
    }

    public void deleteItems(Collection<ListViewModel> data) {
        addSubscription(Observable.fromCallable(() -> dataMapper.transform(data))
                .flatMap(list -> deleteLists.init(list).get())
                .subscribe(new DefaultSubscriber<>()));
    }

    public void emailShare(List<ListViewModel> data) {
        showLoadingDialog();
        addSubscription(Observable.from(data)
                .flatMap(shoppingList -> getListItems.init(shoppingList.getId()).get()
                        .map(listItemsViewModelMapper::transformToViewModel)
                        .map(items -> shoppingList.getName() + "\n" + "\n" +
                                ShoppistUtils.buildShareString(items) + "\n"))
                .buffer(data.size())
                .map(strings -> {
                    String result = "";
                    for (String s : strings) {
                        result = result + s;
                    }
                    return result;
                }).subscribe(new DefaultSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        hideLoadingDialog();
                        if (isViewAttached()) {
                            getView().share(s);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }
                }));
    }

    private void showData(List<Pair<HeaderViewModel, List<ListViewModel>>> data) {
        if (isViewAttached()) {
            getView().showData(data);
        }
    }
}
