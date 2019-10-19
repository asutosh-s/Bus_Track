package com.example.android.uber;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener, DirectionCallback {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private Button mRequest, busSchedule, mclickBut;
    private EditText mBusnoE;
    private TextView totalBus;
    private LatLng pickupLocation, driverLatLng;
    private int count = 0, availableBus = 0;
    private String Busno;
    private SupportMapFragment mapFragment;
    private static String[] stops={"Block IX","Admin Block","Boy's Hostel","Type 'B' quarters","Type 'D' quarters","Gate 2","Danapur","Saguna More","Hartali More","Boring Road","Patliputra Golambar"};


    private ProgressDialog progressDialog;
    private Marker mRiderMarker;
    final int LOCATION_REQUEST_CODE = 1;
    private boolean snippet = false;
    List<Marker> markerList = new ArrayList<Marker>();

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    private Toast toast = null;
    private LatLng source;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        View mapView = mapFragment.getView();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        } else {
            mapFragment.getMapAsync(this);
        }

        //change the position of default location marker
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        //adding the ListView
        ListView listView = findViewById(R.id.ListView);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                new String[] {"Bus no - 1","Bus no - 2","Bus no - 3"}));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView <? > arg0, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
//                Toast.makeText(CustomerMapActivity.this, position +"-"+ id, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CustomerMapActivity.this, BusDetails.class));
                if(position>=0) {

                }
            }
        });

        polylines = new ArrayList<>();

        totalBus = (TextView) findViewById(R.id.total_bus);
        busSchedule = (Button) findViewById(R.id.schedule);
        busSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerMapActivity.this, Bus_Schedule.class));
            }
        });
        mRequest = (Button) findViewById(R.id.request);
        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                radius = 1;
                mRequest.setEnabled(false);
//                mRequest.setVisibility(View.GONE);

                getClosestDriver();
            }
        });

        mclickBut = findViewById(R.id.clickBut);
        mclickBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBusnoE = (EditText) findViewById(R.id.BusnoE);
                Busno = mBusnoE.getText().toString();
                clickRoute(Busno);
            }
        });
//        mBusnoE = (EditText) findViewById(R.id.BusnoE);
//        Busno = mBusnoE.getText().toString();
//        Toast.makeText(this, Busno + "not found !!!", Toast.LENGTH_SHORT).show();
    }

    private  void clickRoute(String Busno){
        for(Marker markerIt : markerList) {
            if(markerIt.getTitle().equals("Bus" + Busno))
            {
                LatLng latLng = markerIt.getPosition();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                return;
            }
        }
    }

    GeoQuery geoQuery1 = null;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //disable the direction pointer
        mMap.getUiSettings().setMapToolbarEnabled(false);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading map...");
        progressDialog.show();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        buildGoogleApiClient();

        mMap.setMyLocationEnabled(true);

        requestDirection();
//        LatLng latLng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                DatabaseReference destination = FirebaseDatabase.getInstance().getReference().child("Drivers Data");
                GeoFire geoFire = new GeoFire(destination);
                source = marker.getPosition();
                if(marker.getSnippet()!=null)
                {
                    snippet = true;
                }
                else snippet = false;

                pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                if (mRiderMarker != null) {
                    mRiderMarker.remove();
                }
                mRiderMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Your are here!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user)));


                if(geoQuery1!=null)
                {
                    geoQuery1.removeAllListeners();
                }
                geoQuery1 = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 500);
                geoQuery1.addGeoQueryEventListener(new GeoQueryEventListener() {
                    @Override
                    public void onKeyEntered(String key, GeoLocation location) {

                    }

                    @Override
                    public void onKeyExited(String key) {

                    }

                    @Override
                    public void onKeyMoved(String key, GeoLocation location) {
//                        erasePolylines();
//
//                        getRouteToMarker(source);
                        driverFound = false;
                        getClosestDriver();
                    }



                    @Override
                    public void onGeoQueryReady() {

                    }

                    @Override
                    public void onGeoQueryError(DatabaseError error) {

                    }
                });
                return false;
            }
        });

        return;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    boolean getAllDrivers = false;
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        progressDialog.dismiss();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(!getAllDrivers) {
            getDriversAround();
            getAllDrivers = true;
        }
