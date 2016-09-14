package net.pubnative.library.tracking;

import android.content.Context;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.tracking.model.PubnativeTrackingURLModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 23)
public class PubnativeTrackingManagerTest {

    Context applicationContext;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        PubnativeTrackingManager.setList(applicationContext, PubnativeTrackingManager.SHARED_PENDING_LIST, null);
        PubnativeTrackingManager.setList(applicationContext, PubnativeTrackingManager.SHARED_FAILED_LIST, null);
    }

    @Test
    public void testWithNullContext() {

        PubnativeTrackingManager.track(null, "www.google.com");
    }

    @Test
    public void testWithEmptyUrl() {

        PubnativeTrackingManager.track(applicationContext, "");
        List<PubnativeTrackingURLModel> urlModelList = PubnativeTrackingManager.getList(applicationContext, PubnativeTrackingManager.SHARED_PENDING_LIST);

        assertThat(urlModelList).isEmpty();
    }

    @Test
    public void checkItemEnqued() {

        PubnativeTrackingURLModel model = new PubnativeTrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();

        PubnativeTrackingManager.enqueueItem(applicationContext, PubnativeTrackingManager.SHARED_PENDING_LIST, model);

        List<PubnativeTrackingURLModel> urlModelList = PubnativeTrackingManager.getList(applicationContext, PubnativeTrackingManager.SHARED_PENDING_LIST);

        assertThat(urlModelList).isNotEmpty();
    }

    @Test
    public void checkItemDequeued() {

        PubnativeTrackingURLModel model = new PubnativeTrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();

        PubnativeTrackingManager.enqueueItem(applicationContext, PubnativeTrackingManager.SHARED_PENDING_LIST, model);

        PubnativeTrackingURLModel dequeuedItem = PubnativeTrackingManager.dequeueItem(applicationContext, PubnativeTrackingManager.SHARED_PENDING_LIST);

        assertThat(dequeuedItem.url).isEqualTo(model.url);
        assertThat(dequeuedItem.startTimestamp).isEqualTo(model.startTimestamp);
    }

    @Test
    public void testEnqueueFailedList() {

        List<PubnativeTrackingURLModel> failedList = new ArrayList<PubnativeTrackingURLModel>();
        PubnativeTrackingURLModel model = new PubnativeTrackingURLModel();
        model.url = "www.google.com";
        model.startTimestamp = System.currentTimeMillis();
        failedList.add(model);

        PubnativeTrackingManager.setList(applicationContext, PubnativeTrackingManager.SHARED_FAILED_LIST, failedList);
        PubnativeTrackingManager.enqueueFailedList(applicationContext);

        assertThat(PubnativeTrackingManager.getList(applicationContext, PubnativeTrackingManager.SHARED_FAILED_LIST)).isEmpty();
    }
}
