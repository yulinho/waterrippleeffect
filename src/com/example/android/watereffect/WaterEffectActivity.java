package com.example.android.watereffect;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class WaterEffectActivity extends Activity 
{    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        DisplayMetrics dm = new DisplayMetrics();
        
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.setContentView(new WaterEffectView(this, dm.widthPixels, dm.heightPixels));
    }
}