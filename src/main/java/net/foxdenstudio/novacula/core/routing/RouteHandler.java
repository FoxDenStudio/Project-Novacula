/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 *                                                                                                *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 *                                                                                                *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 *                                                                                                *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 *                                                                                                *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.core.routing;

import net.foxdenstudio.novacula.core.utils.NovaLogger;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
public class RouteHandler {
    private static ArrayList<Route> routeArrayList;

    public RouteHandler(NovaLogger novaLogger, Route... presetRoutes) {
        routeArrayList = new ArrayList<>(Arrays.asList(presetRoutes));
    }

    public void addRoute(Route route) {
        routeArrayList.add(route);
    }

    public void addRouteAtPosition(int index, Route route) {
        routeArrayList.add(index, route);
    }

    public void removeRoute(Route route) {
        routeArrayList.remove(route);
    }

    public static Route getRouteByPath(String path) {
        return getRouteByPath(new Path(path));
    }

    public static Route getRouteByPath(Path path) {
        for (Route route : routeArrayList) {
            System.out.println(route.getPath().getPathAsText() + " :: " + path.getPathAsText());
            if (route.getPath().equals(path)) {
                System.out.println("good");
                return route;
            }
        }

        return null;
    }


    public static String getRoute(String path) {
        System.out.println(path);
        Route tRoute = getRouteByPath(path);
        System.out.println(tRoute == null ? "NULL":"NOTNULL "+tRoute.getPath());
        return "";//tRoute.getPath().getPathAsText();
    }

    public static void outputAllRoutes() {
        for(Route route : routeArrayList){
            System.out.println(route.getPath().getPathAsText());
        }
    }
}
