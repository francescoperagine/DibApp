package it.teamgdm.sms.dibapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class StudentQuestionAdapter extends ArrayAdapter<Question> {

    private Context context;
    private int layoutResourceId;
    ArrayList<Question> data;

    StudentQuestionAdapter(Context context, int layoutResourceId, ArrayList<Question> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        questionHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new questionHolder();
            holder.textQuestion  = row.findViewById(R.id.questiontext);
            holder.rate = row.findViewById(R.id.questionRating);
            holder.btnRateBad = row.findViewById(R.id.questionDislikeButton);
            holder.btnRateGood = row.findViewById(R.id.questionLikeButton);
            row.setTag(holder);
        } else {
            holder = (questionHolder) row.getTag();
        }
        Question question = data.get(position);
        holder.textQuestion.setText(question.getQuestion());
        holder.rate.setText(String.valueOf(question.getRate()));
        holder.btnRateBad.setOnClickListener(v -> Toast.makeText(context, "-1 button Clicked", Toast.LENGTH_LONG).show());
        holder.btnRateGood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "+1 button Clicked", Toast.LENGTH_LONG).show();
            }
        });
        return row;

    }

    static class questionHolder {
        TextView textQuestion;
        TextView rate;
        ImageButton btnRateGood;
        ImageButton btnRateBad;
    }
}
