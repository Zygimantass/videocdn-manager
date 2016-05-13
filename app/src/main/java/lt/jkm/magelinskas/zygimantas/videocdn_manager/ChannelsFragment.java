package lt.jkm.magelinskas.zygimantas.videocdn_manager;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;
import java.util.ArrayList;

/**
 * Created by Zygimantas on 1/29/2016.
 */
public class ChannelsFragment extends ListFragment implements FragmentOnBackClickInterface {

    public ArrayList<String> values;
    public ChannelListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_servers, container, false);

        return rootView;
    }

    private String serverName;
    public ListView lv;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        FloatingActionButton fabBtn = (FloatingActionButton) getView().findViewById(R.id.fab);
        fabBtn.setVisibility(View.INVISIBLE);
        serverName = this.getArguments().getString("serverName");
        super.onActivityCreated(savedInstanceState);
        values = new ArrayList<>();
        adapter = new ChannelListAdapter(getActivity(), values);
        setListAdapter(adapter);
        lv = getListView();
        lv.setOnItemClickListener(this);
        APIHandler.loadChannelList(adapter, serverName, getContext());
    }
    @Override
    public void onClick () {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        ServersFragment frag = new ServersFragment();

        fragmentTransaction.replace(R.id.content_frame, frag);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        String[] actions = new String[3];
        actions[0] = "Preview";
        actions[1] = "Restart";
        actions[2] = "Stream live";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick an action!")
                .setItems(actions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Bundle bundle = new Bundle();
                        switch (which) {
                            case 0:

                                bundle.putString("channel", values.get(position));
                                bundle.putString("serverName", serverName);

                                PreviewFragment frag = new PreviewFragment();
                                frag.setArguments(bundle);

                                fragmentTransaction.replace(R.id.content_frame, frag);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                APIHandler.restartChannel(values.get(position), serverName, getContext(), getActivity()); break;
                            case 2:
                                bundle.putString("channel", values.get(position));
                                bundle.putString("serverName", serverName);

                                LiveFragment liveFrag = new LiveFragment();
                                liveFrag.setArguments(bundle);

                                fragmentTransaction.replace(R.id.content_frame, liveFrag);
                                fragmentTransaction.commit();
                            default: break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
