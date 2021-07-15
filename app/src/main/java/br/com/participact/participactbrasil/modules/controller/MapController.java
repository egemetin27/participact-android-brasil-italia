package br.com.participact.participactbrasil.modules.controller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bergmannsoft.dialog.AlertDialogUtils;
import com.bergmannsoft.location.BSLocationListener;
import com.bergmannsoft.location.BSLocationManager;
import com.bergmannsoft.util.CalendarUtils;
import com.bergmannsoft.util.ConnectionUtils;
import com.bergmannsoft.util.FileCache;
import com.bergmannsoft.util.LocationUtils;
import com.bergmannsoft.util.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import br.com.participact.participactbrasil.R;
import br.com.participact.participactbrasil.modules.App;
import br.com.participact.participactbrasil.modules.activities.fragments.MapFragment;
import br.com.participact.participactbrasil.modules.dialog.ReportDetailsDialog;
import br.com.participact.participactbrasil.modules.dialog.VideoDialog;
import br.com.participact.participactbrasil.modules.network.SessionManager;
import br.com.participact.participactbrasil.modules.network.requests.UrbanProblemsResponse;
import br.com.participact.participactbrasil.modules.support.Tags;
import br.com.participact.participactbrasil.modules.support.UrbanProblem;
import br.com.participact.participactbrasil.modules.support.UserSettings;

public class MapController extends Controller {

    private static final String TAG = MapController.class.getSimpleName();
    private MapFragment mainUI;
    private View view;
    private GoogleMap googleMap;
    private HashMap<Marker, UrbanProblem> markerUrbanProblem = new HashMap<>();
    private LatLng reportingLatLng;
    private LatLng lastLocation;
    private List<UrbanProblem> urbanProblems = new ArrayList<>();
    private MapView mapView;

    public MapController(AppCompatActivity activity, MapFragment mainUI) {
        super(activity);
        this.mainUI = mainUI;
        this.view = mainUI.getView();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public void onResume() {
        mapView.onResume();
    }

    public LatLng getLastLocation() {
        return lastLocation;
    }

    public void init(Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Log.e(Tags.MAP, null, e);
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                googleMap = map;

                // default location
                double latitude = -27.596873;
                double longitude = -48.520492;

                try {
                    map.setMyLocationEnabled(true);
                } catch (SecurityException e) {
                    Log.e(Tags.MAP, null, e);
                }

                Location loc = LocationUtils.getLastKnownLocation(getActivity());
                if (loc != null) {
                    latitude = loc.getLatitude();
                    longitude = loc.getLongitude();
                }

                map.getUiSettings().setMyLocationButtonEnabled(false);

                map.setPadding(0, 150, 0, 0);

                LatLng ll = new LatLng(latitude, longitude);

                final CameraPosition cameraPosition = new CameraPosition.Builder().target(ll).zoom(17).build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                // Marker click
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });

