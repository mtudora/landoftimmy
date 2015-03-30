package com.application.timmy.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.timmy.connectivity.BECommunicator;
import com.application.timmy.connectivity.BEResponseCode;
import com.application.timmy.model.PersonModel;
import com.application.timmy.storage.PreferencesManager;
import com.application.timmy.storage.TimmyData;
import com.application.timmy.utils.Constants;
import com.application.timmy.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timmy.application.com.landoftimmy.R;


public class OfficeMapView extends View implements BECommunicator {
    private Context context;
    private final static int INVALID_POINTER_ID = -1;
    private final static int MINIMUM_SIZE = 100;
  //  private final static float INITIAL_SCALE_FACTOR = 0.5f;
    private final static float INITIAL_SCALE_FACTOR = 1.0f;

    private final static int TOUCH_ZONE = 20;
            
    int viewWidth; //View's width.
    int viewHeight; //View's height.
    Bitmap mBitmap;
  
    Handler viewHandler;
    Runnable updateView;

    ScaleGestureDetector mScaleDetector;
    GestureDetector mGestureDetector;
    
    float mScaleFactor = INITIAL_SCALE_FACTOR;
    
    private float mTouchPosX;
    private float mTouchPosY;
    private float mLastTouchX = -1;
    private float mLastTouchY = -1;

    private Paint mGridPaint;
    private int mOffset;
    private int mCellDim;

    private int targetPicW, targetPicH;

    private int mActivePointerId = INVALID_POINTER_ID;

    private ArrayList<PersonModel> personsList;
    private ArrayList<PersonModel> lifeEventsList;

    private View personDetailsLayout;
    private View personMenuLayout;
    private TextView personDetailsName, personDetailsDep, personDetailsMorale, personDetailsHumour, personDetailsSkill;
    private TextView personMenuName;
    private ImageView personMenuImg;
    private ImageView personDetailsImg;

    private Button personDetailsButton;
    private Button simulateLifeEventButton;

    private Rect personDetailsRect;
    private Rect personMenuRect;

    private int personDetailsW, personDetailsH;

    private ArrayList<RectF> personsTouchZones;
    private  PersonModel selectedPerson, selectedPersonForDetails;
    private Rect clipBounds_canvas;
    private boolean mFingerMove;

    public OfficeMapView(Context context) {
        super(context);        
        initialise(context);
    }

    public OfficeMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    public OfficeMapView(Context context, AttributeSet attrs, int defStyle) {
        // super(context, attrs, defStyle);
        super(context, attrs);
        initialise(context);
    }

    private void initialise(Context context) {
        //Set thread
        //  getHolder().addCallback(this);
        //	setWillNotDraw(false);
        this.context = context;
     
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(new PointListener());
        
        viewHandler = new Handler();
        updateView = new Runnable() {
            @Override
            public void run() {
                invalidate();

            }
        };
        viewHandler.post(updateView);
        setFocusable(true);

        mCellDim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, Constants.OFFICE_CELL_DIM*Constants.OFFICE_HEIGHT,
                getResources().getDisplayMetrics());
        mOffset = (int) getResources().getDimension(R.dimen.office_map_padding);

        mBitmap = Bitmap.createBitmap(mCellDim + 2*mOffset, mCellDim + 2*mOffset, Bitmap.Config.ARGB_8888);
        mBitmap.setHasAlpha(true);

        mGridPaint = new Paint();
        mGridPaint.setAntiAlias(true);
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setColor(Color.BLACK);
        mGridPaint.setStrokeWidth(4f);

        targetPicW =  (int) getResources().getDimension(R.dimen.target_picture_width);
        targetPicH = (int) getResources().getDimension(R.dimen.target_picture_height);

        //Set a Rect for the 200 x 200 px center of a 400 x 400 px area
        personDetailsRect = new Rect();
        int rectW = (int) getResources().getDimension(R.dimen.person_details_popup_width);
        int rectH = (int) getResources().getDimension(R.dimen.person_details_popup_height);
        personDetailsRect.set(100, 100, rectW, rectH);

