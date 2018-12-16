package com.example.midom.dice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    final String APP_ID = "e27ddf72458702e0ac8f2a35f69b09fc";
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast";

    final String Location_Provider = LocationManager.GPS_PROVIDER;
    final long min_time = 5000;
    final float min_dist = 100;
    final int req_code = 123;
    LocationManager locationManager;
    LocationListener locationListener;
    Double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        Log.d("time", String.valueOf(hour));
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.relative);
        LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.layout_deg1);
        LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.layout_deg2);
        LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.layout_deg3);


        if (hour <= 6 || hour >= 18) {
            layout.setBackgroundResource(R.drawable.night);

        } else {
            layout.setBackgroundResource(R.drawable.afternoon);
            linearLayout1.setBackgroundColor(getResources().getColor(R.color.bgforecast_day));
            linearLayout2.setBackgroundColor(getResources().getColor(R.color.bgforecast_day));
            linearLayout3.setBackgroundColor(getResources().getColor(R.color.bgforecast_day));


        }
    }

    // onResume() life cyle callback:


    @Override
    protected void onResume() {
        super.onResume();
        Intent home = getIntent();
        String city_selected = home.getStringExtra("city");
        if (city_selected != null) {
            RequestParams params = new RequestParams();
            params.put("q", city_selected);
            params.put("appid", APP_ID);
            Do_some_networking_weather(params);
            Do_some_networking_forecast(params);
            Log.d("clima", city_selected);
            Toast.makeText(getApplicationContext(), city_selected, Toast.LENGTH_SHORT).show();
        } else
            getcurrentweather();

    }

    private void getcurrentweather() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { // lma al data tt8yr {altitude _ Longitude}

                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                lat = location.getLatitude();
                lon = location.getLongitude();
                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                Do_some_networking_weather(params);
                Do_some_networking_forecast(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) { //lma a8yr el gps

            }

            @Override
            public void onProviderEnabled(String provider) {  //lma a3ml el gps on

            }

            @Override
            public void onProviderDisabled(String provider) { // lma a3ml el gps off

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, req_code);
            return;
        }

        locationManager.requestLocationUpdates(Location_Provider, min_time, min_dist, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == req_code) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getcurrentweather();
        } else {
            Toast.makeText(getApplicationContext(), "Acess Denied !", Toast.LENGTH_LONG).show();
        }
    }

    private void Do_some_networking_weather(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statues, Header[] headers, JSONObject response) {


                weather_DataModel weather_data = new weather_DataModel();
                weather_data.fromJson(response);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.GONE);
                Update_UI(weather_data);
                //   Toast.makeText(getBaseContext(),weather_data.cur_city,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statues, Header[] headers, Throwable e, JSONObject response) {
                Toast.makeText(getApplicationContext(), "Failed Connection! , " + e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    //========================================
    private void Do_some_networking_forecast(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(FORECAST_URL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statues, Header[] headers, JSONObject response) {


                weather_DataModel weather_data = new weather_DataModel();
                weather_data.forecast_json(response, get_time_quarter());
                Update_UI_forecast(weather_data);
                // Toast.makeText(getBaseContext(),weather_data.tempreture_night[0],Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statues, Header[] headers, Throwable e, JSONObject response) {
                Toast.makeText(getApplicationContext(), "Failed Connection! , " + e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    //-------------------------------------------------------------------
    public int get_time_quarter() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int zero_condition_onapi = 0;
        while (hour % 3 != 0) {
            hour--;
            continue;
        }
        return zero_condition_onapi;
    }


    private void Update_UI(weather_DataModel weather_data) {

        //current weather :
        TextView temp = (TextView) findViewById(R.id.curtemp);
        TextView city = (TextView) findViewById(R.id.cityname);
        ImageView icon = (ImageView) findViewById(R.id.curicon);
        TextView hummidity = (TextView) findViewById(R.id.humidityval);
        TextView wind_speed = (TextView) findViewById(R.id.windval);
        hummidity.setText(String.valueOf(weather_data.humidity) + " %");
        wind_speed.setText(String.valueOf(weather_data.wind_speed) + " m/s");

        temp.setText(weather_data.get_temp());
        if (weather_data.get_city().length() > 15) {
//{ firstly
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());// mytouch to be more accurate instead of maskn el4ay :D
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String cityName = addresses.get(0).getLocality(); // lw el citybs addresses.get(0).getLocality()
            //} last
            city.setText(cityName);
        } else {
            city.setText(weather_data.get_city());
        }

        int id_img = getResources().getIdentifier(weather_data.get_icon(), "drawable", getPackageName());
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if ((hour <= 6 || hour >= 18) && weather_data.get_icon() == "sunny") {

            icon.setImageResource(android.R.color.transparent);
        } else

            icon.setImageResource(id_img);
        //------------------------------------------------------------------------------

//


    }

    private void Update_UI_forecast(weather_DataModel weather_data) {
        // forecast weather :
        ImageView icon_1 = (ImageView) findViewById(R.id.icon1);
        ImageView icon_2 = (ImageView) findViewById(R.id.icon2);
        ImageView icon_3 = (ImageView) findViewById(R.id.icon3);

        TextView day_1 = (TextView) findViewById(R.id.day1);
        TextView day_2 = (TextView) findViewById(R.id.day2);
        TextView day_3 = (TextView) findViewById(R.id.day3);

        String daysArray[] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Monday"};

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day_1.setText(daysArray[day]);
        day_2.setText(daysArray[day + 1]);
        day_3.setText(daysArray[day + 2]);


        TextView deg_1 = (TextView) findViewById(R.id.deg1);
        TextView deg_2 = (TextView) findViewById(R.id.deg2);
        TextView deg_3 = (TextView) findViewById(R.id.deg3);

        int id_img_1 = getResources().getIdentifier(weather_data.icon_name[0], "drawable", getPackageName());
        icon_1.setImageResource(id_img_1);
        int id_img_2 = getResources().getIdentifier(weather_data.icon_name[1], "drawable", getPackageName());
        icon_2.setImageResource(id_img_2);
        int id_img_3 = getResources().getIdentifier(weather_data.icon_name[2], "drawable", getPackageName());
        icon_3.setImageResource(id_img_3);


        deg_1.setText(weather_data.tempreture[0] + "       " + weather_data.tempreture_night[0]);
        deg_2.setText(weather_data.tempreture[1] + "       " + weather_data.tempreture_night[1]);
        deg_3.setText(weather_data.tempreture[2] + "       " + weather_data.tempreture_night[2]);
        int padding_factor1A = Integer.valueOf(weather_data.tempreture[0]);
        int padding_factor2A = Integer.valueOf(weather_data.tempreture[1]);
        int padding_factor3A = Integer.valueOf(weather_data.tempreture[2]);

        int padding_factor1B = Integer.valueOf(weather_data.tempreture_night[0]);
        int padding_factor2B = Integer.valueOf(weather_data.tempreture_night[1]);
        int padding_factor3B = Integer.valueOf(weather_data.tempreture_night[2]);
        if (padding_factor1A % 10 < 0 || padding_factor1B % 10 == 0)
            deg_1.setPadding(0, 0, 25, 0);
        if (padding_factor2A % 10 == 0 || padding_factor2B % 10 == 0)
            deg_2.setPadding(0, 0, 25, 0);
        if (padding_factor3A % 10 == 0 || padding_factor3B % 10 == 0)
            deg_3.setPadding(0, 0, 25, 0);


    }


    public void to_select_city(View view) {
        Intent city_selection = new Intent(this, select_city.class);
        startActivity(city_selection);
    }


}
