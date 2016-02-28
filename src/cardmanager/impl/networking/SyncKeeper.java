/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cardmanager.impl.networking;

/**
 *
 * @author Jirka
 */
public interface SyncKeeper<T> {

    public T getWaitingFor();
    public void  setWaitingFor(T object);
}
