package br.udesc.esag.participactbrasil.support;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import br.udesc.esag.participactbrasil.domain.persistence.TaskFlat;
import br.udesc.esag.participactbrasil.utils.LocationUtils;

public class GeolocalizationTaskUtils {

    private static final Logger logger = LoggerFactory.getLogger(GeolocalizationTaskUtils.class);

    public static boolean isGeolocalized(TaskFlat task) {
        return isActivatedByArea(task) || isNotifiedByArea(task);
    }

    public static boolean isActivatedByArea(TaskFlat task) {
        return StringUtils.isNotBlank(task.getActivationArea());
    }

    public static boolean isNotifiedByArea(TaskFlat task) {
        return StringUtils.isNotBlank(task.getNotificationArea());
    }

    public static boolean isInside(Context context, double longitude, double latitude, String wkt) {
        try {

            List<LatLng> points = LocationUtils.polygonStringToLatLngList(wkt);
            boolean isInside = LocationUtils.isPointInPolygon(new LatLng(latitude, longitude), points);
            Log.d("GeolocalizationTask", "Is inside: " + isInside);
            return isInside;

//            Log.d("GeolocalizationTask", wkt);
//
//            Coordinate coordinate = new Coordinate(longitude, latitude);
//            GeometryFactory factory = new GeometryFactory();
//            Geometry current = factory.createPoint(coordinate);
//
//            String[] polygons = StringUtils.split(wkt, "POLYGON");
//
//            for (String string : polygons) {
//                Log.d("GeolocalizationTask", "+++++++++++\n\n");
//                Log.d("GeolocalizationTask", string);
//                Geometry polygon = new WKTReader().read("POLYGON "+string);
//                if (polygon.contains(current)) {
//                    return true;
//                }
//            }

//            return false;

        } catch (Exception e) {
            Log.e("GeolocalizationTask", null, e);
            logger.warn("Exception checking if wkt {} contains current location", wkt, e);
        }

        return false;
    }

}
