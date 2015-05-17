package cc.chatclient;

import android.annotation.SuppressLint;
import android.widget.BaseAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.List;

public class MessagesListAdapter extends BaseAdapter {
    private Context context;
    private List<Message> messages;

    public MessagesListAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = messages.get(position);

        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if(messages.get(position).isSelf()) {
            convertView = messageInflater.inflate(R.layout.msg_right,null);
        } else {
            convertView = messageInflater.inflate(R.layout.msg_left,null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        lblFrom.setText(message.getUser());
        txtMsg.setText(message.getMessage());

        return convertView;
    }
}
