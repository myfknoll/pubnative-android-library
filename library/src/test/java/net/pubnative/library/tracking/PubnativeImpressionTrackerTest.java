package net.pubnative.library.tracking;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.View;

import net.pubnative.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeImpressionTrackerTest {

    Context applicationContext;
    Activity activity;

    @Before
    public void setUp() {

        this.applicationContext = RuntimeEnvironment.application.getApplicationContext();
        activity = Robolectric.buildActivity(Activity.class)
                              .create()
                              .resume()
                              .get();
    }

    @Test
    public void testWithValidListener() {

        PubnativeVisibilityTracker.Listener listener = spy(PubnativeVisibilityTracker.Listener.class);
        PubnativeImpressionTracker impressionTracker = spy(PubnativeImpressionTracker.class);
        impressionTracker.mHandler = new Handler();
        impressionTracker.mVisibilityListener = listener;
        View view = new View(activity);
        impressionTracker.addView(view);
        verify(impressionTracker, times(1)).getVisibilityTracker();
    }
}
