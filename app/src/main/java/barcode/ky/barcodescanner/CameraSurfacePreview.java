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
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.jar.Attributes;

import android.os.Handler;

import util.camera.PreviewCallback;
/**
 * Created by kylin on 15/1/4.
 */
public class CameraSurfacePreview extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener  {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    String TAG = "CameraSurface";
    private int angle;
    Context mContex;
    Handler mHandler;

    PreviewCallback previewCallback = new PreviewCallback();
    int width = 1280;
    int height = 720;
    int scan_width = 300;
    int scan_height = 200;
    ScaleGestureDetector scaleGestur;
    Rect rect = new Rect();
    public Camera getCamera(){
        return mCamera;
    }
    public void stopPreview(){
        mCamera.stopPreview();
    }
    public void startPreview(){
        mCamera.setPreviewCallback(previewCallback);
        mCamera.startPreview();
    }
    private void setPreviewSize(){
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

    }
    private void initSurface(Context context){
        mHolder = getHolder();
        mHolder.addCallback(this);
        //mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mContex = context;
        previewCallback.setContext(mContex);

        angle = 0;

        rect.top = height/2 - scan_height/2;
        rect.left = width/2 - scan_width/2;
        rect.bottom = height/2 + scan_height/2;
        rect.right = width/2 + scan_width/2;
        previewCallback.setRect(rect);
        previewCallback.setPreviewSize(new Point(width, height));

        this.setOnTouchListener(this);

        scaleGestur = new ScaleGestureDetector(context,scaleGestureDetector);
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
        super(context, attrs);
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

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(0,cameraInfo);
        Log.i(TAG, "cameraInfo.orientation  " + cameraInfo.orientation);

        parameters.setPreviewSize(width, height);
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(cameraInfo.orientation - 90);
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


            mCamera.startPreview();
        }catch (Exception ioe){

        }
    }
//    Toast t= null;
//    private void decode(byte[] data, int width, int height) {
//        long start = System.currentTimeMillis();
//      //  Log.e("barcode","decode");
//        Result rawResult = null;
//        MultiFormatReader multiFormatReader = new MultiFormatReader();
//        PlanarYUVLuminanceSource source =new PlanarYUVLuminanceSource(data, width, height, 10,10,
//                310, 210, false);
//        if (source != null) {
//            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//            try {
//                rawResult = multiFormatReader.decodeWithState(bitmap);
//            } catch (ReaderException re) {
//                // continue
//            } finally {
//                multiFormatReader.reset();
//            }
//        }
//
//        if(rawResult !=null){
//            Log.e("barcode", " " + rawResult);
//            if(t != null) {
//                t.cancel();
//            }
//            t = Toast.makeText(mContex, "barcode " + rawResult, Toast.LENGTH_SHORT);
//            t.show();
//        }
//
//    }
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e(TAG, "surfaceDestroyed");
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
        Log.e(TAG,"onDraw " + rect.top + " " + rect.bottom);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);

        int lineLen = 1280;
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,paint);
        invalidate();
    }
    public void setLineAngle(int angle){
        this.angle = angle;

    }
    public void setHandler(Handler handler){
        mHandler = handler;
        previewCallback.setHandler(mHandler);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e(TAG, "onTouch");
        boolean handled = scaleGestur.onTouchEvent(event);
        return false;
    }
    float beginx = 0,beginy = 0;
    int vw=400,vh=400;
    ScaleGestureDetector.OnScaleGestureListener scaleGestureDetector = new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //return super.onScale(detector);
            Log.e(TAG,"onScale " + detector.getScaleFactor());
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.e(TAG,"onScale begin " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY());
            beginx = detector.getCurrentSpanX();
            beginy = detector.getCurrentSpanY();
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            //Log.e(TAG,"onScale end " + detector.getCurrentSpanX() + " " + detector.getCurrentSpanY() );
            Log.e(TAG, "onScale end x y" + beginx / detector.getCurrentSpanX() + " " + beginy / detector.getCurrentSpanY());

            //vw =(int) (vw * beginx / (float)(detector.getCurrentSpanX()));
            //vh =(int) (vh * beginy / (float)(detector.getCurrentSpanY()));

            //width*= beginx / (float)(detector.getCurrentSpanX());
            //height*= beginy / (float)(detector.getCurrentSpanY());

            scan_width*=  (float)(detector.getCurrentSpanX()) / beginx ;
            scan_height*= (float)(detector.getCurrentSpanY()) / beginy;

            rect.top = height/2 - scan_height/2;
            rect.left = width/2 - scan_width/2;
            rect.bottom = height/2 + scan_height/2;
            rect.right = width/2 + scan_width/2;

//            rect.top -=10;
//            rect.left --;
//            rect.bottom --;
//            rect.right --;
            super.onScaleEnd(detector);
        }

    };
}