        LayoutInflater inflater = LayoutInflater.from(context);
        personDetailsLayout= inflater.inflate(R.layout.person_details, null, false);
        personDetailsName = (TextView) personDetailsLayout.findViewById(R.id.personDetailsName);
        personDetailsDep = (TextView) personDetailsLayout.findViewById(R.id.personDetailsDepartment);
        personDetailsHumour = (TextView) personDetailsLayout.findViewById(R.id.personDetailsHumour);
        personDetailsSkill = (TextView) personDetailsLayout.findViewById(R.id.personDetailsSkill);
        personDetailsMorale = (TextView) personDetailsLayout.findViewById(R.id.personDetailsMorale);
        personDetailsImg = (ImageView) personDetailsLayout.findViewById(R.id.personDetailsImg);

        //Measure the view at the exact dimensions (otherwise the text won't center correctly)
        int widthSpec = View.MeasureSpec.makeMeasureSpec(personDetailsRect.width(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(personDetailsRect.height(), View.MeasureSpec.EXACTLY);
        personDetailsLayout.measure(widthSpec, heightSpec);

        //Lay the view out at the rect width and height
        personDetailsLayout.layout(0, 0, personDetailsRect.width(), personDetailsRect.height());

        //Set a Rect for the 200 x 200 px center of a 400 x 400 px area
        personMenuRect = new Rect();
        rectW = (int) getResources().getDimension(R.dimen.person_details_popup_width);
        rectH = (int) getResources().getDimension(R.dimen.person_details_popup_height);
        personMenuRect.set(100, 100, rectW, rectH);

        inflater = LayoutInflater.from(context);
        personMenuLayout= inflater.inflate(R.layout.person_menu, null, false);

       // personMenuLayout = (LinearLayout) findViewById(R.id.personMenuLayout);
        personMenuName = (TextView) personMenuLayout.findViewById(R.id.personMenuName);
        personMenuImg = (ImageView) personMenuLayout.findViewById(R.id.personMenuImg);
        personDetailsButton = (Button) personMenuLayout.findViewById(R.id.personDetailsButton);
        simulateLifeEventButton = (Button) personMenuLayout.findViewById(R.id.simulateLifeEvent);

        //Measure the view at the exact dimensions (otherwise the text won't center correctly)
        widthSpec = View.MeasureSpec.makeMeasureSpec(personDetailsRect.width(), View.MeasureSpec.EXACTLY);
        heightSpec = View.MeasureSpec.makeMeasureSpec(personDetailsRect.height(), View.MeasureSpec.EXACTLY);
        personMenuLayout.measure(widthSpec, heightSpec);

        //Lay the view out at the rect width and height
        personMenuLayout.layout(0, 0, personMenuRect.width(), personMenuRect.height());

        setData();
    }


    public void setData(LinearLayout menuLayout) {
        personMenuLayout = menuLayout;
        personMenuName = (TextView) personMenuLayout.findViewById(R.id.personMenuName);
        personMenuImg = (ImageView) personMenuLayout.findViewById(R.id.personMenuImg);
    }

    public void setData() {
        personsTouchZones = new ArrayList<RectF>();

        if (personsList != null)
            for (int i = 0; i < personsList.size(); i++) {
                RectF touchZone = new RectF();
           /*     touchZone.top = (mOffset + personsList.get(i).getDeskY()*mCellDim - TOUCH_ZONE - targetPicW/2)/mScaleFactor;
                touchZone.left = (mOffset + personsList.get(i).getDeskX()*mCellDim - TOUCH_ZONE - targetPicH/2)/mScaleFactor;
                touchZone.right = (mOffset + personsList.get(i).getDeskX()*mCellDim + TOUCH_ZONE + targetPicW/2)/mScaleFactor;
                touchZone.bottom = (mOffset + personsList.get(i).getDeskY()*mCellDim + TOUCH_ZONE + targetPicH/2)/mScaleFactor;
*/
                touchZone.top = (mOffset + personsList.get(i).getDeskY()*mCellDim - TOUCH_ZONE - targetPicW/2) + mTouchPosY;
                touchZone.left = (mOffset + personsList.get(i).getDeskX()*mCellDim - TOUCH_ZONE - targetPicH/2)+ mTouchPosX;
                touchZone.right = (mOffset + personsList.get(i).getDeskX()*mCellDim + TOUCH_ZONE + targetPicW/2)+ mTouchPosX;
                touchZone.bottom = (mOffset + personsList.get(i).getDeskY()*mCellDim + TOUCH_ZONE + targetPicH/2)+ mTouchPosY;
                personsTouchZones.add(touchZone);
            }

    }

    /**
     * Set image bitmap
     * 
     * @param bitmap The bitmap to view and zoom into
     */
    public void setImage(Bitmap bitmap) {
        mBitmap = bitmap;

        mScaleFactor = INITIAL_SCALE_FACTOR; 
        mTouchPosX = 0;
        mTouchPosY = 0;
      //  mAspectQuotient.updateAspectQuotient(getWidth(), getHeight(), mBitmap.getWidth(), mBitmap
        //        .getHeight());
        //mAspectQuotient.notifyObservers();

        invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //This event-method provides the real dimensions of this custom view.
        viewWidth = w;
        viewHeight = h;
       
     /*   int size = (viewHeight > viewWidth) ? viewHeight : viewWidth;
        mBackgroundImage = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
        chartCanvas = new Canvas(mBackgroundImage);*/
         
    }

    @Override
    public synchronized boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
      
        mScaleDetector.onTouchEvent(ev);
        mGestureDetector.onTouchEvent(ev);
        
        
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {  
                
                mLastTouchX = (int) ev.getX();
                mLastTouchY = (int) ev.getY();
             
                mActivePointerId = ev.getPointerId(0);

                mFingerMove = true;

                break;
            }
            case MotionEvent.ACTION_MOVE: {  
                
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                
             // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mTouchPosX += dx;
                    mTouchPosY += dy;

                    invalidate();
                }else{
                    mFingerMove = false;
                }
                
                mLastTouchX = (int) x;
                mLastTouchY = (int) y;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = (int) ev.getX(newPointerIndex);
                    mLastTouchY = (int) ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                //selectedPerson = getTouchedPerson(ev.getX()/mScaleFactor + clipBounds_canvas.left, ev.getY()/mScaleFactor + clipBounds_canvas.top);
                   // selectedPerson = getTouchedPerson(ev.getX(), ev.getY());
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        clipBounds_canvas = canvas.getClipBounds();

        viewHandler.removeCallbacks(updateView);
        
        canvas.save();


        canvas.translate(mTouchPosX, mTouchPosY);
        canvas.scale(mScaleFactor, mScaleFactor, viewWidth >> 1, viewHeight >> 1);

       
        //repaint the whole surface with black
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, viewWidth/2 - mBitmap.getWidth()/2, viewHeight/2 - mBitmap.getHeight()/2, null);

