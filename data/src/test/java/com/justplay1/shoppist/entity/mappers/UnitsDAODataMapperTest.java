package com.justplay1.shoppist.entity.mappers;

import com.justplay1.shoppist.entity.UnitDAO;
import com.justplay1.shoppist.models.UnitModel;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.justplay1.shoppist.entity.TestUtil.FAKE_ID;
import static com.justplay1.shoppist.entity.TestUtil.FAKE_NAME;
import static com.justplay1.shoppist.entity.TestUtil.FAKE_SHORT_NAME;
import static com.justplay1.shoppist.entity.TestUtil.createFakeUnitDAO;
import static com.justplay1.shoppist.entity.TestUtil.createFakeUnitModel;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by Mkhytar Mkhoian.
 */
public class UnitsDAODataMapperTest {

    @Test
    public void transformUnitDAO() {
        UnitsDAODataMapper dataMapper = new UnitsDAODataMapper();
        UnitDAO dao = createFakeUnitDAO();

        UnitModel model = dataMapper.transformFromDAO(dao);

        assertThat(model, is(instanceOf(UnitModel.class)));
        assertThat(model.getId(), is(FAKE_ID));
        assertThat(model.getName(), is(FAKE_NAME));
        assertThat(model.getShortName(), is(FAKE_SHORT_NAME));
    }

    @Test
    public void transformUnitDAOCollection() {
        UnitsDAODataMapper dataMapper = new UnitsDAODataMapper();

        UnitDAO mockDAOOne = mock(UnitDAO.class);
        UnitDAO mockDAOTwo = mock(UnitDAO.class);

        List<UnitDAO> list = new ArrayList<>(5);
        list.add(mockDAOOne);
        list.add(mockDAOTwo);

        Collection<UnitModel> collection = dataMapper.transformFromDAO(list);

        assertThat(collection.toArray()[0], is(instanceOf(UnitModel.class)));
        assertThat(collection.toArray()[1], is(instanceOf(UnitModel.class)));
        assertThat(collection.size(), is(2));
    }

    @Test
    public void transformUnitModel() {
        UnitsDAODataMapper dataMapper = new UnitsDAODataMapper();
        UnitModel model = createFakeUnitModel();

        UnitDAO dao = dataMapper.transformToDAO(model);

        assertThat(dao, is(instanceOf(UnitDAO.class)));
        assertThat(dao.getId(), is(FAKE_ID));
        assertThat(dao.getName(), is(FAKE_NAME));
        assertThat(dao.getShortName(), is(FAKE_SHORT_NAME));
    }

    @Test
    public void transformUnitModelCollection() {
        UnitsDAODataMapper dataMapper = new UnitsDAODataMapper();

        UnitModel mockModelOne = mock(UnitModel.class);
        UnitModel mockModelTwo = mock(UnitModel.class);

        List<UnitModel> list = new ArrayList<>(5);
        list.add(mockModelOne);
        list.add(mockModelTwo);

        Collection<UnitDAO> collection = dataMapper.transformToDAO(list);

        assertThat(collection.toArray()[0], is(instanceOf(UnitDAO.class)));
        assertThat(collection.toArray()[1], is(instanceOf(UnitDAO.class)));
        assertThat(collection.size(), is(2));
    }
}