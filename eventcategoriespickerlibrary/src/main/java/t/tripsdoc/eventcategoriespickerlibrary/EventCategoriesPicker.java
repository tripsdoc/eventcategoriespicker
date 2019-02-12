package t.tripsdoc.eventcategoriespickerlibrary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import t.tripsdoc.eventcategoriespickerlibrary.language.en;
import t.tripsdoc.eventcategoriespickerlibrary.language.in;
import t.tripsdoc.eventcategoriespickerlibrary.language.th;
import t.tripsdoc.eventcategoriespickerlibrary.listener.BottomSheetInteractionListener;
import t.tripsdoc.eventcategoriespickerlibrary.listener.OnEventCategoryPickerListener;
import t.tripsdoc.eventcategoriespickerlibrary.listener.OnItemClickListener;

public class EventCategoriesPicker implements BottomSheetInteractionListener {

    public static final int SORT_BY_NONE = 0;
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_ID = 2;
    public static final String LOCALE_DEFAULT = "en";

    private Context context;
    private String locale = LOCALE_DEFAULT;
    private int sortBy = SORT_BY_NONE;
    private OnEventCategoryPickerListener onEventCategoryPickerListener;
    private boolean canSearch = true;

    private List<EventCategories> eventCategories;
    private List<EventCategories> searchResults;
    private EditText searchtext;
    private RecyclerView eventCategoriesRec;
    private RelativeLayout rootView;
    private EventCategoriesPickerAdapter adapter;
    private AlertDialog alertDialog;
    private BottomSheetInteractionListener bottomSheetInteractionListener;
    private Dialog dialog;

    private EventCategoriesPicker(){}

    EventCategoriesPicker(Builder builder){
        sortBy = builder.sortBy;
        locale = builder.locale;
        if(builder.onEventCategoryPickerListener != null){
            onEventCategoryPickerListener = builder.onEventCategoryPickerListener;
        }
        context = builder.context;
        canSearch = builder.canSearch;
        switch (locale) {
            case "en":
                eventCategories = new ArrayList<>(Arrays.asList(en.CATEGORIES));
                break;
            case "in":
                eventCategories = new ArrayList<>(Arrays.asList(in.CATEGORIES));
                break;
            case "th":
                eventCategories = new ArrayList<>(Arrays.asList(th.CATEGORIES));
                break;
            default:
                eventCategories = new ArrayList<>(Arrays.asList(en.CATEGORIES));
                break;
        }
        sortEventCategories(eventCategories);
    }

    private void sortEventCategories(@NonNull List<EventCategories> eventCategories) {
        if (sortBy == SORT_BY_NAME) {
            Collections.sort(eventCategories, new Comparator<EventCategories>() {
                @Override
                public int compare(EventCategories category1, EventCategories category2) {
                    return category1.getName().trim().compareToIgnoreCase(category2.getName().trim());
                }
            });
        }
        else if (sortBy == SORT_BY_ID) {
            Collections.sort(eventCategories, new Comparator<EventCategories>() {
                @Override
                public int compare(EventCategories category1, EventCategories category2) {
                    Integer data1 = Integer.parseInt(category1.getId().toString());
                    Integer data2 = Integer.parseInt(category2.getId().toString());
                    return data1.compareTo(data2);
                }
            });
        }
    }

    public void showDialog(@NonNull Activity activity){
        if(eventCategories == null || eventCategories.isEmpty()){
            throw new IllegalArgumentException("No Category found");
        } else {
            dialog = new Dialog(activity);
            View dialogView = activity.getLayoutInflater().inflate(R.layout.content_event_categories, null);
            initiateUi(dialogView);
            setSearchEditText();
            setupRecyclerView(dialogView);
            dialog.setContentView(dialogView);
            if (dialog.getWindow() != null) {
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.height = LinearLayout.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(params);
            }
            dialog.show();
        }
    }

