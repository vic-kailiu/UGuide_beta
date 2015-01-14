package com.kai.uGuide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.edmodo.cropper.CropImageView;
import com.enrique.stackblur.StackBlurManager;
import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kai.uGuide.ui.adapter.HomePagerAdapter;
import com.kai.uGuide.ui.fragment.CurrentWeatherFragment;
import com.kai.uGuide.ui.fragment.ScrollMapFragment;
import com.kai.uGuide.ui.fragment.WeatherFragment;
import com.kai.uGuide.utils.PicShrink;
import com.nineoldandroids.view.ViewHelper;
import com.qq.wx.img.imgsearcher.ImgListener;
import com.qq.wx.img.imgsearcher.ImgResult;
import com.qq.wx.img.imgsearcher.ImgSearcher;
import com.qq.wx.img.imgsearcher.ImgSearcherState;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


public class MainActivity extends ActionBarActivity implements ObservableScrollViewCallbacks, ImgListener, WeatherFragment.WeatherEventListener {

    public enum Stage {
        MAIN,
        PROCESS,
        CROP,
        SEARCH,
        RESULT
    }

    //Scroll
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = false;
    private static final String screKey = "f31db0b2967e23e2f15d8a2948f860ed3791fce38dec856a";
    private final int TAKE_PICTURE = 1;
    private final int FROM_ALBUM = 2;
    private final int MAP_ZOOM = 16;
    //Main Variables
    @Optional
    @InjectView(R.id.scroll)
    ObservableScrollView mScrollView;
    @Optional
    @InjectView(R.id.toolbar)
    View mToolbar;
    @Optional
    @InjectView(R.id.image)
    View mImageView;
    @Optional
    @InjectView(R.id.overlay)
    View mOverlayView;
    @Optional
    @InjectView(R.id.title)
    TextView mTitleView;
    @Optional
    @InjectView(R.id.fab)
    FloatingActionsMenu mFab;
    @Optional
    @InjectView(R.id.fab_expand_menu_button)
    AddFloatingActionButton mAddButton;
    @Optional
    @InjectView(R.id.profile)
    FloatingActionButton profileButton;
    @Optional
    @InjectView(R.id.camera)
    FloatingActionButton cameraButton;
    @Optional
    @InjectView(R.id.gallery)
    FloatingActionButton galleryButton;
    @Optional
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Optional
    @InjectView(R.id.pager)
    ViewPager pager;
    @Optional
    @InjectView(R.id.viewSwitch)
    ImageButton viewSwitchBtn;

    // Google Map
    private ScrollMapFragment mapFragment;

    @Optional
    @InjectView(R.id.currentWeatherFrag)
    FrameLayout weatherView;

    //Result Page
    @Optional
    @InjectView(R.id.cancel)
    Button mCancelBtn;
    @Optional
    @InjectView(R.id.start_searching)
    TextView mTextView;
    int mInitSucc = 0;

    //Crop Page
    @Optional
    @InjectView(R.id.CropImageView)
    CropImageView cropImageView;
    @Optional
    @InjectView(R.id.crop_back_button)
    ImageButton cropBackBtn;
    @Optional
    @InjectView(R.id.crop_done_button)
    ImageButton cropDoneBtn;
    @Optional
    @InjectView(R.id.crop_cancel_button)
    ImageButton cropCancelBtn;

    //Process Page
    @Optional
    @InjectView(R.id.process_logo)
    ImageView img;

    private Context context;
    private DisplayMetrics displayMetrics;
    private Stage currentStage;
    private RelativeLayout introView;
    private LinearLayout.LayoutParams introParams;
    private View text_overlay;
    private RelativeLayout.LayoutParams text_overlay_params;
    private Bitmap bm = null;
    private Bitmap bmCropped = null;
    private Bitmap bmBlurred = null;
    private String imgFileName = null;
    private Uri uri = null;
    private int resultMode = -1;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private int mToolbarColor;
    private int mActionBarSize;
    private GoogleMap googleMap;
    private Marker marker;
    private String mResUrl;
    private String mResMD5;
    private String mResPicDesc;

