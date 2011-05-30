package uk.ac.ebi.bioinvindex.services.cache;

import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 28/05/2011
 *         Time: 14:44
 */
public interface Cache<V, T> {

    public boolean attach(V key, T value);
    public T find(V key);
    public T detach(V key);
    public Set<V> getKeys();
}
