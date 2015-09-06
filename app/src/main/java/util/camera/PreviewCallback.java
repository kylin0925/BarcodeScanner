package util.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
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

/**
 * Created by kylin25 on 2015/8/23.
 */
public class PreviewCallback implements Camera.PreviewCallback {
    Context mContext;
    Handler mHandler;
    Rect mRect;
    public void setContext(Context context) {
        mContext = context;
    }
    public void setHandler(Handler handler) {
        mHandler = handler;
    }
    public void setRect(Rect rect) {
        mRect = rect;
    }
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
        decode(data, 640, 480);
       // Log.e("PreviewCallback", "decode end ");
    }

    Toast t = null;

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        //  Log.e("barcode","decode");
        Result rawResult = null;
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, mRect.left, mRect.top,
                mRect.right, mRect.bottom, false);
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
            //Log.e("PreviewCallback", " " + rawResult);
            if (t != null) {
                t.cancel();
            }
            t = Toast.makeText(mContext, "barcode " + rawResult, Toast.LENGTH_SHORT);
            t.show();
            Message message = new Message();
            message.arg1 = 123;
            message.obj = rawResult.toString();
            mHandler.sendMessage(message);
            //sentBarcode(rawResult.toString());
        }

    }

}
