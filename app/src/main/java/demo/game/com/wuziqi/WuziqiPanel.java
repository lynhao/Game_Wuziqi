package demo.game.com.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linhao on 16/3/30.
 */
public class WuziqiPanel extends View {
    private int mPanelWidth;
    private float mLineHight;
    private int MAX_LINE = 10;

    private int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;

    //白旗先手
    private boolean mIsWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<Point>();
    private ArrayList<Point> mBlackArray = new ArrayList<Point>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsGameOver) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            Point p = getValidPoint(x, y);
            if (mWhiteArray.contains(p) || mBlackArray.contains(p)) {
                return false;
            }
            if (mIsWhite) {
                mWhiteArray.add(p);
            } else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
        }
        return true;
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHight), (int) (y / mLineHight));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            width = heightSize;
        } else if (heightMode == MeasureSpec.UNSPECIFIED) {
            width = widthSize;
        }
        setMeasuredDimension(width, width);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHight = mPanelWidth * 1.0f / MAX_LINE;
        //  Log.d("tag", String.valueOf(mLineHight)+","+String.valueOf(mPanelWidth)); --> 65.6,656
        int pieceWidth = (int) (mLineHight * ratioPieceOfLineHeight);

        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInline(mWhiteArray);
        boolean blackWin = checkFiveInline(mBlackArray);
        if (whiteWin || blackWin) {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            String text = mIsWhiteWinner ? "白棋胜" : "黑棋胜";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkFiveInline(List<Point> points) {
        for (Point p : points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            //如果横向已经有5个直接返回true
            if (win) {

                return true;
            }
            win = checkVertical(x, y, points);
            if (win) {

                return true;
            }
            win = checkLeftDiagonal(x, y, points);
            if (win) {

                return true;
            }
            win = checkRightDiagonal(x, y, points);
            if (win) {

                return true;
            }

        }
        return false;
    }

    /**
     * 判断x y 位置的棋子，是否有横向有相邻的5个一致
     *
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //右
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //左斜 下部分
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //左斜 上部分
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //右斜 上部分
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x - i, y - i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        //左斜 下部分
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++) {
            if (points.contains(new Point(x + i, y + i))) {
                count++;
            } else {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) {
            return true;
        }
        return false;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece, (whitePoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHight, null);
        }
        for (int j = 0, n = mBlackArray.size(); j < n; j++) {
            Point blackPoint = mBlackArray.get(j);
            canvas.drawBitmap(mBlackPiece, (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2) * mLineHight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2) * mLineHight, null);
        }
    }

    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHight;

        for (int i = 0; i < MAX_LINE; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);
            canvas.drawLine(y, startX, y, endX, mPaint);
            // Log.d("tag",String.valueOf(startX)+",endX="+String.valueOf(endX)+",y="+String.valueOf(y));
            /**
             * 03-30 14:41:56.476 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=32
             03-30 14:41:56.476 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=98
             03-30 14:41:56.476 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=163
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=229
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=295
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=360
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=426
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=491
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=557
             03-30 14:41:56.477 16371-16371/demo.game.com.wuziqi D/tag: 32,endX=623,y=623
             */
        }
    }

    public void start(){
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
    }


    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER = "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER, mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
