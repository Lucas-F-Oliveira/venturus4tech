package venturus.org.br.ventuchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import venturus.org.br.ventuchat.data.SocketManager;

public final class MainActivity extends AppCompatActivity
{
    private Button mButton;
    private EditText mEditText;

    private Socket mSocket;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        mSocket = SocketManager.getInstance().getSocket();

        mButton = ( Button ) findViewById( R.id.main_entry_button );
        mEditText = ( EditText ) findViewById( R.id.main_nickname_input );

        onNicknameInputHandler();
        onEntryButtonClickHandler();
    }

    private void onNicknameInputHandler()
    {
        mEditText.setOnEditorActionListener( new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction( TextView textView, int actionId, KeyEvent keyEvent )
            {
                boolean handled = false;

                if ( actionId == EditorInfo.IME_ACTION_GO )
                {
                    openChatActivity();
                    handled = true;
                }

                return handled;
            }
        } );
    }

    private void onEntryButtonClickHandler()
    {
        mButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                openChatActivity();
            }
        });
    }

    private void openChatActivity()
    {
        final String nickname = mEditText.getText().toString().trim();

        if ( nickname.isEmpty() )
        {
            Toast.makeText( MainActivity.this, "Nickname inválido!", Toast.LENGTH_LONG ).show();
        }
        else
        {
            mSocket.once( Socket.EVENT_CONNECT, new Emitter.Listener()
            {
                @Override
                public void call( Object... args )
                {
                    Intent intent = new Intent();

                    intent.setClass( MainActivity.this, ChatActivity.class );

                    intent.putExtra( "author", nickname );

                    startActivity( intent );
                }
            });

            mSocket.connect();
        }
    }
}
