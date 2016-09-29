package wayfoo.wayfoo.merchantwayfoo;

/**
 * Created by Axle on 26/03/2016.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static wayfoo.wayfoo.merchantwayfoo.Constants.FIRST_COLUMN;
import static wayfoo.wayfoo.merchantwayfoo.Constants.FOURTH_COLUMN;
import static wayfoo.wayfoo.merchantwayfoo.Constants.SECOND_COLUMN;
import static wayfoo.wayfoo.merchantwayfoo.Constants.THIRD_COLUMN;

public class PerOrderAdapter extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    public PerOrderAdapter(Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public int getViewTypeCount(){
        return getCount();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }


    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub



        LayoutInflater inflater=activity.getLayoutInflater();
        ViewHolderItem v;
        if(convertView == null){

            convertView=inflater.inflate(R.layout.column_row_per, null);
            v = new ViewHolderItem();
            v.txtFirst=(TextView) convertView.findViewById(R.id.name);
            v.txtSecond=(TextView) convertView.findViewById(R.id.quantity);
            v.txtThird=(TextView) convertView.findViewById(R.id.itemID);
            v.txtFourth=(TextView) convertView.findViewById(R.id.amt);
            convertView.setTag(v);
        }
        else{
            v = (ViewHolderItem) convertView.getTag();
        }
        HashMap<String, String> map=list.get(position);
        v.txtFirst.setText(map.get(FIRST_COLUMN));
        v.txtSecond.setText(map.get(SECOND_COLUMN));
        v.txtThird.setText(map.get(THIRD_COLUMN));
        v.txtFourth.setText(map.get(FOURTH_COLUMN));

        return convertView;
    }
    static class ViewHolderItem{
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
    }
}