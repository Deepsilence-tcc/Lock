package com.tcc.lock;

/**
 * Created by ITcouple on 16/4/11.
 */
public class Point {

    public static int STATE_NORMAL = 0;
    public static int STATE_PRESS = 1;
    public static int STATE_ERROR=2;

    float pointX;
    float pointY;

    int state  = STATE_NORMAL;

    public Point(float pointX,float pointY){
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public float getDistance(Point a){

        float distance = (float) Math.sqrt((pointX-a.pointX)*(pointX-a.pointX)+(pointY-a.pointY)*(pointY-a.pointY));
        return distance;
    }
}
