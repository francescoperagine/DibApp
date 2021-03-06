package it.teamgdm.sms.dibapp;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.Geofence;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class StudentLessonListActivity extends BaseActivity implements
            DibappBroadcastReceiver.GeofenceReceiverInterface {

    static int geofenceTransitionAction = 0;
    private DibappBroadcastReceiver dibappBroadcastReceiver;
    private GeofenceAPI geofenceAPI;
    private IntentFilter intentFilter = new IntentFilter(Constants.GEOFENCE_TRANSITION_ACTION);

    Intent loginIntent;
    RecyclerView recyclerView;
    View classListContainer;
    RecyclerView.Adapter adapter;
    TextView textViewEmptyClassList;

    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreate-");
        super.onCreate(savedInstanceState);
        dibappBroadcastReceiver = new DibappBroadcastReceiver(this);
        geofenceAPI = new GeofenceAPI(this);
        loginIntent = getIntent();

        classListContainer = findViewById(R.id.classListContainer);
        recyclerView = findViewById(R.id.class_list);
        textViewEmptyClassList = findViewById(R.id.lesson_list_empty);
        String today = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(new Date());
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.lesson_today_schedule) + Constants.KEY_BLANK + today);
        disableBackButton();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -setupRecyclerView-");
        LessonList lessonListData = new StudentCareer();
        JSONArray lessonListLoader = DAO.getLessonList(Session.getUserID(), Constants.KEY_ROLE_STUDENT);
        lessonListData.setLessonList(lessonListLoader);
        ArrayList<Lesson> classList = lessonListData.getLessonList();
        if(classList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            textViewEmptyClassList.setVisibility(View.VISIBLE);
        } else {
            Log.i(Constants.TAG, getClass().getSimpleName() + " -setupRecyclerView-");
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new LessonRecyclerViewAdapter(this, classList, geofenceAPI);
            recyclerView.setAdapter(adapter);
            textViewEmptyClassList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onGeofenceTransitionAction(int geofenceReceiverAction) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onGeofenceTransitionAction-");
        geofenceTransitionAction = geofenceReceiverAction;
        if(adapter != null) adapter.notifyDataSetChanged();
        notifyGeofenceTransition();
    }

    void notifyGeofenceTransition() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -getBottomFragment-"+ StudentLessonListActivity.geofenceTransitionAction);
        if (StudentLessonListActivity.geofenceTransitionAction == Geofence.GEOFENCE_TRANSITION_DWELL) {
            String message = getResources().getString(R.string.geofence_transition_dwelling);
            Log.i(Constants.TAG, getClass().getSimpleName() + " " + message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            String message = getResources().getString(R.string.geofence_transition_left);
            Log.i(Constants.TAG, getClass().getSimpleName() + " " + message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onResume-");
        super.onResume();
        if(geofenceAPI != null) {
            if(geofenceAPI.hasGeofencePermissions()){
                startLocationUpdates();
            } else {
                geofenceAPI.askGeofencePermissions();
            }
        }
    }

    private void startLocationUpdates() {
        registerReceiver(dibappBroadcastReceiver, intentFilter);
        if(geofenceAPI != null) {
            geofenceAPI.startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onPause-");
        super.onPause();
        if(geofenceAPI != null) {
            if(geofenceAPI.hasGeofencePermissions()){
                stopLocationsUpdate();
            } else {
                geofenceAPI.askGeofencePermissions();
            }
        }
    }

    private void stopLocationsUpdate() {
        if(geofenceAPI != null) {
            geofenceAPI.stopLocationUpdates();
            unregisterReceiver(dibappBroadcastReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onRequestPermissionsResult-\t");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(hasAllPermissionsGranted(grantResults)) {
            geofenceAPI = new GeofenceAPI(this);
            startLocationUpdates();
        } else {
            geofenceAPI = null;
        }
    }

    public boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onDestroy-");
        super.onDestroy();
        if(geofenceAPI != null) {
            geofenceAPI.removeGeofences();
        }
    }

    @Override
    protected int getLayoutResource() {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -getLayoutResource-");
        return R.layout.item_list_activity;
    }

    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
