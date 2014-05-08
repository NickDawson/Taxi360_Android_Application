package sg.edu.astar.ihpc.taxidriver.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class RectView extends View {
	private Rect rect;
	private Paint rectColor;

	public RectView(Context context, int left, int top, int right, int bottom) {
		super(context);
		// TODO Auto-generated constructor stub
		rect = new Rect(left, top, right, bottom);
		rectColor = new Paint();
		rectColor.setColor(Color.WHITE);
		rectColor.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	public void changeColor(int color) {
		rectColor.setColor(color);
		super.invalidate();
	}
	
	public void resetColor() {
		rectColor.setColor(Color.WHITE);
		super.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawRect(rect, rectColor);
	}

}