        drawOfficeGrid(canvas);

        drawPersons(canvas);


        canvas.restore();
        
        viewHandler.postDelayed(updateView, 16);
    }

    public void  drawOfficeGrid(Canvas canvas){
        //draw the horizontal lines
        for (int i = 0 ; i <= Constants.OFFICE_WIDTH; i++){
            canvas.drawLine(mOffset, mOffset + i*mCellDim, Constants.OFFICE_WIDTH*mCellDim + mOffset, mOffset + i*mCellDim, mGridPaint);
            canvas.drawLine(mOffset + i*mCellDim, mOffset, mOffset + i*mCellDim, Constants.OFFICE_HEIGHT*mCellDim + mOffset, mGridPaint);
        }
    }  // --- end of draw_grid

    public void drawPersons(Canvas canvas){
       // ArrayList<PersonModel> persons = TimmyData.getInstance().getPersonsList();

        if (personsList == null || personsList.size() == 0){
            return;
        }

        for (PersonModel person : personsList){
            Bitmap personBitmap = person.getPhotoBitmap();

            if (personBitmap != null)
                canvas.drawBitmap(personBitmap, mOffset + person.getDeskX()*mCellDim - targetPicW/2, mOffset + person.getDeskY()*mCellDim - targetPicH/2, null);
        }

        for (PersonModel person : personsList){
            if (selectedPerson != null && selectedPerson.getId() == person.getId()) {
                //Translate the Canvas into position and draw it
                drawPersonMenu(canvas, person);
            }
            if (selectedPersonForDetails != null && selectedPersonForDetails.getId() == person.getId()) {
                //Translate the Canvas into position and draw it
                drawPersonDetails(canvas, person);
            }
        }
    }

    public void drawPersonMenu(Canvas canvas, PersonModel person){
        canvas.save();
        canvas.translate(mOffset + person.getDeskX() * mCellDim, mOffset + person.getDeskY() * mCellDim);
       // personMenuLayout.setVisibility(View.VISIBLE);
       // RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mOffset + person.getDeskX() * mCellDim, mOffset + person.getDeskY() * mCellDim);
       // personMenuLayout.setLayoutParams(lp);
        personMenuName.setText(person.getName());
        personMenuImg.setImageBitmap(person.getPhotoBitmap());

        personMenuLayout.draw(canvas);
        canvas.restore();
    }

    public void drawPersonDetails(Canvas canvas, PersonModel person){
        canvas.save();
        canvas.translate(mOffset + person.getDeskX() * mCellDim, mOffset + person.getDeskY() * mCellDim);
        personDetailsName.setText(person.getName());
        personDetailsDep.setText(person.getDepartment());
        personDetailsHumour.setText(String.valueOf(person.getHumour()));
        personDetailsMorale.setText(String.valueOf(person.getMorale()));
        personDetailsSkill.setText(String.valueOf(person.getSkill()));
        personDetailsImg.setImageBitmap(person.getPhotoBitmap());

        personDetailsLayout.draw(canvas);
        canvas.restore();
    }
    /**
     * @return If the touch point is on one of the pie titles
     */
    private PersonModel getTouchedPerson(PersonModel person, double xTouch, double yTouch) {
        if (personsTouchZones == null || personsList == null)
            return null;

       /* for (int i = 0; i < personsTouchZones.size(); i++) {
            if (personsTouchZones.get(i).contains((float) xTouch, (float) yTouch)) {
                if (selectedPerson == null || (selectedPerson != null && selectedPerson.getId() != personsList.get(i).getId()))
                    return personsList.get(i);
            }
        }*/

        for (int i = 0; i < personsList.size(); i++) {
            float startX = (mOffset + personsList.get(i).getDeskX()*mCellDim - targetPicW/2 + mTouchPosX)*mScaleFactor;
            float stopX = (mOffset + personsList.get(i).getDeskX()*mCellDim + targetPicW/2 + mTouchPosX)*mScaleFactor;
            float startY = (mOffset + personsList.get(i).getDeskY()*mCellDim - targetPicH/2 + mTouchPosY)*mScaleFactor;
            float stopY = (mOffset + personsList.get(i).getDeskY()*mCellDim + targetPicH/2 + mTouchPosY)*mScaleFactor;

            if (xTouch < stopX && xTouch > startX && yTouch > startY && yTouch < stopY) {
                if (person == null || (person != null && person.getId() != personsList.get(i).getId()))
                    return personsList.get(i);
            }
        }

        return null;
    }

    /**
     * @return If the touch point is on one of the pie titles
     */
    private boolean isInMenuPopupTouch(PersonModel person, double xTouch, double yTouch) {
        float startX = (mOffset + person.getDeskX() * mCellDim + mTouchPosX)*mScaleFactor;
        float stopX = (mOffset + person.getDeskX() * mCellDim + mTouchPosX + personMenuRect.width())*mScaleFactor;
        float startY = (mOffset + person.getDeskY() * mCellDim + mTouchPosY)*mScaleFactor;
        float stopY = (mOffset + person.getDeskY() * mCellDim + mTouchPosY + personMenuRect.height())*mScaleFactor;

        if (xTouch < stopX && xTouch > startX && yTouch > startY && yTouch < stopY) {
                return true;
        }

        return false;
    }


    private boolean isPersonDetailsPressed(PersonModel person, double xTouch, double yTouch) {
        float startX = (mOffset + person.getDeskX() * mCellDim + mTouchPosX)*mScaleFactor;
        float stopX = (mOffset + person.getDeskX() * mCellDim + mTouchPosX + personMenuRect.width())*mScaleFactor;
        float startY = (mOffset + person.getDeskY() * mCellDim + mTouchPosY)*mScaleFactor;
        float stopY = (mOffset + person.getDeskY() * mCellDim + mTouchPosY + personMenuRect.height())*mScaleFactor;

        float buttonLeft = personDetailsButton.getLeft();
        float buttonRight = personDetailsButton.getRight();
        float buttonTop = personDetailsButton.getTop();
        float buttonButtom = personDetailsButton.getBottom();

        System.out.println(buttonLeft+buttonTop+buttonRight+buttonButtom);
        if (xTouch < (startX + buttonRight) && xTouch > (startX + buttonLeft) && yTouch > (stopY - buttonButtom) && yTouch < (stopY - buttonTop)) {
            return true;
        }

        return false;
    }


    private boolean isLifeEventPressed(PersonModel person, double xTouch, double yTouch) {
        float startX = (mOffset + person.getDeskX() * mCellDim + mTouchPosX)*mScaleFactor;
        float stopX = (mOffset + person.getDeskX() * mCellDim + mTouchPosX + personMenuRect.width())*mScaleFactor;
        float startY = (mOffset + person.getDeskY() * mCellDim + mTouchPosY)*mScaleFactor;
        float stopY = (mOffset + person.getDeskY() * mCellDim + mTouchPosY + personMenuRect.height())*mScaleFactor;

        float buttonLeft = simulateLifeEventButton.getLeft();
        float buttonRight = simulateLifeEventButton.getRight();
        float buttonTop = simulateLifeEventButton.getTop();
        float buttonButtom = simulateLifeEventButton.getBottom();

        System.out.println(buttonLeft+buttonTop+buttonRight+buttonButtom);
        if (xTouch < (startX + buttonRight) && xTouch > (startX + buttonLeft) && yTouch > (stopY - buttonButtom) && yTouch < (stopY - buttonTop)) {
            return true;
        }

        return false;
    }


    @Override
    public void onDataItemUpdated(Object data) {
    }

    @Override
    public void onDataArrayUpdated(Object[] data) {

    }

    @Override
    public void onDataListUpdated(List<?> data) {
        if (data != null && data instanceof  ArrayList){
            personsList = (ArrayList<PersonModel>) data;

            setData();
        }

    }

    @Override
    public void onDataFailed(BEResponseCode responseCode) {

    }

    /* Internal classes */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }



    private class PointListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (selectedPerson != null){
                //check if the touch was in the menu box
                boolean isInsideMenu = isInMenuPopupTouch(selectedPerson, e.getX(), e.getY());
                if (isInsideMenu){
                    //check if one of the buttons was pressed
                    if (isPersonDetailsPressed(selectedPerson, e.getX(), e.getY())){
                        selectedPersonForDetails = selectedPerson;
                        //selectedPerson = null;
                        return super.onSingleTapUp(e);
                    }else
                    if (isLifeEventPressed(selectedPerson, e.getX(), e.getY())){
                        Intent newIntent = new Intent(context, ChooseLifeEventActivity.class);
                        newIntent.putExtra("chosen_person", (Parcelable) selectedPerson);
                        context.startActivity(newIntent);

                    }
                }else{
                    selectedPerson = getTouchedPerson(selectedPerson, e.getX(), e.getY());
                }
            }else {
                selectedPerson = getTouchedPerson(selectedPerson, e.getX(), e.getY());
            }

            selectedPersonForDetails = null;


            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            selectedPersonForDetails = getTouchedPerson(selectedPersonForDetails, e.getX(), e.getY());

            super.onLongPress(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            mScaleFactor = 1.f;
            invalidate();

            return super.onDoubleTap(e);
        }
    }
    

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // this gets called, but with a canvas sized after the padding.
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    /**
     * Used in Android measurement system to enable onMeasure() to compute the correct size for the view
     *
     * @param measureSpec provided measure spec. used to obtain the provided size and measurement mode
     * @return the calculated correct size, based on the provided measure spec.
     */
    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED || (specMode != MeasureSpec.UNSPECIFIED && specSize < MINIMUM_SIZE)) {
            return MINIMUM_SIZE;
        } else {
            return resolveSize(specSize, specSize);
        }
    }

    public static float applyDimension(int unit, float value,
                                       DisplayMetrics metrics)
    {
        switch (unit) {
            case TypedValue.COMPLEX_UNIT_PX:
                return value;
            case TypedValue.COMPLEX_UNIT_DIP:
                return value * metrics.density;
            case TypedValue.COMPLEX_UNIT_SP:
                return value * metrics.scaledDensity;
            case TypedValue.COMPLEX_UNIT_PT:
                return value * metrics.xdpi * (1.0f/72);
            case TypedValue.COMPLEX_UNIT_IN:
                return value * metrics.xdpi;
            case TypedValue.COMPLEX_UNIT_MM:
                return value * metrics.xdpi * (1.0f/25.4f);
        }
        return 0;
    }
}
