package net.pubnative.library.video;

import android.os.Handler;

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
public class PubnativeVideoTest {
    @Test
    public void load_withNullContext_shouldCallbackFail() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        video.load(null, "app_token");

        verify(listener).onPubnativeVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void load_withInvalidAppToken_shouldCallbackFail() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        video.load(RuntimeEnvironment.application.getApplicationContext(), "");

        verify(listener).onPubnativeVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void load_withNullAppToken_shouldCallbackFail() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        video.load(RuntimeEnvironment.application.getApplicationContext(), null);

        verify(listener).onPubnativeVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withoutAds_shouldCallbackFail() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestSuccess(request, null);
        verify(listener).onPubnativeVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestFailed_shouldCallbackFail() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestFailed(request, mock(Exception.class));
        verify(listener).onPubnativeVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void invokeLoadFail_withoutListener_shouldPass() {
        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        video.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFail_withValidListener_shouldCallbackFail() {
        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        Exception exception = mock(Exception.class);
        video.invokeLoadFail(exception);

        verify(listener).onPubnativeVideoLoadFail(eq(video), eq(exception));
    }

    @Test
    public void invokeLoadFinish_withoutListener_shouldPass() {
        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        video.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFinish_withValidListener_shouldCallbackFinish() {
        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        video.invokeLoadFinish();

        verify(listener).onPubnativeVideoLoadFinish(eq(video));
    }

    @Test
    public void invokeShow_withoutListener_shouldPass() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        video.invokeShow();
    }

    @Test
    public void invokeShow_withValidListener_shouldCallbackShow() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        video.mHandler = new Handler();
        video.invokeShow();

        verify(listener).onPubnativeVideoShow(eq(video));
    }

    @Test
    public void invokeClick_withoutListener_shouldPass() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        video.mHandler = new Handler();
        video.invokeVideoClick();
    }

    @Test
    public void invokeClick_withValidListener_shouldCallbackClick() {

        PubnativeVideo video = spy(PubnativeVideo.class);
        PubnativeVideo.Listener listener = mock(PubnativeVideo.Listener.class);
        video.mListener = listener;
        video.mHandler = new Handler();
        video.invokeVideoClick();

        verify(listener).onPubnativeVideoClick(eq(video));
    }
}