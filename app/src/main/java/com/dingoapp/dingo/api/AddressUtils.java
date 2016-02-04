package com.dingoapp.dingo.api;

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

    /*Address Utils*/
    public static boolean hasRoutePattern(String route){
        Matcher matcher = routePattern.matcher(route);
        return matcher.find();
    }

    /**
     * Extract route and number
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



}
