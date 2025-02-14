package proj.definitions;

/**
 * Enum for the quality of a fingerprint in relation to a different stored fingerprint
 * Five levels of quality are defined
 * These leves only serve as a guideline and or information, the percentage values to "pass" the check
 * are defined in the FpDetectionSensitivityLevels class
 *
 */
public enum FpQualityRating {
    VERY_HIGH,
    HIGH,
    MEDIUM,
    LOW,
    VERY_LOW,
}

