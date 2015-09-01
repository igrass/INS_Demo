package cc.minieye.objects;

import android.location.Location;
import cc.minieye.kalman.maths.CVector;
import cc.minieye.kalman.util.Constants;
import cc.minieye.kalman.util.LVLH;
import cc.minieye.kalman.util.WGS84;

public class GPSListener {
    public static int GPS_DISABLE;
    public static int GPS_FIXING;
    public static int GPS_READY;
    private double altitude;
    private double filteredAltitude;
    protected float gpsaccuracy;
    protected float gpsheading;
    protected CVector gpspos;
    protected CVector gpsvel;
    private double groundSpeed;
    private double lastTimeStamp;
    private double lastaltitude;
    private double latitude;
    private double longitude;
    private long mInitialTimeStamp;
    private int nfilteredAltitude;

    static {
        GPS_READY = 1;
        GPS_DISABLE = 0;
        GPS_FIXING = 2;
    }

    public GPSListener() {
        this.lastaltitude = 0.0d;
        this.lastTimeStamp = 0.0d;
        this.groundSpeed = 0.0d;
        this.gpspos = new CVector(3);
        this.gpsvel = new CVector(3);
        this.gpsheading = 0.0f;
        this.gpsaccuracy = 0.0f;
        this.filteredAltitude = 0.0d;
        this.nfilteredAltitude = 0;
    }

    private double estimateVerticalSpeed(double d, Location location) {
        this.altitude = location.getAltitude();
        this.filteredAltitude += this.altitude;
        this.nfilteredAltitude++;
        double d2 = d - this.lastTimeStamp;
        if (d2 <= 0.3d) {
            return 0.0d;
        }
        this.filteredAltitude /= (double) this.nfilteredAltitude;
        d2 = (this.filteredAltitude - this.lastaltitude) / d2;
        this.lastTimeStamp = d;
        this.lastaltitude = this.filteredAltitude;
        this.filteredAltitude = 0.0d;
        this.nfilteredAltitude = 0;
        return d2;
    }

    private CVector getPositionFromLoc(Location location) {
        return WGS84.wgs84Coordinate(location.getLatitude() * Constants.DEG2RAD, location.getLongitude() * Constants.DEG2RAD, location.getAltitude());
    }

    private CVector getVelocityFromLoc(Location location, double d) {
        double cos = Math.cos(Constants.DEG2RAD * ((double) location.getBearing())) * ((double) location.getSpeed());
        return LVLH.getVelocityInECEF(new CVector(new double[]{((double) location.getSpeed()) * Math.sin(Constants.DEG2RAD * ((double) location.getBearing())), cos, d}), Constants.DEG2RAD * location.getLatitude(), Constants.DEG2RAD * location.getLongitude());
    }

    protected void Init() {
        this.mInitialTimeStamp = System.currentTimeMillis();
    }

    public float getAccuracy() {
        return this.gpsaccuracy;
    }

    public double getAltitude() {
        return this.altitude;
    }

    public CVector getECEFCartesianPosition() {
        return this.gpspos;
    }

    public double getGroundSpeed() {
        return this.groundSpeed;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public CVector getPosition() {
        return this.gpspos;
    }

    public CVector getVelocity() {
        return this.gpsvel;
    }

    public boolean setLocation(Location location) {
        if (location == null) {
            return false;
        }
        double time = ((double) (location.getTime() - this.mInitialTimeStamp)) / 1000.0d;
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.groundSpeed = (double) location.getSpeed();
        this.gpsaccuracy = location.getAccuracy();
        this.gpspos = getPositionFromLoc(location);
        this.gpsvel = getVelocityFromLoc(location, estimateVerticalSpeed(time, location));
        this.gpsheading = location.getBearing();
        return true;
    }
}
