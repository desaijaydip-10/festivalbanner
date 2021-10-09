package com.example.newfestivalpost.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newfestivalpost.Activities.ActivitySingleCategoyList;
import com.example.newfestivalpost.Activities.ActivitySingleVideoList;
import com.example.newfestivalpost.ModelRetrofit.CategoriesData;
import com.example.newfestivalpost.ModelRetrofit.VideoCategoriesData;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeData;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterVideoList extends RecyclerView.Adapter<AdapterVideoList.ViewHolder> {

    Context context;
   // ArrayList<ModelHomeChild> modelHomeChildList;
    ArrayList<VideoHomeData> videoCategoriesDataArrayList;
    String comefrom;
    View view;

   public AdapterVideoList(Context context, ArrayList<VideoHomeData> videoCategoriesDataArrayList,String comefrom) {
        this.context = context;
        this.comefrom = comefrom;
        this.videoCategoriesDataArrayList = videoCategoriesDataArrayList;
   }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(comefrom.equals("home")){
            view = inflater.inflate(R.layout.item_rv_child_category, parent, false);


        }
        else {
            view = inflater.inflate(R.layout.item_rv_video, parent, false);

        }
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String childitemtittle = videoCategoriesDataArrayList.get(position).getName();
        holder.tv_childitem_tittle.setText(childitemtittle);
        Log.d("dhshdd","dshsa"+childitemtittle);
      //  Picasso.get().load(videoCategoriesDataArrayList.get(position).getVideo_url()).placeholder(R.drawable.placeholder).into(holder.iv_video);
        Glide.with(context).load(videoCategoriesDataArrayList.get(position).getImage_url()).placeholder(R.drawable.placeholder).into(holder.riv_childitemimage);
        final String catnameid=videoCategoriesDataArrayList.get(position).getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comefrom.equals("businessviewall")){
                    Constance.ComeFrom=comefrom;
                    Intent i = new Intent(context, ActivitySingleVideoList.class);
                    // Constance.childDataList = modelHomeChildList;
                    i.putExtra("childitemtittle",childitemtittle);
                    i.putExtra("catnameid",catnameid);
                    context.startActivity(i);
                }
                else {

                    if(videoCategoriesDataArrayList.get(position).getDetail_display().equals("Yes")){
                        if(comefrom.equals("home")){
                            comefrom="festivalviewall";
                        }
                        Constance.ComeFrom=comefrom;
                        Intent i = new Intent(context, ActivitySingleVideoList.class);
                        // Constance.childDataList = modelHomeChildList;
                        i.putExtra("childitemtittle",childitemtittle);
                        i.putExtra("catnameid",catnameid);
                        context.startActivity(i);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(videoCategoriesDataArrayList.get(position).getDetail_message())
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return videoCategoriesDataArrayList.size();
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
