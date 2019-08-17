package javax.naming.ldap;

import com.sun.jndi.ldap.BerEncoder;
import java.io.IOException;

public final class SortControl extends BasicControl {
  public static final String OID = "1.2.840.113556.1.4.473";
  
  private static final long serialVersionUID = -1965961680233330744L;
  
  public SortControl(String paramString, boolean paramBoolean) throws IOException {
    super("1.2.840.113556.1.4.473", paramBoolean, null);
    this.value = setEncodedValue(new SortKey[] { new SortKey(paramString) });
  }
  
  public SortControl(String[] paramArrayOfString, boolean paramBoolean) throws IOException {
    super("1.2.840.113556.1.4.473", paramBoolean, null);
    SortKey[] arrayOfSortKey = new SortKey[paramArrayOfString.length];
    for (byte b = 0; b < paramArrayOfString.length; b++)
      arrayOfSortKey[b] = new SortKey(paramArrayOfString[b]); 
    this.value = setEncodedValue(arrayOfSortKey);
  }
  
  public SortControl(SortKey[] paramArrayOfSortKey, boolean paramBoolean) throws IOException {
    super("1.2.840.113556.1.4.473", paramBoolean, null);
    this.value = setEncodedValue(paramArrayOfSortKey);
  }
  
  private byte[] setEncodedValue(SortKey[] paramArrayOfSortKey) throws IOException {
    BerEncoder berEncoder = new BerEncoder(30 * paramArrayOfSortKey.length + 10);
    berEncoder.beginSeq(48);
    for (byte b = 0; b < paramArrayOfSortKey.length; b++) {
      berEncoder.beginSeq(48);
      berEncoder.encodeString(paramArrayOfSortKey[b].getAttributeID(), true);
      String str;
      if ((str = paramArrayOfSortKey[b].getMatchingRuleID()) != null)
        berEncoder.encodeString(str, 128, true); 
      if (!paramArrayOfSortKey[b].isAscending())
        berEncoder.encodeBoolean(true, 129); 
      berEncoder.endSeq();
    } 
    berEncoder.endSeq();
    return berEncoder.getTrimmedBuf();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\SortControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */