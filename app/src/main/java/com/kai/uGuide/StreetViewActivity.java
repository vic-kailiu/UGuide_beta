package com.kai.uGuide;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
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
    private static final LatLng ART = new LatLng(1.286456,103.859885);

    private int screenWidth;
    private int screenHeight;

    private Button[] labels = new Button[5];

    // within 180;
    private double[] merHAngle = {13, 20, 72, 93, 114};
    private double[] merVAngle = {16, 10, 5,  2,  13};

    private double[] mbsHAngle = {-360, -60, 50, 153, -110 };
    private double[] mbsVAngle = {-360, 7, 5,  28,  31   };

    private double[] currentH = merHAngle;
    private double[] currentV = merVAngle;

    private StreetViewPanorama svp;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenWidth = (int) (1.0 * displaymetrics.widthPixels / displaymetrics.density);
        screenHeight = (int) (1.0 * displaymetrics.heightPixels / displaymetrics.density);

        labels[0] = (Button) findViewById(R.id.buttonMer);
        labels[1] = (Button) findViewById(R.id.buttonEsp);
        labels[2] = (Button) findViewById(R.id.buttonFly);
        labels[3] = (Button) findViewById(R.id.buttonArt);
        labels[4] = (Button) findViewById(R.id.buttonMBS);

        labels[0].setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentH = merHAngle;
                        currentV = merVAngle;
                        svp.setPosition(MERLION);
                    }
                }
        );
        labels[1].setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        svp.setPosition(MERLION);
                    }
                }
        );
        labels[2].setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        svp.setPosition(MERLION);
                    }
                }
        );
        labels[3].setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentH = mbsHAngle;
                        currentV = mbsVAngle;
                        svp.setPosition(ART);
                    }
                }
        );
        labels[4].setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        svp.setPosition(MERLION);
                    }
                }
        );

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        svp = panorama;
                        svp.setOnStreetViewPanoramaChangeListener(StreetViewActivity.this);
                        svp.setOnStreetViewPanoramaCameraChangeListener(StreetViewActivity.this);
                        svp.setUserNavigationEnabled(false);


                        if (savedInstanceState == null) {
                            svp.setPosition(MERLION);
                        }
                    }
                });
    }

    @Override
    public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera streetViewPanoramaCamera) {
        for (int i = 0; i < 5; i++) {
            double HAngle = streetViewPanoramaCamera.bearing;
            if (HAngle > 180)
                HAngle = HAngle - 360;
            double halfHFov = getHHalfFOV(streetViewPanoramaCamera.zoom);
            double factorH = (HAngle - currentH[i]) / halfHFov;
            int centerX = (int) (screenWidth / 2.0 * (1 - factorH));

            double VAngle = streetViewPanoramaCamera.tilt;
            double halfVFOV = getVHalfFOV(streetViewPanoramaCamera.zoom);
            double factorV = (VAngle - currentV[i]) / halfVFOV;
            int centerY = (int) (screenHeight / 2.0 * (1 + factorV));

            if (centerX > 0 && centerX < screenWidth) {
                ViewHelper.setTranslationX(labels[i], centerX);
                ViewHelper.setTranslationY(labels[i], centerY);
            }
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
