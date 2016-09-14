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

package net.pubnative.library.feed.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;
import net.pubnative.library.tracking.PubnativeVisibilityTracker;
import net.pubnative.player.VASTParser;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

import java.util.List;

public class PubnativeFeedVideo implements Parcelable,
                                           PubnativeRequest.Listener,
                                           PubnativeVisibilityTracker.Listener,
                                           VASTPlayer.Listener {

    private static final String TAG = PubnativeFeedVideo.class.getSimpleName();

    //==============================================================================================
    // Properties
    //==============================================================================================
    protected Handler                     mHandler;
    protected Context                     mContext;
    protected PubnativeAdModel            mAdModel;
    protected PubnativeFeedVideo.Listener mListener;
    protected String                      mAppToken;
    protected boolean                     mIsLoading;
    protected boolean                     mIsShown;
    protected boolean                     mIsVideoLoaded;
    protected boolean                     mIsVideoPlaying;
    protected boolean                     mIsTrackingWaiting;
    protected boolean                     mIsAlreadyShown;
    protected PubnativeVisibilityTracker  mVisibilityTracker;
    protected VASTModel                   mVASTModel;
    protected Target                      mBannerTarget;

    // Video view
    protected VASTPlayer mVASTPlayer;


    /**
     * Interface for callbacks related to the video behaviour
     */
    public interface Listener {

        /**
         * Called whenever the video finished loading an ad
         *
         * @param video video that finished the load
         */
        void onPubnativeFeedVideoLoadFinish(PubnativeFeedVideo video);

        /**
         * Called whenever the video failed loading an ad
         *
         * @param video     video that failed the load
         * @param exception exception with the description of the load error
         */
        void onPubnativeFeedVideoLoadFail(PubnativeFeedVideo video, Exception exception);
    }

    public PubnativeFeedVideo(){
        //Need empty constructor,
        //because we use Parcelable interface
    }

    protected PubnativeFeedVideo(Parcel in) {

        mAppToken = in.readString();
        mIsLoading = in.readByte() != 0;
        mIsShown = in.readByte() != 0;
        mIsVideoLoaded = in.readByte() != 0;
        mIsVideoPlaying = in.readByte() != 0;
        mIsTrackingWaiting = in.readByte() != 0;
        mIsAlreadyShown = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mAppToken);
        dest.writeByte((byte) (mIsLoading ? 1 : 0));
        dest.writeByte((byte) (mIsShown ? 1 : 0));
        dest.writeByte((byte) (mIsVideoLoaded ? 1 : 0));
        dest.writeByte((byte) (mIsVideoPlaying ? 1 : 0));
        dest.writeByte((byte) (mIsTrackingWaiting ? 1 : 0));
        dest.writeByte((byte) (mIsAlreadyShown ? 1 : 0));
    }

    @Override
    public int describeContents() {

        return 0;
    }

    public static final Creator<PubnativeFeedVideo> CREATOR = new Creator<PubnativeFeedVideo>() {

        @Override
        public PubnativeFeedVideo createFromParcel(Parcel in) {

            return new PubnativeFeedVideo(in);
        }

        @Override
        public PubnativeFeedVideo[] newArray(int size) {

            return new PubnativeFeedVideo[size];
        }
    };

    /**
     * Sets a callback listener for this video object
     *
     * @param listener valid PubnativeFeedVideo.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Starts loading an ad for this video
     *
     * @param context  valid Context
     * @param appToken valid App token where to request the ad from
     */
    public void load(Context context, String appToken) {

        Log.v(TAG, "load");
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mListener == null) {
            Log.w(TAG, "load - The ad hasn't a listener");
        }
        if (TextUtils.isEmpty(appToken)) {
            invokeLoadFail(new Exception("PubnativeFeedVideo - load error: app token is null or empty"));
        } else if (context == null) {
            invokeLoadFail(new Exception("PubnativeFeedVideo - load error: context is null or empty"));
        } else if (mIsLoading) {
            Log.w(TAG, "load - The ad is being loaded, dropping the call");
        } else if (mIsShown) {
            Log.w(TAG, "show - the ad is already shown, dropping the call");
        } else if (isReady() && mVASTModel != null) {
            mIsVideoPlaying = false;
            mVASTPlayer.load(mVASTModel);
        } else {
            mContext = context;
            mAppToken = appToken;
            mIsShown = false;
            mIsLoading = true;
            mIsVideoLoaded = false;
            mIsVideoPlaying = false;
            mIsTrackingWaiting = true;
            mIsAlreadyShown = false;
            initialize();
            PubnativeRequest request = new PubnativeRequest();
            request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, mAppToken);
            String[] assets = new String[]{
                    PubnativeAsset.BANNER,
                    PubnativeAsset.VAST
            };
            request.setParameterArray(PubnativeRequest.Parameters.ASSET_FIELDS, assets);
            request.start(mContext, this);
        }
    }

    /**
     * Method that checks if the video is ready to be shown in the screenhahaha
     *
     * @return true if the video can be shown false if not
     */
    public boolean isReady() {

        boolean result = mAdModel != null && !mIsShown && !mIsLoading;
        Log.v(TAG, "isReady: " + result);
        return result;
    }

    /**
     * Returns the initialised video view
     *
     * @return the video view
     */
    public View getView() {

        return mVASTPlayer;
    }

    /**
     * Hides video ad
     */
    public void hide() {

        Log.v(TAG, "hide");
        if (mIsShown) {

            if (mIsVideoLoaded && mIsVideoPlaying) {
                mVASTPlayer.pause();
                mIsVideoPlaying = false;
            }

            mAdModel.stopTracking();
            mIsTrackingWaiting = true;

            //stopVisibilityTracking();
            mIsShown = false;
        }
    }

    /**
     * Destroy the current InFeed Video
     */
    public void destroy() {

        Log.v(TAG, "destroy");
        mVASTPlayer.destroy();
        mAdModel.stopTracking();
        mAdModel = null;
        mIsLoading = false;
        mIsShown = false;
        mIsVideoLoaded = false;
        mIsVideoPlaying = false;
        stopVisibilityTracking();
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================

    protected void startVisibilityTracking() {

        Log.v(TAG, "startTrackingVisibility");

        stopVisibilityTracking();
        mVisibilityTracker = new PubnativeVisibilityTracker();
        mVisibilityTracker.setListener(this);
        mVisibilityTracker.addView(mVASTPlayer, -1); // We want to be noticed as invisible if visibility is absolute 0
    }

    protected void stopVisibilityTracking() {

        Log.v(TAG, "stopTrackingVisibility");
        if (mVisibilityTracker != null) {
            mVisibilityTracker.clear();
            mVisibilityTracker = null;
        }
    }

    protected void initialize() {

        Log.v(TAG, "initialize");
        if (mVASTPlayer == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View container = layoutInflater.inflate(R.layout.pubnative_feed_video, null);

            mVASTPlayer = (VASTPlayer) container.findViewById(R.id.player);
            mVASTPlayer.setListener(this);
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsLoading = false;
                if (mListener != null) {
                    mListener.onPubnativeFeedVideoLoadFinish(PubnativeFeedVideo.this);
                }
            }
        });
    }

    protected void invokeLoadFail(final Exception exception) {

        Log.v(TAG, "invokeLoadFail", exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsLoading = false;
                if (mListener != null) {
                    mListener.onPubnativeFeedVideoLoadFail(PubnativeFeedVideo.this, exception);
                }
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================
    // ================
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

        Log.v(TAG, "onPubnativeRequestSuccess");
        if (ads == null || ads.size() == 0) {
            invokeLoadFail(new Exception("PubnativeFeedVideo - load error: error loading resources"));
        } else {

            // Start tracker of impression
            startVisibilityTracking();

            mAdModel = ads.get(0);
            // Fetch banner
            if (mAdModel.getBannerUrl() != null) {

                if(mBannerTarget == null) {
                    mBannerTarget = new Target() {

                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mVASTPlayer.setBanner(bitmap);
                            loadVideo();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            loadVideo();
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // Do nothing
                        }
                    };
                }
                Picasso.with(mContext)
                       .load(mAdModel.getBannerUrl())
                       .into(mBannerTarget);

            } else {
                loadVideo();
            }
        }
    }

    protected void loadVideo() {

        // Load video
        new VASTParser(mContext).setListener(new VASTParser.Listener() {

            @Override
            public void onVASTParserError(int error) {

                Log.v(TAG, "onVASTParserError");
                invokeLoadFail(new Exception("PubnativeFeedVideo - render error: error loading resources"));
            }

            @Override
            public void onVASTParserFinished(VASTModel model) {

                Log.v(TAG, "onVASTParserFinished");

                mVASTModel = model;
                mVASTPlayer.load(mVASTModel);
                invokeLoadFinish();
            }

        }).execute(mAdModel.getVast());
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        Log.v(TAG, "onPubnativeRequestFailed");
        invokeLoadFail(ex);
    }

    //----------------------------------------------------------------------------------------------
    // VASTPlayer.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onVASTPlayerLoadFinish() {

        Log.v(TAG, "onVASTPlayerLoadFinish");
        mIsVideoLoaded = true;
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {

        Log.v(TAG, "onVASTPlayerFail");
        invokeLoadFail(new Exception("PubnativeFeedVideo - show error: error loading player"));
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

        Log.v(TAG, "onVASTPlayerPlaybackStart");
        // Do nothing
    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

        Log.v(TAG, "onVASTPlayerPlaybackFinish");
        mIsAlreadyShown = true;
    }

    @Override
    public void onVASTPlayerOpenOffer() {

        Log.v(TAG, "onVASTPlayerOpenOffer");
        mIsAlreadyShown = true;
    }

    //----------------------------------------------------------------------------------------------
    // PubnativeVisibilityTracker.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onVisibilityCheck(List<View> visibleViews, List<View> invisibleViews) {

        if (visibleViews.contains(mVASTPlayer)) {

            mIsShown = true;
            if (mIsTrackingWaiting) {

                mIsTrackingWaiting = false;
                mAdModel.startTracking(mVASTPlayer, null, null);
            }
            if (mIsVideoLoaded && !mIsVideoPlaying && !mIsAlreadyShown) {
                mVASTPlayer.play();
                mIsVideoPlaying = true;
            }
        }

        if (invisibleViews.contains(mVASTPlayer)) {

            hide();
        }
    }
}
