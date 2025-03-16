package proj.core.fingerprinting.comparators;

/**
 * - Compares two IP addresses
 * - This comparator classes are all implementations of the fpComparator interface
 * - They are used to compare two objects (fingerprint criteria) of the same type
 * - Each of them is in it's own file for a better overview and code structure despite most of them probably using the
 * - default String comparator
 */
public class Cmp1_IP extends DefaultStringComparator implements FpComparator {
}
