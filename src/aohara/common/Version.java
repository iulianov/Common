package aohara.common;



public class Version {
    private String m_versionString;
    private String[] m_versionParts;

    private Version(String versionStr) {
        m_versionParts=versionStr.split("\\.");
        m_versionString = versionStr;
    }
    
    public boolean greaterThan(Version other) {
        if(other==null) {
            return true;
        }
        for(int n=0;n<m_versionParts.length && n<other.m_versionParts.length;n++) {
            String thisPart = m_versionParts[n];
            String otherPart = other.m_versionParts[n];
            try {
                int thisNum = Integer.parseInt(thisPart);
                int otherNum = Integer.parseInt(otherPart);
                if(thisNum!=otherNum) {
                    return thisNum>otherNum;
                }
            } catch (NumberFormatException e) {
                //one or both are not numbers so just do a string comparison
                int cmpRes = thisPart.compareTo(otherPart);
                if(cmpRes!=0) {
                    return cmpRes>0;
                }
            }
        }
        //we have compared all the matching parts and they all match
        //the version with the most parts is greater.
        return m_versionParts.length > other.m_versionParts.length;
    }
    
    public String getNormalVersion() {
        return m_versionString;
    }
    
    public static Version valueOf(String versionStr) {
        if(versionStr==null || versionStr.isEmpty()) {
            throw new IllegalArgumentException("Input string is NULL or empty");
        }
        return new Version(versionStr);
    }

    public String toString() {
        return m_versionString;
    }
}
