package venturus.org.br.ventuchat.model;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import venturus.org.br.ventuchat.util.Helper;

public final class ChatHistoryTask extends AsyncTask<Void, Void, JSONArray>
{
    private static final String TAG = ChatHistoryTask.class.getName();

    private final ChatAdapter mChatAdpater;

    public ChatHistoryTask( final ChatAdapter chatAdpater )
    {
        this.mChatAdpater = chatAdpater;
    }

    @Override
    protected JSONArray doInBackground( final Void... voids )
    {
        JSONArray messages = new JSONArray();

        try
        {
            URL url = new URL( Helper.URI.getValue() );

            HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();

            InputStream inputStream = new BufferedInputStream( connection.getInputStream() );

            Scanner scanner = new Scanner( inputStream ).useDelimiter( "\\A" );

            String result = scanner.hasNext() ? scanner.next() : "";

            messages.put( result );
            
            connection.disconnect();
        }
        catch ( IOException e )
        {
            Log.e( TAG, e.getMessage() );
        }

        return messages;
    }

    @Override
    protected void onPostExecute( final JSONArray messages )
    {
		try
        {
            for ( int i = 0; i < messages.length(); i++ )
		    {
		        JSONObject message = messages.getJSONObject( i );

	            mChatAdpater.addMessage( message );   
		    }
        }
        catch ( JSONException e )
        {
            Log.e( TAG, e.getMessage() );
        }

        super.onPostExecute( messages );
    }
}
