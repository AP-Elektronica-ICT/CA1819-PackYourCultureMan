package com.ap.pacyourcultureman;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.ap.pacyourcultureman.Helpers.GetBitmap.getBitmapFromDrawable;


public class Dot {

    private LatLng location;
    private Marker marker;
    private  int height;
    private  int width;
    private int id;
    private boolean taken;
    private Double lat, lon;

    //first constructor with location lar and lon only
    public Dot(double lat, double lon){

        //init default settings
        height = 40;
        width = 40;
        taken = false;

        //set
        this.lat = lat;
        this.lon = lon;
        this.location =  new LatLng(lat,lon);
    }

    //2nd constructor with location,boolean and id
    public Dot(int id ,double lat, double lon, boolean taken ){

        //init default settings
        height = 40;
        width = 40;
        this.taken = false;

        //set
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.location =  new LatLng(lat,lon);
        this.taken = taken;
    }


// getters and setters

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
    public void setLocation(LatLng location){
        this.location = location;
    }
    public void setLocationWithLatAndLon(double lat,double lon){
        this.location =  new LatLng(lat,lon);
    }
    public void setTaken(){
        this.taken = true;
    }
    public void setHeight(int height){
        this.height = height;
    }
    public void setWidth( int width){
        this.width = width;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }
    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }
    public Marker getMarker() { return marker;}
    public Double getLon() {
        return lon;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }


    //methods
    public void removeMarker() {
        marker.remove();
    }

    public void setMarkerVisible(Marker marker, boolean bool) {
        marker.setVisible(bool);
        this.marker = marker;
    }

    public void Draw(GoogleMap mMap, Context context){
        Bitmap dot = getBitmapFromDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.pacman_dot, null));
        Bitmap scaledDot = Bitmap.createScaledBitmap(dot, width, height, false);
        marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(scaledDot))
                .flat(true)
                .anchor(0.5f,0.5f));
    }
}