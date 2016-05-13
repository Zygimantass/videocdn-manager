package lt.jkm.magelinskas.zygimantas.videocdn_manager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;

/**
 * Created by Zygimantas on 1/16/2016.
 */
public class ServersFragment extends ListFragment implements AdapterView.OnItemClickListener{

    public ArrayList<String> values;
    public ArrayAdapter<String> adapter;
    private AlertDialog oldDialog;


    public ServersFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_servers, container, false);
        FloatingActionButton fabBtn = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabBtn.setVisibility(View.VISIBLE);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new AddServerFragment()).commit();
            }
        });
        return rootView;
    }

    public ListView lv;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        values = new ArrayList<String>();
        DBHandler dbh = new DBHandler(getContext(), null, null, 1);
        for (Server s : dbh.listServers())
        {
            values.add(s.getServerName());
        }
        adapter = new ServerListAdapter(getActivity(), values);
        setListAdapter(adapter);
        lv = getListView();
        lv.setOnItemClickListener(this);
    }

    public void editServer (final int position) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("serverName", values.get(position));

        EditServerFragment frag = new EditServerFragment();
        frag.setArguments(bundle);

        fragmentTransaction.replace(R.id.content_frame, frag);
        fragmentTransaction.commit();

    }


    public void onItemClick(AdapterView<?> parent, View view, final int position,
                            long id) {
        if (true) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage("Loading").setTitle("Server info");

            builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // show details
                    DBHandler dbh = new DBHandler(getContext(), null, null, 1);
                    dbh.deleteServer(values.get(position));
                    values.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editServer(position);
                }
            });

            AlertDialog oldDialog = builder.create();
            oldDialog.setMessage("");
            oldDialog.show();
            DBHandler dbh = new DBHandler(getContext(), null, null, 1);
            APIHandler.loadInfoDialog(builder, oldDialog, values.get(position), getContext(), getFragmentManager(), values, adapter, position);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(oldDialog!= null)
            oldDialog.dismiss();
    }

}
