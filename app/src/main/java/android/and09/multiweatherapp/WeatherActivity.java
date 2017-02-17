package android.and09.multiweatherapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.tag;
import static android.and09.multiweatherapp.OpenWeatherMapAPI.fromLatLon;
import static android.location.LocationManager.GPS_PROVIDER;


public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "WeatherActivity";
    public ConditionVariable fragmentReady;
    private boolean prefsChanged = false;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceListener;
    double lat;
    double lon;

    Location mLastLocation;
    GoogleApiClient mGoogleApiClient;
   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentReady = new ConditionVariable(false);
        setContentView(R.layout.activity_weather_tablayout);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        //To insert child view that represent each page, we will use adapter, Ojbect from type
        //WeatherPagerAdapter invoking Fagement manager:
        PagerAdapter pageAdapter = new WeatherPageAdapter(getSupportFragmentManager());
        //Set the adapter to the view page via its id:
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pageAdapter);

        //Anonymous class to handle the event via a listener:
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    WeatherRequestTask task = new WeatherRequestTask();
                    task.execute();
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0 && prefsChanged == true) {
                    WeatherRequestTask task = new WeatherRequestTask();
                    task.execute();
                    prefsChanged = false;
                }
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //Second handler to activate the right button:
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        WeatherRequestTask task = new WeatherRequestTask();
        task.execute();
        //We set anonymous onSharedPreferenes class with a strong reference assigned to prevent the
        //garbage collection destroy the object of the sharedprefernces.
        sharedPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                prefsChanged = true;
                Log.d(getClass().getSimpleName(), "Setting Changed.");
                if (key.equals("use_gps")) {
                    Boolean useGps = prefs.getBoolean("use_gps", false);
                    //Checkbox checked and the automatic location determination start or stopped:
                  if (useGps == true)  startLocationService();

                    } else {           stopLocationService();
                    }
                }

        };
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceListener);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    private void startLocationService() {
        Toast.makeText(WeatherActivity.this, "Location Start", Toast.LENGTH_LONG).show();
        Log.d(TAG, "Lat" + String.valueOf(lat) + "Lng" + String.valueOf(lon) );
        mGoogleApiClient.connect();
    }

    private void stopLocationService() {
        Toast.makeText(WeatherActivity.this, "Location Stopped", Toast.LENGTH_LONG).show();
        mGoogleApiClient.disconnect();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {  }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {   }


    //Inner static class to handle the page adapter, fragment stat pager adapter:
    public static class WeatherPageAdapter extends FragmentStatePagerAdapter {
        static final int NUM_TABS = 2;

        public WeatherPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:  return new WeatherFragment();
                case 1:  return new WeatherPreferenceFragment();
                default: return null; } }
        @Override
        public int getCount() {
            return NUM_TABS;
        }  }

    @Override
    public void onResume() {
        super.onResume();
        WeatherRequestTask task = new WeatherRequestTask();
        task.execute();  }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    //Implement the menu to update the weather data:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;   }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reconnect) {
            WeatherRequestTask task = new WeatherRequestTask();
            task.execute();
            Toast.makeText(WeatherActivity.this, "Weather data updated!", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class WeatherRequestTask extends AsyncTask<Void, Void, IWeatherAPI> {
        @Override
        protected IWeatherAPI doInBackground(Void... voids) {
            //For weather data current city, we use reflection api weather api factory
            //class via gps location. getDefaultSharedPreferences method return sharedpreferences obj.
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
            Boolean useGps = prefs.getBoolean("use_gps", false);
            String locationName = prefs.getString("location_name", ""); //From key to store in String locationName.
            String weatherProviderClass = prefs.getString("weather_provider_class", "");

            //Read stored value from the server address PrefEditText:
            String ServerAddress = prefs.getString("server", "");
           //Set it into the setServer static method in class ServletWeatherAPI.
            ServletWeatherAPI.setServer(ServerAddress);

            IWeatherAPI api = null;

            if (useGps == false) {
                try {api = WeatherAPIFactory.fromLocationName(weatherProviderClass, locationName);


                } catch (Exception ex) {
                    Log.e(getClass().getSimpleName(), ex.toString());
                }
                fragmentReady.block();
                return api;
            } else {
                //If coords are not available from the google client, call from LocationName:
                try { api = WeatherAPIFactory.fromLatLon(weatherProviderClass, lat, lon);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), e.toString());
                }
                fragmentReady.block();
                return api;
            }
        }
        @Override
        protected void onPostExecute(IWeatherAPI api) {
            if (api == null) {
                Log.e(getClass().getSimpleName(), "doInBackground is null");
                Toast.makeText(WeatherActivity.this, "No data is availabe, please check connection settings", Toast.LENGTH_LONG).show();
                return;
            }
            try {

                Log.d(getClass().getSimpleName(), "Temperatur: " + api.getTemperature());
                Log.d(getClass().getSimpleName(), "Beschreibung: " + api.getDescription());
                Log.d(getClass().getSimpleName(), "Provider: " + api.getProviderInfo());
                Log.d(getClass().getSimpleName(), "Icon: " + api.getIconPath());

                TextView textViewTemperature = (TextView)WeatherActivity.this.findViewById(R.id.textview_temperature);
                textViewTemperature.setText((int)api.getTemperature() + "° C");
                TextView textViewDescription = (TextView)WeatherActivity.this.findViewById(R.id.textview_description);
                textViewDescription.setText(api.getDescription());
                TextView textViewProvider = (TextView)WeatherActivity.this.findViewById(R.id.textview_weatherprovider);
                textViewProvider.setText(api.getProviderInfo());
                //Set dynamic icon for the weather image:
                InputStream bitmapStream = getAssets().open(api.getIconPath());
                Bitmap bitmap = BitmapFactory.decodeStream(bitmapStream);
                ImageView imageView = (ImageView)WeatherActivity.this.findViewById(R.id.imageview_weathericon);
                imageView.setImageBitmap(bitmap);
            }catch (JSONException ex){
            Log.d(getClass().getSimpleName(),ex.toString());
            } catch (Exception ex) {
                Log.e(getClass().getSimpleName(), ex.toString());
              try {
                   Log.e(getClass().getSimpleName(), api.getError());
                   Toast.makeText(WeatherActivity.this, "Error: " + api.getError(),Toast.LENGTH_LONG).show();
                    } catch (Exception innerEx) {
                    Log.e(getClass().getSimpleName(), innerEx.toString());
                  Toast.makeText(WeatherActivity.this,"Unkown error. \nTry other server.",Toast.LENGTH_LONG).show();
                } }  } }
}
