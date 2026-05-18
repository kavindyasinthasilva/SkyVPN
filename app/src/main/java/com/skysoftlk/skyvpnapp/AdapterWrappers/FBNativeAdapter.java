package com.skysoftlk.skyvpnapp.AdapterWrappers;

import android.view.ViewGroup;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FBNativeAdapter extends RecyclerViewAdapterWrapper {

    private FBNativeAdapter(Param param) {
        super(param.adapter);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }

    private static class Param {
        RecyclerView.Adapter adapter;
    }

    public static class Builder {
        private final Param mParam;

        private Builder(Param param) {
            mParam = param;
        }

        public static Builder with(String placementId, RecyclerView.Adapter wrapped) {
            Param param = new Param();
            param.adapter = wrapped;
            return new Builder(param);
        }

        public Builder adItemInterval(int interval) {
            return this;
        }

        public Builder adLayout(int layoutContainerRes, int itemContainerId) {
            return this;
        }

        public FBNativeAdapter build() {
            return new FBNativeAdapter(mParam);
        }

        public Builder enableSpanRow(GridLayoutManager layoutManager) {
            return this;
        }

        public Builder forceReloadAdOnBind(boolean forced) {
            return this;
        }
    }
}
