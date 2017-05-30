package com.crossriverwatch.crossriverwatch.fragments;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.accounts.AuthenticatorService;
import com.crossriverwatch.crossriverwatch.database.NewsContract;
import com.crossriverwatch.crossriverwatch.database.NewsProvider;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private FirebaseAnalytics mFirebaseAnalytics;


    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        addPreferencesFromResource(R.xml.preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(getString(R.string.pref_sync_key)))
        {
            String CONTENT_AUTHORITY = NewsContract.CONTENT_AUTHORITY;
            String ACCOUNT_TYPE = "com.crossriverwatch.crossriverwatch.accounts";
            Account account = AuthenticatorService.GetAccount(ACCOUNT_TYPE);

            String defSyncTime = getString(R.string.pref_title_sync_frequency_default);
            String SyncKey = getString(R.string.pref_sync_key);
            String syncTime = sharedPreferences.getString(SyncKey,defSyncTime);
            long SYNC_FREQUENCY = 60 * Long.valueOf(syncTime);

            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);

        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, "open settings");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
