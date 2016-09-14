package net.pubnative.library.feed.video;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;
import net.pubnative.player.VASTPlayer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = net.pubnative.library.BuildConfig.class,
        sdk = 21)
public class PubnativeFeedVideoTest {
    @Test
    public void load_withNullContext_shouldCallbackFail() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.load(null, "app_token");

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void load_withInvalidAppToken_shouldCallbackFail() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.load(RuntimeEnvironment.application.getApplicationContext(), "");

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void load_withNullAppToken_shouldCallbackFail() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.load(RuntimeEnvironment.application.getApplicationContext(), null);

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withoutAds_shouldCallbackFail() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestSuccess(request, null);
        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void onPubnativeRequestSuccess_withAds_shouldStartVisibilityTracking() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        List<PubnativeAdModel> ads = new ArrayList<PubnativeAdModel>();
        ads.add(spy(PubnativeAdModel.class));
        video.mListener = listener;
        VASTPlayer player = new VASTPlayer(Robolectric.buildActivity(Activity.class).create().get());
        video.mVASTPlayer = spy(player);
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestSuccess(request, ads);
        verify(video).startVisibilityTracking();
    }

    @Test
    public void onPubnativeRequestFailed_shouldCallbackFail() {

        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        PubnativeRequest request = mock(PubnativeRequest.class);
        video.onPubnativeRequestFailed(request, mock(Exception.class));
        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), any(Exception.class));
    }

    @Test
    public void invokeLoadFail_withoutListener_shouldPass() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        video.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFail_withValidListener_shouldCallbackFail() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        Exception exception = mock(Exception.class);
        video.invokeLoadFail(exception);

        verify(listener).onPubnativeFeedVideoLoadFail(eq(video), eq(exception));
    }

    @Test
    public void invokeLoadFinish_withoutListener_shouldPass() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        video.invokeLoadFinish();
    }

    @Test
    public void invokeLoadFinish_withoutListener_shouldCallbackFinish() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        video.mHandler = new Handler();
        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
        video.mListener = listener;
        video.invokeLoadFinish();

        verify(listener).onPubnativeFeedVideoLoadFinish(eq(video));
    }

    @Test
    public void saveInParcel_restoreFromParcel_shouldBeTheSame() {
        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("video", video);

        assertThat(bundle.getParcelable("video")).isEqualTo(video);
    }

//    @Test
//    public void invokeShow_withoutListener_shouldPass() {
//
//        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
//        video.mHandler = new Handler();
//        video.invokeShow();
//    }
//
//    @Test
//    public void invokeShow_withValidListener_shouldCallbackShow() {
//
//        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
//        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
//        video.mListener = listener;
//        video.mHandler = new Handler();
//        video.invokeShow();
//
//        verify(listener).onPubnativeFeedVideoShow(eq(video));
//    }
//
//    @Test
//    public void invokeClick_withoutListener_shouldPass() {
//
//        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
//        video.mHandler = new Handler();
//        video.invokeVideoClick();
//    }
//
//    @Test
//    public void invokeClick_withValidListener_shouldCallbackClick() {
//
//        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
//        PubnativeFeedVideo.Listener listener = mock(PubnativeFeedVideo.Listener.class);
//        video.mListener = listener;
//        video.mHandler = new Handler();
//        video.invokeVideoClick();
//
//        verify(listener).onPubnativeFeedVideoClick(eq(video));
//    }
//
//    @Test
//    public void show_withNullContainer_shouldPass() {
//
//        PubnativeFeedVideo video = spy(PubnativeFeedVideo.class);
//        video.mHandler = new Handler();
//        video.show(null);
//    }
}