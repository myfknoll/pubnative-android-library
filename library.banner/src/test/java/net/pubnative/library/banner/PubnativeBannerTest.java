package net.pubnative.library.banner;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class PubnativeBannerTest {

    @Test
    public void invokeLoadFail_withValidListener_shouldCallOnLoadFail() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        Exception ex = mock(Exception.class);
        banner.invokeLoadFail(ex);

        verify(listener).onPubnativeBannerLoadFail(eq(banner), eq(ex));
    }

    @Test
    public void invokeLoadFail_withNullListener_shouldPass() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.mHandler = new Handler();
        banner.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFinish_withValidListener_shouldCallOnLoadFinish() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeLoadFinish();

        verify(listener).onPubnativeBannerLoadFinish(eq(banner));
    }

    @Test
    public void invokeLoadFinish_withNullListener_shouldPass() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.mHandler = new Handler();
        banner.invokeLoadFinish();
    }

    @Test
    public void invokeShow_withValidListener_shouldCallOnShow() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeShow();

        verify(listener).onPubnativeBannerShow(eq(banner));
    }

    @Test
    public void invokeShow_withNullListener_shouldPass() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.mHandler = new Handler();
        banner.invokeShow();
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_shouldCallOnImpressionConfirmed() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeImpressionConfirmed();

        verify(listener).onPubnativeBannerImpressionConfirmed(eq(banner));
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_shouldPass() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.mHandler = new Handler();
        banner.invokeImpressionConfirmed();
    }

    @Test
    public void invokeClick_withValidListener_shouldCallOnClick() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeClick();

        verify(listener).onPubnativeBannerClick(eq(banner));
    }

    @Test
    public void invokeClick_withNullListener_shouldPass() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.mHandler = new Handler();
        banner.invokeClick();
    }

    @Test
    public void invokeHide_withValidListener_shouldCallOnHide() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeHide();

        verify(listener).onPubnativeBannerHide(eq(banner));
    }

    @Test
    public void invokeHide_withNullListener_shouldPass() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        banner.mHandler = new Handler();
        banner.invokeHide();
    }

    @Test
    public void loadBanner_withNullContext_callOnPubnativeBannerLoadFail() {

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.load(null, "123456", PubnativeBanner.Size.BANNER_50, PubnativeBanner.Position.BOTTOM);

        verify(listener).onPubnativeBannerLoadFail(eq(banner), any(Exception.class));

    }

    @Test
    public void loadBanner_withNotActivityContext_callOnPubnativeBannerLoadFail() {

        Context context = RuntimeEnvironment.application.getApplicationContext();

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.load(context, "123456", PubnativeBanner.Size.BANNER_50, PubnativeBanner.Position.BOTTOM);

        verify(listener).onPubnativeBannerLoadFail(eq(banner), any(Exception.class));

    }

    @Test
    public void loadBanner_withEmptyAppToken_callOnPubnativeBannerLoadFail() {

        String appToken = "";
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.load(activity, appToken, PubnativeBanner.Size.BANNER_50, PubnativeBanner.Position.BOTTOM);

        verify(listener).onPubnativeBannerLoadFail(eq(banner), any(Exception.class));

    }

    @Test
    public void loadBanner_whenBannerReady_callOnPubnativeBannerLoadFinish() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        when(banner.isReady()).thenReturn(true);
        banner.load(activity, "123456", PubnativeBanner.Size.BANNER_50, PubnativeBanner.Position.BOTTOM);

        verify(listener).onPubnativeBannerLoadFinish(banner);
    }

    @Test
    public void loadBanner_withNullHandler_createNewHandler(){

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeBanner banner = spy(PubnativeBanner.class);
        PubnativeBanner.Listener listener = mock(PubnativeBanner.Listener.class);
        banner.mListener = listener;
        when(banner.isReady()).thenReturn(true);
        banner.load(activity, "123456", PubnativeBanner.Size.BANNER_50, PubnativeBanner.Position.BOTTOM);

        assertThat(banner.mHandler).isNotNull();

    }

}