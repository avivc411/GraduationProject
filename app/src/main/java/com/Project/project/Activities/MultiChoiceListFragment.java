package com.Project.project.Activities;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.ListFragment;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.Project.project.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Use the {@link MultiChoiceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiChoiceListFragment extends ListFragment {
    /**
     * Interface for calling activity to implement.
     */
    public interface DataReceiver {
        /**
         * @param data Chosen items
         */
        void onDataReceived(List<String> data);
    }

    private DataReceiver dataReceiver;
    private List<String> items, chosenItems;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataReceiver = (DataReceiver) context;
    }

    public MultiChoiceListFragment() {
        chosenItems = new ArrayList<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param params Items on list.
     * @return A new instance of fragment MultiChoiceListFragment.
     */
    public static MultiChoiceListFragment newInstance(String...params) {
        MultiChoiceListFragment fragment = new MultiChoiceListFragment();
        Bundle args = new Bundle();
        if(params!=null) {
            args.putInt("ArgLen", params.length);
            for (int i = 0; i < params.length; i++)
                args.putString("ArgItem" + i, params[i]);
            fragment.setArguments(args);
            return fragment;
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && items == null) {
            this.items = new ArrayList<>();
            for(int i=0; i<getArguments().getInt("ArgLen"); i++)
                items.add(getArguments().getString("ArgItem"+i));

            ListAdapter myListAdapter = new ArrayAdapter<>(
                    getActivity(),
                    android.R.layout.simple_list_item_multiple_choice,
                    items);

            setListAdapter(myListAdapter);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        int count = getListView().getCount();
        SparseBooleanArray sparseBooleanArray = getListView().getCheckedItemPositions();
        for (int i = 0; i < count; i++){
            if (sparseBooleanArray.get(i)) {
                chosenItems.add(getListView().getItemAtPosition(i).toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_multi_choice_list, container, false);
        Button button = view.findViewById(R.id.button5);
        button.setOnClickListener(this::Done);
        if(savedInstanceState!=null){
            items = savedInstanceState.getStringArrayList("Items");
            chosenItems = savedInstanceState.getStringArrayList("ChosenItems");
        }
        return view;
    }

    private void Done(View view){
        chosenItems = new ArrayList<>(new HashSet<String>(chosenItems));
        dataReceiver.onDataReceived(chosenItems);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("Items", new ArrayList(items));
        outState.putStringArrayList("ChosenItems", new ArrayList(chosenItems));
    }
}
