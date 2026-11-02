package com.example.itinventory;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.AssetViewHolder> {

    private List<Asset> assetList = new ArrayList<>();

    public void setAssets(List<Asset> assets) {
        this.assetList = assets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AssetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asset, parent, false);
        return new AssetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssetViewHolder holder, int position) {
        Asset asset = assetList.get(position);
        holder.bind(asset);
    }

    @Override
    public int getItemCount() {
        return assetList.size();
    }

    static class AssetViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAssetNo;
        private final Chip tvType;
        private final TextView tvUser;
        private final TextView tvDept;

        public AssetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAssetNo = itemView.findViewById(R.id.tvAssetNo);
            tvType = itemView.findViewById(R.id.tvType);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDept = itemView.findViewById(R.id.tvDept);
        }

        public void bind(Asset asset) {
            // Null checks are essential since NoSQL nested fields may be missing
            
            // CoreDetails checks
            if (asset.getCoreDetails() != null) {
                String assetNo = asset.getCoreDetails().getAssetNo();
                tvAssetNo.setText(assetNo != null ? assetNo : "Unknown Asset No");
                
                String type = asset.getCoreDetails().getType();
                tvType.setText(type != null ? type : "Unknown");
            } else {
                tvAssetNo.setText("Unknown Asset No");
                tvType.setText("Unknown");
            }

            // Assignment checks
            if (asset.getAssignment() != null) {
                String user = asset.getAssignment().getUser();
                tvUser.setText(user != null ? user : "Unassigned");
                
                String dept = asset.getAssignment().getDept();
                tvDept.setText(dept != null ? dept : "No Dept");
            } else {
                tvUser.setText("Unassigned");
                tvDept.setText("No Dept");
            }
            
            // Set click listener to open details
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), AssetDetailActivity.class);
                intent.putExtra("asset_data", asset);
                v.getContext().startActivity(intent);
            });
        }
    }
}
