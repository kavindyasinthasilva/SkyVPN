package com.skysoftlk.vpnapp.AdapterWrappers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.skysoftlk.vpnapp.Fragments.FragmentVip;
import com.skysoftlk.vpnapp.R;
import com.skysoftlk.vpnapp.model.Countries;

import java.util.ArrayList;
import java.util.List;

public class ServerListAdapterVip extends RecyclerView.Adapter<ServerListAdapterVip.mViewhoder> {

    ArrayList<Countries> datalist = new ArrayList<>();
    private final Context context;

    public ServerListAdapterVip(Context ctx) {
        this.context = ctx;
    }

    @NonNull
    @Override
    public ServerListAdapterVip.mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.region_list_item, parent, false);
        return new ServerListAdapterVip.mViewhoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ServerListAdapterVip.mViewhoder holder, int position) {
        Countries data = datalist.get(position);
        if (data != null) {
            holder.app_name.setText(data.getCountry());

            Glide.with(context)
                    .load(data.getFlagUrl())
                    .into(holder.flag);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentVip.onItemClick(data);
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
        ImageView flag;

        public mViewhoder(View itemView) {
            super(itemView);
            app_name = itemView.findViewById(R.id.region_title);
            flag = itemView.findViewById(R.id.country_flag);
        }
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
