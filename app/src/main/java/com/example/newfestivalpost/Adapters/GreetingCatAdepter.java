package com.example.newfestivalpost.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newfestivalpost.Activities.ActivitySingleCategoyList;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeData;
import com.example.newfestivalpost.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GreetingCatAdepter extends RecyclerView.Adapter<GreetingCatAdepter.ViewHolder> {

    Context context;
   // ArrayList<ModelHomeChild> modelHomeChildList;
    ArrayList<VideoHomeData> modelHomeChildList;
    int resource;
    View view;

    /*public AdapterViewAllList(Context context, ArrayList<ModelHomeChild> modelHomeChildList) {
        this.context = context;
        this.modelHomeChildList = modelHomeChildList;

    }*/
    public GreetingCatAdepter(Context context,int resource, ArrayList<VideoHomeData> modelHomeChildList) {
        this.context = context;
        this.resource = resource;
        this.modelHomeChildList = modelHomeChildList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(resource, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

       // final String childitemtittle = modelHomeChildList.get(position).getChilditemtittle();
        final String childitemtittle = modelHomeChildList.get(position).getName();
        holder.tv_childitem_tittle.setText(childitemtittle);
     //   Picasso.get().load(modelHomeChildList.get(position).getChilditemimage()).placeholder(R.drawable.placeholder).into(holder.iv_viewallimage);
        Picasso.get().load(modelHomeChildList.get(position).getImage_url()).placeholder(R.drawable.placeholder).into(holder.riv_childitemimage);
        final String catnameid=modelHomeChildList.get(position).getId();
      /* if(childitemtittle.equals("Upcoming Festival & days"))
       {
           holder.ll_viewall_date.setVisibility(View.VISIBLE);

       }else
       {
           holder.ll_viewall_date.setVisibility(View.GONE);

       }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("fdfjsfi","jfdsfisf"+catnameid);
                Intent i = new Intent(context, ActivitySingleCategoyList.class);
               // Constance.childDataList = modelHomeChildList;
                i.putExtra("childitemtittle",childitemtittle);
                i.putExtra("catnameid",catnameid);
                i.putExtra("catType","greeting");
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelHomeChildList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView riv_childitemimage;
        TextView tv_childitem_tittle;
        LinearLayout ll_viewall_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            riv_childitemimage = itemView.findViewById(R.id.riv_childitemimage);
            tv_childitem_tittle = itemView.findViewById(R.id.tv_childitem_tittle);
           /*// ll_viewall_date = itemView.findViewById(R.id.ll_viewall_date);*/

        }
    }


}
