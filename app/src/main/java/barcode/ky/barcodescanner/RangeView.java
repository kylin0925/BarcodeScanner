package barcode.ky.barcodescanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;

/**
 * Created by kylin25 on 2015/12/26.
 */
public class RangeView extends View implements View.OnTouchListener {
    String TAG = "RangeView";
    Rect rect = new Rect();
    ScaleGestureDetector scaleGestur;
    int vw=400,vh=400;
    public RangeView(Context context) {
        super(context);

        rect.top = 0;
        rect.left = 0;
        rect.bottom = 100;
        rect.right = 100;
        this.setOnTouchListener(this);
        scaleGestur = new ScaleGestureDetector(context,scaleGestureDetector);
    }

    public RangeView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(this);
        scaleGestur = new ScaleGestureDetector(context,scaleGestureDetector);
    }

    public RangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        scaleGestur = new ScaleGestureDetector(context,scaleGestureDetector);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        //Log.e(TAG, "onDraw");
////        Paint paint = new Paint();
////        rect.top = 10;
////        rect.left = 10;
////        rect.bottom = 100;
////        rect.right = 100;
////
////        paint.setColor(Color.YELLOW);
////        paint.setStyle(Paint.Style.STROKE);
////        canvas.drawRect(rect,paint);
////        paint.setColor(Color.BLUE);
////        canvas.drawText("hello",10,10,paint);
//        //invalidate();
//    }
    float x1,y1,x2,y2;
    @Override
    public boolean onTouch(View v, MotionEvent event) {
       // Log.e(TAG, "event ");
        //v.setLayoutParams(new LinearLayout.LayoutParams(500, 300));
        //v.animate().x(400).y(10).setDuration(0);

        //Log.e(TAG, "event " + event.getPointerCount());


        if(event.getPointerCount() >1){
            boolean handled = scaleGestur.onTouchEvent(event);
//            Log.e(TAG, "event " + event.getAction() + " " +  event.getPointerCount());
//
//            if( MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_POINTER_DOWN ) {
//                Log.e(TAG, "event " + MotionEventCompat.getActionMasked(event) + " " +  event.getPointerCount());
//                int index = MotionEventCompat.getActionIndex(event);
//                Log.e(TAG, "mask " + index + " x " + MotionEventCompat.getX(event, index) + " , " +  MotionEventCompat.getY(event,index));
//
//                x1 = event.getX(0);
//                y1 = event.getY(0);
//                x2 = event.getX(1);
//                y2 = event.getY(1);
//
//            }else if( MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_POINTER_UP){
//                float w1 = Math.abs(x1 - x2);
//                float h1 = Math.abs(y1 - y2);
//                float w2 = Math.abs(event.getX(0) - event.getX(1));
//                float h2 = Math.abs(event.getY(0) - event.getY(1));
//                float dx = w2 / w1;
//                float dy = h2 / h1;
//                Log.e(TAG, "scale " + dx + " " + dy);
//                setLayoutParams(new LinearLayout.LayoutParams((int) (vw * dx), (int) (vh * dy)));
//            }
        }
        return true;
    }
    float beginx = 0,beginy = 0;
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

            vw =(int) (vw * beginx / (float)(detector.getCurrentSpanX()));
            vh =(int) (vh * beginy / (float)(detector.getCurrentSpanY()));
            setLayoutParams(new LinearLayout.LayoutParams(vw , vh));
//            if( beginy/detector.getCurrentSpanY() < 1) {
//                setLayoutParams(new LinearLayout.LayoutParams(vw , vh));
//            } else {
//                setLayoutParams(new LinearLayout.LayoutParams(400, 300));
//            }
            super.onScaleEnd(detector);
        }

    };
}
