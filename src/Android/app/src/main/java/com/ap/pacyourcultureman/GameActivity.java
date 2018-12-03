package com.ap.pacyourcultureman;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ap.pacyourcultureman.Helpers.ApiHelper;
import com.ap.pacyourcultureman.Helpers.AppController;
import com.ap.pacyourcultureman.Helpers.BearingCalc;
import com.ap.pacyourcultureman.Helpers.CollisionDetection;
import com.ap.pacyourcultureman.Helpers.CollisionHandler;
import com.ap.pacyourcultureman.Helpers.GunHandler;
import com.ap.pacyourcultureman.Helpers.SightsAdapter;
import com.ap.pacyourcultureman.Menus.Gunmenu;
import com.ap.pacyourcultureman.Menus.NavigationMenu;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Ghost Blinky;
    //
    private RequestQueue mRequestQueue;
    private Double latA,lngA,latB,lngB;
    //
    private static final int MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION = 1;
    List<Assignment> assignments = ApiHelper.assignments;
    List<Dot> dots = ApiHelper.dots;
    //
    List<Dot> dotstest = new ArrayList<>();
    //
    Location mLastLocation;
    Location mCurrentLocation;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    Assignment currentAssigment;
    List<Marker> assigmentMarkers = new ArrayList<>();
    SlidingUpPanelLayout bottomPanel;
    TextView txtName, txtWebsite, txtShortDesc, txtLongDesc, txtCurrentScore, txtCurrentLifePoints, txtCurrentAssignment, txtCurrentHeading, txtCurrentDistance;
    ImageView imgSight;
    Marker selectedMarker;
    Location location;
    List<Circle> circles;
    Marker perth;
    Player player = ApiHelper.player;
    int userId;
    ApiHelper apiHelper;
    CollisionDetection collisionDetection;
    Intent iin;
    Bundle b;
    String jwt;
    static LatLng currentPos;
    static Boolean ghostCollide = false;
    BearingCalc bearingCalc;
    CollisionHandler collisionHandler;
    Gunmenu gunmenu;
    static final LatLng PERTH = new LatLng(51.230663, 4.407146);
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializer();
        Blinky = new Ghost();
        //
        mRequestQueue = Volley.newRequestQueue(this);
        latA = 51.229656;
        lngA = 4.402030;
        latB = 51.227970;
        lngB = 4.401912;
        parseRoadsApi(latA,lngA,latB,lngB);
        dotstest.add(new Dot(51.232212, 4.409960));
        //
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void parseRoadsApi(Double latA,Double lngA,Double latB, Double lngB) {
        final String URL = "https://roads.googleapis.com/v1/snapToRoads?path="+latA+","+lngA+"|"+latB+","+lngB+"&interpolate=true&key=AIzaSyB4HgIDhaV6sv3ddo_Xol9r4fDLj7RpOaU";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,URL,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.d("GoogleRoadsApi", "1 object");
                Log.d("GoogleRoadsApi", response.toString());
                try {
                    JSONArray snappedPoints = response.getJSONArray("snappedPoints");
                    for (int i = 0; i < snappedPoints.length(); i++) {
                        JSONObject location = snappedPoints.getJSONObject(i);
                        JSONObject loc = location.getJSONObject("location");
                        Double lat = loc.getDouble("latitude");
                        Double lng = loc.getDouble("longitude");
                        Log.d("GoogleRoadsApi", lat + "," + lng);
                        dotstest.add(new Dot(lat,lng));
                        //dotstest.add(new Dot(Double.valueOf(51.232212), Double.valueOf(4.409960)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mRequestQueue.add(request);
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onPause(){
        super.onPause();
        // Stops Location Updates if activity is paused
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.227076, 4.417227), 15));
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(500); // 0.5 second interval
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        currentAssigment = getRandomAssignment();
        //dots
        for (int i = 0; i < dots.size(); i++) {dots.get(i).Draw(mMap, getApplicationContext());}
        for (int i = 0; i < dotstest.size(); i++) {dotstest.get(i).Draw(mMap, getApplicationContext());}
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        //Blinky draw and dummy movement
        Blinky.Draw(mMap, getApplicationContext());
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Blinky.FollowPath(new LatLng(1,1), new LatLng(1,1));
            }
        });
        Log.d("Movement", "Ik ben non-blocking");
        List<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(51.217065, 4.397200));
        for(int i = 0; i < assignments.size(); i++) {
            Marker mark;
            LatLng assigmentMarker = new LatLng(assignments.get(i).lat, assignments.get(i).lon);
            mark = mMap.addMarker(new MarkerOptions().position(assigmentMarker).title(assignments.get(i).name));
            assigmentMarkers.add(mark);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION);
            }
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            }
        });
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle));

        if (!success) {
            Log.e("Style failed", "Style parsing failed.");
        }
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d("Draggable Marker loc: ", "latitude : "+ marker.getPosition().latitude + "longitude : " + marker.getPosition().longitude);
                currentPos = marker.getPosition();
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                if(collisionDetection.collisionDetect(marker.getPosition(), currentAssigment.latLng, 10)){
                    collisionHandler.currentAssigmentCollision();
                    currentAssigment = getRandomAssignment();
                    txtCurrentScore.setText(Integer.toString(player.getPlayerStats().getCurrentScore()));
                }
                for(int i = 0; i < dots.size(); i++) {
                    if(collisionDetection.collisionDetect(marker.getPosition(), new LatLng(dots.get(i).getLat(), dots.get(i).getLon()), 8)) {
                        player.getPlayerStats().setCurrentScore(player.getPlayerStats().getCurrentScore() + 1);
                        txtCurrentScore.setText("x " + player.getPlayerStats().getCurrentScore());
                    }
                }
                txtCurrentHeading.setText(bearingCalc.getBearingInString(marker.getPosition().latitude, marker.getPosition().longitude, currentAssigment.lat, currentAssigment.lon));
                txtCurrentAssignment.setText(currentAssigment.name);
                txtCurrentDistance.setText(bearingCalc.getDistance(marker.getPosition(), new LatLng(currentAssigment.lat, currentAssigment.lon)));
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

        });
        perth = mMap.addMarker(new MarkerOptions()
                .position(PERTH)
                .draggable(true));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCES_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // TODO: Disable Maps functionality
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                for(int i = 0; i < assignments.size(); i++) {
                    collisionDetection.collisionDetect(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(assignments.get(i).lat, assignments.get(i).lon), 10);

                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CircleOptions circleOptions = new CircleOptions();
                LatLng markable = perth.getPosition();
                collisionDetection.collisionDetect(markable, new LatLng(currentAssigment.lat, currentAssigment.lon), 10);
                Log.d(String.valueOf(markable.latitude), String.valueOf(markable.longitude));

                //dots collision
                for(int i = 0; i < dots.size(); i++) {
                    collisionDetection.collisionDetect(markable, new LatLng(dots.get(i).getLat(), dots.get(i).getLon()), 5);
                }
                if(ghostCollide) {
                    currentAssigment = getRandomAssignment();
                    ghostCollide = false;
                    collisionHandler.ghostCollision();
                    if(player.getPlayerGameStats().getLifePoints() == 0) {
                                        getRandomAssignment();
                    }
                    txtCurrentLifePoints.setText("x " + player.getPlayerGameStats().getLifePoints());
                }
            }
        }
    };
    private Assignment getRandomAssignment() {
        currentAssigment = currentAssigment.getRandomAssignment(GameActivity.this, mMap, currentAssigment, assignments, circles);
        txtCurrentAssignment.setText(currentAssigment.name);
        return currentAssigment;
    }


    @NonNull
    public static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        for(int i = 0; i < assignments.size(); i++) {
            if (marker.equals(assigmentMarkers.get(i)))
            {
                bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                bottomPanel.setPanelHeight(400);
                txtName.setText(assignments.get(i).name);
                if(assignments.get(i).website != "N/A") {
                    txtWebsite.setVisibility(View.VISIBLE);
                    txtWebsite.setText(assignments.get(i).website);
                }
                else txtWebsite.setVisibility(View.INVISIBLE);
                Picasso.get().load(assignments.get(i).imgUrl).into(imgSight);
                txtShortDesc.setText(assignments.get(i).shortDescr);
                txtLongDesc.setText(assignments.get(i).longDescr);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d("Pushed", "pushed");
        if(marker.equals(Blinky.marker)) {
                GunHandler gunHandler = new GunHandler(Blinky, this);
                gunHandler.gunHandler();
                gunmenu.gunUpdater();
            return true;
        }
        selectedMarker = marker;
        return false;
    }
    @Override
    public void onBackPressed() {
        if (bottomPanel != null &&
                (bottomPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)) {
            bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        }
        else if(bottomPanel.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            bottomPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
        else if(selectedMarker != null) {
                selectedMarker.hideInfoWindow();
        } else super.onBackPressed();

    }
    private void initializer() {
        apiHelper = new ApiHelper();
        bottomPanel = findViewById(R.id.sliding_layout);
        bottomPanel.setPanelHeight(0);
        txtName = findViewById(R.id.txtName);
        imgSight = findViewById(R.id.imgSight);
        txtWebsite = findViewById(R.id.txtWebsite);
        txtShortDesc = findViewById(R.id.txtShortDesc);
        txtLongDesc = findViewById(R.id.txtLongDesc);
        txtCurrentScore = findViewById(R.id.game_txt_currentscore);
        txtCurrentScore.setText("x " + player.getPlayerStats().getCurrentScore());
        txtCurrentLifePoints = findViewById(R.id.game_txt_currentLifePoints);
        txtCurrentLifePoints.setText("x " + ApiHelper.player.getPlayerGameStats().getLifePoints());
        txtCurrentAssignment = findViewById(R.id.game_txt_currentAssginment);
        txtCurrentHeading = findViewById(R.id.game_txt_currentHeading);
        txtCurrentDistance = findViewById(R.id.game_txt_currentDistance);
        NavigationMenu navigationMenu = new NavigationMenu(this);
        gunmenu = new Gunmenu(this);
        collisionDetection = new CollisionDetection();
        bearingCalc = new BearingCalc();
        collisionHandler = new CollisionHandler(GameActivity.this);
        currentAssigment = assignments.get(0);
        circles = new ArrayList<>();
        iin = getIntent();
        b = iin.getExtras();
        currentPos = PERTH;
        if(b!=null){
            userId = (int) b.get("userid");
            jwt = (String) b.get("jwt");
        }
    }



}
