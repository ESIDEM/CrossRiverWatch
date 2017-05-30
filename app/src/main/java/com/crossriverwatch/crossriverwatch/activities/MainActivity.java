package com.crossriverwatch.crossriverwatch.activities;


import android.annotation.TargetApi;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.crossriverwatch.crossriverwatch.parser.Post;
import com.crossriverwatch.crossriverwatch.utility.AppController;
import com.crossriverwatch.crossriverwatch.R;
import com.crossriverwatch.crossriverwatch.fragments.BusinessNews;

import com.crossriverwatch.crossriverwatch.fragments.EducationNews;
import com.crossriverwatch.crossriverwatch.fragments.Entertainment;
import com.crossriverwatch.crossriverwatch.fragments.HealthNews;
import com.crossriverwatch.crossriverwatch.fragments.PoliticsNews;
import com.crossriverwatch.crossriverwatch.fragments.RecentPost;
import com.crossriverwatch.crossriverwatch.fragments.Reports;
import com.crossriverwatch.crossriverwatch.fragments.SportsNews;
import com.crossriverwatch.crossriverwatch.fragments.TechNews;
import com.crossriverwatch.crossriverwatch.services.SyncUtils;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.PersonBuilder;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener

         {


    private static final String TAG = "MainActivity";

    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_INVITE = 1;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final int REQUEST_IMAGE = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = RecentPost.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
              //  .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(AppInvite.API)
                .build();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        //mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        ((AppController) this.getApplication()).setBASE_URL("http://crossriverwatch.com/?json=get_posts&page=");





       // getSupportLoaderManager().initLoader(0, null, this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(prefs.getBoolean(getString(R.string.pref_sync_on_open_key),true)) {
            SyncUtils.TriggerRefresh();

        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.VALUE, "app open");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }else {
            super.onBackPressed();
        }
    }



             private void sendInvitation() {
                 Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                         .setMessage(getString(R.string.invitation_message))
                         .setCallToActionText(getString(R.string.invitation_cta))
                         .build();
                 startActivityForResult(intent, REQUEST_INVITE);
             }

             @Override
             protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                 super.onActivityResult(requestCode, resultCode, data);
                 Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

                 if (requestCode == REQUEST_IMAGE) {
                     // ...
                 } else if (requestCode == REQUEST_INVITE) {
                     if (resultCode == RESULT_OK) {
                         Bundle payload = new Bundle();
                         payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
                         mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                                 payload);
                         // Check how many invitations were sent and log.
                         String[] ids = AppInviteInvitation.getInvitationIds(resultCode,
                                 data);
                         Log.d(TAG, "Invitations sent: " + ids.length);
                     } else {
                         Bundle payload = new Bundle();
                         payload.putString(FirebaseAnalytics.Param.VALUE, "not sent");
                         mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                                 payload);
                         // Sending failed or it was canceled, show failure message to
                         // the user
                         Log.d(TAG, "Failed to send invitation.");
                     }
                 }
             }
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, Settings.class));
            return true;
        }else if(id == R.id.action_favorite ){

            startActivity(new Intent(this,Favourites.class));
        }

        //else if(id == R.id.action_search){


       // }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;

        if (id == R.id.nav_latest) {

            fragmentClass = RecentPost.class;



        } else if (id==R.id.nav_sport){

            fragmentClass = SportsNews.class;




        }else if(id==R.id.nav_tech){

            fragmentClass = TechNews.class;
        }
        else if (id == R.id.nav_education) {

            fragmentClass = EducationNews.class;

        } else if (id == R.id.nav_report) {

            fragmentClass = Reports.class;

        } else if (id == R.id.nav_health) {

            fragmentClass = HealthNews.class;
        } else if (id == R.id.nav_politics){

            fragmentClass = PoliticsNews.class;

        }else if( id == R.id.nav_entertainment){

            fragmentClass = Entertainment.class;

        }else if (id == R.id.nav_business){

            fragmentClass = BusinessNews.class;

        }
        else if(id == R.id.nav_invite){

            sendInvitation();

            return true;
        }

        else if (id == R.id.nav_favourite){
            Intent intent = new Intent(this,Favourites.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.nav_settings){

            startActivity(new Intent(this, Settings.class));

            return true;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }




    @Override
    public void onDestroy() {


        super.onDestroy();
    }


             @Override
             public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                 Log.d(TAG, "onConnectionFailed:" + connectionResult);
             }
         }
