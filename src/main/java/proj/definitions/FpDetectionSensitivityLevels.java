package proj.definitions;

/**
 * Constants for the sensitivity levels of the fingerprint detection.
 * Three levels of sensitivity are defined,
 * with HIGH being the most sensitive and LOW being the least sensitive.
 * The sensitivity level is used to determine the threshold for the similarity of two fingerprints.
 * <p>
 * The value assigned to each level is the minimum similarity percentage required for a fingerprint to be considered a match.
 * (Percentages are rounded down to the nearest integer)
 * <p>
 * The percentages can be changed to fine tune the sensitivity of each major criteria.
 * The major criteria can be used to quickly change the sensitivity of the entire system without worrying too much about the details.
 */
public class FpDetectionSensitivityLevels  {
    //Percentages only configurable inline
    public static final int HIGH = 85;
    public static final int MEDIUM = 70;
    public static final int LOW = 50;

}

