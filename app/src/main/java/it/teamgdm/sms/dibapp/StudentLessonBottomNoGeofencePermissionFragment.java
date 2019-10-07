package it.teamgdm.sms.dibapp;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentLessonBottomNoGeofencePermissionFragment extends Fragment {

    public StudentLessonBottomNoGeofencePermissionFragment() {
        // Required empty public constructor
    }

    static StudentLessonBottomNoGeofencePermissionFragment newInstance() {
        Log.i(Constants.TAG, StudentLessonBottomNoGeofencePermissionFragment.class.getSimpleName() + " -newInstance-");
        return new StudentLessonBottomNoGeofencePermissionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(Constants.TAG, getClass().getSimpleName() + " -onCreateView-");
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.student_lesson_bottom_no_geofence_permission_fragment, container, false);
        TextView noGeofencePermission = rootView.findViewById(R.id.noGeofencePermission);
        noGeofencePermission.setTextColor(Color.RED);
        return rootView;
    }

}