package venturus.org.br.ventuchat.data;

import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

import venturus.org.br.ventuchat.util.Helper;

public final class SocketManager
{
    private static final String TAG = SocketManager.class.getName();

    private static SocketManager mInstance;

    private Socket mSocket;

    public static SocketManager getInstance()
    {
        if ( null == mInstance )
        {
            mInstance = new SocketManager();
        }

        return mInstance;
    }

    private SocketManager()
    {
        try
        {
            mSocket = IO.socket( Helper.URI.getValue() );
        }
        catch ( URISyntaxException e )
        {
            Log.e( TAG, e.getMessage() );
        }
    }

    public Socket getSocket()
    {
        return mSocket;
    }
}