    private HomePagerAdapter adapter;
    //private int currentColor = 0xFF666666;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            imgFileName = savedInstanceState.getString("imgFileName");
        }

        context = this;
        displayMetrics = getResources().getDisplayMetrics();

        initMainUI();
        preInitImg();
    }

    private void initMainUI() {
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        //TODO: mark null?

        if (currentStage == Stage.CROP)
            resetBackground();
        currentStage = Stage.MAIN;

        initializeScrollView();
        initializeFloatingActionButton();

        try {
            // Loading map
            initializeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();

        initializeWeatherFrag();
    }

    private void initProcessUI() {
        setContentView(R.layout.layout_process);
        ButterKnife.inject(this);

        currentStage = Stage.PROCESS;

        float density = getResources().getDisplayMetrics().density;
        Animation an = new RotateAnimation(0.0f, 360.0f, 32 * density, 32 * density);
        an.setDuration(2000);
        an.setRepeatCount(Animation.INFINITE);
        an.setRepeatMode(Animation.INFINITE);

        Thread thread = new Thread() {
            public void run() {
                if (bm != null && !bm.isRecycled()) {
                    bm.recycle();
                }
                if (TAKE_PICTURE == resultMode) {
                    bm = PicShrink.compress(imgFileName);
                    File file = new File(imgFileName);
                    file.delete();
                } else {
                    bm = PicShrink.compress(context, uri);
                }

                //create blur image
                generateBackground();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (bm != null && bmBlurred != null)
                            initCropUI();
                        else
                            initMainUI();
                    }
                });
            }
        };
        thread.setDaemon(true);

        img.startAnimation(an);
        thread.start();
    }

    private void initCropUI() {
        setContentView(R.layout.layout_crop);
        ButterKnife.inject(this);

        currentStage = Stage.CROP;

        cropDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmCropped = cropImageView.getCroppedImage();
                initSearchUI();
            }
        });
        cropBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMainUI();
            }
        });
        cropCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMainUI();
            }
        });

        cropImageView.setImageBitmap(bm);
        getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), bmBlurred));
    }

    @SuppressWarnings("deprecation")
    private void initSearchUI() {
        setContentView(R.layout.layout_search);
        ButterKnife.inject(this);

        if (currentStage == Stage.CROP)
            resetBackground();
        currentStage = Stage.SEARCH;

        LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.staff);

        Drawable mDrawable = new BitmapDrawable(getResources(), bmCropped);
        mLinearLayout.setBackgroundDrawable(mDrawable);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                int ret = ImgSearcher.shareInstance().cancel();
                if (0 != ret) {
                    initMainUI();
                }
            }
        });

        byte[] imgByte = getJpg(bmCropped);
        int ret = startImgSearching(imgByte);
        if (0 != ret) {
            initMainUI();
        }
    }

    //region MainUI

    private void initializeScrollView() {
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
//        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mToolbarColor = getResources().getColor(R.color.primary);

        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mScrollView.setScrollViewCallbacks(this);
        mTitleView.setText("Singapore");
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

//                mScrollView.scrollTo(0, 1);
//                mScrollView.scrollTo(0, 0);

//                onScrollChanged(0, false, false);
            }
        });
    }

    private void initializeFloatingActionButton() {
        profileButton.setIcon(R.drawable.ic_profile);
        cameraButton.setIcon(R.drawable.ic_camera);
        galleryButton.setIcon(R.drawable.ic_gallery);

        mAddButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startCamera();
                return true;
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFab.collapse();
                //onScrollChanged(-100, false, false);
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFab.collapse();
                startCamera();
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFab.collapse();
                startGallery();
            }
        });

        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
