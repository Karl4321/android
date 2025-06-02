package ru.mirea.yakovlev.yandexdriver;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.yandex.mapkit.MapKit;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

import ru.mirea.yakovlev.yandexdriver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements
        DrivingSession.DrivingRouteListener,
        UserLocationObjectListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int MAX_ROUTES = 4;
    private static final int[] ROUTE_COLORS = {0xFFFF0000, 0xFF00FF00, 0x00FFBBBB, 0xFF0000FF};

    private ActivityMainBinding binding;
    private MapView mapView;
    private MapObjectCollection routeLines;
    private DrivingRouter drivingRouter;
    private DrivingSession activeDrivingSession;
    private UserLocationLayer userLocationLayer;
    private FusedLocationProviderClient locationClient;

    private final Point destination = new Point(55.554796, 37.708981);
    private Point currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mapView = binding.mapview;
        mapView.getMap().setRotateGesturesEnabled(false);
        mapView.getMap().move(new CameraPosition(
                new Point(
                    (destination.getLatitude() + 55.7558) / 2,
                (destination.getLongitude() + 37.6173) / 2),
                10, 0, 0
        ));
        routeLines = mapView.getMap().getMapObjects().addCollection();
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeLocationServices();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initializeLocationServices() {
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        MapKit mapKit = MapKitFactory.getInstance();
        userLocationLayer = mapKit.createUserLocationLayer(mapView.getMapWindow());
        userLocationLayer.setVisible(true);
        userLocationLayer.setHeadingEnabled(true);
        userLocationLayer.setObjectListener(this);
        addDestinationMarker();
    }

    private void addDestinationMarker() {
        Drawable pinDrawable = ContextCompat.getDrawable(this, R.drawable.icon);
        PlacemarkMapObject marker = mapView.getMap().getMapObjects().addPlacemark(
                destination,
                convertDrawableToImageProvider(pinDrawable)
        );

        marker.addTapListener((mapObject, point) -> {
            Toast.makeText(this, "г. Видное, магазин Ермолино", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private ImageProvider convertDrawableToImageProvider(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return ImageProvider.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeLocationServices();
            } else {
                Toast.makeText(this, "Для работы приложения требуется доступ к геолокации", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onObjectAdded(@NonNull UserLocationView userLocationView) {
        adjustUserLocationMarkerPosition();
        fetchCurrentLocation();
    }

    private void adjustUserLocationMarkerPosition() {
        userLocationLayer.setAnchor(
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.5)),
                new PointF((float) (mapView.getWidth() * 0.5), (float) (mapView.getHeight() * 0.83))
        );
    }

    @Override
    public void onObjectUpdated(@NonNull UserLocationView userLocationView, @NonNull ObjectEvent event) {
        fetchCurrentLocation();
    }

    @Override
    public void onObjectRemoved(@NonNull UserLocationView userLocationView) {}

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = new Point(location.getLatitude(), location.getLongitude());
                requestRouteCalculation();
            }
        });
    }

    private void requestRouteCalculation() {
        if (currentLocation == null) {
            Toast.makeText(this, "Определяем ваше местоположение...", Toast.LENGTH_SHORT).show();
            return;
        }

        clearExistingRoutes();

        DrivingOptions options = new DrivingOptions();
        options.setRoutesCount(MAX_ROUTES);

        VehicleOptions vehicleOptions = new VehicleOptions();

        List<RequestPoint> points = new ArrayList<>();
        points.add(new RequestPoint(currentLocation, RequestPointType.WAYPOINT, null));
        points.add(new RequestPoint(destination, RequestPointType.WAYPOINT, null));

        activeDrivingSession = drivingRouter.requestRoutes(points, options, vehicleOptions, this);
    }

    private void clearExistingRoutes() {
        routeLines.clear();
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        for (int i = 0; i < routes.size(); i++) {
            int colorIndex = i % ROUTE_COLORS.length;
            routeLines.addPolyline(routes.get(i).getGeometry())
                    .setStrokeColor(ROUTE_COLORS[colorIndex]);
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull com.yandex.runtime.Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (activeDrivingSession != null) {
            activeDrivingSession.cancel();
        }
        super.onDestroy();
    }
}
