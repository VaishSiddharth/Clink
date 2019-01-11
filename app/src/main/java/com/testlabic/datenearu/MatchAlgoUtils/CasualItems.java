package com.testlabic.datenearu.MatchAlgoUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.testlabic.datenearu.R;

public class CasualItems extends AppCompatActivity {
    
    private TextView option1, option2, option3, option4, choice1, choice2, choice3, choice4;
    public CharSequence dragData;
    Typeface normal, bold;
    Button continu;
    int count =0;
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casual_items);
        continu = findViewById(R.id.continu);
        bold = Typeface.createFromAsset(getAssets(), "fonts/SF-Pro-Display-Bold.otf");
        normal = Typeface.createFromAsset(getAssets(), "fonts/SF-Pro-Display-Medium.otf");
        //get both sets of text views
        
        //views to drag
        option1 = (TextView)findViewById(R.id.option_1);
        option2 = (TextView)findViewById(R.id.option_2);
        option3 = (TextView)findViewById(R.id.option_3);
        option4 = (TextView)findViewById(R.id.option_4);
      
        //views to drop onto
        choice1 = (TextView)findViewById(R.id.choice_1);
        choice2 = (TextView)findViewById(R.id.choice_2);
        choice3 = (TextView)findViewById(R.id.choice_3);
        choice4 = (TextView)findViewById(R.id.choice_4);
      
        //set touch listeners
        option1.setOnTouchListener(new CasualItems.ChoiceTouchListener());
        option2.setOnTouchListener(new CasualItems.ChoiceTouchListener());
        option3.setOnTouchListener(new CasualItems.ChoiceTouchListener());
        option4.setOnTouchListener(new CasualItems.ChoiceTouchListener());
      
        
        //set drag listeners
        choice1.setOnDragListener(new CasualItems.ChoiceDragListener());
        choice2.setOnDragListener(new CasualItems.ChoiceDragListener());
        choice3.setOnDragListener(new CasualItems.ChoiceDragListener());
        choice4.setOnDragListener(new CasualItems.ChoiceDragListener());
       
        
        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count==6)
                    Toast.makeText(CasualItems.this, "All done go! "+count, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * ChoiceTouchListener will handle touch events on draggable views
     *
     */
    private final class ChoiceTouchListener implements View.OnTouchListener {
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
                 * - clip data could be set to pass data as part of drag
                 * - shadow can be tailored
                 */
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
        
    }
    
    /**
     * DragListener will handle dragged views being dropped on the drop area
     * - only the drop action will have processing added to it as we are not
     * - amending the default behavior for other parts of the drag process
     *
     */
    @SuppressLint("NewApi")
    private class ChoiceDragListener implements View.OnDragListener {
        
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    
                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();
                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;
                    //view being dragged and dropped
                    TextView dropped = (TextView) view;
                    //checking whether first character of dropTarget equals first character of dropped
                    //if(dropTarget.getText().toString().charAt(0) == dropped.getText().toString().charAt(0))
                {
                    //stop displaying the view where it was before it was dragged
                    view.setVisibility(View.INVISIBLE);
                    //update the text in the target view to reflect the data being dropped
                    dropTarget.setText(dropped.getText().toString()+" - "+dropTarget.getText().toString());
                    count++;
                    //make it bold to highlight the fact that an item has been dropped
                    dropTarget.setTypeface(bold);
                    //if an item has already been dropped here, there will be a tag
                    Object tag = dropTarget.getTag();
                    //if there is already an item here, set it back visible in its original place
                    if(tag!=null)
                    {
                        //the tag is the view id already dropped here
                        int existingID = (Integer)tag;
                        //set the original view visible again
                        findViewById(existingID).setVisibility(View.VISIBLE);
                    }
                    //set the tag in the target view being dropped on - to the ID of the view being dropped
                    dropTarget.setTag(dropped.getId());
                    //remove setOnDragListener by setting OnDragListener to null, so that no further drag & dropping on this TextView can be done
                    dropTarget.setOnDragListener(null);
                }
                //  else
                //displays message if first character of dropTarget is not equal to first character of dropped
                //  Toast.makeText(ArrangingItems.this, dropTarget.getText().toString() + "is not " + dropped.getText().toString(), Toast.LENGTH_LONG).show();
                break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }
    }
    
    public void reset(View view)
    {
        count =0;
        option1.setVisibility(TextView.VISIBLE);
        option2.setVisibility(TextView.VISIBLE);
        option3.setVisibility(TextView.VISIBLE);
        option4.setVisibility(TextView.VISIBLE);
      
        
        choice1.setText("A) The eye catcher:Physical features");
        choice2.setText("B) I am Sapiosexual (intelligence n intellectual ability)");
        choice3.setText("C) Must be helpful, caring and compassionate towards you and society");
        choice4.setText("D) Has an ambition in life and is serious towards it");
     
        choice1.setTag(null);
        choice2.setTag(null);
        choice3.setTag(null);
        choice4.setTag(null);
    
        
        
        choice1.setTypeface(normal);
        choice2.setTypeface(normal);
        choice3.setTypeface(normal);
        choice4.setTypeface(normal);
     
        
        choice1.setOnDragListener(new CasualItems.ChoiceDragListener());
        choice2.setOnDragListener(new CasualItems.ChoiceDragListener());
        choice3.setOnDragListener(new CasualItems.ChoiceDragListener());
        choice4.setOnDragListener(new CasualItems.ChoiceDragListener());

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.Ar, menu);
        return true;
    }
    
}

