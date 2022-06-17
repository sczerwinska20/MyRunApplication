package appentwicklung.android.myrunapplication;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import appentwicklung.android.myrunapplication.helper.DBHelper;
import appentwicklung.android.myrunapplication.helper.HelperUtils;

/**
 * Service that tracks the user´s location (every 5th second) and stores it in a SQLite database.
 * Broadcasts new locations to the MainActivity, where the new location is updated in the GoogleMap.
 *
 * @author Daniel Johansson
 */
public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    /**
     * GoogleApiClient
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Location Request
     */
    private LocationRequest mLocationRequest;

    /**
     * Elapsed time of the workout session
     */
    private long mElapsedTime;

    /**
     * Service start time
     */
    private long mServiceStartTime;

    /**
     * Id of the WorkoutSession
     */
    private long mSessionId;

    /**
     * DBHelper
     */
    private DBHelper mDb;

    /**
     * Status of the service
     */
    private static boolean runningService = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mDb = new DBHelper(this);

        createLocationRequest();

        //Creates the GoogleApiClient and connects it
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    /**
     * Creates a LocationRequest
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runningService = true;

        //Sets service start time in seconds
        mServiceStartTime = System.currentTimeMillis() / 1000;

        //Gets current session id from the intent
        mSessionId = Long.valueOf(intent.getExtras().getString("sessionId"));

        //Gets elapsed time from the intent
        mElapsedTime = Long.valueOf(intent.getExtras().getString("elapsedTime"));

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        //If permission ACCESS_FINE_LOCATION is granted - gets the last location
                        //available and requests location updates
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            Location startLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        long locId = saveLocationToDb(location);          //Saves the location to db

        //Sends a local broadcast to the RouteBroadCastReceiver in MainActivity
        Intent localBroadcastIntent = new Intent("com.daniel.workouttracker.TrackingService");
        localBroadcastIntent.putExtra("RESULT_CODE", "LOCAL");
        localBroadcastIntent.putExtra("sessionId", Long.toString(mSessionId));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localBroadcastIntent);
    }

    /**
     * Saves the users location (WorkoutLocation) to db
     *
     * @param location location of the user
     * @return id of the stored WorkoutLocation
     */
    public long saveLocationToDb(Location location) {
        //Sets the elapsed time in service
        long elapsedTimeInService = (System.currentTimeMillis() / 1000) - mServiceStartTime;

        //Total elapsed time=elapsed time from MainActivity + elapsed time in service
        long totalElapsedTime = mElapsedTime + elapsedTimeInService;

        //Saves the WorkoutLocation to db
        long id = mDb.createWorkoutLocation(mSessionId, String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()), HelperUtils.formatDouble(location.getAltitude()),
                HelperUtils.convertSpeed(location.getSpeed()), totalElapsedTime);

        return id;
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        Log.d("TrackingService", "TrackingService is being killed");
        runningService = false;
        super.onDestroy();
    }

    /**
     * Checks if the service is running
     *
     * @return boolean that shows the status of the service
     */
    public static boolean isServiceRunning() {
        return runningService;
    }
}
