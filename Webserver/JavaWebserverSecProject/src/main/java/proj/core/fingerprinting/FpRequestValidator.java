package proj.core.fingerprinting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.core.fingerprinting.comparators.*;
import proj.definitions.FpDetectionSensitivityLevels;
import proj.entities.FingerprintData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Performs a validation of two metadata objects, to detect unusual patterns
 * Will only be instantiated once, in HandlerAccessSensitiveContent
 * It's goal in this project is to detect a invalid request to handle sensitive content
 */
public final class FpRequestValidator {
    // Read from the configuration File / FpDetectionSensitivityLevels
    // Percentage of similarity needed to pass the validation
    private final int sensitivityLevel;
    private int totalCriteriaCount;
    private int passedCriteriaCount;
    private static final Logger logger = LoggerFactory.getLogger(FpRequestValidator.class);


    public FpRequestValidator(
            String sensitivityLevel
    ) {
        switch (sensitivityLevel) {
            //Getting Sensitivity Level from Config File as String and Obtaining Percentage from FpDetectionSensitivityLevels
            case "LOW" -> this.sensitivityLevel = FpDetectionSensitivityLevels.LOW;
            case "HIGH" -> this.sensitivityLevel = FpDetectionSensitivityLevels.HIGH;
            default -> this.sensitivityLevel = FpDetectionSensitivityLevels.MEDIUM;
        }
    }

    /**
     * ONLY method other than constructor that can/should be accessed from outside
     * Validates a Fingerprint Data object in relation to an already validated object
     * This is the main class that should be accessed from outside
     * @param fpNew object which is being validated
     * @param fpOld object which is used as reference for validation
     * @return true if the metadata objects are valid, false otherwise
     */
    public boolean validateMetadata(FingerprintData fpNew, FingerprintData fpOld) {
        //comparing all criteria and writing the results to a map
        HashMap<String,Boolean> map = initialComparison(fpNew, fpOld);
        //Obtaining the number of passed and failed criteria (written to object fields)
        try {
            evaluateComparisonResult(map);
        }catch (RuntimeException e){
            logger.error("FpValidator: Evaluation of Comparison Result failed");
            throw e;
        }
        //Obtaining calculated value needed to pass the check
        int valueNeededToPass = percentageToThreshold(sensitivityLevel);
        //DEBUG info print
        printComparisonResult(map);
        //Returning the result of the check
        return passedCriteriaCount >= valueNeededToPass;
    }


    /**
     * Internally used to run every comparison and return the results in a map for further evaluation
     * @param fpNew object which is being validated
     * @param fpOld object which is used as reference for validation
     * @return HashMap with the results of the comparison
     */
    private HashMap<String,Boolean> initialComparison(FingerprintData fpNew, FingerprintData fpOld){
        HashMap<String,Boolean> map = new HashMap<>();
        //int AttributeCount = FingerprintData.class.getFields().length; //Too convoluted, bec. no association between fields and comparators
        map.put("IP", new Cmp1_IP().compare(fpOld.getIP(), fpNew.getIP()));
        map.put("Accept",new Cmp2_Accept().compare(fpOld.getAccept(),fpNew.getAccept()));
        map.put("Encoding",new Cmp2_Encoding().compare(fpOld.getEncoding(),fpNew.getEncoding()));
        map.put("Geolocation", new Cmp3_Geolocation().compare(fpOld.getLocation(),fpNew.getLocation()));
        map.put("Screen", new Cmp4_ScreenResolution().compare(fpOld.getScreen(),fpNew.getScreen()));
        map.put("Language", new Cmp5_LanguageHeaders().compare(fpOld.getLanguage(),fpNew.getLanguage()));
        map.put("Timezone", new Cmp6_Timezone().compare(fpOld.getTimezone(),fpNew.getTimezone()));
        map.put("Browser", new Cmp7_Browser().compare(fpOld.getUserAgent().getBrowser(),fpNew.getUserAgent().getBrowser()));
        map.put("BrowserVersion", new Cmp8_BrowserVersion().compare(fpOld.getUserAgent().getBrowserVersion(),fpNew.getUserAgent().getBrowserVersion()));
        map.put("OS", new Cmp9_Platform().compare(fpOld.getUserAgent().getPlatform(),fpNew.getUserAgent().getPlatform()));
        map.put("Canvas", new Cmp10_Canvas().compare(fpOld.getCanvas(),fpNew.getCanvas()));
        map.put("WebglVendor", new Cmp11_WebGlVendor().compare(fpOld.getWebglVendor(),fpNew.getWebglVendor()));
        map.put("WebglRenderer", new Cmp12_WebGlRenderer().compare(fpOld.getWebglRenderer(),fpNew.getWebglRenderer()));
        map.put("DeviceMemory", new Cmp13_DeviceMemory().compare(fpOld.getDeviceMemory(),fpNew.getDeviceMemory()));
        //map.put("CookiesAccepted", (fpOld.isCookiesAccepted()==fpNew.isCookiesAccepted()));
        this.totalCriteriaCount = map.size();
        return map;
    }

    /**
     * Counts Criteria and wirtes them into the class fields
     * @param map the map generated by the initialComparison method
     */
    private void evaluateComparisonResult(HashMap<String,Boolean> map) throws RuntimeException{
        int passedCriteriaCount = 0;
        int failedCriteriaCount = 0;
        int totalCriteriaCount = map.size();
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            if(entry.getValue()){
                passedCriteriaCount++;
            }else{
                failedCriteriaCount++;
            }
        }
        this.passedCriteriaCount = passedCriteriaCount;
        logger.trace("M:evaluateComparisonResult: \n" +
                "Number of Passed Criteria: " + passedCriteriaCount + '\n' +
                "Number of Failed Criteria: " + failedCriteriaCount + '\n');
        //If somehting should go wrong:
        if(totalCriteriaCount != passedCriteriaCount + failedCriteriaCount){
            logger.error("FpValidator: Evaluation of Comparison Result failed");
            throw new RuntimeException("FpValidator: Evaluation of Comparison Result failed");
        }
    }

    //Returns the number of criteria that need to be passed to pass the check
    private int percentageToThreshold(int percentage){
        //number of fields / criteria in FingerprintData
        //Minus 2 because  blockId and the cookiesAccepted are not valid criteria;
        //Calculating Percentage to Pass check (Rounded up)
        int threshold = (int) Math.ceil(this.totalCriteriaCount * (percentage / 100.0));
        logger.trace("M:percentageToThreshold: \n" +
                " Percentage for Calculation: " + percentage + "\n"
                +"Threshold Calculated: " + threshold + "\n" +
                "For AttributeCount: " + this.totalCriteriaCount);
        return threshold;
    }


    //Print Comparison Result to the console for Debugging
    private void printComparisonResult(HashMap<String,Boolean> map){
        StringBuilder dbgStr = new StringBuilder("DEBUG FpValidator Comparison Result: " + '\n');
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + " : " + entry.getValue());
            dbgStr.append(entry.getKey()).append(" : ").append(entry.getValue()).append('\n');
        }
        dbgStr.append('\n');
        logger.trace(dbgStr.toString());
    }

}
