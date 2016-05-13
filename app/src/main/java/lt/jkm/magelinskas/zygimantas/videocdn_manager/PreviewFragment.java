package lt.jkm.magelinskas.zygimantas.videocdn_manager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;

/**
 * Created by Zygimantas on 5/12/2016.
 */
public class PreviewFragment extends Fragment implements FragmentOnBackClickInterface{

    private String serverName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_preview, container, false);
        final String finalServerName = this.getArguments().getString("serverName");
        serverName = this.getArguments().getString("serverName");
        String channel = this.getArguments().getString("channel");

        DBHandler dbh = new DBHandler(getContext(), null, null, 1);
        Server srv = dbh.findServer(serverName);

        final TextView errorView = (TextView) rootView.findViewById(R.id.previewError);
        ((ImageView) rootView.findViewById(R.id.previewView)).setAdjustViewBounds(true);
        MainActivity.imageLoader.displayImage("https://" + srv.getServerIP() + ":" + srv.getPort() + "/" + channel + "/preview.jpg", (ImageView) rootView.findViewById(R.id.previewView), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                errorView.setText("This stream doesn't have preview enabled.");
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        Log.e("dd", "https://"+srv.getServerIP() + ":" + srv.getPort() + "/" + channel + "/preview.jpg");
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onClick() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("serverName", serverName);

        ChannelsFragment frag = new ChannelsFragment();
        frag.setArguments(bundle);

        fragmentTransaction.replace(R.id.content_frame, frag);
        fragmentTransaction.commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
