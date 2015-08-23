package barcode.ky.barcodescanner;

import android.graphics.Bitmap;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;

import android.os.Handler;

import util.camera.PreviewCallback;
/**
 * Created by kylin on 15/1/4.
 */
public class CameraSurfacePreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    String TAG = "CameraSurface";
    private int angle;
    Context mContex;
    Handler mHandler;

    PreviewCallback previewCallback = new PreviewCallback();
    private void initSurface(Context context){
        mHolder = getHolder();
        mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mContex = context;
        previewCallback.setContext(mContex);

        angle = 0;

    }
    public CameraSurfacePreview(Context context){
        super(context);
        Log.e(TAG, "CameraSurfacePreview");
        initSurface(context);
    }
    public CameraSurfacePreview(Context context,AttributeSet attrs,int defStyles){
        super(context,attrs,defStyles);
        initSurface(context);
    }
    public CameraSurfacePreview(Context context,AttributeSet attrs){
        super(context,attrs);
        initSurface(context);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCamera = Camera.open();

        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size preSize = parameters.getPreviewSize();

        int h = preSize.height;
        int w = preSize.width;
        Log.e(TAG, "h " + h + " w " + w);
        parameters.setPreviewSize(640, 480);
        mCamera.setParameters(parameters);
        try{
            //mCamera.setPreviewTexture(surfaceTexture);
            setWillNotDraw(false);
           // mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters1 = mCamera.getParameters();
            parameters1.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters1);
            Log.e(TAG, "preview size " + parameters1.getPreviewSize().width + " " + parameters1.getPreviewSize().height);
            mCamera.setPreviewCallback(previewCallback);

//            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
//                @Override
//                public void onPreviewFrame(byte[] data, Camera camera) {
//                    //Log.e("Camera","preview " + data[0]);
//                    decode(data, 640, 480);
//                    String filename = Environment.getExternalStorageDirectory() + "/code/dump.yuv";
//                    try {
//                        FileOutputStream fo = new FileOutputStream(new File(filename));
//                        fo.write(data);
//                        fo.close();
//                    }catch (Exception ex){
//                        ex.printStackTrace();
//                    }
//
//                }
//            });
            mCamera.startPreview();
        }catch (Exception ioe){

        }
    }
    Toast t= null;
    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
      //  Log.e("barcode","decode");
        Result rawResult = null;
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        PlanarYUVLuminanceSource source =new PlanarYUVLuminanceSource(data, width, height, 10,10,
                310, 210, false);
        if (source != null) {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            try {
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                // continue
            } finally {
                multiFormatReader.reset();
            }
        }

        if(rawResult !=null){
            Log.e("barcode", " " + rawResult);
            if(t != null) {
                t.cancel();
            }
            t = Toast.makeText(mContex, "barcode " + rawResult, Toast.LENGTH_SHORT);
            t.show();
        }

    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG,"surfaceDestroyed");
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        //return true;
    }
    @Override
    public void onDraw(Canvas canvas){
        //landscape
        // w 1280 h 720
        //super.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);

        canvas.drawLine(0 , 240 , 640, 240, paint);

        int lineLen = 1280;
//        double start_x = 360 * Math.cos((180 + angle)*Math.PI/180);
//        double start_y = 360 * Math.sin((180 + angle)*Math.PI/180);
//
//        double end_x = 360 * Math.cos(angle*Math.PI/180);
//        double end_y = 360 * Math.sin(angle*Math.PI/180);
        paint.setColor(Color.YELLOW);
//        canvas.drawLine(640 + (int) start_x, 360 - (int) start_y, 640 + (int) end_x, 360 - (int) end_y, paint);
        //Log.e(TAG,"draw x " + end_x + " y " + end_y);

        /*try{
           Canvas c = mHolder.lockCanvas();
        }catch (Exception e){
            Log.e(TAG,"error");
        }*/
        paint.setStyle(Paint.Style.STROKE);
        //canvas.drawRect(10f,10f,410f,310f,paint);
        canvas.drawRect(10f,10f,310f,210f,paint);
        invalidate();
    }
    public void setLineAngle(int angle){
        this.angle = angle;

    }
    public void setHandler(Handler handler){
        mHandler = handler;
        previewCallback.setHandler(mHandler);
    }
}
