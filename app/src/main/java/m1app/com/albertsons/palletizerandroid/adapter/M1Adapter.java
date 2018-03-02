package m1app.com.albertsons.palletizerandroid.adapter;


import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import m1app.com.albertsons.palletizerandroid.R;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.holder.M1ItemHolder;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.NameValue;
import m1app.com.albertsons.palletizerandroid.utility.Utils;

public class M1Adapter extends RecyclerView.Adapter<M1ItemHolder> {

    private final String TAG = M1Adapter.class.getSimpleName();
    private List<MIRecord> miRecordList;
    private String serialNumber;
    private String lotNumber;

    public void addRecord(List<MIRecord> miRecords) {
        this.miRecordList = miRecords;
    }

    public void addSerialNumber(String serialNumber, String lotNumber) {
        this.serialNumber = serialNumber;
        this.lotNumber = lotNumber;
        notifyDataSetChanged();
    }

    public void clearAdapter () {
        int size = miRecordList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                miRecordList.remove(0);
            }
            notifyItemRangeRemoved(0,size);
        }
    }

    @Override
    public M1ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //boolean isTablet = parent.getContext().getResources().getBoolean(R.bool.isTablet);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mi_item, parent, false);
        return new M1ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(M1ItemHolder holder, int position) {
        MIRecord miRecord = miRecordList.get(position);
        holder.serialNumber.setText(serialNumber);
        List<NameValue> nameValues = miRecord.getNameValue();
        for (NameValue value : nameValues) {
            if (Config.Field.VHPRNO.equals(value.getName())) {
                holder.itemNumber.setText(value.getValue());
            } else if (Config.Field.VHBANO.equals(value.getName())) {
                if (!TextUtils.isEmpty(lotNumber)) {
                    holder.lot.setText(lotNumber);
                } else {
                    holder.lot.setText(value.getValue());
                }
            } else if (Config.Field.VHMAUN.equals(value.getName())) {
                holder.uom.setText(value.getValue());
            } else if (Config.Field.MMFUDS.equals(value.getName())) {
                holder.description.setText(value.getValue());
            } else if (Config.Field.MBSLDY.equals(value.getName())) {

                try {
                    String pullDate = Utils.addDays(Utils.TODAYS_DATE, Integer.valueOf(value.getValue().trim()));
                    Log.d(TAG, "SLDY:" + value.getValue().trim());
                    holder.pullDate.setText(pullDate);
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't parse Shelf Life Days  parameter:", e);
                }

                holder.productionDate.setText(Utils.getTime(Utils.TODAYS_DATE));
            }
        }
    }

    @Override
    public int getItemCount() {
        return miRecordList == null ? 0 : miRecordList.size();
    }
}
