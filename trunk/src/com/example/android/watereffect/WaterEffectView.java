package com.example.android.watereffect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WaterEffectView extends SurfaceView implements SurfaceHolder.Callback
{
	// This is quite a piece. I was challenged by a FPS around 7, which I though was not okey.
	// So I came up with an idea, if I scale the bitmap by 50% and calculate the ripples and then 
	// render the bitmap in full size it will at least be around 15 FPS. But it came up to 25 FPS
	// and I was a happy camper.
	private final int SCALE = 2;
	
	private WaterEffectThread _thread = null;

	private Bitmap _orginalImage = null;
	private Bitmap _rippleImage = null;
	
	private int _orginalImageData[] = null;
	private int _rippleImageData[] = null;
	private int _bufferOne[] = null;
	private int _bufferTwo[] = null;
	private int _bufferTmp[] = null;
	
	private int _imageDataLength = 0;
	private int _rippleSize = -500;
	private int _imageWidth = 0;
	private int _imageHeight = 0;
	private int _screenImageWidth = 0;
	private int _screenImageHeight = 0;
	private int _xTouched = 0;
	private int _yTouched = 0;
	private int _damping = 5;
    private int _offset = 0;
	
	private boolean _screenTouched = false;

	public WaterEffectView(Context context, int width, int height)
	{
		super(context);
		
		SurfaceHolder holder = this.getHolder();
		holder.addCallback(this);

		_screenImageWidth = width;
		_screenImageHeight = height;
		
		_orginalImage = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
		_orginalImage = Bitmap.createScaledBitmap(_orginalImage, width / SCALE, height / SCALE, true);
	
		_thread = new WaterEffectThread(holder, this);
		
		_imageWidth = _orginalImage.getWidth() - 2;
		_imageHeight = _orginalImage.getHeight() - 2;
		_imageDataLength = _imageWidth * _imageHeight;
		
		_orginalImageData = new int[_imageDataLength];
		_rippleImageData = new int[_imageDataLength];
		
		_orginalImage.getPixels(_orginalImageData, 0, _imageWidth, 0, 0, _imageWidth, _imageHeight);
		
		_bufferOne = new int[_imageDataLength];
		_bufferTwo = new int[_imageDataLength];
		
		for(int i = 0; i < _imageDataLength; i++)
		{
			this._bufferOne[i] = 0;
			this._bufferTwo[i] = 0;
		}
	}
	
	//This method is the heart of the ripple effect.
	@Override
	protected void onDraw(Canvas canvas)
	{ 
		// Switch the buffers, e.g. ripple frames.
		_bufferTmp = _bufferOne;
        _bufferOne = _bufferTwo;
        _bufferTwo = _bufferTmp;
		
		for (int i = _imageWidth; i < _bufferOne.length - _imageWidth; i++)
        {			
            if ((i % _imageWidth == 0) || ((i % _imageWidth) == _imageWidth - 1)) 
            {
                continue;
            }

            // Calculates the data for current pixel by the surrounding pixels. 
            _bufferOne[i] = 
            	(((_bufferTwo[i - 1] + 
            	   _bufferTwo[i + 1] + 
            	   _bufferTwo[i - _imageWidth] +
            	   _bufferTwo[i + _imageWidth]) >> 1)) - _bufferOne[i];
            
            // Creates ripple damping, without this the ripple will never ends
            _bufferOne[i] -= (_bufferOne[i] >> _damping);

            _offset = i + (_bufferOne[i - 1] - _bufferOne[i + 1]) + 
            	(_bufferOne[i - _imageWidth] - _bufferOne[i + _imageWidth]) * _imageWidth;
            
            if (_offset > 0 && _offset < _bufferOne.length) 
            {
                for (int x = 0; x < 3; x++) 
                {
                	if((i + x) < _rippleImageData.length && (_offset + x) < _orginalImageData.length)
                	{
                		_rippleImageData[i + x] = _orginalImageData[_offset + x];
                	}
                }
            }
        }

		// Scale image to full screen.
		// Coders Note: By some reason, which I can not understand, createBitmap and createScaledBitmap 
		// reports GC_EXTERNAL_ALLOC. So I think there are some memory leaks inside those methods.
		_rippleImage = Bitmap.createBitmap(_rippleImageData, _imageWidth, _imageHeight, Config.RGB_565);
		_rippleImage = Bitmap.createScaledBitmap(_rippleImage, _screenImageWidth, _screenImageHeight, true);
		canvas.drawBitmap(_rippleImage, 0, 0, null);

        if(_screenTouched)
        {
        	_bufferOne[_yTouched * _imageWidth + _xTouched] += _rippleSize;        	
        	_screenTouched = false;
        }
	}	
    
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		_screenTouched = true;
		_xTouched = (int)event.getX() - 2;
		_yTouched = (int)event.getY() - 2;
		
		_xTouched /= SCALE;
		_yTouched /= SCALE;
		
		return super.onTouchEvent(event);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,	int height) 
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) 
	{
		_thread.setRunning(true);
		_thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		boolean retry = true;
		
		_thread.setRunning(false);
		
		while(retry)
		{
			try
			{
				_thread.join();
				retry = false;
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
}
