package com.kai.uGuide;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.astuetz.PagerSlidingTabStrip;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.kai.uGuide.ui.adapter.AtomPayListAdapter;
import com.kai.uGuide.ui.adapter.HomePagerAdapter;
import com.kai.uGuide.ui.adapter.Result;
import com.kai.uGuide.utils.KeyResolver;
import com.kai.uGuide.utils.NuanceTTS;
import com.kai.uGuide.utils.XmlParser;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nuance.nmdp.speechkit.SpeechError;
import com.nuance.nmdp.speechkit.Vocalizer;
import com.nuance.nmdp.speechkit.Vocalizer.Listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


public class ResultActivity extends FragmentActivity implements ObservableScrollViewCallbacks, Listener {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = false;
    private final int MAP_ZOOM = 15;
    List<XmlParser.Entry> entries;
    //About Results

    @Optional
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Optional
    @InjectView(R.id.pager)
    ViewPager pager;
    @Optional
    @InjectView(R.id.viewSwitch)
    ImageButton viewSwitchBtn;

    private String mResMD5;
    private String mResPicDesc;
    private View mToolbar;
    private ImageView mImageView;
    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private int deviceHeightHalf;

    private LinearLayout introView;
    private LinearLayout.LayoutParams introParams;

    private FloatingActionButton mFab;

    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private boolean mFabIsShown;
    // Google Map
    private MapFragment mapFragment;
    private View mapView;
    private GoogleMap googleMap;
    private Marker marker;

    private Vocalizer _vocalizer;
    private Object _ttsContext = null;

    private HomePagerAdapter adapter;

    private AtomPayListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        mResMD5 = bundle.getString("md5");
        mResPicDesc = bundle.getString("picDesc");

        resultUI();
    }

    public void resultUI() {
        setContentView(R.layout.activity_result);
        ButterKnife.inject(this);

        AssetManager assetManager = this.getAssets();
        try {
            InputStream is = assetManager.open( KeyResolver.resolve(mResMD5) + ".xml");
            XmlParser xmlParser = new XmlParser();

            entries = xmlParser.parse(is);
            //int foo = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeScrollView();

        initializeFloatingActionButton();

        try {
            initializeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY)));
        ViewHelper.setTranslationY(mImageView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, Math.max(0, Math.min(1, (float) scrollY / flexibleRange)));

        // Scale title text
        float scale = 1 + Math.max(0, Math.min(MAX_TEXT_SCALE_DELTA, (flexibleRange - scrollY) / flexibleRange));
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        int fabTranslationY = Math.max(mActionBarSize - mFab.getHeight() / 2,
                Math.min(maxFabTranslationY, -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2));
        ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
        ViewHelper.setTranslationY(mFab, fabTranslationY);

        // Show/hide FAB
        if (ViewHelper.getTranslationY(mFab) < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }

        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                setBackgroundAlpha(mToolbar, 1, mToolbarColor);
            } else {
                setBackgroundAlpha(mToolbar, 0, mToolbarColor);
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }

        updateView(introView, introParams, scrollY);
        //updateView(mapView, mapParams, scrollY - lastScrollY);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private void updateView(View view, LinearLayout.LayoutParams params, int scroll) {
        int startScroll = 0;
        int range = (int) getResources().getDimension(R.dimen.sectionViewMaxHeight) - (int) getResources().getDimension(R.dimen.sectionViewMinHeight);

        double factor = 1.0 * Math.abs(scroll - (startScroll + range)) / range;
        if (factor > 1)
            return;

//        params.height = (int) getResources().getDimension(R.dimen.sectionViewMinHeight) + (int) (range * (1 - factor));// * factor));
//        view.setLayoutParams(params);

