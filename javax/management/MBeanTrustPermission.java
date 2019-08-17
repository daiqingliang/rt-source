package javax.management;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;

public class MBeanTrustPermission extends BasicPermission {
  private static final long serialVersionUID = -2952178077029018140L;
  
  public MBeanTrustPermission(String paramString) { this(paramString, null); }
  
  public MBeanTrustPermission(String paramString1, String paramString2) {
    super(paramString1, paramString2);
    validate(paramString1, paramString2);
  }
  
  private static void validate(String paramString1, String paramString2) {
    if (paramString2 != null && paramString2.length() > 0)
      throw new IllegalArgumentException("MBeanTrustPermission actions must be null: " + paramString2); 
    if (!paramString1.equals("register") && !paramString1.equals("*"))
      throw new IllegalArgumentException("MBeanTrustPermission: Unknown target name [" + paramString1 + "]"); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      validate(getName(), getActions());
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new InvalidObjectException(illegalArgumentException.getMessage());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanTrustPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */