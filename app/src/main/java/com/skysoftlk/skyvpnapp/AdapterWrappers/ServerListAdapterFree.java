package com.skysoftlk.skyvpnapp.AdapterWrappers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.Activities.MainActivity;
import com.skysoftlk.skyvpnapp.model.Countries;

import java.util.ArrayList;
import java.util.List;

public class ServerListAdapterFree extends RecyclerView.Adapter<ServerListAdapterFree.mViewhoder> {

    ArrayList<Countries> datalist = new ArrayList<>();
    private Context context;

    public ServerListAdapterFree(Context ctx) {
        this.context = ctx;
    }

    @NonNull
    @Override
    public mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.server_list_free, parent, false);
        return new mViewhoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewhoder holder, int position) {
        Countries data = datalist.get(position);
        if (data != null) {
            holder.app_name.setText(data.getCountry());

            Glide.with(context)
                    .load(data.getFlagUrl())
                    .into(holder.flag);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("c", data);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class mViewhoder extends RecyclerView.ViewHolder {
        TextView app_name;
        ImageView flag, limit;

        public mViewhoder(View itemView) {
            super(itemView);
            app_name = itemView.findViewById(R.id.region_title);
            limit = itemView.findViewById(R.id.region_limit);
            flag = itemView.findViewById(R.id.country_flag);
        }
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Countries item);
    }

    public void setData(List<Countries> servers) {
        datalist.clear();
        for (Countries server : servers) {
            if (server != null) {
                datalist.add(server);
            }
        }
        notifyDataSetChanged();
    }
}
