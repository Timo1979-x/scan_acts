package gto.by.acts.helpers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ltv on 13.10.2015.
 */
public class DictionaryAdapter extends BaseAdapter{
    public ArrayList<HashMap.SimpleEntry<Integer, String>> data = new ArrayList<HashMap.SimpleEntry<Integer, String>>();

    public DictionaryAdapter(HashMap<Integer, String> in_data) {
        for (Integer k : in_data.keySet()) {
            data.add(0, new HashMap.SimpleEntry<Integer, String>(k, in_data.get(k)));
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i).getValue();
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).getKey();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
