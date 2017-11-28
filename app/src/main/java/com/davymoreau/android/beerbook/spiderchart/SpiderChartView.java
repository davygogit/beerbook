package com.davymoreau.android.beerbook.spiderchart;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.davymoreau.android.beerbook.R;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class SpiderChartView extends View {

    public static final String LABEL = "label";
    public static final String VALUE = "value";

    private float mMaxValue = 5.0f;
    private float mRange = 1.0f;
    private float mPlotSize = 5.0f;
    private float mWebLineWidth = 1.0f;
    private int mWebLineColor = Color.BLACK;
    private float mPolyLineWidth = 5.0f;
    private int mPolyLineColor = Color.BLUE;
    private int mPolyFillColor = Color.CYAN;
    private int mPolyAlpha = 25;
    private boolean mDrawPlot = true;

    private ArrayList<ContentValues> mData;
    private Paint mWebPaint;
    private Paint mPolyPaint;
    private Paint mPlotPaint;
    private Paint mPoliLinePaint;
    private Paint mLabelPaint;

    public SpiderChartView(Context context) {
        super(context);
        init(null, 0);
    }

    public SpiderChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SpiderChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void setData(ArrayList data) {
        mData = data;
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SpiderChartView, defStyle, 0);

        ////
        mPlotSize = a.getDimension(R.styleable.SpiderChartView_plotSize, mPlotSize);
        mPolyFillColor = a.getColor(R.styleable.SpiderChartView_polyFillColor, mPolyFillColor);
        mPolyLineColor = a.getColor(R.styleable.SpiderChartView_polyLineColor, mPolyLineColor);
        mMaxValue = a.getFloat(R.styleable.SpiderChartView_maxValue, mMaxValue);
        mPolyLineWidth = a.getFloat(R.styleable.SpiderChartView_polyLineWidth, mPolyLineWidth);
        mRange = a.getFloat(R.styleable.SpiderChartView_range, mRange);
        mWebLineWidth = a.getFloat(R.styleable.SpiderChartView_webLineWidth, mWebLineWidth);
        mWebLineColor = a.getColor(R.styleable.SpiderChartView_webLineColor, mWebLineColor);
        mPolyAlpha = a.getInteger(R.styleable.SpiderChartView_polyAlpha, mPolyAlpha);
        mDrawPlot = a.getBoolean(R.styleable.SpiderChartView_drawPlot, mDrawPlot);

        mWebPaint = new Paint();
        mPolyPaint = new Paint();
        mPoliLinePaint = new Paint();
        mLabelPaint = new Paint();

        a.recycle();

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mWebPaint.setColor(mWebLineColor);
        mWebPaint.setStrokeWidth(mWebLineWidth);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mPolyPaint.setColor(mPolyFillColor);
        mPolyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPolyPaint.setAlpha(mPolyAlpha);

        mPoliLinePaint.setColor(mPolyLineColor);
        mPoliLinePaint.setStrokeWidth(mPolyLineWidth);
        mPoliLinePaint.setStyle(Paint.Style.STROKE);
        mPoliLinePaint.setAntiAlias(true);

        mLabelPaint.setTextSize(35);
        mLabelPaint.setColor(Color.BLACK);
        mLabelPaint.setSubpixelText(true);
        mLabelPaint.setAntiAlias(true);
        mLabelPaint.setTextSize(30);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // spider chart
        if (mData != null) {
            int size = mData.size();
            int width = canvas.getWidth();
            int heigh = canvas.getHeight();
            int centerX = width / 2;
            int centerY = heigh / 2;
            int radius = centerX - (int)(centerX / 2.3);
            Rect bound;
            Path path = new Path();

            for (int i = 0; i < size; i++) {
                double angle =  ((Math.PI * 2) / size) * i;
                angle = (angle - Math.PI / 2);
                int X = (int) (Math.cos(angle) * radius);
                int Y = (int) (Math.sin(angle) * radius);
                canvas.drawLine(centerX, centerY, centerX + X, centerY + Y, mWebPaint);

                // positionnement du texte
                ContentValues cv = mData.get(i);
                String label = cv.getAsString(LABEL);
                bound = new Rect();
                mLabelPaint.getTextBounds(label, 0, label.length(), bound);
                int Xlabel = centerX + (int) (Math.cos(angle) * radius * 1.05);
                int Ylabel = centerY + (int) (Math.sin(angle) * radius * 1.05);
                //Xlabel = (int) (Xlabel + Math.cos(angle) );
                int shiftX;
                int shiftY;

                if (Math.cos(angle) < -0.1) shiftX = (int) (-1 * bound.right);
                else if (Math.cos(angle) < 0.1) shiftX = (int) (-0.5 * bound.right);
                else shiftX = 0;

                if (Math.sin(angle) > 0.1) shiftY = (int) (-1 * bound.top);
                else if (Math.sin(angle) > -0.1) shiftY = (int) (-0.5 * bound.top);
                else shiftY = 0;

                canvas.drawText(label, Xlabel + shiftX, Ylabel +  shiftY, mLabelPaint);

                // Data
                float value = cv.getAsFloat(VALUE);
                float valueRadius = (radius / mMaxValue) * value;
                int XPath = (int) (Math.cos(angle) * valueRadius) + centerX;
                int YPath = (int) (Math.sin(angle) * valueRadius) + centerY;
                if (i == 0) {
                    path.moveTo(XPath, YPath);
                } else {
                    path.lineTo(XPath, YPath);
                }

            }
            path.close();

            // dessins lignes transverses
            int nbLines = (int) (mMaxValue / mRange);
            for (int j = 0; j < nbLines; j++) {
                double lastAngle = (float) ((Math.PI * 2) / size) * (size - 1);
                int smallRadius = (radius / nbLines) * (j + 1);
                int LastX = (int) (Math.cos(lastAngle) * smallRadius) + centerX;
                int LastY = (int) (Math.sin(lastAngle) * smallRadius) + centerY;
                canvas.drawCircle(centerX, centerY, smallRadius, mWebPaint);

        }


            canvas.drawPath(path, mPolyPaint);
            canvas.drawPath(path, mPoliLinePaint);
        }
    }
}
