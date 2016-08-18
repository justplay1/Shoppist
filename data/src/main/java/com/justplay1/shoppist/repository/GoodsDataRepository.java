package com.justplay1.shoppist.repository;

import com.justplay1.shoppist.entity.mappers.GoodsDAODataMapper;
import com.justplay1.shoppist.models.ProductModel;
import com.justplay1.shoppist.repository.datasource.local.LocalGoodsDataStore;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Mkhytar on 28.04.2016.
 */
@Singleton
public class GoodsDataRepository implements GoodsRepository {

    private final LocalGoodsDataStore mDataStore;
    private final GoodsDAODataMapper mDataMapper;

    @Inject
    public GoodsDataRepository(LocalGoodsDataStore dataStore, GoodsDAODataMapper dataMapper) {
        this.mDataStore = dataStore;
        this.mDataMapper = dataMapper;
    }

    @Override
    public Observable<List<ProductModel>> getItems() {
        return mDataStore.getItems()
                .map(mDataMapper::transformFromDAO);
    }

    @Override
    public Observable<List<ProductModel>> getDirtyItems() {
        return mDataStore.getDirtyItems()
                .map(mDataMapper::transformFromDAO);
    }

    @Override
    public Observable<List<ProductModel>> getItems(long timestamp) {
        return mDataStore.getItems(timestamp)
                .map(mDataMapper::transformFromDAO);
    }

    @Override
    public Observable<ProductModel> getItem(String id) {
        return mDataStore.getItem(id)
                .map(mDataMapper::transformFromDAO);
    }

    @Override
    public void save(Collection<ProductModel> data) throws Exception {
        mDataStore.save(mDataMapper.transformToDAO(data));
    }

    @Override
    public void save(ProductModel data) throws Exception {
        mDataStore.save(mDataMapper.transform(data));
    }

    @Override
    public void delete(Collection<ProductModel> data) throws Exception {
        mDataStore.delete(mDataMapper.transformToDAO(data));
    }

    @Override
    public void delete(ProductModel data) throws Exception {
        mDataStore.delete(mDataMapper.transform(data));
    }

    @Override
    public void update(Collection<ProductModel> data) throws Exception {
        mDataStore.update(mDataMapper.transformToDAO(data));
    }

    @Override
    public void update(ProductModel data) throws Exception {
        mDataStore.update(mDataMapper.transform(data));
    }

    @Override
    public int clear() {
        return mDataStore.clear();
    }
}
