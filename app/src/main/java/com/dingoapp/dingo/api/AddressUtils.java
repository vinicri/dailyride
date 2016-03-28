package com.dingoapp.dingo.api;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.dingoapp.dingo.api.model.Address;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by guestguest on 03/02/16.
 */
public class AddressUtils {

    public final static String PT_BR_ROUTE_PATTERN = "\\s*Av\\.\\w*|\\s*R\\.\\w*|\\s*Al\\.\\w*|\\s*Pca\\.\\w*|\\s*Rua\\w*|\\s*Avenida\\w*|\\s*Alameda\\w*|\\s*Praça\\w*";
    private static Pattern routePattern = Pattern.compile(PT_BR_ROUTE_PATTERN);
    private static Pattern numberRangePattern = Pattern.compile("\\d+-\\d+$");
    private static Pattern oneNumberPattern = Pattern.compile("\\d+$");
    private static AddressDistanceComparator mAddressDistanceComparator = new AddressDistanceComparator();
    static final public double EARTH_RADIUS = 6378137;

    /*Address Utils*/
    public static boolean hasRoutePattern(String route){
        Matcher matcher = routePattern.matcher(route);
        return matcher.find();
    }

    /**
     * Extract route and number. If number there is only one number. If number is a range, it's ignored.
     *
     * @param route
     * @return String[], [0] contains route, [1] contains number. [1] is empty if the number a a range
     */
    public static String[] extractRouteAndPreciseNumber(String route){

        String[] components = new String[2];

        Matcher oneNumberMatcher = oneNumberPattern.matcher(route);

        if(oneNumberMatcher.find()){
            String number = oneNumberMatcher.group();
            int numberPosition = route.lastIndexOf(number);
            String routeName = route.substring(0, numberPosition - 1).trim();
            if(routeName.charAt(routeName.length() - 1) == ','){
                routeName = routeName.substring(0, routeName.length() - 1);
            }
            components[0] = routeName;
            components[1] = number;
            return components;
        }
        else{
            Matcher numberRangeMatcher  = numberRangePattern.matcher(route);

            if(numberRangeMatcher.find()){
                String numberRange = numberRangeMatcher.group();
                int numberRangePosition = route.lastIndexOf(numberRange);
                String routeName = route.substring(0, numberRangePosition - 1).trim();
                if(routeName.charAt(routeName.length() - 1) == ','){
                    routeName = routeName.substring(0, routeName.length() - 1);
                }
                components[0] = routeName;
                return components;
            }
        }

        return null;
    }

    public static String getOneLineAddress(Address address, boolean privacy){
        return getOneLineAddress(address, privacy, false);
    }

    public static String getOneLineAddress(Address address, boolean privacy, boolean highlightNumber){
        if(address.isRouteType()){
            String route = address.getRouteLong();
            if(!privacy){
                route +=  ", " + address.getNumber();
            }
            else{
                return route;
            }

            if (!highlightNumber){
                return route;
            }
            else{
                SpannableString spannableString = new SpannableString(route);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD), address.getRouteLong().length() + 1, address.getRouteLong().length() + 1 + address.getNumber().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannableString.toString();
            }
        }
        else if(address.isEstablishmentType()){
            return address.getName();
        }

        throw new RuntimeException("Address in unexpected format");
    }

    public static LatLng getLatLng(Address address){
        return new LatLng(address.getLatitude(), address.getLongitude());
    }

    public static List<Address> orderAddressByDistanceFromAnchor(Address anchor, List<Address> addresses){
        List<AddressDistance> addressDistances = new ArrayList<>();

        for(Address address: addresses){
            AddressDistance addressDistance = new AddressDistance();
            addressDistance.address = address;
            addressDistance.distance = distanceBySphericalCosineLaw(getLatLng(anchor), getLatLng(address));
            addressDistances.add(addressDistance);
        }

        Collections.sort(addressDistances, mAddressDistanceComparator);

        List<Address> orderedAddresses = new ArrayList<>();
        for(AddressDistance addressDistance: addressDistances){
            orderedAddresses.add(addressDistance.address);
        }

        return orderedAddresses;

    }

    private static class AddressDistanceComparator implements Comparator<AddressDistance>{

        @Override
        public int compare(AddressDistance address, AddressDistance other) {
            return Double.compare(address.distance, other.distance);
        }
    }

    private static class AddressDistance {
        Address address;
        double distance;
    }

    private static double distanceBySphericalCosineLaw(LatLng location1, LatLng location2)
    {

        //ACOS( SIN(lat1)*SIN(lat2) + COS(lat1)*COS(lat2)*COS(lon2-lon1) ) * 6371
        double lat1 = Math.toRadians(location1.latitude);
        double lng1 = Math.toRadians(location1.longitude);

        double lat2 = Math.toRadians(location2.latitude);
        double lng2 = Math.toRadians(location2.longitude);

        double sins = Math.sin(lat1)*Math.sin(lat2);
        double coss = Math.cos(lat1)*Math.cos(lat2)*Math.cos(lng2 - lng1);

        double distance = Math.acos(sins+coss) * EARTH_RADIUS;

        return distance;

    }

}
