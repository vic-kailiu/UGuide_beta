package com.kai.uGuide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ResultActivity extends ActionBarActivity {
    private String mResMD5;
    private String mResPicDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Bundle bundle = getIntent().getExtras();
        mResMD5 = bundle.getString("md5");
        mResPicDesc = bundle.getString("picDesc");

        TextView view = (TextView) findViewById(R.id.hello);

        view.setText(mResPicDesc);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ResultActivity.this, StreetViewActivity.class);
                        startActivity(i);
                    }
                }
        );
    }
}
