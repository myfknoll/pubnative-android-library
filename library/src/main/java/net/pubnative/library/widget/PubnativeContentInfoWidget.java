package net.pubnative.library.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.pubnative.library.R;

public class PubnativeContentInfoWidget extends RelativeLayout {

    private static final String TAG = PubnativeContentInfoWidget.class.getSimpleName();

    private RelativeLayout mContainerView;
    private TextView mContentInfoText;
    private ImageView mContentInfoIcon;

    private Handler mHandler;

    private Runnable mCloseTask = new Runnable() {
        @Override
        public void run() {
            closeLayout();
        }
    };

    public PubnativeContentInfoWidget(Context context) {
        super(context);
        init(context);
    }

    public PubnativeContentInfoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PubnativeContentInfoWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        LayoutInflater mInflator = LayoutInflater.from(context);
        mHandler = new Handler(Looper.getMainLooper());
        mContainerView = (RelativeLayout) mInflator.inflate(R.layout.content_info_layout, this, true);
        mContentInfoIcon = (ImageView) mContainerView.findViewById(R.id.ic_context_icon);
        mContentInfoText = (TextView) mContainerView.findViewById(R.id.tv_context_text);
    }

    public void openLayout() {
        mContentInfoText.setVisibility(VISIBLE);
        mHandler.postDelayed(mCloseTask, 3000);
    }

    public void closeLayout() {
        mContentInfoText.setVisibility(GONE);
    }

    public void setIconUrl(String iconUrl) {
        Picasso.with(getContext()).load(iconUrl).into(mContentInfoIcon);
    }

    public void setIconClickUrl(final String iconClickUrl) {
        mContentInfoText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openLink = new Intent(Intent.ACTION_VIEW);
                openLink.setData(Uri.parse(iconClickUrl));
                view.getContext().startActivity(openLink);
            }
        });
    }

    public void setContextText(String text) {
        if (text != null && !text.isEmpty()) {
            mContentInfoText.setText(text);
        }
    }
}
