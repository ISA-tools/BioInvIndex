package uk.ac.ebi.bioinvindex.services.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 28/05/2011
 *         Time: 14:43
 */
public class BIICache<V, T> extends HashMap<V, T> implements Cache<V, T> {

    public BIICache() {
    }


    public boolean attach(V key, T value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        }

        return false;
    }

    public T find(V key) {
        if (containsKey(key)) {
            return get(key);
        }

        return null;
    }

    public T detach(V key) {
        if (containsKey(key)) {
            return remove(key);
        }

        return null;
    }

    public Set<V> getKeys() {
        return keySet();
    }

    public void clearCache() {
        clear();
    }


}
