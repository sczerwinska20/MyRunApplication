package appentwicklung.android.myrunapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for the WorkoutSession
 *
 * @author Daniel Johanssonse
 */
public class WorkoutSession implements Parcelable {

    /**
     * Id of the WorkoutSession
     */
    private int mId;

    /**
     * Start date of the WorkoutSession
     */
    private String mStartDate;

    /**
     * Start time of the WorkoutSession
     */
    private String mStartTime;

    /**
     * Duration of the WorkoutSession, in seconds
     */
    private long mDuration;

    /**
     * Distance of the WorkoutSession, in metres
     */
    private float mDistance;

    /**
     * List of the WorkoutSession´s WorkoutLocations
     */
    private List<WorkoutLocation> mLocations = new ArrayList<>();

    public WorkoutSession() {
    }

    /**
     * Constructor
     *
     * @param startDate start date of the WorkoutSession
     * @param startTime start time of the WorkoutSession
     * @param duration  duration of the WorkoutSession
     * @param distance  distance of the WorkoutSession
     */
    public WorkoutSession(String startDate, String startTime, long duration, float distance) {
        this.mStartDate = startDate;
        this.mStartTime = startTime;
        this.mDuration = duration;
        this.mDistance = distance;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public float getDistance() {
        return mDistance;
    }

    public void setDistance(float distance) {
        this.mDistance = distance;
    }

    public List<WorkoutLocation> getLocations() {
        return mLocations;
    }

    public void setLocations(List<WorkoutLocation> locations) {
        this.mLocations = locations;
    }

    /**
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mId);
        out.writeString(mStartDate);
        out.writeString(mStartTime);
        out.writeLong(mDuration);
        out.writeFloat(mDistance);
        out.writeTypedList(mLocations);
    }

    public static final Creator<WorkoutSession> CREATOR = new Creator<WorkoutSession>() {
        public WorkoutSession createFromParcel(Parcel in) {
            return new WorkoutSession(in);
        }

        public WorkoutSession[] newArray(int size) {
            return new WorkoutSession[size];
        }
    };

    /**
     * Constructor used by Parcelable.Creator
     */
    private WorkoutSession(Parcel in) {
        mId = in.readInt();
        mStartDate = in.readString();
        mStartTime = in.readString();
        mDuration = in.readLong();
        mDistance = in.readFloat();
        mLocations = in.createTypedArrayList(WorkoutLocation.CREATOR);
    }

    public int describeContents() {
        return 0;
    }

}
