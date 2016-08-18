package com.justplay1.shoppist.presenter;

import android.os.Bundle;

import com.justplay1.shoppist.di.scope.PerActivity;
import com.justplay1.shoppist.interactor.DefaultSubscriber;
import com.justplay1.shoppist.interactor.category.GetCategories;
import com.justplay1.shoppist.interactor.goods.AddGoods;
import com.justplay1.shoppist.interactor.goods.UpdateGoods;
import com.justplay1.shoppist.interactor.units.GetUnits;
import com.justplay1.shoppist.models.CategoryViewModel;
import com.justplay1.shoppist.models.ProductViewModel;
import com.justplay1.shoppist.models.UnitViewModel;
import com.justplay1.shoppist.models.mappers.CategoryModelDataMapper;
import com.justplay1.shoppist.models.mappers.GoodsModelDataMapper;
import com.justplay1.shoppist.models.mappers.UnitsDataModelMapper;
import com.justplay1.shoppist.presenter.base.BaseRxPresenter;
import com.justplay1.shoppist.utils.Const;
import com.justplay1.shoppist.view.AddGoodsView;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by Mkhytar on 07.08.2016.
 */
@PerActivity
public class AddGoodsPresenter extends BaseRxPresenter<AddGoodsView> {

    private final GoodsModelDataMapper mGoodsModelDataMapper;
    private final CategoryModelDataMapper mCategoryModelDataMapper;
    private final UnitsDataModelMapper mUnitsDataModelMapper;

    private final UpdateGoods mUpdateGoods;
    private final AddGoods mAddGoods;
    private final GetUnits mGetUnits;
    private final GetCategories mGetCategories;

    private CategoryViewModel mCategoryModel;
    private UnitViewModel mUnitModel;
    private ProductViewModel mItem;
    private String mNewName;

    @Inject
    public AddGoodsPresenter(GoodsModelDataMapper dataMapper,
                             UpdateGoods updateGoods,
                             AddGoods addGoods,
                             GetUnits getUnits,
                             GetCategories getCategories,
                             CategoryModelDataMapper categoryModelDataMapper,
                             UnitsDataModelMapper unitsDataModelMapper) {
        this.mGoodsModelDataMapper = dataMapper;
        this.mUpdateGoods = updateGoods;
        this.mAddGoods = addGoods;
        this.mGetUnits = getUnits;
        this.mGetCategories = getCategories;
        this.mUnitsDataModelMapper = unitsDataModelMapper;
        this.mCategoryModelDataMapper = categoryModelDataMapper;
    }

    @Override
    public void onCreate(Bundle arguments, Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);
        if (arguments != null) {
            mNewName = arguments.getString(Const.NEW_NAME);
            mItem = arguments.getParcelable(ProductViewModel.class.getName());
        } else if (savedInstanceState != null) {
            mItem = savedInstanceState.getParcelable(ProductViewModel.class.getName());
            mNewName = savedInstanceState.getString(Const.NEW_NAME);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelable(ProductViewModel.class.getName(), mItem);
        bundle.putString(Const.NEW_NAME, mNewName);
    }

    public void init() {
        if (mItem != null) {
            setName(mItem.getName());
            setDefaultUpdateTitle();
        } else {
            setDefaultNewTitle();
            setName(mNewName);
        }
        loadCategories();
        loadUnits();
    }

    public void onPositiveButtonClick(String name) {
        saveGoods(name);
    }

    public void onNegativeButtonClick() {
        closeDialog();
    }

    public void onAddUnitClick() {
        showUnitDialog(null);
    }

    public void onEditUnitClick(UnitViewModel unit) {
        showUnitDialog(unit);
    }

    public void onCategorySelected(CategoryViewModel category) {
        mCategoryModel = category;
    }

    public void onUnitSelected(UnitViewModel unit) {
        mUnitModel = unit;
    }

    public void loadCategories() {
        mSubscriptions.add(mGetCategories.get()
                .map(mCategoryModelDataMapper::transformToViewModel)
                .subscribe(new DefaultSubscriber<List<CategoryViewModel>>() {

                    @Override
                    public void onNext(List<CategoryViewModel> category) {
                        setCategory(category);
                        if (mItem != null) {
                            selectCategory(mItem.getCategory().getId());
                        } else {
                            selectCategory(CategoryViewModel.NO_CATEGORY_ID);
                        }
                    }
                }));
    }

