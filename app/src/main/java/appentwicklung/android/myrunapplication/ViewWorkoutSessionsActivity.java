package appentwicklung.android.myrunapplication;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import appentwicklung.android.myrunapplication.helper.DBHelper;
import appentwicklung.android.myrunapplication.model.WorkoutSession;

/**
 * Activity class for viewing WorkoutSessions
 *
 * @author Daniel Johansson
 */
public class ViewWorkoutSessionsActivity extends AppCompatActivity implements
        DeleteSessionDialog.DeleteSessionDialogListener {

    /** ArrayAdapter */
    private WorkoutSessionActivity mSessionArrayAdapter;

    /** List of WorkoutSessions */
    private List<WorkoutSession> mSessions;

    /** Id of WorkoutSession */
    private long mSessionId;

    /** DBHelper object */
    private DBHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_sessions);


        //Constructs the data source
        mDb = new DBHelper(this.getApplicationContext());
        mSessions = mDb.getAllWorkoutSessions();

        //Creates the adapter to convert the array to views
        mSessionArrayAdapter = new WorkoutSessionActivity(this, mSessions);

    }



    /**
     * Handles a positive click on the DeleteSessionDialog. Deletes the selected WorkoutSession,
     * updates data in the adapter and notifies that the data has changed
     *
     * @param dialog DeleteSessionDialog
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mDb.deleteWorkoutSession(mSessionId);                     //Delete the selected WorkoutSession

        //Updates data in the adapter
        mSessions = mDb.getAllWorkoutSessions();            //Gets all WorkoutSessions from db
        mSessionArrayAdapter.clear();
        mSessionArrayAdapter.addAll(mSessions);

        //Notifies that the data set has changed to refresh the listview
        mSessionArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}

