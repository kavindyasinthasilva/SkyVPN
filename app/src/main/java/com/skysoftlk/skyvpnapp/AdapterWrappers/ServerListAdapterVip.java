package com.skysoftlk.skyvpnapp.AdapterWrappers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.skysoftlk.skyvpnapp.R;
import com.skysoftlk.skyvpnapp.model.Countries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServerListAdapterVip extends RecyclerView.Adapter<ServerListAdapterVip.mViewhoder> {

    ArrayList<Countries> datalist = new ArrayList<>();
    private final Context context;
    private OnServerClickListener clickListener;

    public interface OnServerClickListener {
        void onServerClick(Countries country);
    }

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

            // Update Ping and Signal Status
            int pingValue = data.getPing();
            updateSignalStatus(holder, pingValue);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onServerClick(data);
                    }
                }
            });
        }
    }

    private void updateSignalStatus(mViewhoder holder, int ping) {
        if (ping <= 0) {
            holder.region_limit.setText("---");
            holder.region_limit.setTextColor(ContextCompat.getColor(context, R.color.colorlightgrey));
            holder.signal_dot.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorlightgrey)));
            return;
        }

        holder.region_limit.setText(String.format(Locale.getDefault(), "%dms", ping));
        int color;
        if (ping < 100) {
            color = ContextCompat.getColor(context, R.color.neon_green);
        } else if (ping < 250) {
            color = ContextCompat.getColor(context, android.R.color.holo_orange_light);
        } else {
            color = ContextCompat.getColor(context, R.color.red);
        }
        holder.region_limit.setTextColor(color);
        holder.signal_dot.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public static class mViewhoder extends RecyclerView.ViewHolder {
        TextView app_name, region_limit;
        ImageView flag;
        View signal_dot;

        public mViewhoder(View itemView) {
            super(itemView);
            app_name = itemView.findViewById(R.id.region_title);
            flag = itemView.findViewById(R.id.country_flag);
            region_limit = itemView.findViewById(R.id.region_limit);
            signal_dot = itemView.findViewById(R.id.signal_dot);
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

    public void setOnServerClickListener(OnServerClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
