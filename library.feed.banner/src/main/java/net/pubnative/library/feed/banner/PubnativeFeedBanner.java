package net.pubnative.library.feed.banner;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.pubnative.library.request.PubnativeAsset;
import net.pubnative.library.request.PubnativeRequest;
import net.pubnative.library.request.model.PubnativeAdModel;
import net.pubnative.library.widget.PubnativeContentInfoWidget;

import java.util.List;

public class PubnativeFeedBanner implements PubnativeRequest.Listener, PubnativeAdModel.Listener {

    private static final String TAG = PubnativeFeedBanner.class.getSimpleName();

    protected Context                      mContext;
    protected PubnativeFeedBanner.Listener mListener;
    protected boolean                      mIsLoading;
    protected boolean                      mLoadFailed;
    protected Handler                      mHandler;
    protected PubnativeAdModel             mAdModel;

    // InFeed Banner view
    protected RelativeLayout                mInFeedBannerView;
    protected View                          mContainer;
    protected View                          mLoader;
    protected TextView                      mTitle;
    protected TextView                      mDescription;
    protected ImageView                     mIconImage;
    protected ImageView                     mBannerImage;
    protected PubnativeContentInfoWidget    mContentInfo;
    protected Button                        mCallToAction;
    protected RatingBar                     mRating;

    /**
     * Interface for callbacks related to the in-feed banner
     */
    public interface Listener {

        /**
         * Called whenever the in-feed banner finished loading ad ad
         *
         * @param feedBanner feedBanner that finished the load
         */
        void onPubnativeFeedBannerLoadFinish(PubnativeFeedBanner feedBanner);

        /**
         * Called whenever the in-feed banner failed loading an ad
         *
         * @param feedBanner feedBanner that failed the load
         * @param exception  exception with the description of the load error
         */
        void onPubnativeFeedBannerLoadFailed(PubnativeFeedBanner feedBanner, Exception exception);

        /**
         * Called when the in-feed banner impression was confirmed
         *
         * @param feedBanner feedBanner which impression was confirmed
         */
        void onPubnativeFeedBannerImpressionConfirmed(PubnativeFeedBanner feedBanner);

        /**
         * Called whenever the in-feed banner was clicked by the user
         *
         * @param feedBanner feedBanner that was clicked
         */
        void onPubnativeFeedBannerClick(PubnativeFeedBanner feedBanner);

    }

    //==============================================================================================
    // Public
    //==============================================================================================

    /**
     * Sets a callback listener for this feed banner object
     *
     * @param listener valid PubnativeFeedBanner.Listener object
     */
    public void setListener(Listener listener) {

        Log.v(TAG, "setListener");
        mListener = listener;
    }

