package com.myatminsoe.mkeyboard;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconCompat;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import mm.technomation.dinga.DRM;


public class MainActivity extends AppCompatActivity {

    private MaterialMenuIconCompat materialMenu;
    public static DrawerLayout mDrawerLayout;
    private boolean isDrawerOpen = false;
    Fragment fragment;
    FragmentManager fragmentManager;
    public static DRM drm;
    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        materialMenu = new MaterialMenuIconCompat(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                materialMenu.setTransformationOffset(
                        MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                        isDrawerOpen ? 2 - slideOffset : slideOffset
                );
            }

            @Override
            public void onDrawerOpened(View view) {
                isDrawerOpen = true;
            }

            @Override
            public void onDrawerClosed(View view) {
                isDrawerOpen = false;
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
        z(this);
        isKeyboardOn();

        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("copycon", false)) {
            startService(new Intent(getBaseContext(), Background.class));
        } else {
            stopService(new Intent(getBaseContext(), Background.class));
        }
        NotificationManager mNotificationManager;
        int notificationID = 100;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationID);
        Bundle b = getIntent().getExtras();
        if (b != null && b.getBoolean("isNoti")) {
            fragment = new FragmentConverter();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "").commit();
        } else if (!FragmentSetup.isChosen(this)) {
            fragment = new FragmentSetup();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "").commit();
        } else {
            fragment = new FragmentSettings();
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "").commit();
        }

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                closeDrawer();

                switch (menuItem.getItemId()) {
                    case R.id.setup:
                        fragment = new FragmentSetup();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                        closeDrawer();
                        break;
                    case R.id.settings:
                        fragment = new FragmentSettings();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                        closeDrawer();
                        break;
                    case R.id.converter:
                        fragment = new FragmentConverter();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                        closeDrawer();
                        break;
                    case R.id.about:
                        fragment = new FragmentAbout();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                        closeDrawer();
                        break;
                    case R.id.tutorial:
                        fragment = new FragmentTutorial();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                        closeDrawer();
                        break;
                }
                return true;
            }
        });
    }


    /*public void onMenuClicked(View v) {
        switch (v.getId()) {
            case R.id.acc:
                fragment = new FragmentAccount();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                closeDrawer();
                break;
            case R.id.setup:
                fragment = new FragmentSetup();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                closeDrawer();
                break;
            case R.id.settings:
                fragment = new FragmentSettings();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                closeDrawer();
                break;
            case R.id.converter:
                fragment = new FragmentConverter();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                closeDrawer();
                break;
            case R.id.about:
                fragment = new FragmentAbout();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                closeDrawer();
                break;
            case R.id.tutorial:
                fragment = new FragmentTutorial();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, "Detail").commit();
                closeDrawer();
                break;
        }
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        materialMenu.syncState(savedInstanceState);
    }

    private void isKeyboardOn() {
        if (!drm.isDingaInstalled(this)) {
            drm.askDingaStore(this);
        }
    }

    boolean shown = false;

    @Override
    protected void onResume() {
        super.onResume();
        z(this);
        try {
            if (!drm.isDingaInstalled(this)) {
                drm.askDingaStore(this);
            } else if (!drm.Arm()) {
                drm.Purchase(this, 100);
            }
        } catch (DRM.AuthenticationException ex) {

        }
    }

    public static void z(Context c) {
        drm = new DRM("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDTiLCvSjOWUfDa/gsI1SkH6GJjQRe0JmCbgu5KFkDckAmjzXSn+bU7VnpSt5DptEeXJNZGsE8k5VMalNNxV589yAc0hxNYS6KvXwWtOZHzPvBEjBljJ23zfKGnuB5qkW5U0O5VEcL8mdWPfsrzuZkTjATDsV33XDmFko249Jw4ZIP1oLoNfXhqQBqpFaX4WuArKQttLWwXPAaLI1wjuYYwGFU2cmYVCkp42zEC0LEBsigDYD9NfwPkw/h422WqJxzPVzS4jFYKp6Nt+sGpNd6HVEZQjCIbys+dSZFXKX8QMFCYaOwWA80yaDNgbXqUiKhkK+1LHjGG+5vagpNnSlWDAgMBAAECggEANRyh+DEySLDkP6f/j0UuGy0u3uruD65/HSy/tdxmhrqnoPgqLS7WYUKyTeiHOxanzvxRzMfh50qpFgh+Gnw8oF+D551urwG2pD/AhiDpU2DfJObkTPAOfE8zq4/gGzPOtv8KiFOAWLorU+8q+A0GsVC7tYLoK/589tu4N9M7EjE9sWmUBEzYXZ6NuXvDftoJ6NYYA0ngeLMI5GHUzEyyAJikhruVNigR59XeYC+JXaRvh1eoVgVQty4SbABUS6pzBU+JWLQEheGo0xGy+ItDErBIZgALyejMv3oMjyHm3MaY+nZK9oSpwXZ6Wc65J1Oend/ISYhfMd3TuVYSOkUFwQKBgQDzrRRBXWrD1xCZAUT9exqN/FiNHA20EpeX4+mniKgayPaALg56rPYk86NQuAxbCAJCJuWmoXwWlg21o4t3rZlXSTRLeXQbcYKpyyAw3UlW4ddrg8DC9uMoJXsBLDq8I5suFTHKJKrxX+aHu9oaw9P5hH8CldOwT46iErpelmZFIwKBgQDeO3XoARFnWv4rA8itbU/d5FMeAm90hTfE1fL6WSyMGbEpiSSl0lEplRcrutxVJeOnwkLvtL6iICLICT9PPK+lMvT1mG+os2cdcDikRVjk9inucd52ztTJxKYg4aozAYgOAbmzZL5WJkvIK8kUZC38h40VYLG3zenwyRuOGHukIQKBgGZ1C+NACDg4IX50DjIWpN+2jvUmKozglGUFK6WJzEUW4q6arvPd7dEzsCOsf2V3RJvUBxO9KpPEnBcXoPi9QZB180sY0j0HyUzjX7GsGb4yT4WhbFeNv42N1N51Xmzk+8awmrLIIChFjoTb86T1Whe8IjtQDCZCDVMQVNSYoH/JAoGAUoIpCHY9C+6Qp0/EVf9bw7pd0ap9zvkW95GnoKMzEuEHEbDFVnt7fsDH1YiO/V4P490QFQc6L99WioZEZxrU67S7leN+sVfKVPexT+wfUQLEFq9YNSwdfLvyIgsmscFDzk99rskckceuIyd+hH+dQ0DgdKtt0Bw3WxqPjHYykYECgYBIeCFgriqalv/oPz6CsEfvz47eCcb3taYTxtuLWjW1nk3fctB5q5QJgY6A7ybbtisXOxTHAgxYUvL9BDH+czujQOtfurpnWPqA58esgOB3ESQfRCpdZSbHfmZNiRXD8choPu4TPg+rn7oS9s+2Njfs3gNWdJx5kC0FJBTA4KqIGQ==", "b0ad3054-70fe-472e-9dc1-b99bc4888169", c);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        materialMenu.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            toggleDrawer();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleDrawer() {
        if (isDrawerOpen) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public static void openDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
}
