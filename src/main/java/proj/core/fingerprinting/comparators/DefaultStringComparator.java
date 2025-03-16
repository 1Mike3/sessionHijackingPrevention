package proj.core.fingerprinting.comparators;

/**
 * Default comparator for strings, if one criteria can be compared by just seeing if the strings are equal,
 * this class can be extended to implement it's compare method automatically.
 */
public class DefaultStringComparator implements FpComparator {
    @Override
    public boolean compare(Object oldV, Object newV){
        String oldVtmp = (String) oldV;
        String newVtmp = (String) newV;
        //Removing possible trailing or leading whitespaces
        if (oldVtmp != null) {
            oldVtmp = oldVtmp.trim();
            newVtmp = newVtmp.trim();
        }
        if (oldVtmp == null && newVtmp == null) {
            return true;
        } else if (oldVtmp == null || newVtmp == null) {
            return false;
        } else return oldVtmp.equals(newVtmp);
    }
}
