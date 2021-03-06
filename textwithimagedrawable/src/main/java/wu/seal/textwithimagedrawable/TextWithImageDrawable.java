package wu.seal.textwithimagedrawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.TypedValue;

/**
 * 文字带图片的drawable
 * Created By: Seal.Wu
 * Date: 2016/6/17
 * Time: 9:56
 */
public class TextWithImageDrawable extends Drawable {

    /**
     * 图片放置的相对于Text的位置
     */
    public enum Position {
        LEFT, TOP, RIGHT, BOTTOM
    }

    /**
     * 文字超出大小的省略模式
     */
    public enum EllipsizeModel {
        PRE, MID, END
    }

    private Context mContext;


    private Drawable mDrawable;

    /**
     * 文字
     */
    private String mText;

    /**
     * 原始文字
     */
    private String originText;

    private int paddingLeft, paddingRight, paddingTop, paddingBottom;

    private int drawablePadding;

    private int alpha;

    private Resources mResources;

    private Position position = Position.LEFT;

    private TextPaint mTextPaint;

    private float mTextSize = 14;

    private int mTextHeight;

    /**
     * 文字顶部值偏移差
     */
    private int textTopDelBaseLine;


    /**
     * 文字最大长度
     */
    private int maxTextLength = Integer.MAX_VALUE;

    /**
     * 省略号
     */
    private final static String SUFFIX = "…";

    /**
     * 文字的省略模式
     */
    private EllipsizeModel ellipsizeModel = EllipsizeModel.END;