//        text_overlay_params.height = (int) (factor * 100.0);
//        text_overlay.setLayoutParams(text_overlay_params);
    }

    private void initializeScrollView() {
        //setSupportActionBar((Toolbar) findViewById(R.id.result_toolbar));

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.primary);

        mToolbar = findViewById(R.id.result_toolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mImageView = (ImageView) findViewById(R.id.resultImage);
        try {
            Bitmap bmp = BitmapFactory.decodeStream(this.getAssets().open(KeyResolver.resolve(mResMD5) + ".jpg"));
            mImageView.setImageBitmap(bmp);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        mOverlayView = findViewById(R.id.resultOverlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.resultScroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.result_title);
        mTitleView.setText(mResPicDesc);
        setTitle(null);

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    mScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                onScrollChanged(0, false, false);
            }
        });
    }

    private void initializeFloatingActionButton() {
        mFab = (FloatingActionButton) findViewById(R.id.result_fab);
        mFab.setIcon(R.drawable.ic_home);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ResultActivity.this, StreetViewActivity.class);
                startActivity(it);
                //finish();
            }
        });

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
//        ViewHelper.setScaleX(mFab, 0);
//        ViewHelper.setScaleY(mFab, 0);
    }

    private void initializeMap() {
        if (googleMap == null) {
            mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.result_map));
            googleMap = mapFragment.getMap();
            mapView = (RelativeLayout) findViewById(R.id.result_mapView);
            //mapParams = (LinearLayout.LayoutParams) mapView.getLayoutParams();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

            googleMap.setMyLocationEnabled(true); // false to disable
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            LatLng pos = new LatLng(entries.get(0).lati, entries.get(0).longti);
            marker = googleMap.addMarker(
                    new MarkerOptions().position(pos).flat(true));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MAP_ZOOM));

            googleMap.clear();
            for (int i = 0; i< entries.get(0).path.size(); i++) {
                List<LatLng> decodedPath = PolyUtil.decode(entries.get(0).path.get(i));
                googleMap.addPolyline(new PolylineOptions().addAll(decodedPath).width(5).color(R.color.primary).zIndex(100));
            }
            googleMap.addMarker( new MarkerOptions().position(new LatLng(1.286274, 103.859266)).flat(true) );
            googleMap.addMarker( new MarkerOptions().position(new LatLng(1.283649, 103.860346)).flat(true) );
            googleMap.addMarker( new MarkerOptions().position(new LatLng(1.286848, 103.854532)).flat(true) );
            googleMap.addMarker( new MarkerOptions().position(new LatLng(1.289299, 103.863137)).flat(true) );
            googleMap.addMarker( new MarkerOptions().position(new LatLng(1.289793, 103.855817)).flat(true) );
            googleMap.addMarker( new MarkerOptions().position(new LatLng(1.2876834, 103.8605974)).flat(true) );
        }
    }

    private void initializeViews() {
//        introView = (LinearLayout) findViewById(R.id.resultFrame);
//        introParams = (LinearLayout.LayoutParams) introView.getLayoutParams();
        ListView listView = (ListView) findViewById(R.id.list_view);
        listAdapter = new AtomPayListAdapter(ResultActivity.this, R.layout.item_speech_list, new ArrayList<AtomPayment>());
        listAdapter.insert(new AtomPayment("", 0), 0);
        listAdapter.insert(new AtomPayment("", 0), 0);
        listAdapter.insert(new AtomPayment("", 0), 0);
        listView.setAdapter(listAdapter);

        adapter = new HomePagerAdapter(getSupportFragmentManager(), Result.TITLES);
        pager.setAdapter(null);
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        pager.setCurrentItem(0);

        tabs.setIndicatorColor(getResources().getColor(R.color.primary));

        viewSwitchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = pager.getCurrentItem();
                        adapter.getPager(position).getAdapter().toggle();
                    }
                }
        );

        TextView title = (TextView) findViewById(R.id.result_title);
        title.setText(mResPicDesc);

//        TextView speechTitle = (TextView) findViewById(R.id.title);
//        speechTitle.setText(entries.get(0).title);
//
//        Context context = getApplicationContext();
//        //TODO:could move it to splash maybe
//        NuanceTTS.SetUpNuanceTTS(context);
//
//        // Create a single Vocalizer here.
//        _vocalizer = NuanceTTS.getSpeechKit().createVocalizerWithLanguage("en_US", this, new Handler());
//        _vocalizer.setVoice("Ava");

//        ImageButton play = (ImageButton) findViewById(R.id.result_play);
//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                _ttsContext = new Object();
//                _vocalizer.speakString(entries.get(0).summary, _ttsContext);
//            }
//        });
    }

    public void removeAtomPayOnClickHandler(View v) {
        AtomPayment itemToRemove = (AtomPayment)v.getTag();
        listAdapter.remove(itemToRemove);
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_vocalizer != null) {
            _vocalizer.cancel();
            _vocalizer = null;
        }
        NuanceTTS.Destroy();
    }

    @Override
    public void onSpeakingBegin(Vocalizer vocalizer, String text, Object context) {
        //updateCurrentText("Playing text: \"" + text + "\"", Color.GREEN, false);
        // for debugging purpose: printing out the speechkit session id
        android.util.Log.d("Nuance SampleVoiceApp", "Vocalizer.Listener.onSpeakingBegin: session id ["
                + NuanceTTS.getSpeechKit().getSessionId() + "]");
    }

    @Override
    public void onSpeakingDone(Vocalizer vocalizer,
                               String text, SpeechError error, Object context) {
        // Use the context to detemine if this was the final TTS phrase
        if (context != _ttsContext) {
            //updateCurrentText("More phrases remaining", Color.YELLOW, false);
        } else {
            //updateCurrentText("", Color.YELLOW, false);
        }
        // for debugging purpose: printing out the speechkit session id
        android.util.Log.d("Nuance SampleVoiceApp", "Vocalizer.Listener.onSpeakingDone: session id ["
                + NuanceTTS.getSpeechKit().getSessionId() + "]");
    }
}
