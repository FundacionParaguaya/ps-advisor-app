package org.fundacionparaguaya.advisorapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import static junit.framework.Assert.fail;

/**
 * A utility with tools for testing live data.
 */
class LiveDataTestUtil {

    /**
     * Waits for a value to be populated in a live data and returns it.
     * @param liveData The live data to wait on.
     * @param <T> The type of the live data value.
     * @return The populated value.
     */
    static <T> T waitForValue(final LiveData<T> liveData) {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                liveData.removeObserver(this);
            }
        };
        liveData.observeForever(observer);
        try {
            latch.await(2, TimeUnit.SECONDS);
            return (T) data[0];
        } catch (InterruptedException e) {
            fail();
            return null;
        }
    }
}
