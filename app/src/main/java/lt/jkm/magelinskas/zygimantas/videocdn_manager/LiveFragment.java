package lt.jkm.magelinskas.zygimantas.videocdn_manager;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.media.MediaCodec;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.SurfaceView;
import android.widget.AdapterView;
import android.widget.MediaController;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;


import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;

import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;

/**
 * Created by Zygimantas on 5/13/2016.
 */
public class LiveFragment extends Fragment implements FragmentOnBackClickInterface {

    private String serverName;
    private String channel;
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;
    private static final int RENDERER_COUNT = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live, container, false);
        final String finalServerName = this.getArguments().getString("serverName");
        final String finalChannel = this.getArguments().getString("channel");
        serverName = this.getArguments().getString("serverName");
        channel = this.getArguments().getString("channel");
        DBHandler dbh = new DBHandler(getContext(), null, null, 1);
        Server srv = dbh.findServer(serverName);
        String link = "http://" + srv.getServerIP() + ":" + 81 + "/" + channel + "/index.m3u8";



        Uri uri = Uri.parse(link);
        final EMVideoView emVideoView = (EMVideoView)rootView.findViewById(R.id.video_view);
        emVideoView.setVideoURI(uri);
        emVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                emVideoView.start();
            }
        });
        return rootView;
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
