package com.ozdevcode.simpleringpercentagegauge;

/**
 * Created by zainozzaini on 4/7/14.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class SimpleRingGauge extends TextView {

    private int framesPerSecond = 60;
    private long animationDuration; // 10 seconds

    private long startTime = 0;

    private int centerX, centerY;
    private int mRadius;




    private int gaugeColor, ringBackColor, textPercentColor;
    private int textPercentSize;
    private float percentage ;
    private TypedArray a;
    private String regex ="(?=.*\\d)\\d*(?:\\.0)?$";






    public SimpleRingGauge(Context context) {
        this(context, null);
    }


    public SimpleRingGauge(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ringChartStyle);
    }

    public SimpleRingGauge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SimpleRingGauge,
                0, 0);

        try {
            gaugeColor = a.getColor(R.styleable.SimpleRingGauge_gaugeColor, Color.WHITE);
            ringBackColor = a.getColor(R.styleable.SimpleRingGauge_ringBackColor, Color.DKGRAY);
            gaugeColor = a.getColor(R.styleable.SimpleRingGauge_gaugeColor, Color.WHITE);
            ringBackColor = a.getColor(R.styleable.SimpleRingGauge_ringBackColor, Color.DKGRAY);
            animationDuration = (long) a.getFloat(R.styleable.SimpleRingGauge_animationDurationMilSec, 2000);
            textPercentColor = a.getColor(R.styleable.SimpleRingGauge_textPercentColor, Color.WHITE);
            textPercentSize = a.getInt(R.styleable.SimpleRingGauge_textPercentSize, 120);


            ///this only works in onDraw
            final int mRadius = a.getInteger(R.styleable.SimpleRingGauge_radius, getWidth() / 4);
//            final int centerX = a.getInteger(R.styleable.SimpleRingGauge_centerX, getWidth() / 2);
//            final int centerY = a.getInteger(R.styleable.SimpleRingGauge_centerY, getHeight() / 2);
            final float percentage = a.getFloat(R.styleable.SimpleRingGauge_percentage, 1f);

            this.mRadius = mRadius;
            this.percentage = percentage;

        } finally {
            a.recycle();
        }






        this.startTime = System.currentTimeMillis();
        this.postInvalidate();

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        centerX=getWidth()/2;
        centerY=getHeight()/2;
        //mRadius = getWidth()/4;




        Paint textPain = new Paint(Paint.LINEAR_TEXT_FLAG);
        textPain.setAntiAlias(true);

        Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint gaugePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeCap(Paint.Cap.ROUND);
        ringPaint.setStrokeWidth(19);
        ringPaint.setShader(gradientStyle());
        ringPaint.setColor(ringBackColor);

        gaugePaint.setStrokeWidth(18.5f);
        gaugePaint.setStyle(Paint.Style.STROKE);
        gaugePaint.setStrokeCap(Paint.Cap.ROUND);
        gaugePaint.setColor(gaugeColor);


        RectF rectF = new RectF(centerX - mRadius, centerY - mRadius, centerX
                + mRadius, centerY + mRadius);


        long elapsedTime = System.currentTimeMillis() - startTime;
        elapsedTime = elapsedTime <= animationDuration ? elapsedTime : animationDuration;

        int txtSize;

        textPain.setTypeface(Typeface.MONOSPACE);
        textPain.setTextAlign(Paint.Align.CENTER);

        //draw background

        canvas.drawArc(rectF, 135, 275f, false, ringPaint);


        if (elapsedTime / animationDuration <= 1) {

            float currPercent = (float)100 * (float)elapsedTime / animationDuration * percentage;
            String strCurrPercent;

            if(String.format("%.01f", currPercent).matches(regex)){
                txtSize= textPercentSize + 5;
                strCurrPercent = (int)(currPercent )+"%";
            }else{
                txtSize = textPercentSize;
                strCurrPercent= String.format("%.01f", currPercent)+"%";
            }

            textPain.setColor(textPercentColor);


//          textPain.setTextSize((int)(30*benchMark));
//          canvas.drawText(getResources().getString(R.string.selesai),rectF.centerX(),rectF.centerY()+(float)(55*benchMark),textPain);
            textPain.setTextSize((int) (txtSize));
            canvas.drawText(strCurrPercent, rectF.centerX(), rectF.centerY() + 16, textPain);
            canvas.drawArc(rectF, 135, 275f * elapsedTime / animationDuration * percentage, false, gaugePaint);


        }


        if (elapsedTime < animationDuration)
            this.postInvalidateDelayed(1000 / framesPerSecond);


    }

    private LinearGradient gradientStyle() {
        float f1 = 34.0F * mRadius / 35.0F;
        float f2 = getWidth() / 2;
        float f3 = getHeight() / 2;

        return new LinearGradient(f2, f3 - f1, f2, f3 + f1, new int[]{-16777216, 0}, null, Shader.TileMode.CLAMP);
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}