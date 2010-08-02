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
    	// Basic stuff, set the screen to fullscreen, no title etc.
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        
        // We want to render the background image over the whole screen, so by the lines below we
        // fetch DisplayMetrics (eg. width and height).
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        // Creates and sets the surface to the ContentView.
        this.setContentView(new WaterEffectView(this, dm.widthPixels, dm.heightPixels));
    }
}