package net.foxdenstudio.novacula.outreach;

import net.foxdenstudio.novacula.core.utils.NovaLogger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/25/15.
 */
public class OutreachServer {

    private final NovaLogger novaLogger;
    protected static ConcurrentHashMap<String, ArrayList<Object>> dataList;

    public OutreachServer(NovaLogger novaLogger) throws RemoteException {
        super();
        this.novaLogger = novaLogger;
        dataList = new ConcurrentHashMap<>();
    }

    public void startServer() throws MalformedURLException, RemoteException {

        try {
            LocateRegistry.createRegistry(1099);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Naming.rebind("//localhost/NovaOutreachServer/IBasicData", new BasicDataRemote());
        this.novaLogger.log("Bound in registry...");

    }

    public static ArrayList<Object> getData(String key) {
        return dataList.getOrDefault(key, new ArrayList<>());
    }

}
