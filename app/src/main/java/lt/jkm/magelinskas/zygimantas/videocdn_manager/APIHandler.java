package lt.jkm.magelinskas.zygimantas.videocdn_manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.net.Proxy;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import lt.jkm.magelinskas.zygimantas.flussonicstatus.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CertificatePinner;
import okhttp3.Challenge;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Zygimantas on 1/17/2016.
 */



public class APIHandler {
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    public static final long TIMEOUT = 3;

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient client = new OkHttpClient();
            OkHttpClient.Builder builder = client.newBuilder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.connectTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.writeTimeout(TIMEOUT, TimeUnit.SECONDS);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void heartbeat(String url, String port, String username, String password, final ImageView img)
    {
        final WeakReference<ImageView> imageViewReference = new WeakReference<ImageView>(img);
        String formedURL = "https://" + url + ":" + port + "/flussonic/api/server";
        Log.e("formedUrl", formedURL);
        Log.e("creds", Credentials.basic(username, password));

        Request request = new Request.Builder()
                .url(formedURL)
                .header("Authorization", Credentials.basic(username, password))
                .build();
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        img.setImageResource(R.drawable.ic_red);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful())
                {
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageResource(R.drawable.ic_green);
                        }
                    });
                }
                else{
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("asddd", response.message());
                            img.setImageResource(R.drawable.ic_red);
                        }
                    });
                }
            }
        });
    }

    public static void loadInfoDialog(final AlertDialog.Builder builder, final AlertDialog oldDialog, final String serverName, final Context context, final FragmentManager fragManager, final ArrayList<String> values, final ArrayAdapter<String> sla, final int position)
    {
        DBHandler dbh = new DBHandler(context, null, null, 1);
        Server srv = dbh.findServer(serverName);


        String formedURL = "https://" + srv.getServerIP() + ":" + srv.getPort() + "/flussonic/api/server";
        Request request = new Request.Builder()
                .url(formedURL)
                .header("Authorization", Credentials.basic(srv.getUsername(), srv.getPassword()))
                .build();
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        oldDialog.setMessage("Cannot load the server's info");
                        oldDialog.setTitle("Error");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    int channels = 0;
                    int users = 0;
                    String version = "";
                    JSONObject respJson = responseToJson(response);
                    try {
                        channels = respJson.getInt("total_streams");
                        users = respJson.getInt("total_clients");
                        version = respJson.getString("version");
                    } catch (JSONException | NullPointerException e) {
                        Log.e("Exception", e.getStackTrace().toString());
                    }
                    final int chan = channels;
                    final int user = users;
                    final String ver = version;
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            oldDialog.hide();
                            AlertDialog.Builder aBuilder = new AlertDialog.Builder(context);
                            aBuilder.setMessage("Users: " + user + "\nStreams: " + chan + "\nServer version: " + ver);
                            aBuilder.setTitle("Server info");
                            aBuilder.setPositiveButton("Channels", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // show details
                                    FragmentTransaction fragmentTransaction = fragManager.beginTransaction();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("serverName", serverName);

                                    ChannelsFragment frag = new ChannelsFragment();
                                    frag.setArguments(bundle);

                                    fragmentTransaction.replace(R.id.content_frame, frag);
                                    fragmentTransaction.commit();
                                }
                            });
                            aBuilder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // show details
                                    DBHandler dbh = new DBHandler(context, null, null, 1);
                                    dbh.deleteServer(serverName);
                                    values.remove(position);
                                    sla.notifyDataSetChanged();
                                }
                            });
                            aBuilder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentTransaction fragmentTransaction = fragManager.beginTransaction();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("serverName", serverName);

                                    EditServerFragment frag = new EditServerFragment();
                                    frag.setArguments(bundle);

                                    fragmentTransaction.replace(R.id.content_frame, frag);
                                    fragmentTransaction.commit();
                                }
                            });


                            aBuilder.create().show();
                        }
                    });
                }
                else {
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            oldDialog.setMessage("Cannot load the server's info");
                            oldDialog.setTitle("Error");
                        }
                    });

                }


            }
        });
    }

    public static void loadChannelList(final ChannelListAdapter adapter, String serverName, Context context) {
        DBHandler dbh = new DBHandler(context, null, null, 1);
        Server srv = dbh.findServer(serverName);
        Log.e("d", srv.getPassword());

        String formedURL = "https://" + srv.getServerIP() + ":" + srv.getPort() + "/flussonic/api/streams";
        Request request = new Request.Builder()
                .url(formedURL)
                .header("Authorization", Credentials.basic(srv.getUsername(), srv.getPassword()))
                .build();
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //handle the error
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject responseJson = responseToJson(response);
                    try {
                        final JSONArray streamArray = responseJson.getJSONArray("streams");

                        UIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    for (int i = 0; i < adapter.getCount(); i++){
                                        adapter.remove(adapter.getItem(i));
                                    }

                                    for (int i = 0; i < streamArray.length(); i++){
                                        JSONObject stream = streamArray.getJSONObject(i);
                                        adapter.add(stream.getString("name"));
                                    }
                                } catch (JSONException e){

                                }
                            }
                        });
                    } catch (JSONException e){

                    }

                }
                else {
                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //salalalala;
                        }
                    });

                }


            }
        });
    }

    public static void restartChannel(final String channelName, String serverName, final Context context, final Activity activity) {
        DBHandler dbh = new DBHandler(context, null, null, 1);
        Server srv = dbh.findServer(serverName);
        Log.e("d", srv.getPassword());

        String formedURL = "https://" + srv.getServerIP() + ":" + srv.getPort() + "/flussonic/api/stream_restart/" + channelName;
        Request request = new Request.Builder()
                .url(formedURL)
                .header("Authorization", Credentials.basic(srv.getUsername(), srv.getPassword()))
                .build();
        OkHttpClient okHttpClient = getUnsafeOkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity, "Couldn't restart" + channelName, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity, "Successfully restarted " + channelName, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        });
    }

    public static JSONObject responseToJson(Response response) {
        try {
            return new JSONObject(response.body().string());
        } catch (JSONException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
