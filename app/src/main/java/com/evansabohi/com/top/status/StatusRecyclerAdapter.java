package com.evansabohi.com.top.status;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.evansabohi.com.top.R;
import java.util.List;

public class StatusRecyclerAdapter extends RecyclerView.Adapter<StatusRecyclerAdapter.ViewHolder> {

    public List<String> status_list;
    public Context context;


    public StatusRecyclerAdapter(List<String> status_list){
        this.status_list = status_list;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addstatusrow, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String status = status_list.get(position);

        if (!TextUtils.isEmpty(status)){
            holder.setBlogImage(status);
        }


    }


    @Override
    public int getItemCount() {
        return status_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private ImageView imageStatus;
        private TextDrawable mTextDrawable;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }


        public void setBlogImage(String status){


                mTextDrawable= TextDrawable.builder().beginConfig()
                        .textColor(Color.WHITE)
                        .fontSize(20)
                        .bold()
                        .toUpperCase()
                        .withBorder(4)
                        .endConfig().buildRoundRect(status,Color.DKGRAY,10);
                imageStatus = mView.findViewById(R.id.statusimage);
                imageStatus.setImageDrawable(mTextDrawable);


        }

    }
}
