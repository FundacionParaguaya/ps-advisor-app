package org.fundacionparaguaya.advisorapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.support.test.runner.AndroidJUnit4;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static junit.framework.Assert.assertEquals;
import static org.fundacionparaguaya.advisorapp.data.LiveDataTestUtil.waitForValue;

/**
 * The tests for the family data access object.
 */
@RunWith(AndroidJUnit4.class)
public class FamilyDaoTest {
    private LocalDatabase db;
    private FamilyDao familyDao;


    @Before
    public void init() {
        db = Room.inMemoryDatabaseBuilder(getTargetContext(), LocalDatabase.class).build();
        familyDao = db.familyDao();
    }

    @After
    public void close() {
        db.close();
    }

    @Test
    public void ShouldBeAbleToInsertAFamily() {
        Family family = new Family(1L, "Smith");

        familyDao.insertFamily(family);

        LiveData<List<Family>> result = familyDao.queryFamilies();
        List<Family> value = waitForValue(result);
        assertEquals(1, value.size());
        assertEquals(family, value.get(0));
    }

    @Test
    public void ShouldBeAbleToInsertFamilies() {
        Family family1 = new Family(1L, "Smith");
        Family family2 = new Family(2L, "Gogan");

        familyDao.insertFamily(family1);
        familyDao.insertFamily(family2);

        LiveData<List<Family>> result = familyDao.queryFamilies();
        List<Family> value = waitForValue(result);
        assertEquals(2, value.size());
    }

    @Test
    public void ShouldBeAbleToQueryFamilyById() {
        Family family1 = new Family(1L, "Smith");
        Family family2 = new Family(2L, "Gogan");
        familyDao.insertFamily(family1);
        familyDao.insertFamily(family2);

        LiveData<Family> result = familyDao.queryFamily(2L);
        Family value = waitForValue(result);
        assertEquals(value, family2);
    }

    @Test
    public void ShouldBeAbleToUpdateAFamily() {
        Family family = new Family(1L, "Smith");
        familyDao.insertFamily(family);

        family.setName("Smithy");
        familyDao.updateFamily(family);

        LiveData<List<Family>> result = familyDao.queryFamilies();
        List<Family> value = waitForValue(result);
        assertEquals(1, value.size());
        assertEquals(family, value.get(0));
    }


    @Test
    public void ShouldBeAbleToDeleteAFamily() {
        Family family = new Family(1L, "Smith");
        familyDao.insertFamily(family);

        familyDao.deleteFamily(family);

        LiveData<List<Family>> result = familyDao.queryFamilies();
        List<Family> value = waitForValue(result);
        assertEquals(0, value.size());
    }
}
