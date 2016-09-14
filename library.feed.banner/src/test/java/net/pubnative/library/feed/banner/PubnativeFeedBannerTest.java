package net.pubnative.library.feed.banner;

import android.os.Handler;
import android.view.View;

import net.pubnative.library.request.PubnativeRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = net.pubnative.library.BuildConfig.class,
        sdk = 21)
public class PubnativeFeedBannerTest {
    @Test
    public void load_withNullContext_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.load(null, "app_token");

        verify(listener).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void load_withInvalidAppToken_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.load(RuntimeEnvironment.application.getApplicationContext(), "");

        verify(listener).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void load_withNullAppToken_pass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.load(RuntimeEnvironment.application.getApplicationContext(), null);

        verify(listener).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withoutAds_invokesLoadFail() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        banner.onPubnativeRequestSuccess(request, null);
        verify(listener).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestFailed_invokesLoadFailed() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        banner.onPubnativeRequestFailed(request, mock(Exception.class));
        verify(listener).onPubnativeFeedBannerLoadFailed(eq(banner), any(Exception.class));
    }

    @Test
    public void invokeLoadFail_withNullListener_shouldPass() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFail_withValidListener_invokesLoadFailed() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        Exception exception = mock(Exception.class);
        banner.invokeLoadFail(exception);

        verify(listener).onPubnativeFeedBannerLoadFailed(eq(banner), eq(exception));
    }

    @Test
    public void invokeLoadFinish_withNullListener_shouldPass() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.mContainer = new View(RuntimeEnvironment.application.getApplicationContext());
        banner.mLoader = new View(RuntimeEnvironment.application.getApplicationContext());
        banner.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFinish_withValidListener_invokesLoadFinish() {
        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.mContainer = new View(RuntimeEnvironment.application.getApplicationContext());
        banner.mLoader = new View(RuntimeEnvironment.application.getApplicationContext());
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.invokeLoadFinish();

        verify(listener).onPubnativeFeedBannerLoadFinish(eq(banner));
    }

    @Test
    public void invokeImpressionConfirmed_withNullListener_shouldPass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeImpressionConfirmed();
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_invokesImpressionConfirmed() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeImpressionConfirmed();

        verify(listener).onPubnativeFeedBannerImpressionConfirmed(eq(banner));
    }

    @Test
    public void invokeClick_withNullListener_shouldPass() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        banner.mHandler = new Handler();
        banner.invokeClick();
    }

    @Test
    public void invokeClick_withValidListener_invokesClick() {

        PubnativeFeedBanner banner = spy(PubnativeFeedBanner.class);
        PubnativeFeedBanner.Listener listener = mock(PubnativeFeedBanner.Listener.class);
        banner.mListener = listener;
        banner.mHandler = new Handler();
        banner.invokeClick();

        verify(listener).onPubnativeFeedBannerClick(eq(banner));
    }
}