//        if (count < 2) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//            count++;
//        }


    }

    private double radius = 0.01;
    private Boolean driverFound = false;
    private String driverFoundId;

    private void getClosestDriver() {

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Finding Bus...");
//        progressDialog.show();
        mRequest.setText("Finding Bus...");

        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("Drivers Data");
//        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driverAvailable");

        GeoFire geoFire = new GeoFire(driverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(source.latitude, source.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound) {
                    driverFound = true;
                    driverFoundId = key;

                    //IT WAS MEANT TO TELL WHICH CUSTOMER TO PICKUP
//                    progressDialog.dismiss();
                    getDriverLocation();
                    mRequest.setText("Bus found");
//                    Toast.makeText(RiderMapActivity.this, "Bus Found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                count++;
                if(count <500) {
                    if (!driverFound) {
                        radius += 0.01;
                        getClosestDriver();
                    }
                }
                else
                {
//                    mRequest.setVisibility(View.VISIBLE);
                    mRequest.setText("No bus available!");
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private boolean arrival = false;
    private Marker mDriverMarker;

    private void getDriverLocation() {
        DatabaseReference driverLocationRef = FirebaseDatabase.getInstance().getReference().child("Drivers Data").child(driverFoundId).child("l");//THE CHILD WAS MEANT TO BE driversWorking
        driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
//                    Toast.makeText(RiderMapActivity.this, "Bus found!", Toast.LENGTH_SHORT).show();
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    source = new LatLng(locationLat, locationLng);

                    if (mRiderMarker != null) {
                        mRiderMarker.remove();
                    }
//                    mRiderMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Your are here!").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user)));

//                    if (mDriverMarker != null) {
//                        mDriverMarker.remove();
//                    }
//                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus)));

                    Location loc1 = new Location(" ");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);    //pickupLocation.longitude

                    Location loc2 = new Location(" ");
                    loc2.setLatitude(source.latitude);
                    loc2.setLongitude(source.longitude);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(pickupLocation);
                    builder.include(source);
                    LatLngBounds bounds = builder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int padding = (int) (width * 0.2);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cameraUpdate);

                    erasePolylines();
                    getRouteToMarker(source);

                    float distance = loc1.distanceTo(loc2);

                    if (distance < 70) {
//                        Toast.makeText(RiderMapActivity.this, "Bus arrived", Toast.LENGTH_LONG).show();
//                        mRequest.setVisibility(View.VISIBLE);
                        if(toast!=null)
                        {
                            toast.cancel();
                        }
                        if(snippet==false)
                        {
                            toast = Toast.makeText(CustomerMapActivity.this, "Bus arrived", Toast.LENGTH_SHORT);
                        }
                        else
                            toast = Toast.makeText(CustomerMapActivity.this, "Bus Stop arrived", Toast.LENGTH_SHORT);
                        toast.show();
                        mRequest.setText("Bus Arrived");
                        arrival = true;
                    }
//                    else Toast.makeText(RiderMapActivity.this, "Bus " + numberAsString + "kms away", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getRouteToMarker(LatLng driverLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .key("AIzaSyDg5TWM1HZUZVZ0C8LG5tdehz6Nm-S4694")
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), driverLatLng)
                .build();
        routing.execute();

    }


    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        erasePolylines();
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.parseColor("#ff6666"));
            polyOptions.width(20);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            if (!arrival) {
                if(toast!=null)
                {
                    toast.cancel();
                }
                if(snippet==false)
                {
                    toast = Toast.makeText(this, "Bus " + route.get(i).getDistanceValue() / 1000.0 + "kms away", Toast.LENGTH_SHORT);
                }
                else
                    toast = Toast.makeText(this, "Bus Stop " + route.get(i).getDistanceValue() / 1000.0 + "kms away", Toast.LENGTH_SHORT);
                toast.show();
//                Toast.makeText(this, "Bus " + route.get(i).getDistanceValue() / 1000.0 + "kms away", Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), "Bus " + route.get(i).getDistanceValue() / 1000.0 + "kms away", Toast.LENGTH_SHORT).show();
//                mRequest.setVisibility(View.VISIBLE);
                mRequest.setText("Bus " + route.get(i).getDistanceValue() / 1000.0 + "kms away");
            }
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    public void requestDirection() {
//        Snackbar.make(btnRequestDirection, "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        List<LatLng> waypoints = Arrays.asList(
                new LatLng(25.535262, 84.851673),//Admin Block
                new LatLng(25.540567, 84.851615),//Boy's Hostel
                new LatLng(25.547983, 84.858946),//Type 'B' quarters
                new LatLng(25.552066, 84.859553),//Type 'D' quarters
                new LatLng(25.554656, 84.857486),//Gate 2
                new LatLng(25.582967, 85.04339),//Danapur
                new LatLng(25.623005, 85.041389),//Saguna More
                new LatLng(25.607063, 85.117137),//Hartali More
                new LatLng(25.616443, 85.113839)//Boring Road

        );
        GoogleDirection.withServerKey("AIzaSyDg5TWM1HZUZVZ0C8LG5tdehz6Nm-S4694")
                .from(new LatLng(25.532351, 84.851926))//Block IX
                .and(waypoints)
                .to(new LatLng(25.614783, 85.146805))//Patliputra Golambar
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
//            com.akexorcist.googledirection.model.Route route;
            int legCount = route.getLegList().size();

            for (int index = 0; index < legCount; index++) {
                Leg leg = route.getLegList().get(index);
                mMap.addMarker(new MarkerOptions().title(stops[index]).snippet("Bus Stop").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stand)).position(leg.getStartLocation().getCoordination()));
