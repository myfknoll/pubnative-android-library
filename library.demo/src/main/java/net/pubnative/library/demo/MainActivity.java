package net.pubnative.library.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import net.pubnative.library.Pubnative;
import net.pubnative.library.predefined.PubnativeActivityListener;

@SuppressWarnings("deprecation")
public class MainActivity extends ActionBarActivity implements OnClickListener, PubnativeActivityListener
{
    // OLD_API: 62cb87940b3a70cf67a200c3b443b47c20fd00b0a1aece21ec435d8e0521eb60
    // NEW_API: 6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092
    private final String app_token = "6651a94cad554c30c47427cbaf0b613a967abcca317df325f363ef154a027092";
    
    View settingsButton = null;
    View interstitialButton    = null;
    View   gameListButton    = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        settingsButton = this.findViewById(R.id.settings_text);
        settingsButton.setOnClickListener(this);
        
        interstitialButton = this.findViewById(R.id.interstitial_view);
        interstitialButton.setOnClickListener(this);
        
        gameListButton = this.findViewById(R.id.game_list_view);
        gameListButton.setOnClickListener(this);
    }
    
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view)
    {
        if (this.interstitialButton == view)
        {
            Pubnative.show(this, Pubnative.FullScreen.INTERSTITIAL, app_token, this);
        }
        else if(this.gameListButton == view)
        {
            Pubnative.show(this, Pubnative.FullScreen.GAME_LIST, app_token, this);
        }
        else if(this.settingsButton == view)
        {
            Toast.makeText(this, "Under development", Toast.LENGTH_SHORT).show();;
        }
    }
    
    //PubnativeActivityListener

    @Override
    public void onPubnativeActivityStarted(String identifier)
    {
        Log.v("pubnative-library-demo", "onPubnativeActivityStarted: " + identifier);
    }

    @Override
    public void onPubnativeActivityFailed(String identifier, Exception exception)
    {
        Log.v("pubnative-library-demo", "onPubnativeActivityFailed: " + identifier + " - Exception: " + exception);
    }

    @Override
    public void onPubnativeActivityOpened(String identifier)
    {
        Log.v("pubnative-library-demo", "onPubnativeActivityOpened: " + identifier);
    }

    @Override
    public void onPubnativeActivityClosed(String identifier)
    {
        Log.v("pubnative-library-demo", "onPubnativeActivityClosed: " + identifier);
    }
}
