package t.tripsdoc.eventcategoriespickerlibrary;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import t.tripsdoc.eventcategoriespickerlibrary.listener.OnItemClickListener;

public class EventCategoriesPickerAdapter extends RecyclerView.Adapter<EventCategoriesPickerAdapter.EventCategoriesPickerHolder> implements Filterable {

    private Context context;
    private List<EventCategories> eventCategoriesList;
    private List<EventCategories> eventCategoriesListFiltered;
    private OnItemClickListener listener;

    public class EventCategoriesPickerHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public RelativeLayout viewforeground;

        public EventCategoriesPickerHolder(View view){
            super(view);
            viewforeground = view.findViewById(R.id.view_foreground);
            name = view.findViewById(R.id.eventcategoryname);
        }
    }

    public EventCategoriesPickerAdapter(Context context, List<EventCategories> eventCategoriesList, OnItemClickListener listener) {
        this.context = context;
        this.eventCategoriesList = eventCategoriesList;
        this.eventCategoriesListFiltered = eventCategoriesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventCategoriesPickerAdapter.EventCategoriesPickerHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_categories_items, parent, false);
        return new EventCategoriesPickerAdapter.EventCategoriesPickerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventCategoriesPickerHolder holder, int position) {
        final EventCategories eventCategories = eventCategoriesListFiltered.get(position);
        holder.name.setText(eventCategories.getName());
        holder.viewforeground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClicked(eventCategories);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventCategoriesListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    eventCategoriesListFiltered = eventCategoriesList;
                } else {
                    List<EventCategories> filteredList = new ArrayList<>();
                    for (EventCategories row : eventCategoriesList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    eventCategoriesListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = eventCategoriesListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                eventCategoriesListFiltered = (ArrayList<EventCategories>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


}
