package com.hm.draw;

import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Path;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.media.image.PixelMap;
import ohos.multimodalinput.event.MmiPoint;
import ohos.multimodalinput.event.TouchEvent;

public class DrawComponment extends Component implements Component.DrawTask, Component.TouchEventListener {
    private static final String TAG = "DrawComponment";
    PixelMap mPixelMap;
    Canvas mCanvas;
    Path mPath = new Path();
    Paint mPaint;
    Point mPrePoint = new Point();
    Point mPreCtrlPoint = new Point();

    public DrawComponment(Context context) {
        super(context);
        //初始化paint
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(5f);
        mPaint.setStyle(Paint.Style.STROKE_STYLE);
        //添加绘制任务
        addDrawTask(this::onDraw);
        //设置TouchEvent监听
        setTouchEventListener(this::onTouchEvent);
    }

    @Override
    public void onDraw(Component component, Canvas canvas) {
        canvas.drawPath(mPath, mPaint);
    }


    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case TouchEvent.PRIMARY_POINT_DOWN: {
                //鸿蒙Log工具
                HiLog.info(new HiLogLabel(0, 0, TAG), "TouchEvent.PRIMARY_POINT_DOWN");
                //获取点信息
                MmiPoint point = touchEvent.getPointerPosition(touchEvent.getIndex());
                mPath.reset();
                mPath.moveTo(point.getX(), point.getY());
                mPrePoint.position[0] = point.getX();
                mPrePoint.position[1] = point.getY();
                mPreCtrlPoint.position[0] = point.getX();
                mPreCtrlPoint.position[1] = point.getY();
                //PRIMARY_POINT_DOWN 一定要返回true
                return true;
            }
            case TouchEvent.PRIMARY_POINT_UP:

                break;
            case TouchEvent.POINT_MOVE: {
                HiLog.info(new HiLogLabel(0, 0, TAG), "TouchEvent.POINT_MOVE");
                MmiPoint point = touchEvent.getPointerPosition(touchEvent.getIndex());
                Point currCtrlPoint = new Point((point.getX() + mPrePoint.position[0]) / 2,
                        (point.getY() + mPrePoint.position[1]) / 2);
                //绘制三阶贝塞尔曲线
                mPath.cubicTo(mPrePoint, mPreCtrlPoint, currCtrlPoint);
                mPreCtrlPoint.position[0] = currCtrlPoint.position[0];
                mPreCtrlPoint.position[1] = currCtrlPoint.position[1];
                mPrePoint.position[0] = point.getX();
                mPrePoint.position[1] = point.getY();
                //更新显示
                invalidate();
                break;
            }

        }
        return false;
    }
}