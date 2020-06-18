package com.Project.project.Utilities;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.Project.project.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Question> {

    private AppCompatActivity activity;
    private List<Question> questionList;
    private List<Integer> imageList;

    // Constructor.
    public ListViewAdapter(AppCompatActivity context, int resource, List<Question> questions, List<Integer> image) {
        super(context, resource, questions);
        this.activity = context;
        this.questionList = questions;
        this.imageList = image;
    }

    /**
     * Retrieving the chosen question from the questionnaire.
     *
     * @param position
     * @return selected question in position.
     */
    @Override
    public Question getItem(int position) {
        return questionList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
            //holder.ratingBar.getTag(position);
        }

        holder.ratingBar.setOnRatingBarChangeListener(onRatingChangedListener(holder, position));
        holder.ratingBar.setTag(position);
        holder.ratingBar.setRating(getItem(position).getRating());
        holder.QuestName.setText(getItem(position).getName());
        holder.QuestImage.setImageResource(getItem(position).getImage());

        return convertView;
    }

    /**
     * Updating the adapter for changing question's score.
     *
     * @param holder
     * @param position
     * @return
     */
    private RatingBar.OnRatingBarChangeListener onRatingChangedListener(final ViewHolder holder, final int position) {
        return (ratingBar, v, b) -> {
            Question clickedQuestion = getItem(position);
            clickedQuestion.setRating((int) v);
            Log.i("Adapter", "star: " + v);
        };
    }

    private static class ViewHolder {
        private RatingBar ratingBar;
        private TextView QuestName;
        private ImageView QuestImage;

        public ViewHolder(View view) {
            ratingBar = view.findViewById(R.id.rate_img);
            QuestName = view.findViewById(R.id.text);
            QuestImage = view.findViewById(R.id.imageView);
        }
    }
}
