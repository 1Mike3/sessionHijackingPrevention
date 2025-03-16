package proj.core;

import org.junit.jupiter.api.Test;
import proj.core.fingerprinting.comparators.Cmp8_BrowserVersion;
import static org.junit.jupiter.api.Assertions.*;

public class BrowserVersionComparatorTest {

        @Test
    public void testNormalVersion() {
        Cmp8_BrowserVersion cmp8 = new Cmp8_BrowserVersion();
        String version1 = "1.0.0";
        String version2 = "1.0.1";
        assertTrue(cmp8.compare(version1, version2));
        version1 = "1.0.0";
        version2 = "2.0.0";
        assertTrue(cmp8.compare(version1, version2));
        version1 = "1.0.0";
        version2 = "3.0.0";
        assertTrue(cmp8.compare(version1, version2));
        version1 = "1.0.0";
        version2 = "1.6.0";
        assertTrue(cmp8.compare(version1, version2));
        version1 = "1.0.0";
        version2 = "1.0.8";
        assertTrue(cmp8.compare(version1, version2));
        version1 = "145";
        version2 = "146";
        assertTrue(cmp8.compare(version1, version2));
        version1 = "200";
        version2 = "200";
        assertTrue(cmp8.compare(version1, version2));

        }


    @Test
    public void TestInvalidVersions() {
        Cmp8_BrowserVersion cmp8 = new Cmp8_BrowserVersion();
        String version1 = "1.0.0";
        String version2 = "4.0.0";
        assertFalse(cmp8.compare(version1, version2));
        version1 = "2.0.0";
        version2 = "1.0.0";
        assertFalse(cmp8.compare(version1, version2));
        version1 = "300";
        version2 = "299";
        assertFalse(cmp8.compare(version1, version2));
        assertTrue(cmp8.compare(null, null));
    }


}
