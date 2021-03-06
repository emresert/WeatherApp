package emresert.tk.whetherapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import emresert.tk.whetherapp.Common.Common;
import emresert.tk.whetherapp.Helper.Helper;
import emresert.tk.whetherapp.Model.OpenWeatherMap;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;
    LocationManager locationManager;
    String provider;
    static double lat, lng;
    OpenWeatherMap openWeatherMap ;
    TextView t;

    int MY_PERMISSION =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        t=(TextView) findViewById(R.id.txtCity);
        Typeface myCustomFont=Typeface.createFromAsset(getAssets(),"fonts/Raleway-ExtraBold.ttf");
        t.setTypeface(myCustomFont);

        t=(TextView) findViewById(R.id.txtDescription);
        Typeface myCustomFont2=Typeface.createFromAsset(getAssets(),"fonts/Raleway-ExtraBold.ttf");
        t.setTypeface(myCustomFont2);

        t=(TextView) findViewById(R.id.txtLastUpdate);
        Typeface myCustomFont3=Typeface.createFromAsset(getAssets(),"fonts/Raleway-Thin.ttf");
        t.setTypeface(myCustomFont3);

        t=(TextView) findViewById(R.id.txtHumidity);
        Typeface myCustomFont4=Typeface.createFromAsset(getAssets(),"fonts/Raleway-ExtraBold.ttf");
        t.setTypeface(myCustomFont4);

        t=(TextView) findViewById(R.id.txtTime);
        Typeface myCustomFont5=Typeface.createFromAsset(getAssets(),"fonts/Raleway-ExtraBold.ttf");
        t.setTypeface(myCustomFont5);

        t=(TextView) findViewById(R.id.txtCelsius);
        Typeface myCustomFont6=Typeface.createFromAsset(getAssets(),"fonts/Raleway-ExtraBold.ttf");
        t.setTypeface(myCustomFont6);

        openWeatherMap = new OpenWeatherMap();
        //Control
        txtCity=(TextView) findViewById(R.id.txtCity);
        txtLastUpdate=(TextView) findViewById(R.id.txtLastUpdate);
        txtDescription=(TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtTime=(TextView) findViewById(R.id.txtTime);
        txtCelsius=(TextView) findViewById(R.id.txtCelsius);
        imageView=(ImageView) findViewById(R.id.imageView);

        // Get coordinates
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
              Manifest.permission.INTERNET,
              Manifest.permission.ACCESS_COARSE_LOCATION,
              Manifest.permission.ACCESS_FINE_LOCATION,
              Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE



            },MY_PERMISSION);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if(location ==null){
            Log.e("TAG","No Location");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE


            }, MY_PERMISSION);

        }
        locationManager.removeUpdates(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE



            },MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider,400,1,this);
    }

    @Override
    public void onLocationChanged(Location location) {

        lat= location.getLatitude();
        lng=location.getLongitude();
        new GetWeather().execute(Common.apiRequest(String.valueOf(lat),String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private  class  GetWeather extends AsyncTask<String,Void,String>{
        ProgressDialog pd = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait...");
            pd.show();
        }


        @Override
        protected String doInBackground(String... params) {

            String stream = null;
          String urlString = params[0];
            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return  stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains("Error: Not Found the City")){
                pd.dismiss();
                return;
            }
            Gson gson = new Gson();
            Type mType  = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();
            txtCity.setText(String.format("%s,%s",openWeatherMap.getName(),openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Update: %s",Common.getDateNow()));
            txtDescription.setText(String.format("%s",openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("%d%%",openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("%s/%s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelsius.setText(String.format("%.2f °C",openWeatherMap.getMain().getTemp()));

            JSONObject images = new JSONObject();
            try {
                images.put("01d", R.drawable.r01d);
                images.put("01n", R.drawable.r01n);
                images.put("02d", R.drawable.r02d);
                images.put("02n", R.drawable.r02n);
                images.put("03d", R.drawable.r03d);
                images.put("03n", R.drawable.r03n);
                images.put("04d", R.drawable.r04d);
                images.put("04n", R.drawable.r04n);
                images.put("09d", R.drawable.r09d);
                images.put("09n", R.drawable.r09n);
                images.put("10d", R.drawable.r10d);
                images.put("10n", R.drawable.r10n);
                images.put("11d", R.drawable.r11d);
                images.put("11n", R.drawable.r11n);
                images.put("13d", R.drawable.r13d);
                images.put("13n", R.drawable.r13n);
                images.put("50d", R.drawable.r50d);
                images.put("50n", R.drawable.r50n);

                imageView.setImageResource(images.getInt(openWeatherMap.getWeather().get(0).getIcon()));
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Picasso.get().load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
              //      .into(imageView);
        }
    }

}
