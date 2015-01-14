/**
 * This is a tutorial source code 
 * provided "as is" and without warranties.
 *
 * For any question please visit the web site
 * http://www.survivingwithandroid.com
 *
 * or write an email to
 * survivingwithandroid@gmail.com
 *
 */
package com.kai.uGuide.utils;

import com.kai.uGuide.R;

public class WeatherIconMapper {
	
	public static int getWeatherResource(String id, int wId) {
       // Log.d("App", "Id ["+id+"]");
        if (wId == 500)
			return R.drawable.w500d;
		
		if (wId == 501)
			return R.drawable.w501d;
		
		if (wId == 212)
			return R.drawable.w212d;

        switch (id) {
            case "01d":
                return R.drawable.w01d;
            case "01n":
                return R.drawable.w01n;
            case "02d":
            case "02n":
                return R.drawable.w02d;
            case "03d":
            case "03n":
                return R.drawable.w03d;
            case "04d":
            case "04n":
                return R.drawable.w04d;
            case "09d":
            case "09n":
                return R.drawable.w500d;
            case "10d":
            case "10n":
                return R.drawable.w501d;
            case "11d":
            case "11n":
                return R.drawable.w212d;
            case "13d":
            case "13n":
                return R.drawable.w13d;
            case "50d":
            case "50n":
                return R.drawable.w50d;
        }

		return R.drawable.w01d;
		
	}

}
