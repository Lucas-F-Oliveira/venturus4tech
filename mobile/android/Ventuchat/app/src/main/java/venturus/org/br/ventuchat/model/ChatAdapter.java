package venturus.org.br.ventuchat.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import venturus.org.br.ventuchat.R;

public final class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
{
    private static final String TAG = ChatAdapter.class.getName();

    private final List<JSONObject> mData;

    public ChatAdapter()
    {
        this.mData = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
    {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.chat_list_item, parent, false );

        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position )
    {
        try
        {
            String author = mData.get( position ).getString( "author" );
            String message = mData.get( position ).getString( "message" );
            String time = mData.get( position ).getString( "time" );
            String nameInitial = author.substring( 0, 1 );

            holder.mAuthor.setText( author );
            holder.mMessage.setText( message );
            holder.mTime.setText( time );
            holder.mNameInitial.setText( nameInitial );
        }
        catch ( JSONException e )
        {
            Log.e( TAG, e.getMessage() );
        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }

    public void addMessage( final JSONObject message )
    {
        mData.add( message );
        notifyDataSetChanged();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View mViewItem;
        public final TextView mAuthor;
        public final TextView mMessage;
        public final TextView mTime;
        public final TextView mNameInitial;

        public ViewHolder( final View itemView )
        {
            super( itemView );

            mViewItem = itemView;
            mAuthor = ( TextView ) itemView.findViewById( R.id.chat_list_author );
            mMessage = ( TextView ) itemView.findViewById( R.id.chat_list_message );
            mTime = ( TextView ) itemView.findViewById( R.id.chat_list_time );
            mNameInitial = ( TextView ) itemView.findViewById( R.id.chat_list_name_initial );
        }
    }
}
