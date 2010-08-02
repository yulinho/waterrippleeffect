package com.example.android.watereffect;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class WaterEffectThread extends Thread
{
	private SurfaceHolder _holder = null;
	private WaterEffectView _view = null;
	private boolean _run = false;
	
	public WaterEffectThread(SurfaceHolder holder, WaterEffectView view)
	{
		this._view = view;
		this._holder = holder;
	}
	
	public void setRunning(boolean run)
	{
		this._run = run;
	}
	
	@Override
	public void run()
	{
		Canvas canvas = null;
		
		while(this._run)
		{
			try
			{
				canvas = _holder.lockCanvas();
				
				synchronized (canvas) 
				{
					_view.onDraw(canvas);
				}
			}
			finally
			{
				if(canvas != null)
				{
					_holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
}
