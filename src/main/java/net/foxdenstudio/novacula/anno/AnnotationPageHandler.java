/**************************************************************************************************
 * The MIT License (MIT)                                                                          *
 * *
 * Copyright (c) 2015. FoxDenStudio                                                               *
 * *
 * Permission is hereby granted, free of charge, to any person obtaining a copy                   *
 * of this software and associated documentation files (the "Software"), to deal                  *
 * in the Software without restriction, including without limitation the rights                   *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell                      *
 * copies of the Software, and to permit persons to whom the Software is                          *
 * furnished to do so, subject to the following conditions:                                       *
 * *
 * The above copyright notice and this permission notice shall be included in all                 *
 * copies or substantial portions of the Software.                                                *
 * *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR                     *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,                       *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE                    *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER                         *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,                  *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                  *
 * SOFTWARE.                                                                                      *
 **************************************************************************************************/

package net.foxdenstudio.novacula.anno;

import net.foxdenstudio.novacula.anno.requests.IWebServiceRequest;
import net.foxdenstudio.novacula.anno.responses.IWebServiceResponse;
import net.foxdenstudio.novacula.core.plugins.EventHandler;
import net.foxdenstudio.novacula.core.plugins.detector.ADetect;
import net.foxdenstudio.novacula.core.plugins.events.LoadEvent;
import net.foxdenstudio.novacula.core.plugins.NovaPlugin;
import net.foxdenstudio.novacula.core.plugins.events.ServerRequestEvent;
import net.foxdenstudio.novacula.core.utils.HTTPHeaderParser;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/23/15.
 */
@NovaPlugin(name = "Page Loading via. Annotation", uniqueID = "anno_page_loading")
public class AnnotationPageHandler {

    private final HashMap<String, HashMap<String, NovaMethodListenerData>> pluginAndPathRegistry = new HashMap<>();

    @EventHandler
    public void onLoad(LoadEvent event) {

        final ADetect.TypeReporter reporter = new ADetect.TypeReporter() {
            @Override
            public void reportTypeAnnotation(Class<? extends Annotation> annoClazz, String className) {
                HashMap<String, NovaMethodListenerData> tempHashMap = new HashMap<>();
                try {
                    Class clazz = Class.forName(className);
                    for (Method method : clazz.getMethods()) {

                        if (method.isAnnotationPresent(NovaMethodListener.class) && method.getReturnType().isAssignableFrom(IWebServiceResponse.class) && method.getParameterTypes()[0].isAssignableFrom(IWebServiceRequest.class)) {
                            NovaMethodListener annotation = method.getAnnotation(NovaMethodListener.class);
                            String name = annotation.name();
                            tempHashMap.put(name, new NovaMethodListenerData(annotation.requestType(), clazz.newInstance(), method));
                        }
                    }
                    pluginAndPathRegistry.put(((NovaClassListener) clazz.getAnnotation(NovaClassListener.class)).name(), tempHashMap);
                } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public Class<? extends Annotation>[] annotations() {
                //noinspection unchecked
                return new Class[]{NovaClassListener.class};
            }
        };

        final ADetect aDetect = new ADetect(reporter);
        try {
            aDetect.detect();
            System.out.println(pluginAndPathRegistry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @EventHandler
    public void onServerRequestEvent(ServerRequestEvent event) {
        String path = event.getHttpHeaderParser().getRequestURL().substring(1);
        if (pluginAndPathRegistry.containsKey(path.substring(0, path.indexOf('/')))) {
            HashMap<String, NovaMethodListenerData> dataHashMap = pluginAndPathRegistry.get(path.substring(0, path.indexOf('/')));
            String path2 = path.substring(path.lastIndexOf('/') + 1);

            if (dataHashMap.containsKey(path2)) {
                NovaMethodListenerData listenerData = dataHashMap.get(path2.trim());

                try {
                    IWebServiceRequest iWebServiceRequest = event::getHttpHeaderParser;

                    Object object = listenerData.getMethod().invoke(listenerData.getClassInstance(), iWebServiceRequest);
                    if (object instanceof IWebServiceResponse) {
                        IWebServiceResponse serviceResponse = (IWebServiceResponse) object;
                        String make = "";
                        make += "HTTP/1.1 200 OK\r\n";
                        make += "Date: " + new Date().toString() + "\r\n";
                        make += "Server: NovaServer1.5r\n";
                        make += "Accept-Ranges: bytes\r\n";
                        make += "Content-Type: " + serviceResponse.mimeType() + "\r\n";
                        make += "\r\n";
                        event.getClientOutputStream().write(make.getBytes());
                        event.getClientOutputStream().flush();

                        for (int i = 0; i < serviceResponse.getByteData().length; i++) {
                            event.getClientOutputStream().write(serviceResponse.getByteData()[i]);
                            event.getClientOutputStream().flush();
                        }

                        event.handle();
                    }
                } catch (InvocationTargetException | IllegalAccessException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
