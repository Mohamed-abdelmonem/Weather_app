package com.example.midom.dice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by midom on 11/25/2018.
 */

public class weather_DataModel {

    String[] city = new String[3];
    String[] tempreture = new String[3];
    String[] tempreture_night = new String[3];
    String[] icon_name = new String[3];
    int[] condition = new int[3], condition_night = new int[3];


    String cur_city, cur_temp, cur_icon_name;
    int cur_condition, humidity;
    double wind_speed;

    public weather_DataModel fromJson(JSONObject jsonObject) {

        // JSON parsing is risky business.

        try {

            // weather_DataModel weatherData = new weather_DataModel();


            this.cur_city = jsonObject.getString("name");

            this.cur_condition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");

            this.cur_icon_name = update_icon(this.cur_condition);


            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;

            int roundedValue = (int) Math.rint(tempResult);


            this.cur_temp = Integer.toString(roundedValue);
            this.humidity = jsonObject.getJSONObject("main").getInt("humidity");
            this.wind_speed = jsonObject.getJSONObject("wind").getDouble("speed");


            return this;


        } catch (JSONException e) {
            e.printStackTrace();

            return null;

        }

    }

    public weather_DataModel forecast_json(JSONObject jsonObject, int time_quarter) {

        int day_counter = 0;
        for (int i = 0; i < city.length; i++) {

            try {
                this.city[i] = jsonObject.getJSONObject("city").getString("name") + "," + jsonObject.getJSONObject("city").getString("country");

                Double temp = jsonObject.getJSONArray("list").getJSONObject(get_midnight(time_quarter) + day_counter).getJSONObject("main").getDouble("temp") - 273.15;
                int rounded_temp = (int) Math.rint(temp);
                this.tempreture_night[i] = String.valueOf(rounded_temp);
                this.condition_night[i] = jsonObject.getJSONArray("list").getJSONObject(get_midnight(time_quarter) + day_counter).getJSONArray("weather").getJSONObject(0).getInt("id");
                // reinitialize bdl m3ml var gdeeda
                temp = jsonObject.getJSONArray("list").getJSONObject(get_midday(time_quarter) + day_counter).getJSONObject("main").getDouble("temp") - 273.15;
                rounded_temp = (int) Math.rint(temp);
                this.tempreture[i] = String.valueOf(rounded_temp);
                this.condition[i] = jsonObject.getJSONArray("list").getJSONObject(get_midday(time_quarter) + day_counter).getJSONArray("weather").getJSONObject(0).getInt("id");
                this.icon_name[i] = update_icon(this.condition[i]);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            day_counter += 8;
        }
        return this;
    }

    public int get_midnight(int time_quarter) {
        if (time_quarter == 0)
            return 8;
        else if (time_quarter == 3)
            return 7;
        else if (time_quarter == 6)
            return 6;
        else if (time_quarter == 9)
            return 5;
        else if (time_quarter == 12)
            return 4;
        else if (time_quarter == 15)
            return 3;
        else if (time_quarter == 18)
            return 2;
        else if (time_quarter == 21)
            return 1;
        else return -1;
    }

    public int get_midday(int time_quarter) {
        if (time_quarter == 0)
            return 12;
        else if (time_quarter == 3)
            return 11;
        else if (time_quarter == 6)
            return 10;
        else if (time_quarter == 9)
            return 9;
        else if (time_quarter == 12)
            return 8;
        else if (time_quarter == 15)
            return 7;
        else if (time_quarter == 18)
            return 6;
        else if (time_quarter == 21)
            return 5;
        else return -1;
    }


    private static String update_icon(int condition) {

        if (condition >= 0 && condition < 300) {

            return "stormxxl";
        } else if (condition >= 300 && condition < 500) {
            return "rainxxl";
        } else if (condition >= 500 && condition < 600) {

            return "rainxxl";

        } else if (condition >= 600 && condition <= 700) {

            return "snow4";

        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "stormxxl";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudyxxl";
        } else if (condition >= 900 && condition <= 902) {
            return "stormxxl";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "stormxxl";
        }

        return "dunno";
    }

    public String get_city() {
        return cur_city;
    }

    public String get_temp() {
        return cur_temp + "°";
    }

    public int get_condition() {
        return cur_condition;
    }

    public String get_icon() {
        return cur_icon_name;
    }


}