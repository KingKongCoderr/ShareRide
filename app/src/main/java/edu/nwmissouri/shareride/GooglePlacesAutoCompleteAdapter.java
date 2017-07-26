package edu.nwmissouri.shareride;


import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import static edu.nwmissouri.shareride.MainActivity.autocomplete;

/**
 * Created by ntankasala on 7/26/17.
 */

 class GooglePlacesAutoCompleteAdapter extends ArrayAdapter implements Filterable
{
    private ArrayList resultList;
    public GooglePlacesAutoCompleteAdapter(Context context, int textViewResourceId)
    {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index).toString();
    }

    @Override

    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override

            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();

                if (constraint != null) {
                    // Retrieve the autocomplete results.

                    resultList = autocomplete(constraint.toString());
                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

                if (results != null && results.count > 0) {

                    notifyDataSetChanged();

                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }
}

