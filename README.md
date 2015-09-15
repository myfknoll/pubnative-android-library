![ScreenShot](PNLogo.png)

PubNative is an API-based publisher platform dedicated to native advertising which does not require the integration of an Library.

Through PubNative, publishers can request over 20 parameters to enrich their ads and thereby create any number of combinations for unique and truly native ad units.

#pubnative-android-library

pubnative-android-library is a collection of Open Source tools to implement API based native ads in Android.

##Contents

* [Requirements](#requirements)
* [Install](#install)
* [Usage](#usage)
  * [Setup](#usage_setup)
  * [Native ads](#usage_native)
  * [Predefined ads](#usage_predefined)
* [Misc](#misc)
  * [Dependencies](#misc_dependencies)
  * [License](#misc_license)
  * [Contributing](#misc_contributing)

<a name="requirements"></a>
# Requirements

* Android 4.0+.
* An App Token provided in PubNative Dashboard.
* Google Play Services (https://developer.android.com/google/play-services/index.html) imported in your project

<a name="install"></a>
# Install

Clone the repository and import the needed projects

####pubnative-library
This library handles the basic behaviour to request native ads and build a custom ad view. You will need to add the following activity to your `AndroidManifest.xml` **inside your application tag**
``` xml
<meta-data android:name="com.google.android.gms.version"
           android:value="@integer/google_play_services_version" />
```

<a name="usage"></a>
# Usage

PubNative library is a lean yet complete library that allow you request and show ads in different ways.

In general, it alows you to request the following ads:

* [Native](#usage_native): Use native ads if you want to manually control requests and fully customize your ads appearance.
* [Predefined](#usage_predefined): Use predefined for direct usage of the library if you don't want to mess up creating your own ad format.

<a name="usage_native"></a>
## Native

Basic integration steps are:

1. [Request](#usage_native_request): Using `AdRequest` and `AdRequestListener`
2. [Show](#usage_native_show): Using `NativeAdRenderer` and `AdRendererListener`
3. [Confirm impression](#usage_native_confirm_impression)

<a name="usage_native_request"></a>
### 1) Request

You will need to create an `AdRequest`, add all the required parameters to it and start it with a listener for the results specifying which endpoint you want to request to `NATIVE` or `VIDEO`.

For simplier usage we're providing an interface `Request` that contains all valid parameters for a request.

```java
AdRequest request = new AdRequest(context);
request.setParameter(Request.APP_TOKEN, "----YOUR_APP_TOKEN_HERE---");
request.setParameter(Request.AD_COUNT, "5");
request.setParameter(Request.ICON_SIZE, "200x200");
request.setParameter(Request.BANNER_SIZE, "1200x627");
request.start(Endpoint.NATIVE, new AdRequestListener()
{
    @Override
    public void onAdRequestStarted(AdRequest request)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAdRequestFailed(AdRequest request, Exception ex)
    {
        // TODO Auto-generated method stub
    }
});
```

<a name="usage_native_show"></a>
### 2) Show

Once the ads are downloaded, you can use them manually by accessing properties inside the model. Although, we've developed a tool that will work for most cases `NativeAdRenderer` for Native ads.

It downloads the needed resources, ensures that the data is correctly downloaded and sets it into the view.

Simply create a `NativeAdRenderer` for your custom view and render it with an AdRendererListener.

```java
NativeAdRenderer renderer = new NativeAdRenderer(context);
renderer.titleView = (TextView) view.findViewById(R.id.<YOUR_TITLE_VIEW_ID>);
renderer.descriptionView = (TextView) view.findViewById(R.id.<YOUR_DESCRIPTION_VIEW_ID>);
renderer.bannerView = (TextView) view.findViewById(R.id.<YOUR_BANNER_VIEW_ID>);
renderer.iconView = (ImageView) view.findViewById(R.id.<YOUR_ICON_VIEW_ID>);
renderer.downloadView = (TextView) view.findViewById(R.id.<YOUR_CTA_VIEW_ID>);
renderer.render(<YOU_AD_MODEL>, new AdRendererListener()
{
    @Override
    public void onAdRenderStarted(AdRenderer renderer)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAdRenderFailed(AdRenderer renderer, Exception e)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAdRenderFinished(AdRenderer renderer)
    {
        // TODO Auto-generated method stub
    }
});
```

<a name="usage_native_confirm_impression"></a>
### 3) Confirm impression

For confirming impressions, the `NativeAdModel` contains the tools to confirm it, just specify the view that contains that ad and the library will automatically confirm the impression as soon as the view is shown.

```java
ad.confirmImpressionAutomatically(context, <YOUR_AD_CONTAINER_VIEW>);
```

<a name="usage_predefined"></a>
## Predefined ads

If you're going to use predefined ads, you'll need to add a an Activity to your `AndroidManifest.xml` **inside your application tag**

```xml
<activity android:name="net.pubnative.library.predefined.PubnativeActivity"
          android:configChanges="keyboardHidden|orientation|screenSize"
          android:hardwareAccelerated="true"
          android:taskAffinity="net.pubnative.library.predefined"/>
```

It's also required to inform the Pubnative interface about the Activity callbacks, dd the following inside your Activity:

```java
@Override
protected void onPause()
{
	super.onPause();
	Pubnative.onPause();
}

@Override
protected void onResume()
{
	super.onResume();
	Pubnative.onResume();
}

@Override
protected void onDestroy()
{
	super.onDestroy();
	Pubnative.onDestroy();
}
```

Using the `Pubnative` interface you will have access to several predefined formats in case that you don't want to mess around by creating your own ad formats.

If you would like to know about the ad behaviour just specify a `PubnativeActivityListener` or pass null to the `show method`.

Available types are:

* Pubnative.FullScreen.INTERSTITIAL: Show a single fullscreen offer
* Punative.FullScreen.GAME_LIST: Shows a fullscreen list of 10 offers

```java
Pubnative.show(context, <SELECTED_TYPE>, "<YOUR_APP_TOKEN>",  new PubnativeActivityListener()
{
    @Override
    public void onPubnativeActivityStarted(String identifier)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPubnativeActivityFailed(String identifier, Exception exception)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPubnativeActivityOpened(String identifier)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPubnativeActivityClosed(String identifier)
    {
        // TODO Auto-generated method stub
    }
});
```

<a name="misc"></a>
# Misc

<a name="misc_dependencies"></a>
### Dependencies

* [Droidparts](https://github.com/yanchenko/droidparts) for making our life easier

<a name="misc_license"></a>
### License

This code is distributed under the terms and conditions of the MIT license.

<a name="misc_contributing"></a>
### Contributing

**NB!** If you fix a bug you discovered or have development ideas, feel free to make a pull request.
