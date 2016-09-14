// The MIT License (MIT)
//
// Copyright (c) 2016 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.library.request.model;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import net.pubnative.library.BuildConfig;
import net.pubnative.library.request.model.api.PubnativeAPIV3AdModel;
import net.pubnative.library.request.model.api.PubnativeAPIV3DataModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class,
        sdk = 21)
public class PubnativeAdModelTest {

    Context  applicationContext;
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
    public void testImpressionCallbackWithValidListener() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        model.mListener = listener;
        model.invokeOnImpression(adView);
        verify(listener, times(1)).onPubnativeAdModelImpression(eq(model), eq(adView));
    }

    @Test
    public void testClickCallbackWithValidListener() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        View adView = spy(new View(applicationContext));
        model.mListener = listener;
        model.invokeOnClick(adView);
        verify(listener, times(1)).onPubnativeAdModelClick(eq(model), eq(adView));
    }

    @Test
    public void testOpenOfferCallbackWithValidListener() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        model.mListener = listener;
        model.invokeOnOpenOffer();
        verify(listener, times(1)).onPubnativeAdModelOpenOffer(eq(model));
    }

    @Test
    public void testCallbacksWithNullListener() {

        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.mListener = null;
        model.invokeOnOpenOffer();
        model.invokeOnClick(null);
        model.invokeOnImpression(null);
    }

    @Test
    public void testStartTrackingWithClickableView() {

        PubnativeAPIV3AdModel adDataModel = spy(PubnativeAPIV3AdModel.class);
        adDataModel.link = "http://www.google.com";
        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.mData = adDataModel;
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        model.startTracking(adView, clickableView, listener);
        verify(clickableView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithClickableViewForValidClickListener() {

        PubnativeAPIV3AdModel adDataModel = spy(PubnativeAPIV3AdModel.class);
        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.mData = adDataModel;
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        model.startTracking(adView, clickableView, listener);
        verify(adView, never()).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void testStartTrackingWithoutClickableView() {

        PubnativeAPIV3AdModel adDataModel = spy(PubnativeAPIV3AdModel.class);
        adDataModel.link = "http://www.google.com";
        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.mData = adDataModel;
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        View adView = spy(new View(activity));
        model.startTracking(adView, listener);
        verify(adView, times(1)).setOnClickListener(any(View.OnClickListener.class));
    }

    @Test
    public void openUrl_withValidData_onPubnativeAdModelOpenOfferCalled() {
        PubnativeAPIV3AdModel adDataModel = spy(PubnativeAPIV3AdModel.class);
        String url = "http://www.google.com";
        adDataModel.link = url;
        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.mData = adDataModel;
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        model.startTracking(adView, clickableView, listener);
        model.openURL(url);

        verify(listener).onPubnativeAdModelOpenOffer(eq(model));
    }

    @Test
    public void startOnClick_withValidData_willRemoveLoadingView() {

        PubnativeAPIV3AdModel adDataModel = spy(PubnativeAPIV3AdModel.class);
        PubnativeAdModel model = spy(PubnativeAdModel.class);
        model.mData = adDataModel;
        String url = "http://www.google.com";
        ViewGroup container = new RelativeLayout(applicationContext);
        View adView = spy(new View(activity));
        View clickableView = spy(new View(activity));
        container.addView(adView);
        container.addView(clickableView);
        PubnativeAdModel.Listener listener = mock(PubnativeAdModel.Listener.class);
        model.startTracking(adView, clickableView, listener);
        model.onURLDrillerFinish(url);

        verify(model).getLoadingView();
    }

    @Test
    public void getAsset_withRightData_returnDataModel() {
        String assetName = "text";
        String assetValue = "value";

        PubnativeAPIV3AdModel adModel = spy(PubnativeAPIV3AdModel.class);
        PubnativeAPIV3DataModel adDataModel = spy(PubnativeAPIV3DataModel.class);
        PubnativeAdModel model = spy(PubnativeAdModel.class);
        adDataModel.data = new HashMap<String, String>();
        adModel.assets = new ArrayList<PubnativeAPIV3DataModel>();
        adModel.meta = new ArrayList<PubnativeAPIV3DataModel>();
        adModel.beacons = new ArrayList<PubnativeAPIV3DataModel>();
        adDataModel.data.put(assetName, assetValue);
        adDataModel.type = assetName;
        adModel.assets.add(adDataModel);
        adModel.meta.add(adDataModel);
        adModel.beacons.add(adDataModel);
        model.mData = adModel;

        assertThat(model.getAsset(assetName)).isEqualTo(adDataModel);
    }

}