//        ViewHelper.setScaleX(mFab, 0);
//        ViewHelper.setScaleY(mFab, 0);
    }

    private void initializeMap() {
        mapFragment = ScrollMapFragment.newInstance();
        mapFragment.setListener(
                new ScrollMapFragment.OnMapReadyListener() {
                    @Override
                    public void onMapReady() {
                        googleMap = mapFragment.getMap();

                        // check if map is created successfully or not
                        if (googleMap == null) {
                            Toast.makeText(getApplicationContext(),
                                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                                    .show();
                        }

                        googleMap.setMyLocationEnabled(true); // false to disable
                        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                            @Override
                            public void onMyLocationChange(Location arg0) {
                                LatLng pos = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                                if (marker != null)
                                    marker.remove();
                                marker = googleMap.addMarker(
                                        new MarkerOptions().position(pos).title("You are here!").flat(true));
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, MAP_ZOOM));
                            }
                        });
                    }
                }
        );

        mapFragment.setListener(
                new ScrollMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        if (mScrollView != null)
                            mScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                }
        );

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mapFragment, mapFragment);
        ft.commit();
    }

    private void initializeViews() {
        introView = (RelativeLayout) findViewById(R.id.textView);
        introParams = (LinearLayout.LayoutParams) introView.getLayoutParams();

        text_overlay = findViewById(R.id.text_overlay);
        text_overlay_params = (RelativeLayout.LayoutParams) text_overlay.getLayoutParams();

        adapter = new HomePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        pager.setCurrentItem(0);

        tabs.setIndicatorColor(getResources().getColor(R.color.primary));
        //changeColor(currentColor);

        viewSwitchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = pager.getCurrentItem();
                        adapter.getPager(position).getAdapter().toggle();
                    }
                }
        );
    }

    private void initializeWeatherFrag() {
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransitionStyle(R.style.fragmentAnim);
        CurrentWeatherFragment cf = CurrentWeatherFragment.newInstance();
        ft.add(R.id.currentWeatherFrag, cf, "currentWeather") ;
        ft.commit();
    }

//    private void changeColor(int newColor) {
//
//        tabs.setIndicatorColor(newColor);
//        currentColor = newColor;
//
//    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY)));
        ViewHelper.setTranslationY(mImageView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, Math.max(0, Math.min(1, (float) scrollY / flexibleRange)));
        ViewHelper.setAlpha(weatherView, 1 - Math.max(0, Math.min(1, (float) scrollY / flexibleRange * 2)));

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

        // Translate FAB
        mFab.collapse();

        int maxFabTranslationY = mFlexibleSpaceImageHeight;
        int fabTranslationY = Math.max(mActionBarSize,
                Math.min(maxFabTranslationY, -scrollY + mFlexibleSpaceImageHeight));
        ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
        ViewHelper.setTranslationY(mFab, fabTranslationY - mFab.getHeight() + 56);

        // Show/hide FAB
