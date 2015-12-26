package net.foxdenstudio.novacula.outreach;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/25/15.
 */
public class BasicDataRemote extends UnicastRemoteObject implements IBasicData {

    protected BasicDataRemote() throws RemoteException {
    }

    @Override
    public String getName() throws RemoteException {
        return "This is Novacula";
    }

    @Override
    public void passData(String key, Object data) throws RemoteException {
        if (!OutreachServer.dataList.containsKey(key)) {
            OutreachServer.dataList.put(key, new ArrayList<>());
        }

        OutreachServer.dataList.get(key).add(data);
    }
}
