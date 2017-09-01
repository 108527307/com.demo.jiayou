package myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * 
 * 
 * 这个方法是重写GridView，使GridView没有滑动效果
 * **/
public class NoScrollGridView extends GridView {

	public NoScrollGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public NoScrollGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