    public TextWithImageDrawable(Context context) {
        mContext = context;
        mResources = context.getResources();
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mTextPaint.density = mResources.getDisplayMetrics().density;
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, mResources.getDisplayMetrics()));
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = Math.round(fontMetrics.descent - fontMetrics.ascent);
        textTopDelBaseLine = Math.round(-fontMetrics.ascent - fontMetrics.leading);
    }


    public void setText(String mText) {
        originText = mText;
        if (maxTextLength != Integer.MAX_VALUE && maxTextLength < mText.length()) {
            if (ellipsizeModel == EllipsizeModel.END) {
                this.mText = mText.substring(0, maxTextLength) + SUFFIX;
            } else if (ellipsizeModel == EllipsizeModel.PRE) {
                this.mText = SUFFIX + mText.substring(mText.length() - 3, mText.length());
            } else if (ellipsizeModel == EllipsizeModel.MID) {
                int firstStringLength = maxTextLength / 2;
                this.mText = mText.substring(0, firstStringLength) + SUFFIX +
                        mText.substring((mText.length() - (maxTextLength - firstStringLength)), mText.length());
            }
        } else {
            this.mText = mText;
        }

        invalidateSelf();
    }

    public void setImageRes(int imageRes) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = mResources.getDrawable(imageRes, mContext.getTheme());
        } else {
            drawable = mResources.getDrawable(imageRes);
        }
        setDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bitmap) {
        setDrawable(new BitmapDrawable(mResources, bitmap));
    }

    public void setDrawable(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        this.mDrawable = drawable;
        invalidateSelf();
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        invalidateSelf();
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        invalidateSelf();

    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
        invalidateSelf();

    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
        invalidateSelf();
    }


    public void setImagePadding(int drawablePadding) {
        this.drawablePadding = drawablePadding;
        invalidateSelf();
    }

    /**
     * 设置文字省略模式
     *
     * @param ellipsizeModel 文字省略模式
     */
    public void setEllipsizeModel(EllipsizeModel ellipsizeModel) {
        this.ellipsizeModel = ellipsizeModel;
        if (originText != null) {
            setText(originText);
        }
    }

    @Override
    public void draw(Canvas canvas) {

        canvas.clipRect(paddingLeft, paddingTop, canvas.getWidth() - paddingRight, canvas.getHeight() - paddingBottom);

        int contentHeight = getIntrinsicHeight() - paddingTop - paddingBottom;
        int contentWidth = getIntrinsicWidth() - paddingLeft - paddingRight;

        if (position == Position.LEFT) {
            if (mDrawable != null) {
                final int bitmapTop = (contentHeight - mDrawable.getIntrinsicHeight()) / 2 + paddingTop;
                final int bitmapLeft = this.paddingLeft;
                canvas.translate(bitmapLeft, bitmapTop);
                mDrawable.draw(canvas);

                if (isNotEmpty(mText)) {

                    final int left = this.paddingLeft + mDrawable.getIntrinsicWidth() + drawablePadding;
                    final int top = (contentHeight - mTextHeight) / 2 + textTopDelBaseLine + paddingTop;
                    canvas.drawText(mText, left, top, mTextPaint);
                }
            } else {
                if (isNotEmpty(mText)) {

                    final int left = this.paddingLeft + drawablePadding;
                    final int top = (contentHeight - mTextHeight) / 2 + textTopDelBaseLine + paddingTop;
                    canvas.drawText(mText, left, top, mTextPaint);
                }
            }
        } else if (position == Position.RIGHT) {

            if (isNotEmpty(mText)) {

                final int textLeft = this.paddingLeft;
                final int textTop = (contentHeight - mTextHeight) / 2 + textTopDelBaseLine + paddingTop;
                canvas.drawText(mText, textLeft, textTop, mTextPaint);
            }

            if (mDrawable != null) {
                int textWidth = Math.round(mTextPaint.measureText(mText));
                final int left = paddingLeft + textWidth + drawablePadding;
                final int top = (contentHeight - mDrawable.getIntrinsicHeight()) / 2 + paddingTop;
                canvas.translate(left, top);
                mDrawable.draw(canvas);
            }
        } else if (position == Position.TOP) {
            if (mDrawable != null) {
                final int bitmapLeft = (contentWidth - mDrawable.getIntrinsicWidth()) / 2 + paddingLeft;
                final int bitmapTop = paddingTop;
                canvas.translate(bitmapLeft, bitmapTop);
                mDrawable.draw(canvas);
                if (isNotEmpty(mText)) {
                    final int left = Math.round(contentWidth - mTextPaint.measureText(mText)) / 2 + paddingLeft;
                    final int top = paddingTop + mDrawable.getIntrinsicHeight() + drawablePadding + textTopDelBaseLine;
                    canvas.drawText(mText, left, top, mTextPaint);
                }
            } else {
                if ((isNotEmpty(mText))) {
                    final int left = Math.round(contentWidth - mTextPaint.measureText(mText)) / 2 + paddingLeft;
                    final int top = paddingTop + drawablePadding + textTopDelBaseLine;
                    canvas.drawText(mText, left, top, mTextPaint);
                }
            }
        } else if (position == Position.BOTTOM) {
            if (isNotEmpty(mText)) {
                final int textLeft = Math.round(paddingLeft + (contentWidth - mTextPaint.measureText(mText)) / 2);
                final int textTop = paddingTop + textTopDelBaseLine;
                canvas.drawText(mText, textLeft, textTop, mTextPaint);

                if (mDrawable != null) {
                    final int bitmapLeft = Math.round(paddingLeft + ((contentWidth - mDrawable.getIntrinsicWidth()) / 2));
                    final int bitmapTop = paddingTop + mTextHeight + drawablePadding;
                    canvas.translate(bitmapLeft, bitmapTop);
                    mDrawable.draw(canvas);
                }
            } else {
                if (mDrawable != null) {
                    final int bitmapLeft = Math.round(paddingLeft + ((contentWidth - mDrawable.getIntrinsicWidth()) / 2));
                    final int bitmapTop = paddingTop + mTextHeight + drawablePadding;
                    canvas.translate(bitmapLeft, bitmapTop);
                    mDrawable.draw(canvas);
                }
            }
        }

    }


    /**
     * 设置文字最大长度
     *
     * @param maxTextLength 文字最大长度（字数）
     */
    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
        if (originText != null) {
            setText(originText);
        } else {
            invalidateSelf();
        }
    }

    @Override
    public int getIntrinsicHeight() {

        int drawableHeight = 0;

        if (mDrawable != null) {

            drawableHeight = mDrawable.getIntrinsicHeight();
        }

        if (position == Position.LEFT || position == Position.RIGHT) {

            return Math.max(drawableHeight, mTextHeight) + paddingTop + paddingBottom;

        } else if (position == Position.TOP || position == Position.BOTTOM) {

            return drawableHeight + mTextHeight + paddingTop + drawablePadding + paddingBottom;

        } else {

            throw new IllegalArgumentException("position not known as one of the [ LEFT,TOP,RIGHT,BOTTOM ]!");
        }
    }


    @Override
    public int getIntrinsicWidth() {

        int drawableWidth = 0;
        int textWidth = 0;
        if (mDrawable != null) {
            drawableWidth = mDrawable.getIntrinsicWidth();
        }
        if (isNotEmpty(mText)) {
            textWidth = Math.round(mTextPaint.measureText(mText));
        }

        if (position == Position.LEFT || position == Position.RIGHT) {

            return drawableWidth + textWidth + paddingLeft + paddingRight + drawablePadding;
        } else if (position == Position.TOP || position == Position.BOTTOM) {

            return Math.max(drawableWidth, textWidth) + paddingLeft + paddingRight;

        } else {
            throw new IllegalArgumentException("position not known as one of the [ LEFT,TOP,RIGHT,BOTTOM ]!");
        }

    }

    @Override
    public void setAlpha(int alpha) {
        if (mTextPaint.getAlpha() != alpha) {
            mTextPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public int getOpacity() {
        return mTextPaint.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mTextPaint.getColorFilter() != cf) {
            mTextPaint.setColorFilter(cf);
            invalidateSelf();
        }
    }

    /**
     * 设置文字大小　sp
     *
     * @param textSize 文字大小单位
     */
    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, mResources.getDisplayMetrics()));
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = Math.round(fontMetrics.descent - fontMetrics.ascent);
        invalidateSelf();
    }


    /**
     * 设置文字颜色
     *
     * @param color 色值
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    /**
     * 设置图片在文字的方位（上下左右）
     *
     * @param position 位置
     */
    public void setImagePosition(Position position) {
        this.position = position;
    }


    private boolean isNotEmpty(String mText) {

        return mText != null && !mText.equals("");
    }

}
