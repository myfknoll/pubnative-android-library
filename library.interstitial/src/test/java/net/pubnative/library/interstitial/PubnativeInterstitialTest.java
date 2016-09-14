package net.pubnative.library.interstitial;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(constants = BuildConfig.class, sdk = 16)
@RunWith(RobolectricGradleTestRunner.class)
public class PubnativeInterstitialTest {

    @Test
    public void loadInterstitial_withNullContext_invokeLoadFail() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.load(null, "123456");

        verify(listener).onPubnativeInterstitialLoadFail(eq(interstitial), any(Exception.class));

    }

    @Test
    public void loadInterstitial_withEmptyAppToken_invokeLoadFail() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.load(activity, "");

        verify(listener).onPubnativeInterstitialLoadFail(eq(interstitial), any(Exception.class));

    }

    @Test
    public void loadIterstitial_whenReady_invokeLoadFinish() {

        Activity activity = Robolectric.buildActivity(Activity.class).create().get();

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;

        when(interstitial.isReady()).thenReturn(true);

        interstitial.load(activity, "123456");

        verify(listener).onPubnativeInterstitialLoadFinish(interstitial);

    }

    @Test
    public void invokeLoadFail_withValidListener_shouldCallOnLoadFail() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        Exception ex = mock(Exception.class);
        interstitial.invokeLoadFail(ex);

        verify(listener).onPubnativeInterstitialLoadFail(eq(interstitial), eq(ex));
    }

    @Test
    public void invokeLoadFail_withNullListener_shouldPass() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.invokeLoadFail(mock(Exception.class));
    }

    @Test
    public void invokeLoadFinish_withValidListener_shouldCallOnLoadFinish() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.invokeLoadFinish();

        verify(listener).onPubnativeInterstitialLoadFinish(eq(interstitial));
    }

    @Test
    public void invokeLoadFinish_withNullListener_shouldPass() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.invokeLoadFinish();
    }

    @Test
    public void invokeShow_withValidListener_shouldCallOnShow() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.invokeShow();

        verify(listener).onPubnativeInterstitialShow(eq(interstitial));
    }

    @Test
    public void invokeShow_withNullListener_shouldPass() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.invokeShow();
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_shouldCallOnImpressionConfirmed() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.invokeImpressionConfirmed();

        verify(listener).onPubnativeInterstitialImpressionConfirmed(eq(interstitial));
    }

    @Test
    public void invokeImpressionConfirmed_withValidListener_shouldPass() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.invokeImpressionConfirmed();
    }

    @Test
    public void invokeClick_withValidListener_shouldCallOnClick() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.invokeClick();

        verify(listener).onPubnativeInterstitialClick(eq(interstitial));
    }

    @Test
    public void invokeClick_withNullListener_shouldPass() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.invokeClick();
    }

    @Test
    public void invokeHide_withValidListener_shouldCallOnHide() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        PubnativeInterstitial.Listener listener = mock(PubnativeInterstitial.Listener.class);
        interstitial.mListener = listener;
        interstitial.invokeHide();

        verify(listener).onPubnativeInterstitialHide(eq(interstitial));
    }

    @Test
    public void invokeHide_withNullListener_shouldPass() {

        PubnativeInterstitial interstitial = spy(PubnativeInterstitial.class);
        interstitial.invokeHide();
    }

}
