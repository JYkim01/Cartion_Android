package net.belicon.cartion.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.belicon.cartion.R;
import net.belicon.cartion.models.BleDevices;

import java.util.List;

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.BleViewHolder> {

    private List<BleDevices> mDevices;

    public BleAdapter(List<BleDevices> mDevices) {
        this.mDevices = mDevices;
    }

    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BleViewHolder holder, int position) {
        BleDevices ble = mDevices.get(position);
        holder.mBleName.setText(ble.getName());
//        holder.mBleRssi.setText(ble.getRssi());
//        holder.mBleAddress.setText(ble.getAddress());
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    static class BleViewHolder extends RecyclerView.ViewHolder {

        private TextView mBleName;
//        private TextView mBleRssi;
//        private TextView mBleAddress;

        public BleViewHolder(@NonNull View itemView) {
            super(itemView);

            mBleName = itemView.findViewById(R.id.item_ble_name);
//            mBleRssi = itemView.findViewById(R.id.item_ble_rssi);
//            mBleAddress = itemView.findViewById(R.id.item_ble_address);
        }
    }
}
