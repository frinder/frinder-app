package com.frinder.frinder.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frinder.frinder.R;
import com.frinder.frinder.dataaccess.UserFirebaseDas;
import com.frinder.frinder.model.Request;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsAdapter extends
        RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

    private List<Request> mRequests;
    private Context mContext;

    public RequestsAdapter(Context context, List<Request> requests) {
        mRequests = requests;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.item_request, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        Request request = mRequests.get(position);

        String userId = getUserId(request);
        // TODO: Move this to a static variable/cache this
        UserFirebaseDas das = new UserFirebaseDas(mContext);
        // TODO Fill out user details
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    private String getUserId(Request request) {
        // TODO: Override this based on the tab
        return request.senderId;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfilePic)
        ImageView ivProfilePic;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvUserDesc)
        TextView tvUserDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
