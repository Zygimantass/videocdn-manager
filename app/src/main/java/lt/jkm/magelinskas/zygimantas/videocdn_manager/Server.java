package lt.jkm.magelinskas.zygimantas.videocdn_manager;

/**
 * Created by Zygimantas on 1/16/2016.
 */
public class Server {
    private int _id;
    private String _serverName;
    private String _serverIP;
    private String _port;
    private String _username;
    private String _password;

    public Server() {

    }

    public Server(String serverName, String serverIP, String port, String username, String password) {
        this._serverName = serverName;
        this._serverIP = serverIP;
        this._port = port;
        this._username = username;
        this._password = password;
    }

    public Server(int id, String serverName, String serverIP, String port, String username, String password) {
        this._id = id;
        this._serverName = serverName;
        this._serverIP = serverIP;
        this._port = port;
        this._username = username;
        this._password = password;
    }

    public void setID(int id)
    {
        this._id = id;
    }

    public int getID()
    {
        return this._id;
    }

    public void setServerName(String serverName)
    {
        this._serverName = serverName;
    }

    public String getServerName()
    {
        return this._serverName;
    }

    public void setServerIP(String serverIP)
    {
        this._serverIP = serverIP;
    }

    public String getServerIP()
    {
        return this._serverIP;
    }

    public void setPort(String port)
    {
         this._port = port;
    }

    public String getPort()
    {
        return this._port;
    }

    public void setUsername(String username)
    {
        this._username = username;
    }

    public String getUsername()
    {
        return this._username;
    }

    public void setPassword(String password)
    {
        this._password = password;
    }

    public String getPassword()
    {
        return this._password;
    }
}
