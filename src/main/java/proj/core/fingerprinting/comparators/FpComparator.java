package proj.core.fingerprinting.comparators;

/**
 * Interface for comparing two objects of the fingerprinting module
 */
public interface FpComparator {
    boolean compare(Object oldV, Object newV);
}
