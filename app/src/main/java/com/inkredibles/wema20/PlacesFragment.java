    package com.inkredibles.wema20;

    import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inkredibles.wema20.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.List;

    /*The PlacesFragment handles the logic of displaying the map and sets the markers on the map*/

    public class PlacesFragment extends Fragment implements OnMapReadyCallback {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
            // Defines the xml file for the fragment
            return inflater.inflate(R.layout.fragment_places, parent, false);
        }


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Get the SupportMapFragment and request notification
            // when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment)  getChildFragmentManager()
                    .findFragmentById(R.id.map); //getChildFragmentManager()  is used for fragments instead of getFragmentManager
            mapFragment.getMapAsync(this);
        }


        @Override
        public void onMapReady(GoogleMap googleMap1) {
            final  GoogleMap googleMap = googleMap1;
            googleMap.getUiSettings().setZoomControlsEnabled(true); //make it zoomable
            Post randPost = new Post();
            final Post.Query allPosts = new Post.Query();
            allPosts.getMany().withUser();
            allPosts.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {
                    ParseUser user = ParseUser.getCurrentUser();
                    for (Post post : objects){
                        String title = post.getTitle();
                        ParseGeoPoint geoPoint = post.getLocation();
                        String message = post.getMessage();
                        if (title != null && geoPoint != null ){
                            BitmapDescriptor defaultMarker; //set red if a post was made by the current user or green otherwise
                            defaultMarker = post.getUser().getUsername().equals(user.getUsername()) ?  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) : BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                            // listingPosition is a LatLng point
                            LatLng listingPosition = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                            // Create the marker on the fragment
                            Marker mapMarker = googleMap.addMarker(new MarkerOptions()
                                .position(listingPosition)
                                .title(title)
                                .snippet(message)
                                .icon(defaultMarker));
                        }
                    }
                }
            });
        }


    }
