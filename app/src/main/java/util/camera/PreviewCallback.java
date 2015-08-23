package util.camera;

import android.content.Context;
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

import java.io.File;
import java.io.FileOutputStream;
import android.os.Handler;

/**
 * Created by kylin25 on 2015/8/23.
 */
public class PreviewCallback implements Camera.PreviewCallback {
    Context mContext;
    Handler mHandler;
    public void setContext(Context context) {
        mContext = context;
    }
    public void setHandler(Handler handler) {
        mHandler = handler;
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.e("Camera","preview " + data[0]);
        decode(data, 640, 480);
        String filename = Environment.getExternalStorageDirectory() + "/code/dump.yuv";
//        try {
//            FileOutputStream fo = new FileOutputStream(new File(filename));
//            fo.write(data);
//            fo.close();
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }

    }

    Toast t = null;

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        //  Log.e("barcode","decode");
        Result rawResult = null;
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 10, 10,
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

        if (rawResult != null) {
            Log.e("barcode", " " + rawResult);
            if (t != null) {
                t.cancel();
            }
            t = Toast.makeText(mContext, "barcode " + rawResult, Toast.LENGTH_SHORT);
            t.show();
            Message message = new Message();
            message.arg1 = 123;
            message.obj = rawResult.toString();
            mHandler.sendMessage(message);
        }

    }
}