    /**
     * Load Feed Banner
     *
     * @param context  A valid context
     * @param appToken App token
     */
    public void load(Context context, String appToken) {

        Log.v(TAG, "load");
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mListener == null) {
            Log.w(TAG, "Listener is not set, try to set listener by setListener(Listener listener) method");
        }
        if (TextUtils.isEmpty(appToken)) {
            invokeLoadFail(new Exception("PubnativeFeedBanner - load error: app token is null or empty"));
        } else if (context == null) {
            invokeLoadFail(new Exception("PubnativeFeedBanner - load error: context is null or empty"));
        } else if (mIsLoading) {
            Log.w(TAG, "The ad is being loaded, dropping this call");
        } else if (isReady()) {
            invokeLoadFinish();
        } else {
            mIsLoading = true;
            mContext = context;
            mAdModel = null;
            initialize(); // to prepare the view

            mContainer.setVisibility(View.INVISIBLE);
            mLoader.setVisibility(View.VISIBLE);

            PubnativeRequest request = new PubnativeRequest();
            request.setParameter(PubnativeRequest.Parameters.APP_TOKEN, appToken);
            String[] assets = new String[]{
                    PubnativeAsset.TITLE,
                    PubnativeAsset.DESCRIPTION,
                    PubnativeAsset.ICON,
                    PubnativeAsset.BANNER,
                    PubnativeAsset.CALL_TO_ACTION,
                    PubnativeAsset.RATING
            };
            request.setParameterArray(PubnativeRequest.Parameters.ASSET_FIELDS, assets);
            request.start(mContext, this);
        }
    }

    /**
     * Checks whether ad has been retrieved
     *
     * @return true if retrieved and not shown otherwise false
     */
    public boolean isReady() {

        Log.v(TAG, "isReady");
        if (mLoadFailed) {
            Log.e(TAG, "banner load has failed, you should try loading it again");
        }
        return mAdModel != null && !mIsLoading && !mLoadFailed;
    }

    /**
     * Returns the configured ad view
     *
     * @return Valid View with configured ad view format
     */
    public View getView() {

        return mInFeedBannerView;
    }

    //==============================================================================================
    // Private
    //==============================================================================================

    protected void initialize() {

        Log.v(TAG, "initialize");
        if (mInFeedBannerView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mInFeedBannerView = (RelativeLayout) inflater.inflate(R.layout.pubnative_feed_banner, null);
            mContainer = mInFeedBannerView.findViewById(R.id.pubnative_feed_banner_container);
            mLoader = mInFeedBannerView.findViewById(R.id.pubnative_Feed_banner_loader);
            mTitle = (TextView) mInFeedBannerView.findViewById(R.id.pubnative_feed_banner_title);
            mRating = (RatingBar) mInFeedBannerView.findViewById(R.id.pubnative_infeed_rating);
            mDescription = (TextView) mInFeedBannerView.findViewById(R.id.pubnative_feed_banner_description);
            mIconImage = (ImageView) mInFeedBannerView.findViewById(R.id.pubnative_feed_banner_iconImage);
            mBannerImage = (ImageView) mInFeedBannerView.findViewById(R.id.pubnative_feed_banner_bannerImage);
            mCallToAction = (Button) mInFeedBannerView.findViewById(R.id.pubnative_feed_banner_button);
            mContentInfo = (PubnativeContentInfoWidget) mInFeedBannerView.findViewById(R.id.pubnative_content_info);
        }
    }

    protected void invokeLoadFinish() {

        Log.v(TAG, "invokeLoadFinish");
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mIsLoading = false;
                mContainer.setVisibility(View.VISIBLE);
                mLoader.setVisibility(View.INVISIBLE);
                if (mListener != null) {
                    mListener.onPubnativeFeedBannerLoadFinish(PubnativeFeedBanner.this);
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
                mLoadFailed = true;
                if (mListener != null) {
                    mListener.onPubnativeFeedBannerLoadFailed(PubnativeFeedBanner.this, exception);
                }
            }
        });
    }

    protected void invokeImpressionConfirmed() {

        Log.v(TAG, "invokeImpressionConfirmed");
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onPubnativeFeedBannerImpressionConfirmed(PubnativeFeedBanner.this);
                }
            }
        });
    }

    protected void invokeClick() {

        Log.v(TAG, "invokeClick");
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onPubnativeFeedBannerClick(PubnativeFeedBanner.this);
                }
            }
        });
    }


    //==============================================================================================
    // Callbacks
    //==============================================================================================
    //----------------------------------------------------------------------------------------------
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeRequestSuccess(PubnativeRequest request, List<PubnativeAdModel> ads) {

        Log.v(TAG, "onPubnativeRequestSuccess");

        if (ads == null || ads.size() == 0) {
            invokeLoadFail(new Exception("PubnativeFeedBanner - load error: no-fill"));
            return;
        }

        mAdModel = ads.get(0);
        mAdModel.startTracking(mInFeedBannerView, this);

        // Fill with data
        mTitle.setText(mAdModel.getTitle());
        mDescription.setText(mAdModel.getDescription());
        mCallToAction.setText(mAdModel.getCtaText());

        if (mAdModel.getRating() > 0) {
            mRating.setRating(mAdModel.getRating());
            mRating.setVisibility(View.VISIBLE);
        } else {
            mRating.setVisibility(View.GONE);
        }

        Picasso.with(mContext)
               .load(mAdModel.getIconUrl())
               .into(mIconImage, new Callback() {

                   @Override
                   public void onSuccess() {

                       Picasso.with(mContext)
                              .load(mAdModel.getBannerUrl())
                              .into(mBannerImage, new Callback() {

                                  @Override
                                  public void onSuccess() {
                                      invokeLoadFinish();
                                  }

                                  @Override
                                  public void onError() {
                                      invokeLoadFail(new Exception("Banner loading error"));
                                  }
                              });
                   }

                   @Override
                   public void onError() {
                       invokeLoadFail(new Exception("Icon loading error"));
                   }
               });

        mContentInfo.setIconUrl(mAdModel.getContentInfoIconUrl());
        mContentInfo.setIconClickUrl(mAdModel.getContentInfoLink());
        mContentInfo.setContextText(mAdModel.getContentInfoText());
        mContentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContentInfo.openLayout();
            }
        });
    }

    @Override
    public void onPubnativeRequestFailed(PubnativeRequest request, Exception ex) {

        Log.v(TAG, "onPubnativeRequestFailed");
        invokeLoadFail(ex);
    }

    //----------------------------------------------------------------------------------------------
    // PubnativeRequest.Listener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onPubnativeAdModelImpression(PubnativeAdModel pubnativeAdModel, View view) {
        Log.v(TAG, "onPubnativeAdModelImpression");
        invokeImpressionConfirmed();
    }

    @Override
    public void onPubnativeAdModelClick(PubnativeAdModel pubnativeAdModel, View view) {
        Log.v(TAG, "onPubnativeAdModelClick");
        invokeClick();
    }

    @Override
    public void onPubnativeAdModelOpenOffer(PubnativeAdModel pubnativeAdModel) {
        Log.v(TAG, "onPubnativeAdModelOpenOffer");
        // Do nothing
    }
}
