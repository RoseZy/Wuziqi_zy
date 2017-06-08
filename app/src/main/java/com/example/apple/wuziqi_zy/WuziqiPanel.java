package com.example.apple.wuziqi_zy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class WuziqiPanel extends View {

    private  int mPanelwidth;
    private  float mLineHeigth;
    private static final int MAX_LINE=10;

    public static final int MAX_PIECES_NUMBER=MAX_LINE*MAX_LINE;

    private static final int MAX_COUNT_IN_LINE=5;

    private Paint mpaint=new Paint();

    private Bitmap  mWhitePiece;
    private Bitmap  mBlackPiece;

    private float ratioPieceOfLineHeight=3 * 1.0f /4;

    /*白棋先手，当前轮到白棋*/
    private boolean mIsWhite=true;
    private ArrayList<Point> mWhiteArray=new ArrayList<>();
    private ArrayList<Point> mBlackArray=new ArrayList<>();

    private boolean mIsGameOver;
    private boolean mIsWhiteWinner;


    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
       mpaint.setColor(Color.BLACK);
        mpaint.setAntiAlias(true);
        mpaint.setDither(true);
        mpaint.setStyle(Paint.Style.STROKE);

        mWhitePiece= BitmapFactory.decodeResource(getResources(), R.drawable.b);
        mBlackPiece= BitmapFactory.decodeResource(getResources(), R.drawable.h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize =MeasureSpec.getSize(widthMeasureSpec);
        int widthMode =MeasureSpec.getMode(widthMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode =MeasureSpec.getMode(heightMeasureSpec);

        int width =Math.min(widthSize,heightSize);

        if(widthMode == MeasureSpec.UNSPECIFIED){
                width=heightSize;
        }else if(heightMode == MeasureSpec.UNSPECIFIED){
                width=widthSize;
        }
        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelwidth= w;
        mLineHeigth=mPanelwidth*1.0f / MAX_LINE;
        int pieceWide= (int) (mLineHeigth*ratioPieceOfLineHeight);
        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,pieceWide,pieceWide,false);
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,pieceWide,pieceWide,false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        if(mIsGameOver) return false;
        int action=event.getAction();
        if(action == MotionEvent.ACTION_UP)
        {

            int x=(int)event.getX();
            int y=(int)event.getY();

            Point p=getValidPoain(x,y);

            if(mWhiteArray.contains(p) || mBlackArray.contains(p))
            {
                    return false;
            }

            if(mIsWhite)
            {
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }
            invalidate();
            mIsWhite = !mIsWhite;
            return true;

        }

        return true;
    }

    private Point getValidPoain(int x, int y)
    {
        return new Point((int)(x/mLineHeigth), (int)(y/mLineHeigth));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        drawBoard(canvas);
        drawPieces(canvas);
        checkGameOver();

    }

    private void checkGameOver()
    {
       boolean whiteWin= checkFiveInLine(mWhiteArray);
       boolean backeWin= checkFiveInLine(mBlackArray);

        if(whiteWin || backeWin)
        {
            mIsGameOver=true;
            mIsWhiteWinner=whiteWin;

            String text=mIsWhiteWinner?"亲，白棋胜利了哦":"亲，黑棋胜利了哦";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkFiveInLine(List<Point> points)
    {
        for(Point p:points)
        {
            int x=p.x;
            int y=p.y;

          boolean win =  checkHorizontal(x , y , points);
            if (win)return true;

            win=checkVertical (x,y,points);
            if (win)return true;

            win=checkLeftDiagonal(x,y,points);
            if (win)return true;

            win=checkRightDiagonal(x,y,points);
            if (win)return true;

        }

        return false;
    }

    public static boolean checkIsFull(int number) {
        if(number== WuziqiPanel.MAX_PIECES_NUMBER) {
            return true;
        }
        return false;
    }
    /*判断线，y位置的棋子，是否横向有相邻的五个一致。*/
    private static boolean checkHorizontal(int x, int y, List<Point> piecesArray) {
        int count=1;
        /*判断左右*/
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x-i,y))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x+i,y))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }

    private static boolean checkVertical(int x, int y, List<Point> piecesArray) {
        int count=1;
       /*判断上下*/
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;


        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }

        /*判断左斜*/
    private static boolean checkLeftDiagonal(int x, int y, List<Point> piecesArray) {
        int count=1;

        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x-i,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //判断右斜
        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x+i,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }


    private static boolean checkRightDiagonal(int x, int y, List<Point> piecesArray) {
        int count=1;

        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x-i,y+i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;


        for(int i=1;i<MAX_COUNT_IN_LINE;i++) {
            if(piecesArray.contains(new Point(x+i,y-i))) {
                count++;
            } else {
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        return false;
    }
    private void drawPieces(Canvas canvas)
    {
        for(int i=0, n = mWhiteArray.size(); i< n ; i++)
        {
            Point whitePoint=mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeigth,
                    (whitePoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeigth,null);
        }

        for(int i=0, n = mBlackArray.size(); i< n ; i++)
        {
            Point blackPoint=mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-ratioPieceOfLineHeight)/2)*mLineHeigth,
                    (blackPoint.y+(1-ratioPieceOfLineHeight)/2)*mLineHeigth,null);
        }
    }

    private void drawBoard(Canvas canvas)
    {
        int w=mPanelwidth;
        float lineHeigt=mLineHeigth;

        for(int i= 0;i< MAX_LINE ;i++ )
        {
            int startx =(int)(lineHeigt/2);
            int endx  =(int)(w-lineHeigt/2);
            int y =(int) ((0.5 + i)*lineHeigt);
            canvas.drawLine(startx,y,endx,y,mpaint);
            canvas.drawLine(y,startx,y,endx,mpaint);

        }

    }

    public void start()
    {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();

    }

    private static  final String  INSTANCE ="instance";
    private static  final String  INSTANCE_GAME_OVER="instance_game_over";
    private static  final String  INSTANCE_WHITE_ARRAY="instance_white_array";
    private static  final String  INSTANCE_BLACK_ARRAY="instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if(state instanceof Bundle)
        {
            Bundle bundle=(Bundle) state;
            mIsGameOver=bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray=bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getBundle(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
