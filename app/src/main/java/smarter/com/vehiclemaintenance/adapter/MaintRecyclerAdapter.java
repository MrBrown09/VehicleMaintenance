package smarter.com.vehiclemaintenance.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import smarter.com.vehiclemaintenance.R;
import smarter.com.vehiclemaintenance.activity.Detail;
import smarter.com.vehiclemaintenance.activity.Maintenance;
import smarter.com.vehiclemaintenance.component.model.MaintenanceModel;

import static smarter.com.vehiclemaintenance.utils.Constant.TAG_Code;
import static smarter.com.vehiclemaintenance.utils.Constant.TAG_EmployeeCode;

public class MaintRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    RecyclerView recycleview;
    List<MaintenanceModel> mainList;

    public MaintRecyclerAdapter(Maintenance maintenance, List<MaintenanceModel> xmainList, RecyclerView xrecyclerView) {
        context = maintenance;
        recycleview = xrecyclerView;
        mainList = xmainList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewHolder vh;
        View itemLayoutView = null;
        itemLayoutView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.main_list_item, viewGroup, false);
        vh = new ViewHolder(itemLayoutView);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int positionx) {
        try{
            if (viewHolder instanceof ViewHolder) {
                final MaintenanceModel mainitem = mainList.get(positionx);

                ((ViewHolder) viewHolder).txtVehicleNo.setText(mainitem.getVehicleNumber());
                ((ViewHolder) viewHolder).txtAlias.setText(mainitem.getAlias());
                ((ViewHolder) viewHolder).txtRequest.setText(mainitem.getCreatedByEmployeeFullName());

                ((ViewHolder) viewHolder).card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strMaintaCode = mainitem.getCode();
                        Intent shipDetail = new Intent(context, Detail.class);
                        shipDetail.putExtra(TAG_Code, strMaintaCode);
                        context.startActivity(shipDetail);
                    }
                });

            }

        }catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout card;
        private TextView txtVehicleNo,txtAlias, txtRequest;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            txtVehicleNo = itemLayoutView.findViewById(R.id.id_vehicle_no);
            txtAlias = itemView.findViewById(R.id.id_alias);
            txtRequest = itemView.findViewById(R.id.id_requester);
            card = itemView.findViewById(R.id.id_card);
        }
    }
}
