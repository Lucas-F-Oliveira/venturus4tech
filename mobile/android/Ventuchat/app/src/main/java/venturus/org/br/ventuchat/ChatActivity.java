package venturus.org.br.ventuchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import venturus.org.br.ventuchat.data.SocketManager;
import venturus.org.br.ventuchat.model.ChatAdapter;
import venturus.org.br.ventuchat.model.ChatHistoryTask;

public final class ChatActivity extends AppCompatActivity
{
    private static final String TAG = ChatActivity.class.getName();

    private Button mButton;
    private EditText mEditText;
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;

    private Socket mSocket;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat );

        mSocket = SocketManager.getInstance().getSocket();

        mButton = ( Button ) findViewById( R.id.chat_send_button );
        mEditText = ( EditText ) findViewById( R.id.chat_input_message );
        mRecyclerView = ( RecyclerView ) findViewById( R.id.chat_recycler_view );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager( ChatActivity.this );

        mRecyclerView.setLayoutManager( mLayoutManager );

        mChatAdapter = new ChatAdapter();
        mRecyclerView.setAdapter( mChatAdapter );

        onMessageInputHandler();
        onSendButtonClickHandler();

        mSocket.on( "messages", new Emitter.Listener()
        {
            @Override
            public void call( Object... args )
            {
                final JSONObject message = ( JSONObject ) args[0];

                ChatActivity.this.runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mChatAdapter.addMessage( message );

                        playSound();

                        int scrollPosition = mChatAdapter.getItemCount() - 1;
                        mRecyclerView.smoothScrollToPosition( scrollPosition );
                    }
                });
            }
        });

        ChatHistoryTask task = new ChatHistoryTask( mChatAdapter );
        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.chat_menu, menu );

        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        int itemId = item.getItemId();

        switch ( itemId )
        {
            case R.id.action_sound:
                toggleSoundPreference();
                return true;
            case R.id.action_close:
                closeActivity();
                return true;
            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void onMessageInputHandler()
    {
        mEditText.setOnEditorActionListener( new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction( TextView textView, int actionId, KeyEvent keyEvent )
            {
                boolean handled = false;

                if ( actionId == EditorInfo.IME_ACTION_SEND )
                {
                    sendMessage();
                    handled = true;
                }

                return handled;
            }
        } );
    }

    private void onSendButtonClickHandler()
    {
        mButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                sendMessage();
            }
        });
    }

    private void sendMessage()
    {
        JSONObject message = new JSONObject();

        String author = getIntent().getStringExtra( "author" );
        String inputMessage = mEditText.getText().toString().trim();

        try
        {
            if ( !inputMessage.isEmpty() )
            {
                message.put( "author", author );
                message.put( "message", inputMessage );
                message.put( "sent", sentTime() );
            }
            else
            {
                Toast.makeText( ChatActivity.this, "Mensagem inválida!", Toast.LENGTH_LONG ).show();
            }
        }
        catch ( JSONException e )
        {
            Log.e( TAG, e.getMessage() );
        }

        mSocket.emit( "messages", message );
    }

    private void closeActivity()
    {
        if ( mSocket.connected() )
        {
            mSocket.disconnect();
        }

        finish();
    }

    private void playSound()
    {
        if ( !isSoundEnabled() )
        {
           return;
        }

        MediaPlayer mediaPlayer = MediaPlayer.create( ChatActivity.this, R.raw.message_media );

        mediaPlayer.setOnCompletionListener( new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion( MediaPlayer mp )
            {
                mp.release();
            }
        });

        mediaPlayer.start();
    }

    private boolean isSoundEnabled()
    {
        SharedPreferences preferences = getPreferences( Context.MODE_PRIVATE );

        return preferences.getBoolean( "sound", true );
    }

    private void toggleSoundPreference()
    {
        SharedPreferences preferences = getPreferences( Context.MODE_PRIVATE );

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean( "sound", !isSoundEnabled() );
        editor.apply();
    }

    private String sentTime()
    {
        Date date = new Date();

        DateFormat dateFormat = SimpleDateFormat.getTimeInstance( DateFormat.SHORT );

        return dateFormat.format( date );
    }
}
