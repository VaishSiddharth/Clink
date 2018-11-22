package com.testlabic.datenearu.ChatUtils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.testlabic.datenearu.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;
    private String myUid;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    public ChatListAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;
        myUid = FirebaseAuth.getInstance().getUid();
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        v = LayoutInflater.from(context).inflate(R.layout.empty_view, null, false);
        ChatMessage message = chatMessages.get(position);
        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if(message.getSentFrom()!=null) {
            if (!message.getSentFrom().equals(myUid)) {

                /*
                Other party's message came
                 */

                if (convertView == null) {
                    v = LayoutInflater.from(context).inflate(R.layout.sample_msg_received, null, false);
                    holder1 = new ViewHolder1();
                    holder1.messageTextView = (TextView) v.findViewById(R.id.textview_message);
                    holder1.timeTextView = (TextView) v.findViewById(R.id.textview_time);
                    //holder1.sendersName = v.findViewById(R.id.sendersName);
                    v.setTag(holder1);
                } else {
                    v = convertView;
                    holder1 = (ViewHolder1) v.getTag();

                }
                      //holder1.sendersName.setVisibility(View.GONE);
                holder1.messageTextView.setText(Html.fromHtml(Emoji.replaceEmoji(message.getMessage(),
                        holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16))
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));
                holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getSendingTime()));


            } else if (message.getSentFrom().equals(myUid)) {

                if (convertView == null) {
                    v = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);

                    holder2 = new ViewHolder2();
                    holder2.messageTextView = (TextView) v.findViewById(R.id.textview_message);
                    holder2.timeTextView = (TextView) v.findViewById(R.id.textview_time);
                   // holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                    v.setTag(holder2);

                } else {
                    v = convertView;
                    holder2 = (ViewHolder2) v.getTag();

                }

                holder2.messageTextView.setText(Html.fromHtml(Emoji.replaceEmoji(message.getMessage(),
                        holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16))
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

                //holder2.messageTextView.setText(message.getMessageText());
                 holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getSendingTime()));


              /*  if (!message.getMessageDelivered()) {
                    holder2.messageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.message_got_receipt_from_target));
                } else if (message.getMessageDelivered()) {
                    holder2.messageStatus.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.message_got_receipt_from_server));
                }*/

                /*
                compare dates here
                 */



            }

            long previousTs = 0;
            if(position>=1){
                ChatMessage pm = chatMessages.get(position-1);
                previousTs = pm.getSendingTime();
            }

            TextView date = v.findViewById(R.id.date);

            setTimeTextVisibility(message.getSendingTime(), previousTs, date);


        }

        return v;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private void setTimeTextVisibility(long ts1, long ts2, TextView timeText){

        if(ts2==0){
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(getDate(ts1));
        }else {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTimeInMillis(ts1);
            cal2.setTimeInMillis(ts2);

            boolean sameMonth = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)&&cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            if(sameMonth){
                timeText.setVisibility(View.GONE);
                timeText.setText("");
            }else {
                timeText.setVisibility(View.VISIBLE);
                timeText.setText(getDate(ts1));
            }

        }
    }



    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if(message!=null) {
            if(message.getSentFrom()!=null)
            if (message.getSentFrom().equals(myUid)) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private class ViewHolder1 {
        public TextView messageTextView;
        public TextView timeTextView;
        public TextView sendersName;
    }

    private class ViewHolder2 {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;
    }
}