                // Camera Idle
                map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        //boolean lastLocationNull = lastLocation == null;
                        lastLocation = googleMap.getCameraPosition().target;
                        //if (lastLocationNull || LocationUtils.distanceTo(mainUI.getActivity(), lastLocation, googleMap.getCameraPosition().target) > 5000) {
                            refreshUrbanProblems();
                        //}
                    }
                });

                // Map Long Click
                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        if (googleMap.getCameraPosition().zoom < 17) {
                            try {
                                Toast.makeText(getActivity(), "Aproxime mais para ter certeza que está postando no local correto.", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Crashlytics.log(Log.ERROR, TAG, e.toString());
                            }
                        }
                        reportingLatLng = latLng;
                        Marker marker = markerReporting(latLng);
                        marker.showInfoWindow();
                    }
                });

                // Info Window Click
                map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if (markerUrbanProblem.get(marker) == null) {
                            // Reporting new urban problem
                            mainUI.report(reportingLatLng);
                        } else {
                            // Show details dialog
                            Log.d(TAG, "Show details.");
                            UrbanProblem ticket = markerUrbanProblem.get(marker);
                            new ReportDetailsDialog(mainUI.getActivity(), ticket, new ReportDetailsDialog.Callback() {
                                @Override
                                public void showVideo(String url) {
                                    mainUI.showVideo(url);
                                }
                            }).show();
                        }
                    }
                });

                // Info Window Close
                map.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
                    @Override
                    public void onInfoWindowClose(Marker marker) {
                        if (markerUrbanProblem.get(marker) == null) {
                            // Pin reporting
                            marker.remove();
                        }
                    }
                });

                map.setInfoWindowAdapter(new PAInfoWindowAdapter());

                goToMyLocation();

            }
        });
    }

    public void goToMyLocation() {
        BSLocationManager.getInstance(getActivity()).requestLocationUpdates(new MapLocationListener());
    }

    class MapLocationListener extends BSLocationListener {

        @Override
        public void onLocationChanged(Location location) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            final CameraPosition cameraPosition = new CameraPosition.Builder().target(ll).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        @Override
        public boolean shouldContinueUpdating() {
            return false;
        }
    }

    public void refreshUrbanProblems() {
        refreshUrbanProblems(-1);
    }

    public void refreshUrbanProblems(int days) {
        int rangeDays = days >= 0 ? days : UserSettings.getInstance().getUrbanProblemsRangeDays();
        String startDate = Utils.dateToString("yyyy-MM-dd", CalendarUtils.dateByAddingDays(-rangeDays));
        String endDate = Utils.dateToString("yyyy-MM-dd", new Date());
        boolean mine = rangeDays == 0;
        if (lastLocation != null) {
            SessionManager.getInstance().urbanProblems(lastLocation.latitude, lastLocation.longitude, startDate, endDate, mine, new SessionManager.RequestCallback<UrbanProblemsResponse>() {
                @Override
                public void onResponse(UrbanProblemsResponse response) {
                    if (response != null && response.isSuccess()) {
                        if (response.getItems() != null) {
                            urbanProblems = response.getItems();
                            clearMap(new Runnable() {
                                @Override
                                public void run() {
                                    for (UrbanProblem urbanProblem : urbanProblems) {
                                        marker(urbanProblem, urbanProblem.getLocation());
                                    }
                                }
                            });
                        }
                    } else {
                        //AlertDialogUtils.showError(mainUI.getActivity(), response != null ? response.getMessage() : "Erro obtendo dados do servidor.");
                        try {
                            Toast.makeText(mainUI.getContext(), response != null ? response.getMessage() : "Erro obtendo dados do servidor.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Crashlytics.log(Log.ERROR, TAG, e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    try {
                        Toast.makeText(mainUI.getContext(), ConnectionUtils.isConnected(mainUI.getContext()) ? "Erro de comunicação com o servidor." : "A conexão à internet talvez esteja inativa.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Crashlytics.log(Log.ERROR, TAG, e.toString());
                    }
                    //AlertDialogUtils.showError(mainUI.getActivity(), t.getLocalizedMessage());
                }
            });
        }
    }

    private void clearMap(final Runnable runnable) {
//        for (Marker marker : markerUrbanProblem.keySet()) {
//            marker.remove();
//        }
        boolean success = false;
        try {
            googleMap.clear();
            success = true;
        } catch (IllegalArgumentException e) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    googleMap.clear();
                    runnable.run();
                }
            });
        }
        markerUrbanProblem.clear();
        if (success)
            runnable.run();
    }

    private Marker markerReporting(LatLng latLng) {
        return marker(null, latLng);
    }

    private Marker marker(final UrbanProblem urbanProblem, LatLng latLng) {
        try {
            final MarkerOptions marker = new MarkerOptions()
                    .position(latLng);
            if (urbanProblem == null) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_reporting))
                        .anchor(0.5f, 0.75f);
                return addMarker(marker, urbanProblem);
            } else {
                Bitmap bmp = urbanProblem.pin(new FileCache.OnBitmapDownloadedListener() {
                    @Override
                    public void onDownloaded(Bitmap bmp) {
                        marker.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                        addMarker(marker, urbanProblem);
                    }
                });
                if (bmp != null) {
                    marker.icon(BitmapDescriptorFactory.fromBitmap(bmp));
                    return addMarker(marker, urbanProblem);
                }
            }
        } catch (Exception e) {
            Crashlytics.log(Log.ERROR, TAG, e.toString());
        }
        return null;
    }

    private Marker addMarker(MarkerOptions markerOptions, UrbanProblem urbanProblem) {
        Marker m = googleMap.addMarker(markerOptions);
        if (urbanProblem != null) {
            markerUrbanProblem.put(m, urbanProblem);
        }
        return m;
    }

    class PAInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            if (markerUrbanProblem.get(marker) == null) {
                return App.getInstance().getLayoutInflater().inflate(R.layout.info_window_report_new_issue, null);
            } else {
                UrbanProblem ticket = markerUrbanProblem.get(marker);
                return buildFor(ticket, marker);
            }
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        private View buildFor(UrbanProblem ticket, final Marker marker) {

            if (ticket == null) {
                return null;
            }

            View window = App.getInstance().getLayoutInflater().inflate(R.layout.info_window_report_details, null);

            ImageView iconCategory = window.findViewById(R.id.iconCategory);
            TextView categoryName = window.findViewById(R.id.textCategoryName);
            TextView subcategoryName = window.findViewById(R.id.textSubcategoryName);
            TextView date = window.findViewById(R.id.textDate);
            TextView time = window.findViewById(R.id.textTime);
            ImageView iconComments = window.findViewById(R.id.iconComments);
            ImageView iconAudio = window.findViewById(R.id.iconAudio);
            ImageView iconPhotos = window.findViewById(R.id.iconPhotos);
            TextView photoCount = window.findViewById(R.id.textPhotoCount);
            ImageView iconVideo = window.findViewById(R.id.iconVideo);

            if (ticket.getSubcategory() != null && ticket.getSubcategory().getCategory() != null) {
                iconCategory.setImageBitmap(ticket.getSubcategory().getCategory().icon(new FileCache.OnBitmapDownloadedListener() {
                    @Override
                    public void onDownloaded(Bitmap bmp) {
                        marker.showInfoWindow();
                    }
                }));
                categoryName.setText(ticket.getSubcategory().getCategory().getName());
            }
            subcategoryName.setText(ticket.getSubcategory() != null ? ticket.getSubcategory().getName() : "");
            date.setText(ticket.getDateFormatted());
            time.setText(ticket.getTimeFormatted());
            iconComments.setColorFilter(ticket.hasComments() ? Color.parseColor("#4EC500") : Color.parseColor("#C5CAB9"));
            iconAudio.setColorFilter(ticket.hasAudio() ? Color.parseColor("#4EC500") : Color.parseColor("#C5CAB9"));
            iconPhotos.setColorFilter(ticket.photosCount() > 0 ? Color.parseColor("#4EC500") : Color.parseColor("#C5CAB9"));
            photoCount.setText(String.valueOf(ticket.photosCount()));
            photoCount.setVisibility(ticket.photosCount() > 0 ? View.VISIBLE : View.GONE);
            iconVideo.setColorFilter(ticket.hasVideo() ? Color.parseColor("#4EC500") : Color.parseColor("#C5CAB9"));

            return window;
        }

    }

}
