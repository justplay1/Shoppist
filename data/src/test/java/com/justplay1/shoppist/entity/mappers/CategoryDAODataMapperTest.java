package com.justplay1.shoppist.entity.mappers;

import com.justplay1.shoppist.entity.CategoryDAO;
import com.justplay1.shoppist.models.CategoryModel;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.justplay1.shoppist.entity.DAOUtil.FAKE_COLOR;
import static com.justplay1.shoppist.entity.DAOUtil.FAKE_CREATE_BY_USER;
import static com.justplay1.shoppist.entity.DAOUtil.FAKE_ID;
import static com.justplay1.shoppist.entity.DAOUtil.FAKE_NAME;
import static com.justplay1.shoppist.entity.DAOUtil.createFakeCategoryDAO;
import static com.justplay1.shoppist.entity.DAOUtil.createFakeCategoryModel;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by Mkhytar Mkhoian.
 */
public class CategoryDAODataMapperTest {

    private CategoryDAODataMapper dataMapper;

    @Before
    public void setUp() throws Exception {
        dataMapper = new CategoryDAODataMapper();
    }

    @Test
    public void transformCategoryDAO() {
        CategoryDAO dao = createFakeCategoryDAO();

        CategoryModel model = dataMapper.transformFromDAO(dao);

        assertThat(model, is(instanceOf(CategoryModel.class)));
        assertThat(model.getId(), is(FAKE_ID));
        assertThat(model.getName(), is(FAKE_NAME));
        assertThat(model.getColor(), is(FAKE_COLOR));
        assertThat(model.isCreateByUser(), is(FAKE_CREATE_BY_USER));
    }

    @Test
    public void transformCategoryDAOCollection() {
        CategoryDAO mockDAOOne = mock(CategoryDAO.class);
        CategoryDAO mockDAOTwo = mock(CategoryDAO.class);

        List<CategoryDAO> list = new ArrayList<>(5);
        list.add(mockDAOOne);
        list.add(mockDAOTwo);

        Collection<CategoryModel> collection = dataMapper.transformFromDAO(list);

        assertThat(collection.toArray()[0], is(instanceOf(CategoryModel.class)));
        assertThat(collection.toArray()[1], is(instanceOf(CategoryModel.class)));
        assertThat(collection.size(), is(2));
    }

    @Test
    public void transformCategoryModel() {
        CategoryModel model = createFakeCategoryModel();

        CategoryDAO dao = dataMapper.transformToDAO(model);

        assertThat(dao, is(instanceOf(CategoryDAO.class)));
        assertThat(dao.getId(), is(FAKE_ID));
        assertThat(dao.getName(), is(FAKE_NAME));
        assertThat(dao.getColor(), is(FAKE_COLOR));
        assertThat(dao.isCreateByUser(), is(FAKE_CREATE_BY_USER));
    }

    @Test
    public void transformCategoryModelCollection() {
        CategoryModel mockModelOne = mock(CategoryModel.class);
        CategoryModel mockModelTwo = mock(CategoryModel.class);

        List<CategoryModel> list = new ArrayList<>(5);
        list.add(mockModelOne);
        list.add(mockModelTwo);

        Collection<CategoryDAO> collection = dataMapper.transformToDAO(list);

        assertThat(collection.toArray()[0], is(instanceOf(CategoryDAO.class)));
        assertThat(collection.toArray()[1], is(instanceOf(CategoryDAO.class)));
        assertThat(collection.size(), is(2));
    }
}