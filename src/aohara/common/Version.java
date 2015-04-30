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
            System.out.printf("'%s'>'%s'\n", this, other);
            return true;
        }
        for(int n=0;n<m_versionParts.length && n<other.m_versionParts.length;n++) {
            String thisPart = m_versionParts[n];
            String otherPart = other.m_versionParts[n];
            System.out.printf("'%s'?'%s'---",thisPart,otherPart);
            try {
                int thisNum = Integer.parseInt(thisPart);
                int otherNum = Integer.parseInt(otherPart);
                if(thisNum!=otherNum) {
                    System.out.printf("'%s'%s'%s (%d,%d)'\n", this, (thisNum>otherNum?">":"<"),other, thisNum, otherNum);
                    return thisNum>otherNum;
                }
            } catch (NumberFormatException e) {
                //one or both are not numbers so just do a string comparison
                int cmpRes = thisPart.compareTo(otherPart);
                if(cmpRes!=0) {
                    System.out.printf("'%s'%s'%s' ('%s','%s')\n", this, (cmpRes>0?">":"<"),other,thisPart,otherPart);
                    return cmpRes>0;
                }
            }
        }
        //we have compared all the matching parts and they all match
        //the version with the most parts is greater.
        System.out.printf("'%s'%s'%s' l(%d,%d)\n", this, (m_versionParts.length > other.m_versionParts.length?">":"<"),other,
                          m_versionParts.length,other.m_versionParts.length);
        return m_versionParts.length > other.m_versionParts.length;
    }
    
    public String getNormalVersion() {
        return m_versionString;
    }
    
    public static Version valueOf(String versionStr) {
        if(versionStr==null || versionStr.isEmpty())
            throw new IllegalArgumentException("Input string is NULL or empty");
        return new Version(versionStr);
    }

    public String toString() {
        return m_versionString;
    }
}
