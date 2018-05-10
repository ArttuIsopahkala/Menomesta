package com.ardeapps.menomesta.fragments;


import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ardeapps.menomesta.AppRes;
import com.ardeapps.menomesta.FbRes;
import com.ardeapps.menomesta.R;
import com.ardeapps.menomesta.objects.Bar;
import com.ardeapps.menomesta.objects.FacebookBarDetails;
import com.ardeapps.menomesta.objects.RatingStat;
import com.ardeapps.menomesta.objects.Vote;
import com.ardeapps.menomesta.utils.Helper;
import com.ardeapps.menomesta.views.Loader;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {

    MapView mMapView;
    Map<String, Bar> bars;
    Map<String, ArrayList<Vote>> votes;
    Map<String, RatingStat> allTimeRatingStats;
    TextView titleText;
    LinearLayout showClosest;
    AppRes appRes;
    private GoogleMap googleMap;
    String barName;

    public void refreshData(String barName) {
        this.barName = barName;
        appRes = (AppRes) AppRes.getContext();
        bars = appRes.getBars();
        votes = appRes.getVotes();
        allTimeRatingStats = appRes.getAllTimeRatingStats();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        titleText = (TextView) v.findViewById(R.id.title);
        showClosest = (LinearLayout) v.findViewById(R.id.showClosest);

        titleText.setText(getString(R.string.map_title));

        mMapView.onCreate(savedInstanceState);

        try {
            MapsInitializer.initialize(AppRes.getContext());
            Loader.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.clear();
                Loader.hide();
                // For showing a move to my location button
                final boolean locationPermissionGranted = ContextCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (locationPermissionGranted) {
                    googleMap.setMyLocationEnabled(true);
                }

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    // Use default InfoWindow frame
                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    // Defines the contents of the InfoWindow
                    @Override
                    public View getInfoContents(Marker marker) {

                        // Getting view from the layout file info_window_layout
                        View v = inflater.inflate(R.layout.infowindow, null);
                        v.setLayoutParams(new LinearLayout.LayoutParams(400, LinearLayout.LayoutParams.WRAP_CONTENT));

                        TextView title = (TextView) v.findViewById(R.id.title);
                        RatingBar rating = (RatingBar) v.findViewById(R.id.rating);
                        TextView participants = (TextView) v.findViewById(R.id.participants);

                        title.setText(marker.getTitle());
                        int voteCount = Integer.parseInt(marker.getSnippet());
                        float ratingCount = (float) marker.getTag();
                        if (voteCount == 0) {
                            participants.setText(getString(R.string.no_votes));
                        } else if (voteCount == 1) {
                            participants.setText(voteCount + " " + getString(R.string.vote));
                        } else if (voteCount > 1) {
                            participants.setText(voteCount + " " + getString(R.string.votes));
                        }
                        if (ratingCount == 0) {
                            rating.setVisibility(View.GONE);
                        } else {
                            rating.setNumStars((int) Math.ceil(ratingCount));
                            rating.setRating(ratingCount);
                        }

                        return v;
                    }
                });

                final List<Marker> markers = new ArrayList<>();
                for (Bar bar : bars.values()) {
                    int allBarsVoteCount = 0;
                    int voteCount = 0;
                    LatLng currentBar;
                    FacebookBarDetails detail = FbRes.getBarDetail(bar.barId);
                    if(detail != null && detail.barLocation != null) {
                        currentBar = new LatLng(detail.barLocation.latitude, detail.barLocation.longitude);
                    } else {
                        currentBar = new LatLng(bar.latitude, bar.longitude);
                    }
                    if (votes.get(bar.barId) != null) {
                        voteCount = votes.get(bar.barId).size();
                    }
                    for (ArrayList<Vote> votes : votes.values()) {
                        allBarsVoteCount += votes.size();
                    }

                    Marker newMarker = googleMap.addMarker(new MarkerOptions().position(currentBar).icon(getMarkerIcon(voteCount, allBarsVoteCount)).title(FbRes.getBarDetail(bar.barId) != null ? FbRes.getBarDetail(bar.barId).name : bar.name));
                    if (voteCount == 0) {
                        newMarker.setSnippet("0");
                    } else {
                        newMarker.setSnippet(String.valueOf(voteCount));
                    }
                    RatingStat ratingStat = allTimeRatingStats.get(bar.barId);
                    if (ratingStat != null && ratingStat.ratingCount > 0) {
                        newMarker.setTag((float) (ratingStat.ratingSum / ratingStat.ratingCount));
                    } else {
                        newMarker.setTag(0f);
                    }
                    markers.add(newMarker);
                }

                if (barName == null) {
                    LatLng position = Helper.getLocationFromAddress(AppRes.getCity());
                    if (position != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(14).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                        // Suomen sijainti
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(61.92411, 25.748151)).zoom(5).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        showEnableGPSDialog();
                    }
                } else {
                    for (Marker marker : markers) {
                        if (marker.getTitle().equals(barName)) {
                            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(marker.getPosition()).zoom(14).build()));
                            marker.showInfoWindow();
                            break;
                        }
                    }
                }

                showClosest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location location = appRes.getLocation();
                        if (location != null && markers.size() > 0) {
                            Marker closestMarker = markers.get(0);
                            float lastMarker = 9999999999f;
                            for (Marker marker : markers) {
                                float[] results = new float[1];
                                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                                        marker.getPosition().latitude, marker.getPosition().longitude, results);
                                if (results[0] < lastMarker) {
                                    closestMarker = marker;
                                    lastMarker = results[0];
                                }
                            }
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(closestMarker.getPosition()).zoom(14).build()));
                            closestMarker.showInfoWindow();
                            Answers.getInstance().logCustom(new CustomEvent("Lähin baari etsitty")
                                    .putCustomAttribute("Sijainti löydetty", "kyllä"));
                        } else {
                            showEnableGPSDialog();
                            Answers.getInstance().logCustom(new CustomEvent("Lähin baari etsitty")
                                    .putCustomAttribute("Sijainti löydetty", "ei"));
                        }
                    }
                });
            }
        });

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Fragment avattu")
                .putContentType(this.getClass().getSimpleName()));

        return v;
    }

    public BitmapDescriptor getMarkerIcon(int barVotes, int voteCount) {
        int maxHUE = 120;
        float colorFloat = 0;
        if (voteCount > 0) {
            colorFloat = barVotes * maxHUE / voteCount;
        }
        return BitmapDescriptorFactory.defaultMarker(colorFloat);
    }

    private void showEnableGPSDialog() {
        InfoDialogFragment gpsDialog = InfoDialogFragment.newInstance(getString(R.string.map_no_location_title), getString(R.string.map_no_location_desc));
        gpsDialog.show(getFragmentManager(), "Sijaintia ei löydy");
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
