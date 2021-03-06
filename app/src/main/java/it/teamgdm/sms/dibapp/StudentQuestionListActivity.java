package it.teamgdm.sms.dibapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class StudentQuestionListActivity extends BaseActivity {

    //create ArrayList of String
    private ArrayList<Question> questionList;
    private static boolean addQuestionButtonStatus;
    ListView listView;
    View newQuestionLayout;
    TextView question_list_empty;
    EditText questionText;
    Button addQuestionButton;
    ImageButton submitQuestion;
    int lessonID;
    Menu menu;
    StudentQuestionAdapter studentQuestionAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create object of listview
        listView = findViewById(R.id.listviewstudent);
        newQuestionLayout = findViewById(R.id.addQuestionContainer);
        question_list_empty = findViewById(R.id.question_list_empty);
        addQuestionButton = findViewById(R.id.addQuestionButton);
        questionText = findViewById(R.id.questionText);
        submitQuestion = findViewById(R.id.questionSubmitButton);

        lessonID = getIntent().getIntExtra(Constants.KEY_LESSON_ID, 0);
        String className = getIntent().getStringExtra(Constants.KEY_CLASS_NAME);
        String date = getIntent().getStringExtra(Constants.KEY_LESSON_DATE);

        Objects.requireNonNull(getSupportActionBar()).setTitle(className + " - " + date);

        questionList = getQuestionList(lessonID);
        if(questionList != null) {
            question_list_empty.setVisibility(View.GONE);
        } else {
            question_list_empty.setVisibility(View.VISIBLE);
        }

        submitQuestion.setOnClickListener(submitQuestionListener);

        setListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        addQuestionButtonStatus = false;
    }

    void setListView() {
        studentQuestionAdapter = new StudentQuestionAdapter(this, R.layout.question_item, questionList);
        //assign adapter to listview
        listView.setAdapter(studentQuestionAdapter);
    }

    ArrayList<Question> getQuestionList(int lessonID) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -getQuestionList-");
        JSONArray response = DAO.getLessonQuestion(lessonID);
        ArrayList<Question> arrayList = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject obj = response.getJSONObject(i);
                Question q = Question.Builder.create(obj.optInt(Constants.KEY_QUESTION_ID, 0))
                        .setQuestion(response.getJSONObject(i).optString(Constants.KEY_QUESTION))
                        .setRate(response.getJSONObject(i).optInt(Constants.KEY_QUESTION_RATE, 0 ))
                        .build();
                arrayList.add(q);
                Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreate-Question " + q.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }

    private final View.OnClickListener submitQuestionListener = v -> {
        String input = questionText.getText().toString();
        Log.i(Constants.TAG, getClass().getSimpleName() + " -submitQuestionListener-");
        if(input.isEmpty()) {
            questionText.setError(getResources().getString(R.string.field) + " " + getResources().getString(R.string.inputCannotBeEmpty));
            questionText.requestFocus();
        } else {
            sendQuestion(lessonID, input);
            newQuestionLayout.setVisibility(View.GONE);
            questionText.setText("");
            addQuestionButtonStatus = !addQuestionButtonStatus;
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_add_24dp));
            questionList = getQuestionList(lessonID);
            setListView();
        }

    };

    public void sendQuestion(int lessonID, String input) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -sendQuestion-");
        if(DAO.sendQuestion(lessonID, input)) {
            Toast.makeText(this, getString(R.string.question_sent), Toast.LENGTH_SHORT).show();
            Log.i(Constants.TAG, getClass().getSimpleName() + " -sendQuestion-question sent-");
        } else {
            Toast.makeText(this, getResources().getString(R.string.question_not_sent), Toast.LENGTH_SHORT).show();
            Log.i(Constants.TAG, getClass().getSimpleName() + " -sendQuestion-question not sent-");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onOptionsItemSelected-");
        int id = item.getItemId();
        if (id == R.id.addQuestionButton) {
            addQuestionButtonStatus = !addQuestionButtonStatus;
            if(addQuestionButtonStatus) {
                newQuestionLayout.setVisibility(View.VISIBLE);
                item.setIcon(R.drawable.ic_cancel_24dp);
            } else {
                newQuestionLayout.setVisibility(View.GONE);
                item.setIcon(R.drawable.ic_add_24dp);
            }
            questionText.setError(null);
            questionText.setText(Constants.KEY_EMPTY);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreateOptionsMenu-");
        this.menu = menu;
        getMenuInflater().inflate(R.menu.question_menu, menu);
        return true;
    }

    @Override
    int getLayoutResource() {
        return R.layout.student_question_list_activity;
    }

}
