package it.feio.android.omninotes.models;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SketchView extends View implements OnTouchListener {

	private static final float TOUCH_TOLERANCE = 4;
	private static final float STROKE_WIDTH = 10;

	private Bitmap bitmap;
	private Canvas m_Canvas;
	private Path m_Path;
	private Paint m_Paint;
	ArrayList<Pair> paths = new ArrayList<Pair>();
	private float mX, mY;
	private int width, height;
	
	public static boolean isEraserActive = false;
	

	public SketchView(Context context, AttributeSet attr) {
		super(context, attr);
		setFocusable(true);
		setFocusableInTouchMode(true);

		setBackgroundColor(Color.WHITE);

		this.setOnTouchListener(this);
	}

	private void onCanvasInitialization() {
		m_Paint = new Paint();
		m_Paint.setAntiAlias(true);
		m_Paint.setDither(true);
		m_Paint.setColor(Color.parseColor("#000000"));
		m_Paint.setStyle(Paint.Style.STROKE);
		m_Paint.setStrokeJoin(Paint.Join.ROUND);
		m_Paint.setStrokeCap(Paint.Cap.ROUND);
		m_Paint.setStrokeWidth(STROKE_WIDTH);

		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		m_Canvas = new Canvas(bitmap);

		m_Path = new Path();
		Paint newPaint = new Paint(m_Paint);
		paths.add(new Pair<Path, Paint>(m_Path, newPaint));
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = View.MeasureSpec.getSize(widthMeasureSpec);
	    height = View.MeasureSpec.getSize(heightMeasureSpec);
	    
		onCanvasInitialization();
	    
	    setMeasuredDimension(width, height);
	}
	
	

	public boolean onTouch(View arg0, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (Pair p : paths) {
			canvas.drawPath((Path) p.first, (Paint) p.second);
		}
	}

	private void touch_start(float x, float y) {

		if (isEraserActive) {
			m_Paint.setColor(Color.WHITE);
			m_Paint.setStrokeWidth(STROKE_WIDTH);
			Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
			paths.add(new Pair<Path, Paint>(m_Path, newPaint));
		} else {
			m_Paint.setColor(Color.BLACK);
			m_Paint.setStrokeWidth(STROKE_WIDTH);
			Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
			paths.add(new Pair<Path, Paint>(m_Path, newPaint));
		}

		m_Path.reset();
		m_Path.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			m_Path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touch_up() {
		m_Path.lineTo(mX, mY);

		// commit the path to our offscreen
		m_Canvas.drawPath(m_Path, m_Paint);

		// kill this so we don't double draw
		m_Path = new Path();
		Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
		paths.add(new Pair<Path, Paint>(m_Path, newPaint));
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}