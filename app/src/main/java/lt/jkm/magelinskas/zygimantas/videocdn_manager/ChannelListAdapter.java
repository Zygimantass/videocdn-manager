package lt.jkm.magelinskas.zygimantas.videocdn_manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;
import java.util.ArrayList;

/**
 * Created by Zygimantas on 1/17/2016.
 */
public class ChannelListAdapter extends ArrayAdapter<String>{
    private final Context context;
    private final ArrayList<String> values;

    public ChannelListAdapter(Context context, ArrayList<String> values)
    {
        super(context, R.layout.serverlistlayout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.channellistlayout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(values.get(position));
        String s= values.get(position);

        Log.e("asd", "asd");
        return rowView;
    }
}
