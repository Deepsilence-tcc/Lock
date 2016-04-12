package com.tcc.lock;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class GestureLock extends View {

    private Point[][] points = new Point[3][3];
    private boolean isInit = false;
    private Bitmap bitmapNormal;
    private Bitmap bitmapPress;
    private Bitmap bitmapError;
    private Paint paint;
    private float bitmapR;
    private boolean isDraw = false;
    private float mouseX,mouseY;
    private ArrayList<Point> pointList = new ArrayList<Point>();
//    所选中的点唯一的序号存储
    private ArrayList<Integer> passList = new ArrayList<Integer>();
    private Paint linePaint;
    private Paint errorPaint;
    private onDrawFinishListener listener;

    public void setOnDrawFinishedListener(onDrawFinishListener listener){
        this.listener = listener;
    }

    public GestureLock(Context context) {
        super(context);
    }

    public GestureLock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureLock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit){
            init();
        }

        drawPoint(canvas);
        if (pointList!=null&&pointList.size()>1) {

            Point a = pointList.get(0);
            for (int i = 1; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                drawLine(canvas,a,b);
                a = b;

            }
            if (isDraw){
                drawLine(canvas,a,new Point(mouseX,mouseY));
            }
        }
    }


    public void init(){

        int width = getWidth();
        int height = getHeight();

        int offset = Math.abs(width - height)/2;

        int space;
        int offsetX = 0;
        int offsetY = 0;
        if (width>height){
            offsetX = offset;
            space = width/4;
        }else {
            offsetY = offset;
            space = height/4;
        }
// 第一行
        points[0][0] = new Point(offsetX+space,offsetY+space);
        points[0][1] = new Point(offsetX+space*2,offsetY+space);
        points[0][2] = new Point(offsetX+space*3,offsetY+space);
//第二行
        points[1][0] = new Point(offsetX+space,offsetY+space*2);
        points[1][1] = new Point(offsetX+space*2,offsetY+space*2);
        points[1][2] = new Point(offsetX+space*3,offsetY+space*2);
//第三行
        points[2][0] = new Point(offsetX+space,offsetY+space*3);
        points[2][1] = new Point(offsetX+space*2,offsetY+space*3);
        points[2][2] = new Point(offsetX+space*3,offsetY+space*3);

        bitmapNormal = BitmapFactory.decodeResource(getResources(),R.mipmap.normal);
        bitmapPress = BitmapFactory.decodeResource(getResources(),R.mipmap.press);
        bitmapError = BitmapFactory.decodeResource(getResources(),R.mipmap.error);

        bitmapR = bitmapNormal.getHeight()/2;
//初始化画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint = new Paint();
        linePaint.setColor(Color.YELLOW);
        linePaint.setStrokeWidth(5);

        errorPaint = new Paint();
        errorPaint.setColor(Color.RED);
        errorPaint.setStrokeWidth(5);


        isInit = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        mouseX = event.getX();
        mouseY = event.getY();
        int[] downResult = null;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                resetPoint();
                downResult= getSelectedPoints();
                if (downResult!=null){
                    isDraw = true;
                    points[downResult[0]][downResult[1]].state = Point.STATE_PRESS;
                    pointList.add(points[downResult[0]][downResult[1]]);
                    passList.add(downResult[0]*3+downResult[1]);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDraw==true){
                    downResult= getSelectedPoints();
                    if (downResult!=null){
//                         不能重复添加
                        if (!pointList.contains(points[downResult[0]][downResult[1]])){
                            pointList.add(points[downResult[0]][downResult[1]]);
                            passList.add(downResult[0]*3+downResult[1]);
                            points[downResult[0]][downResult[1]].state = Point.STATE_PRESS;

                        }

                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean valid = false;
                if (listener!=null&&isDraw){

                   valid = listener.onDrawFinish(passList);

                }
                if (valid==false){
                   for (Point p : pointList){

                       p.state = Point.STATE_ERROR;

                   }
                }

                isDraw = false;
                break;

        }
//    调用这个方法， 就是会及时绘制在界面当中

        this.postInvalidate();
        return true;
    }

    private int[] getSelectedPoints(){
        Point pointMouse = new Point(mouseX,mouseY);
        for (int i = 0; i <points.length ; i++) {
            for (int j = 0; j <points[i].length ; j++) {
                if (points[i][j].getDistance(pointMouse)<bitmapR){
                    int [] result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }

        return null;
    }

//    绘制连线方法
    private void drawLine(Canvas canvas ,Point a,Point b){

        if (a.state == Point.STATE_PRESS){
            canvas.drawLine(a.pointX,a.pointY,b.pointX,b.pointY,linePaint);
        }else if (a.state==Point.STATE_ERROR){
            canvas.drawLine(a.pointX,a.pointY,b.pointX,b.pointY,errorPaint);

        }
    }
    private void drawPoint(Canvas canvas){
        for (int i = 0; i <points.length ; i++) {
            for (int j = 0;j<points[i].length;j++){
                if (points[i][j].state==Point.STATE_NORMAL){

                    canvas.drawBitmap(bitmapNormal,points[i][j].pointX - bitmapR,points[i][j].pointY-bitmapR,paint);

                }
                if (points[i][j].state==Point.STATE_PRESS){
                    canvas.drawBitmap(bitmapPress,points[i][j].pointX - bitmapR,points[i][j].pointY-bitmapR,paint);

                }
                if (points[i][j].state==Point.STATE_ERROR){
                    canvas.drawBitmap(bitmapError,points[i][j].pointX - bitmapR,points[i][j].pointY-bitmapR,paint);


                }
            }
        }
    }

    public void resetPoint(){
        pointList.clear();
        passList.clear();
        for (int i = 0; i <points.length ; i++) {
            for (int j = 0; j <points[i].length ; j++) {
                points[i][j].state = Point.STATE_NORMAL;
            }
        }
        this.postInvalidate();
    }

//  用来检测用户是输入的图案是否正确
    public interface onDrawFinishListener {

        public boolean onDrawFinish(List<Integer> passList);

    }

}