    public void loadUnits() {
        mSubscriptions.add(mGetUnits.get()
                .map(mUnitsDataModelMapper::transformToViewModel)
                .subscribe(new DefaultSubscriber<List<UnitViewModel>>() {

                    @Override
                    public void onNext(List<UnitViewModel> unitViewModels) {
                        setUnits(unitViewModels);
                        if (mItem != null && !mItem.isUnitEmpty()) {
                            selectUnit(mItem.getUnit().getId());
                        } else {
                            selectUnit(UnitViewModel.NO_UNIT_ID);
                        }
                    }
                }));
    }

    private void saveGoods(String name) {
        if (checkDataForErrors(name)) {
            ProductViewModel product = new ProductViewModel();
            product.setName(name);
            product.setCategory(mCategoryModel);
            product.setUnit(mUnitModel);

            if (mItem != null) {
                product.setTimestamp(mItem.getTimestamp());
                product.setId(mItem.getId());
                product.setServerId(mItem.getServerId());
                product.setTimeCreated(mItem.getTimeCreated());
                product.setCreateByUser(mItem.isCreateByUser());
                product.setDirty(mItem.isDirty());
                if (!mItem.getCategory().getId().equals(product.getCategory().getId())
                        || !mItem.getUnit().getId().equals(product.getUnit().getId())
                        || !mItem.getName().equals(product.getName())) {
                    product.setCreateByUser(true);
                    product.setDirty(true);
                }
                updateGoods(product);
            } else {
                product.setTimeCreated(System.currentTimeMillis());
                product.setDirty(true);
                product.setCreateByUser(true);
                addGoods(product);
            }
        }
    }

    private void addGoods(ProductViewModel data) {
        showLoading();
        mSubscriptions.add(
                Observable.fromCallable(() -> mGoodsModelDataMapper.transform(data))
                        .flatMap(currency -> {
                            mAddGoods.setData(currency);
                            return mAddGoods.get();
                        }).subscribe(new SaveGoodsSubscriber(true)));
    }

    private void updateGoods(ProductViewModel data) {
        showLoading();
        mSubscriptions.add(
                Observable.fromCallable(() -> mGoodsModelDataMapper.transform(data))
                        .flatMap(currency -> {
                            mUpdateGoods.setData(Collections.singletonList(currency));
                            return mUpdateGoods.get();
                        }).subscribe(new SaveGoodsSubscriber(false)));
    }

    private boolean checkDataForErrors(String name) {
        if (name.isEmpty()) {
            showNameIsRequiredError();
            return false;
        } else if (name.length() > 40) {
            return false;
        }
        return true;
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

    private void onComplete(boolean isUpdate) {
        if (isViewAttached()) {
            getView().onComplete(isUpdate);
        }
    }

    private void showNameIsRequiredError() {
        if (isViewAttached()) {
            getView().showNameIsRequiredError();
        }
    }

    private void setName(String name) {
        if (isViewAttached()) {
            getView().setName(name);
        }
    }

    private void setDefaultNewTitle() {
        if (isViewAttached()) {
            getView().setDefaultNewTitle();
        }
    }

    private void setDefaultUpdateTitle() {
        if (isViewAttached()) {
            getView().setDefaultUpdateTitle();
        }
    }

    private void closeDialog() {
        if (isViewAttached()) {
            getView().closeDialog();
        }
    }

    private void showUnitDialog(UnitViewModel editUnit) {
        if (isViewAttached()) {
            getView().showUnitDialog(editUnit);
        }
    }

    private void setCategory(List<CategoryViewModel> category) {
        if (isViewAttached()) {
            getView().setCategory(category);
        }
    }

    private void setUnits(List<UnitViewModel> units) {
        if (isViewAttached()) {
            getView().setUnits(units);
        }
    }

    private void selectCategory(String id) {
        if (isViewAttached()) {
            getView().selectCategory(id);
        }
    }

    private void selectUnit(String id) {
        if (isViewAttached()) {
            getView().selectUnit(id);
        }
    }

    private final class SaveGoodsSubscriber extends DefaultSubscriber<Boolean> {

        private boolean isAddAction;

        public SaveGoodsSubscriber(boolean isAddAction) {
            this.isAddAction = isAddAction;
        }

        @Override
        public void onError(Throwable e) {
            hideLoading();
        }

        @Override
        public void onNext(Boolean result) {
            hideLoading();
            if (result) {
                if (isAddAction) {
                    onComplete(false);
                } else {
                    onComplete(true);
                }
                closeDialog();
            }
        }
    }
}