//        if (ViewHelper.getTranslationY(mFab) < mFlexibleSpaceShowFabOffset) {
//            hideFab();
//        } else {
//            showFab();
//        }

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
    }

    private void updateView(View view, LinearLayout.LayoutParams params, int scroll) {
        int startScroll = 0;
        int range = (int) getResources().getDimension(R.dimen.sectionViewMaxHeight) - (int) getResources().getDimension(R.dimen.sectionViewMinHeight);

        double factor = 1.0 * Math.abs(scroll - (startScroll + range)) / range;
        if (factor > 1)
            return;

        params.height = (int) getResources().getDimension(R.dimen.sectionViewMinHeight) + (int) (range * (1 - factor));

        view.setLayoutParams(params);

        text_overlay_params.height = (int) (factor * 100.0);
        text_overlay.setLayoutParams(text_overlay_params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {//result is not correct
            return;
        } else {
            if (FROM_ALBUM == requestCode) {
                uri = data.getData();
                if (null == uri) {
                    return;
                }
            }
            resultMode = requestCode;

            initProcessUI();
        }
    }

    private void startCamera() {
        String filePath;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //Judge SD card is present
        if (!sdCardExist) {
            /**
             * Write mobile phone's internal storage
             */
            @SuppressWarnings("deprecation")
            File mediaFilesDir = getDir("mediaFiles", Context.MODE_WORLD_READABLE);
            filePath = mediaFilesDir.getPath();
        } else {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + "/UGuide/mm";
            File outputPath = new File(filePath);
            if (!outputPath.exists()) {
                outputPath.mkdirs();
            }
        }

        //**For test
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = dateFormat.format(date);
        String imgType = ".jpg";
        imgFileName = filePath + "/" + dateStr + imgType;
        File imgFile = new File(imgFileName);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(imgFile);
        //Specify a storage path(in SD card), workupload.jpg is a temp file, will be replaced after taking a picture.
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.putExtra("imgFilename", imgFileName);
        startActivityForResult(cameraIntent, TAKE_PICTURE);
    }

    private void startGallery() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(openAlbumIntent, FROM_ALBUM);
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

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void requestCompleted() {

    }

    //endregion

    //region ProcessUI

    private void generateBackground() {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int x = 0;
        int y = 0;
        double screenRatio = 1.0 * displayMetrics.widthPixels / displayMetrics.heightPixels;
        double imgRatio = 1.0 * bm.getWidth() / bm.getHeight();
        if (imgRatio > screenRatio) {
            width = (int) (height * screenRatio);
            x = (int) ((bm.getWidth() - width) / 2);
        } else if (imgRatio < screenRatio) {
            height = (int) (width / screenRatio);
            y = (int) ((bm.getHeight() - height) / 2);
        }
        Bitmap fitImg = Bitmap.createBitmap(bm, x, y, width, height);

        StackBlurManager _stackBlurManager = new StackBlurManager(fitImg);
        bmBlurred = _stackBlurManager.process(20);
    }

    private void resetBackground() {
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg));
    }

    //endregion

    //region SearchUI
    private void preInitImg() {
        ImgSearcher.shareInstance().setListener(this);
        mInitSucc = ImgSearcher.shareInstance().init(this, screKey);

        if (mInitSucc != 0) {
            Toast.makeText(this, "Failed to initialize",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private int startImgSearching(byte[] img) {
        if (mInitSucc != 0) {
            mInitSucc = ImgSearcher.shareInstance().init(this, screKey);
        }
        if (mInitSucc != 0) {
            Toast.makeText(this, "Failed to initialize",
                    Toast.LENGTH_SHORT).show();
            return -1;
        }

        int ret = ImgSearcher.shareInstance().start(img);
        if (0 == ret) {
            return 0;
        } else {
            Toast.makeText(this, "ErrorCode = " + ret, Toast.LENGTH_LONG).show();
            return -1;
        }
    }

    @Override
    public void onGetError(int errorCode) {
        // TODO Auto-generated method stub
        Toast.makeText(this, "ErrorCode = " + errorCode, Toast.LENGTH_LONG).show();
        initMainUI();
    }

    @Override
    public void onGetResult(ImgResult result) {
        // TODO Auto-generated method stub
        boolean ret = true;
        if (result != null) {
            if (1 == result.ret && result.res != null) {
                int resSize = result.res.size();
                for (int i = 0; i < resSize; ++i) {
                    ImgResult.Result res = (ImgResult.Result) result.res.get(i);
                    if (res != null) {
                        mResMD5 = res.md5;
                        mResUrl = res.url;
                        mResPicDesc = res.picDesc;
                    }
                }
                ret = true;
            } else {
                ret = false;
            }
        }
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        turnToResultActivity(ret);
    }

    @Override
    public void onGetState(ImgSearcherState state) {
        if (ImgSearcherState.Canceling == state) {
            mTextView.setText("Cancelling...");
        } else if (ImgSearcherState.Canceled == state) {
            initMainUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("imgFileName", imgFileName);
    }

    byte[] getJpg(Bitmap bitmap) {
        ByteArrayOutputStream outs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outs);

        return outs.toByteArray();
    }

    void turnToResultActivity(boolean isFound) {
        currentStage = Stage.RESULT;

        Intent it = new Intent(this, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("ret", isFound);
        bundle.putString("url", mResUrl);
        bundle.putString("md5", mResMD5);
        bundle.putString("picDesc", mResPicDesc);
        it.putExtras(bundle);
        startActivity(it);
        //finish();
    }

    //endregion

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        if (currentStage == Stage.RESULT)
            initMainUI();
        else if (currentStage == Stage.MAIN)
            initializeMap();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bm != null && !bm.isRecycled()) {
                bm.recycle();
            }
            // Monitor the return key
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (null != bm && !bm.isRecycled()) {
            bm.recycle();
        }
        ImgSearcher.shareInstance().destroy();
    }

    public void savePic(Bitmap bitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File filepath = new File(root + "/tencent/mm");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateStr = dateFormat.format(date);
        String imgType = ".jpg";
        String fileName = dateStr + imgType;

        File file = new File(filepath, fileName);
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}