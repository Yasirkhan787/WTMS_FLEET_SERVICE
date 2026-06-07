package com.yasirkhan.fleet.utils;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;
import org.locationtech.jts.geom.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpatialUtils {

    // Standard SRID for GPS (WGS84)
    private static final int SRID = 4326;
    private static final GeometryFactory factory = new GeometryFactory(new PrecisionModel(), SRID);

    /*
     * Converts a Google Encoded Polyline into a JTS LineString.
     * Handles the Latitude/Longitude to X/Y (Longitude/Latitude) swap.
     */
    public static LineString toLineString(String encodedPath) {
        if (encodedPath == null || encodedPath.isEmpty()) {
            return null;
        }

        // Decode using Google SDK
        List<LatLng> googlePoints = PolylineEncoding.decode(encodedPath);

        // Map and Swap: Google(Lat, Lng) -> JTS(Lng, Lat)
        Coordinate[] jtsCoordinates =
                googlePoints
                        .stream()
                        .map(point -> new Coordinate(point.lng, point.lat))
                        .toArray(Coordinate[]::new);

        // Create the LineString for PostGIS
        return factory.createLineString(jtsCoordinates);
    }

    public static String toPolyLine(LineString path) {
        if (path == null || path.isEmpty()) {
            return "";
        }

        // Extract JTS Coordinates (Lng, Lat)
        // Map and Swap back to Google Format: JTS(Lng, Lat) -> Google(Lat, Lng)
        List<LatLng> googlePoints =
                Arrays
                        .stream(path.getCoordinates())
                        .map(coordinate -> new LatLng(coordinate.y, coordinate.x)) // y is Latitude, x is Longitude
                        .collect(Collectors.toList());

        // Encode the list of points into a single Polyline string
        return PolylineEncoding.encode(googlePoints);
    }

    /*
     * Creates a JTS Point from raw Lat/Lng.
     * Handles the X/Y (Lng/Lat) swap for PostGIS.
     */
    public static Point toPoint(double lat, double lng) {
        return factory.createPoint(new Coordinate(lng, lat));
    }

    /*
     * Converts a Google Encoded Path into a closed JTS Polygon.
     */
    public static Polygon toPolygon(String encodedPath) {
        if (encodedPath == null || encodedPath.isEmpty()) {
            return null;
        }

        List<LatLng> googlePoints = PolylineEncoding.decode(encodedPath);

        // A valid JTS Polygon MUST be a closed loop.
        // If the frontend didn't send the closing point, we append it automatically.
        if (googlePoints.size() >= 3) {
            LatLng first = googlePoints.get(0);
            LatLng last = googlePoints.get(googlePoints.size() - 1);
            if (first.lat != last.lat || first.lng != last.lng) {
                googlePoints.add(first);
            }
        }

        Coordinate[] jtsCoordinates = googlePoints.stream()
                .map(point -> new Coordinate(point.lng, point.lat))
                .toArray(Coordinate[]::new);

        return factory.createPolygon(jtsCoordinates);
    }
}