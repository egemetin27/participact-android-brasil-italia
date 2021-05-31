package br.udesc.esag.participactbrasil.utils;

import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiobergmann on 21/10/16.
 */

public class LocationUtils {

    @Contract("null -> fail")
    public static List<LatLng> polygonStringToLatLngList(String polygon) {

        if (polygon == null) {
            throw new IllegalArgumentException("polygon is null");
        } else if (!polygon.contains("POLYGON")) {
            throw new IllegalArgumentException("polygon is bad formatted");
        }

        List<LatLng> points = new ArrayList<>();

        String strPoints = polygon.substring(polygon.lastIndexOf('(') + 1);
        strPoints = strPoints.substring(0, strPoints.indexOf(')'));

        String[] arrayPoints = strPoints.split(",");
        for (String point : arrayPoints) {
            String[] ll = point.split(" ");
            points.add(new LatLng(Double.parseDouble(ll[0]), Double.parseDouble(ll[1])));
        }

        return points;

    }

    public static boolean isPointInPolygon(LatLng point, List<LatLng> vertices) {
        int intersectCount = 0;
        for (int j = 0; j < vertices.size() - 1; j++) {
            if (rayCastIntersect(point, vertices.get(j), vertices.get(j + 1))) {
                intersectCount++;
            }
        }

        return ((intersectCount % 2) == 1); // odd = inside, even = outside;
    }

    public static boolean rayCastIntersect(LatLng point, LatLng vertA, LatLng vertB) {

        double aY = vertA.latitude;
        double bY = vertB.latitude;
        double aX = vertA.longitude;
        double bX = vertB.longitude;
        double pY = point.latitude;
        double pX = point.longitude;

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY)
                || (aX < pX && bX < pX)) {
            return false; // a and b can't both be above or below pt.y, and a or
            // b must be east of pt.x
        }

        double m = (aY - bY) / (aX - bX); // Rise over run
        double bee = (-aX) * m + aY; // y = mx + b
        double x = (pY - bee) / m; // algebra is neat!

        return x > pX;
    }

    public static LatLng getCenter(List<LatLng> points) {
        double[] centroid = { 0.0, 0.0 };

        for (int i = 0; i < points.size(); i++) {
            centroid[0] += points.get(i).latitude;
            centroid[1] += points.get(i).longitude;
        }

        int totalPoints = points.size();
        centroid[0] = centroid[0] / totalPoints;
        centroid[1] = centroid[1] / totalPoints;

        return new LatLng(centroid[0], centroid[1]);
    }

}