//                Toast.makeText(this, "number", Toast.LENGTH_SHORT).show();
                if (index == legCount - 1) {
                    mMap.addMarker(new MarkerOptions().title(stops[10]).snippet("Bus Stop").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_stand)).position(leg.getEndLocation().getCoordination()));

                }
                List<Step> stepList = leg.getStepList();
                ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(this, stepList, 4, Color.parseColor("#9966ff"), 3, Color.BLUE);
                for (PolylineOptions polylineOption : polylineOptionList) {
                    mMap.addPolyline(polylineOption);
                }
            }
            setCameraWithCoordinationBounds(route);
        }

    }

    @Override
    public void onDirectionFailure(Throwable t) {
        Toast.makeText(this, "Route failed", Toast.LENGTH_SHORT).show();
    }


    private void setCameraWithCoordinationBounds(com.akexorcist.googledirection.model.Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

    }

    private void getDriversAround() {
        DatabaseReference driversLocation = FirebaseDatabase.getInstance().getReference().child("Drivers Data");
        GeoFire geoFire = new GeoFire(driversLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 500);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for(Marker markerIt : markerList) {
                    if(markerIt.getTag().equals(key))
                        return;
                }

                LatLng driverLocation = new LatLng(location.latitude, location.longitude);
                availableBus++;
                Marker mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLocation).title("Bus" + availableBus).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_bus)));
                mDriverMarker.setTag(key);

                markerList.add(mDriverMarker);
                totalBus.setText("Total Bus: " + availableBus);
            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markerList) {
                    if(markerIt.getTag().equals(key))
                    {
                        availableBus--;
                        markerIt.remove();
                        markerList.remove(markerIt);
                        totalBus.setText("Total Bus: " + availableBus);
                        return;
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markerList) {
                    if(markerIt.getTag().equals(key)) {
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}

