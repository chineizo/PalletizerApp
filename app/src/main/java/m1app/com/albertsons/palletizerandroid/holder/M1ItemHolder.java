package m1app.com.albertsons.palletizerandroid.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import m1app.com.albertsons.palletizerandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by chinedu on 12/23/17.
 */

public class M1ItemHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.serial_number)
    public TextView serialNumber;

    @BindView(R.id.item_number)
    public TextView itemNumber;

    @BindView(R.id.description)
    public TextView description;

    @BindView(R.id.lot)
    public TextView lot;

    @BindView(R.id.uom)
    public TextView uom;

    @BindView(R.id.prod_date)
    public TextView productionDate;

    @BindView(R.id.pull_date)
    public TextView pullDate;


    public M1ItemHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        //nameView = (TextView)itemView.findViewById(R.id.name);
//        descriptionView = (TextView)itemView.findViewById(R.id.description);
//        valueView = itemView.findViewById(R.id.value);
//        lengthView = itemView.findViewById(R.id.length);
//        typeView = itemView.findViewById(R.id.type);
//        mandatoryView = (TextView)itemView.findViewById(R.id.mandatory);
    }

}
