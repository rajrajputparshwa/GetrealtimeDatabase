package com.getrealtimedata.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getrealtimedata.DriverList;
import com.getrealtimedata.Message;
import com.getrealtimedata.R;


import java.util.ArrayList;

/**
 * Created by admin on 12/5/2017.
 */
public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {
    Context context;
    ArrayList<Message> array_list = new ArrayList<>();

    public DriverAdapter(Context context, ArrayList<Message> array_list) {
        this.context = context;
        this.array_list = array_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.driverlist, parent, false);


        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        if (array_list.get(position).getMsg() != null) {
            holder.car.setText(array_list.get(position).getMsg());
        }


      /*  if (array_list.get(position).getDetail() != null) {
            holder.detail.setText(array_list.get(position).getDetail());
        }

        if (array_list.get(position).getCarModel() != null) {
            holder.carmodel.setText(array_list.get(position).getCarModel());
        }*/
    }

    @Override
    public int getItemCount() {


        return array_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView car, carmodel, detail;

        public ViewHolder(View itemView) {
            super(itemView);

            car = itemView.findViewById(R.id.drivercar);
            carmodel = itemView.findViewById(R.id.carmodel);
            detail = itemView.findViewById(R.id.driverdetail);


        }
    }
}