    @Override
    public void setupRecyclerView(View sheetView) {
        searchResults = new ArrayList<>();
        sortEventCategories(eventCategories);
        searchResults.addAll(eventCategories);
        adapter = new EventCategoriesPickerAdapter(sheetView.getContext(), searchResults,
                new OnItemClickListener() {
                    @Override public void onItemClicked(EventCategories eventCategory) {
                        if (onEventCategoryPickerListener != null) {
                            onEventCategoryPickerListener.onSelectEventCategory(eventCategory);
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            dialog = null;
                        }
                    }
                });
        eventCategoriesRec.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(sheetView.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        eventCategoriesRec.setLayoutManager(layoutManager);
        eventCategoriesRec.setAdapter(adapter);
    }

    @Override
    public void setSearchEditText() {
        if (canSearch) {
            searchtext.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Intentionally Empty
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Intentionally Empty
                }

                @Override
                public void afterTextChanged(Editable searchQuery) {
                    search(searchQuery.toString());
                }
            });
            searchtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    InputMethodManager imm = (InputMethodManager) searchtext.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchtext.getWindowToken(), 0);
                    }
                    return true;
                }
            });
        } else {
            searchtext.setVisibility(View.GONE);
        }
    }

    private void search(String searchQuery) {
        searchResults.clear();
        for (EventCategories eventCategory : eventCategories) {
            if (eventCategory.getName().toLowerCase(Locale.ENGLISH).contains(searchQuery.toLowerCase())) {
                searchResults.add(eventCategory);
            }
        }
        sortEventCategories(searchResults);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initiateUi(View sheetView) {
        searchtext = sheetView.findViewById(R.id.search);
        eventCategoriesRec = sheetView.findViewById(R.id.recycleview);
        rootView = sheetView.findViewById(R.id.view_foreground);
    }

    public void setEventCategories(@NonNull List<EventCategories> eventCategory) {
        this.eventCategories.clear();
        this.eventCategories.addAll(eventCategory);
        sortEventCategories(this.eventCategories);
    }

    public EventCategories getEventCategoryByName(String eventCategoryName){
        Collections.sort(eventCategories, new NameComparator());
        EventCategories eventCategory = new EventCategories();
        eventCategory.setName(eventCategoryName);
        int i = Collections.binarySearch(eventCategories, eventCategory, new NameComparator());
        if(i < 0){
            return null;
        } else {
            return eventCategories.get(i);
        }
    }

    public EventCategories getEventCategoryByID(String eventCategoryID){
        Collections.sort(eventCategories, new IDComparator());
        EventCategories eventCategory = new EventCategories();
        eventCategory.setId(eventCategoryID);
        int i = Collections.binarySearch(eventCategories, eventCategory, new IDComparator());
        if(i < 0){
            return null;
        } else {
            return eventCategories.get(i);
        }
    }

    public static class Builder {
        private Context context;
        private String locale;
        private int sortBy = SORT_BY_NONE;
        private boolean canSearch = true;
        private OnEventCategoryPickerListener onEventCategoryPickerListener;
        private int style;

        public Builder with(@NonNull Context context) {
            this.context = context;
            return this;
        }

        public Builder style(@NonNull @StyleRes int style) {
            this.style = style;
            return this;
        }

        public Builder sortBy(@NonNull int sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder locale(@NonNull String locale) {
            this.locale = locale;
            return this;
        }

        public Builder listener(@NonNull OnEventCategoryPickerListener onEventCategoryPickerListener) {
            this.onEventCategoryPickerListener = onEventCategoryPickerListener;
            return this;
        }

        public Builder canSearch(@NonNull boolean canSearch) {
            this.canSearch = canSearch;
            return this;
        }

        public EventCategoriesPicker build() {
            return new EventCategoriesPicker(this);
        }
    }

    public static class IDComparator implements Comparator<EventCategories>{
        @Override
        public int compare(EventCategories eventCategory, EventCategories nextEventCategory) {
            Integer data1 = Integer.parseInt(eventCategory.getId().toString());
            Integer data2 = Integer.parseInt(nextEventCategory.getId().toString());
            return data1.compareTo(data2);
        }
    }

    public static class NameComparator implements Comparator<EventCategories> {
        @Override
        public int compare(EventCategories eventCategory, EventCategories nextEventCategory) {
            return eventCategory.getName().compareToIgnoreCase(nextEventCategory.getName());
        }
    }
}
