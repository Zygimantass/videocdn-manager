package lt.jkm.magelinskas.zygimantas.flussonicstatus;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Zygimantas on 1/29/2016.
 */
public class ChannelsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    public ArrayList<String> values;
    public ChannelListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_servers, container, false);
        return rootView;
    }

    public ListView lv;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        String serverName = this.getArguments().getString("serverName");
        super.onActivityCreated(savedInstanceState);
        values = new ArrayList<>();
        adapter = new ChannelListAdapter(getActivity(), values);
        setListAdapter(adapter);
        lv = getListView();
        lv.setOnItemClickListener(this);
        APIHandler.loadChannelList(adapter, serverName, getContext());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
