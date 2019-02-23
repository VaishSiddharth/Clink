package customfonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class BrushFlowFont extends androidx.appcompat.widget.AppCompatTextView {
    
    public BrushFlowFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public BrushFlowFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public BrushFlowFont(Context context) {
        super(context);
        init();
    }
    
    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/blowbrush.ttf");
            setTypeface(tf);
        }
    }
    
}