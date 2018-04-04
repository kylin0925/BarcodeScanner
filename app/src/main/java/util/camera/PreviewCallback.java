package util.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import android.os.Handler;

import barcode.ky.barcodescanner.RangeView;

/**
 * Created by kylin25 on 2015/8/23.
 */
public class PreviewCallback implements Camera.PreviewCallback {
    Context mContext;
    Handler mHandler;
    Rect mRect;
    Point mPreivewSize;
    String TAG = "PreviewCallback";
    RangeView custview;
    public static Bitmap bitmap = null;
    boolean isScanned = false;
    public void setContext(Context context) {
        mContext = context;
    }
    public void setHandler(Handler handler) {
        mHandler = handler;
    }
    public void setRect(Rect rect) {
        mRect = rect;
    }
    public void setFlag(boolean flag) {
        isScanned = flag;
    }
    public void setPreviewSize(Point p){
        mPreivewSize = p;
    }
    public void setRangeView(RangeView r) {custview = r;}
    private void dumpPreview(byte[] data){
        String filename = Environment.getExternalStorageDirectory() + "/code/dump.yuv";
        try {
            FileOutputStream fo = new FileOutputStream(new File(filename));
            fo.write(data);
            fo.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
       // Log.e("PreviewCallback","preview " + data[0]);
        decode(data, mPreivewSize.x, mPreivewSize.y);
       // Log.e("PreviewCallback", "decode end ");
    }

    Toast t = null;

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        //  Log.e("barcode","decode");
        Result rawResult = null;
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        Log.e(TAG, "decode " + mRect.left);
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, mRect.left, mRect.top,
                mRect.right, mRect.bottom, false);

        //bundleThumbnail(source,new Bundle());
        if (source != null) {


            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                //Log.e("PreviewCallback","multiFormatReader.decodeWithState(bitmap) ");
                rawResult = multiFormatReader.decodeWithState(bitmap);
            } catch (ReaderException re) {
                //Log.e("PreviewCallback","ReaderException " + re.toString());
                // continue
            } finally {
                multiFormatReader.reset();
            }


        }
        //Log.e("PreviewCallback","rawResult");
        if (rawResult != null) {
            //if(t == null)
            {
                int[] pixels = source.renderThumbnail();
                int widthb = source.getThumbnailWidth();
                int heightb = source.getThumbnailHeight();
                Matrix matrix = new Matrix();
                matrix.postRotate(180);
                Bitmap bitmap1 = Bitmap.createBitmap(pixels, 0, widthb, widthb, heightb, Bitmap.Config.ARGB_8888);
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap1, 0, 0, widthb, heightb, matrix, true);
                Bitmap mutablebitmap = bitmap2.copy(Bitmap.Config.ARGB_8888, true);

                //Canvas canvas = new Canvas(mutablebitmap);


                //custview.draw(canvas);
                //custview.invalidate();
                Drawable drawable = new BitmapDrawable(mutablebitmap);
                custview.setBackground(drawable);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthb, heightb);
                custview.setLayoutParams(params);
                custview.setVisibility(View.VISIBLE);
            }
            if (rawResult != null && isScanned == false) {
                isScanned = true;
                //Log.e("PreviewCallback", " " + rawResult);
                if (t != null) {
                    t.cancel();
                }
                t = Toast.makeText(mContext, "barcode " + rawResult, Toast.LENGTH_SHORT);
                t.show();
                //bundleThumbnail(source, new Bundle());
                Message message = new Message();
                message.arg1 = 123;
                message.obj = rawResult.toString();
                mHandler.sendMessage(message);
                //sentBarcode(rawResult.toString());


            }
        }
    }
    private void bundleThumbnail(PlanarYUVLuminanceSource source, Bundle bundle) {
        int[] pixels = source.renderThumbnail();
        int width = source.getThumbnailWidth();
        int height = source.getThumbnailHeight();
        Bitmap bitmap = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
        bundle.putByteArray("barcode_bitmap", out.toByteArray());
        bundle.putFloat("barcode_scaled_factor", (float) width / source.getWidth());
       // Log.e("Previewcallback","bundleThumbnail");
        Message message = new Message();
        message.arg1 = 888;
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

}
