package it.teamgdm.sms.dibapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * An activity representing a list of Exams. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ClassDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ClassListActivity extends BaseActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    Intent loginIntent;

    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreate-");
        super.onCreate(savedInstanceState);
        loginIntent = getIntent();

        if (findViewById(R.id.exam_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.exam_list);

        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected int getLayoutResource() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -getLayoutResource-");
        return R.layout.activity_class_list;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -setupRecyclerView-");
        ArrayList<Exam> classList;
        JSONArray classListLoader;

        if(loginIntent.hasExtra(Constants.KEY_ROLE_PROFESSOR)) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -setupRecyclerView-professorTeaching");
            ProfessorTeaching professorTeaching = new ProfessorTeaching();
            classListLoader = loadClassList(Constants.KEY_ROLE_PROFESSOR);
            professorTeaching.setClassList(classListLoader);
            classList = professorTeaching.getClassList();
        } else {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -setupRecyclerView-studentCareer");
            StudentCareer studentCareer = new StudentCareer();
            classListLoader = loadClassList(Constants.KEY_ROLE_STUDENT);
            studentCareer.setClassList(classListLoader);
            classList = studentCareer.getClassList();
        }
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, classList, mTwoPane));
    }

    private static JSONArray loadClassList(String userRole) {
        Log.i(Constants.TAG, ClassListActivity.class.getSimpleName() + " -loadClassList-");
        JSONObject data = new JSONObject();
        JSONArray response = new JSONArray();
        try {
            data.put(Constants.KEY_ACTION, Constants.GET_CLASS_LIST);
            data.put(Constants.KEY_USER_ID, Session.getUserID());
            data.put(Constants.KEY_USER_ROLE_NAME, userRole);

            Log.i(Constants.TAG, ClassListActivity.class.getSimpleName() + " -loadClassList-data "+data);
            AsyncTaskConnection asyncTaskConnection = new AsyncTaskConnection();
            asyncTaskConnection.execute(data);
            response = asyncTaskConnection.get();
            Log.i(Constants.TAG, ClassListActivity.class.getSimpleName() + " -loadClassList-response "+response);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ClassListActivity mParentActivity;
        private final boolean mTwoPane;
        ArrayList<Exam> classList;

        SimpleItemRecyclerViewAdapter(ClassListActivity parent, ArrayList<Exam> classList, boolean twoPane) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -SimpleItemRecyclerViewAdapter-");
            this.classList = classList;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Exam exam = ClassList.getClassFromID((Integer) view.getTag());

                Log.i(Constants.TAG, getClass().getSimpleName() + " SimpleItemRecyclerViewAdapter-OnClickListener- Exam " + exam);
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putInt(ClassDashboardFragment.ARG_ITEM_ID, exam.getID());
                    Log.i(Constants.TAG, getClass().getSimpleName() + " SimpleItemRecyclerViewAdapter-OnClickListener-mTwoPane- arguments " + ClassDashboardFragment.ARG_ITEM_ID + " " + exam.getID());
                    ClassDashboardFragment fragment = new ClassDashboardFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.exam_detail_container, fragment).commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ClassDetailActivity.class);
                    intent.setAction(Constants.CLASS_LIST_ACTION);
                    intent.putExtra(ClassDashboardFragment.ARG_ITEM_ID, exam.getID());
                    Log.i(Constants.TAG, getClass().getSimpleName() + " SimpleItemRecyclerViewAdapter-OnClickListener- putExtra " + ClassDashboardFragment.ARG_ITEM_ID + " " + exam.getID());
                    context.startActivity(intent);
                }
            }
        };

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreateViewHolder-");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -onBindViewHolder- Position " + position);
            holder.titleView.setText(classList.get(position).getName());
            holder.itemView.setTag(classList.get(position).getID());
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -onBindViewHolder-");
            return classList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView titleView;

            ViewHolder(View view) {
                super(view);
                Log.i(Constants.TAG, getClass().getSimpleName() + " -ViewHolder-");
                titleView = view.findViewById(R.id.content);
            }
        }
    }
}
