package com.kai.uGuide;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.nineoldandroids.view.ViewHelper;

/**
 * This shows how to create a simple activity with streetview
 */
public class StreetViewActivity extends FragmentActivity
        implements StreetViewPanorama.OnStreetViewPanoramaChangeListener,
                   StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener,
                   StreetViewPanorama.OnStreetViewPanoramaClickListener {

    private static final LatLng MERLION = new LatLng(1.2865534, 103.8544511);

    private int screenWidth;
    private int screenHeight;

    // within 180;
    private double merHAngle = 13;
    private double merVAngle = 13;

    private StreetViewPanorama svp;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth =(int) (1.0 * displaymetrics.widthPixels / displaymetrics.density);
        screenHeight = (int) (1.0 * displaymetrics.heightPixels / displaymetrics.density);

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        svp = panorama;
                        svp.setOnStreetViewPanoramaChangeListener(StreetViewActivity.this);
                        svp.setOnStreetViewPanoramaCameraChangeListener(StreetViewActivity.this);

                        if (savedInstanceState == null) {
                            panorama.setPosition(MERLION);
                        }
                    }
                });
    }

    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        double HAngle = streetViewPanoramaCamera.bearing;
        if (HAngle > 180)
            HAngle = HAngle - 360;
        double halfHFov = getHHalfFOV(streetViewPanoramaCamera.zoom);
        double factorH = (HAngle - merHAngle) / halfHFov;
        int centerX = (int) (screenWidth / 2.0 * (1 - factorH));

        double VAngle = streetViewPanoramaCamera.tilt;
        double halfVFOV = getVHalfFOV(streetViewPanoramaCamera.zoom);
        double factorV = (VAngle - merVAngle) / halfVFOV;
        int centerY = (int) (screenHeight / 2.0 * (1 + factorV));

        if (centerX > 0 && centerX < screenWidth) {
            TextView text = (TextView) findViewById(R.id.textView2);
            ViewHelper.setTranslationX(text, centerX);
            ViewHelper.setTranslationY(text, centerY);
        }
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {

    }

    @Override
    public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {
        Point point = svp.orientationToPoint(orientation);
        if (point != null) {
            svp.animateTo(
                    new StreetViewPanoramaCamera.Builder()
                            .orientation(orientation)
                            .zoom(svp.getPanoramaCamera().zoom)
                            .build(), 1000);
        }
    }

    private double getHHalfFOV(double zoom) {
        if (zoom < 0.5)
            return (-24.0 * zoom + 94) / 2.0;
        else
            return (-16.0 * zoom + 62) / 2.0;
    }

    private double getVHalfFOV(double zoom) {
        return (-40.0 * zoom + 90) / 2.0;
    }
}
