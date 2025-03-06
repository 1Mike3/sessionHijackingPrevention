package proj.core.fingerprinting.comparators;

/**
 * Custom implementation where the Browser Version is only flagged under certain conditions
 */
public class Cmp8_BrowserVersion implements FpComparator{
    @Override
    public boolean compare(Object oldV, Object newV) {
        if (oldV == null && newV == null) {return true;}
        if (oldV == null || newV == null) {return false;}
        //If the content is the same, the comparison is successful either way
        if(new DefaultStringComparator().compare(oldV,newV)){
            return true;
        }else {
            //CUSTOMIZABLE
            Integer toleranceBrowserVersionIncrease = 2;

            String oldVersionString = (String) oldV;
            String newVersionString = (String) newV;
            Integer oldVersion = Integer.parseInt(oldVersionString.split("\\.")[0]); //split so no crash on V13.X
            Integer newVersion = Integer.parseInt(newVersionString.split("\\.")[0]);
            //It is assumed that the Browser Version may increase, but not decrease in normal circumstances
            return (newVersion >= oldVersion) && (newVersion <= (oldVersion + toleranceBrowserVersionIncrease));
        }
    }
}
