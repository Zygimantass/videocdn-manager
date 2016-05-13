package lt.jkm.magelinskas.zygimantas.videocdn_manager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;
import java.io.Console;
import java.util.regex.Pattern;

/**
 * Created by Zygimantas on 1/17/2016.
 */
public class EditServerFragment extends Fragment implements FragmentOnBackClickInterface{
    private View rootView;
    private static final Pattern PATTERNIP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private String server;

    private static Pattern pDomainNameOnly;
    private static final String DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
    static {
        pDomainNameOnly = Pattern.compile(DOMAIN_NAME_PATTERN);
    }
    public static boolean isValidDomainName(String domainName) {
        return pDomainNameOnly.matcher(domainName).find();
    }

    public EditServerFragment() {
    }

    public static boolean validateIP(final String ip)
    {
        return PATTERNIP.matcher(ip).matches() || isValidDomainName(ip);
    }

    public void editServer()
    {
        DBHandler dbh = new DBHandler(getContext(), null, null, 1);
        EditText serverName = (EditText) rootView.findViewById(R.id.serverName);
        EditText serverIP = (EditText) rootView.findViewById(R.id.ipAddress);
        EditText serverPortEdit = (EditText) rootView.findViewById(R.id.serverPort);
        String username = ((EditText) rootView.findViewById(R.id.username)).getText().toString();
        String password = ((EditText) rootView.findViewById(R.id.password)).getText().toString();
        int serverPort = Integer.parseInt(serverPortEdit.getText().toString());
        if (!validateIP(serverIP.getText().toString()))
        {
            serverIP.setError("Invalid IP or domain");
            return;
        }
        if (serverPort < 1 || serverPort > 65535)
        {
            serverPortEdit.setError("Invalid port. The port has to be inbetween 0-65535");
            return;
        }

        Server newServer = new Server(serverName.getText().toString(), serverIP.getText().toString(), serverPortEdit.getText().toString(), username, password);
        dbh.addServer(newServer);
        dbh.deleteServer(this.server);
        Server addedServer = dbh.findServer(serverName.getText().toString());
        Log.e("Server added", addedServer.getServerName());
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new ServersFragment()).commit();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_editserver, container, false);
        Button btn = (Button) rootView.findViewById(R.id.editServer);
        DBHandler dbh = new DBHandler(getContext(), null, null, 1);

        EditText serverName = (EditText) rootView.findViewById(R.id.serverName);
        EditText serverIP = (EditText) rootView.findViewById(R.id.ipAddress);
        EditText serverPortEdit = (EditText) rootView.findViewById(R.id.serverPort);
        EditText username = (EditText) rootView.findViewById(R.id.username);
        EditText password = (EditText) rootView.findViewById(R.id.password);

        this.server = this.getArguments().getString("serverName");

        Server oldServer = dbh.findServer(this.server);

        serverName.setText(oldServer.getServerName());
        serverIP.setText(oldServer.getServerIP());
        serverPortEdit.setText(oldServer.getPort().toString());
        username.setText(oldServer.getUsername());
        password.setText(oldServer.getPassword());
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editServer();
            }
        });
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick () {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        ServersFragment frag = new ServersFragment();

        fragmentTransaction.replace(R.id.content_frame, frag);
        fragmentTransaction.commit();
    }
}
