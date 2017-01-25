/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2016.
 */

package ir.irani.telecam.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;

import ir.irani.telecam.messenger.AndroidUtilities;

public class CloseProgressDrawable2 extends Drawable {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private long lastFrameTime;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private RectF rect = new RectF();
    private float angle;
    private boolean animating;

    public CloseProgressDrawable2() {
        super();
        paint.setColor(0xffadadad);
        paint.setStrokeWidth(AndroidUtilities.dp(2));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void startAnimation() {
        animating = true;
        lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
    }

    public void stopAnimation() {
        animating = false;
    }

    @Override
    public void draw(Canvas canvas) {
        long newTime = System.currentTimeMillis();
        boolean invalidate = false;
        if (lastFrameTime != 0) {
            long dt = (newTime - lastFrameTime);
            if (animating || angle != 0) {
                angle += 360 * dt / 500.0f;
                if (!animating && angle >= 720) {
                    angle = 0;
                } else {
                    angle -= (int) (angle / 720) * 720;
                }
                invalidateSelf();
            }
        }

        canvas.save();
        canvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
        canvas.rotate(-45);
        float progress1 = 1.0f;
        float progress2 = 1.0f;
        float progress3 = 1.0f;
        float progress4 = 0.0f;
        if (angle >= 0 && angle < 90) {
            progress1 = (1.0f - angle / 90.0f);
        } else if (angle >= 90 && angle < 180) {
            progress1 = 0.0f;
            progress2 = 1.0f - (angle - 90) / 90.0f;
        } else if (angle >= 180 && angle < 270) {
            progress1 = progress2 = 0;
            progress3 = 1.0f - (angle - 180) / 90.0f;
        } else if (angle >= 270 && angle < 360) {
            progress1 = progress2 = progress3 = 0;
            progress4 = (angle - 270) / 90.0f;
        } else if (angle >= 360 && angle < 450) {
            progress1 = progress2 = progress3 = 0;
            progress4 = 1.0f - (angle - 360) / 90.0f;
        } else if (angle >= 450 && angle < 540) {
            progress2 = progress3 = 0;
            progress1 = (angle - 450) / 90.0f;
        } else if (angle >= 540 && angle < 630) {
            progress3 = 0;
            progress2 = (angle - 540) / 90.0f;
        } else if (angle >= 630 && angle < 720) {
            progress3 = (angle - 630) / 90.0f;
        }

        if (progress1 != 0) {
            canvas.drawLine(0, 0, 0, AndroidUtilities.dp(8) * progress1, paint);
        }
        if (progress2 != 0) {
            canvas.drawLine(-AndroidUtilities.dp(8) * progress2, 0, 0, 0, paint);
        }
        if (progress3 != 0) {
            canvas.drawLine(0, -AndroidUtilities.dp(8) * progress3, 0, 0, paint);
        }
        if (progress4 != 1) {
            canvas.drawLine(AndroidUtilities.dp(8) * progress4, 0, AndroidUtilities.dp(8), 0, paint);
        }

        canvas.restore();

        int cx = getBounds().centerX();
        int cy = getBounds().centerY();
        rect.set(cx - AndroidUtilities.dp(8), cy - AndroidUtilities.dp(8), cx + AndroidUtilities.dp(8), cy + AndroidUtilities.dp(8));
        canvas.drawArc(rect, (angle < 360 ? 0 : angle - 360) - 45, (angle < 360 ? angle : 720 - angle), false, paint);

        lastFrameTime = newTime;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(24);
    }

    @Override
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(24);
    }
}
