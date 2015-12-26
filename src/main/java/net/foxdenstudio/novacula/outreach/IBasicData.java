package net.foxdenstudio.novacula.outreach;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by d4rkfly3r (Joshua F.) on 12/25/15.
 */
public interface IBasicData extends Remote {

    String getName() throws RemoteException;

    void passData(String key, Object data) throws RemoteException;
}
