package proj.core.fingerprinting;

import proj.core.fingerprinting.comparators.*;
import proj.definitions.FpDetectionSensitivityLevels;
import proj.entities.FingerprintData;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Performs a validation of two metadata objects, to detect unusual patterns
 */
public class FpRequestValidator {

    // Read from the configuration File
    private FpDetectionSensitivityLevels sensitivityLevel;

    /**
     * Validates a Fingerprint Data object in relation to an already validated object
     * This is the main class that should be accessed from outside
     * @param fpNew object which is being validated
     * @param fpOld object which is used as reference for validation
     * @return true if the metadata objects are valid, false otherwise
     */
    public boolean validateMetadata(FingerprintData fpNew, FingerprintData fpOld) {
        //comparing all criteria and writing the results to a map
        HashMap<String,Boolean> map = initialComparision(fpNew, fpOld);
        return false;
    }

    private HashMap<String,Boolean> initialComparision(FingerprintData fpNew, FingerprintData fpOld){
        HashMap<String,Boolean> map = new HashMap<>();
        //int AttributeCount = FingerprintData.class.getFields().length; //Too convoluted, bec. no association between fields and comparators
        map.put("IP", new Cmp1_IP().compare(fpOld.getIP(), fpNew.getIP()));
        map.put("Accept",new Cmp2_Accept().compare(fpOld.getAccept(),fpNew.getAccept()));
        map.put("Encoding",new Cmp2_Encoding().compare(fpOld.getEncoding(),fpNew.getEncoding()));
        map.put("Geolocation", new Cmp3_Geolocation().compare(fpNew.getLocation(),fpOld.getLocation()));
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
        map.put("CookiesAccepted", (fpOld.isCookiesAccepted()==fpNew.isCookiesAccepted()));
        return map;
    }

    //Print Comparison Result to the console for Debugging
    private void printComparisonResult(HashMap<String,Boolean> map){
        for (Map.Entry<String, Boolean> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

}
