package sun.security.util;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Set;

public abstract class AbstractAlgorithmConstraints implements AlgorithmConstraints {
  protected final AlgorithmDecomposer decomposer;
  
  protected AbstractAlgorithmConstraints(AlgorithmDecomposer paramAlgorithmDecomposer) { this.decomposer = paramAlgorithmDecomposer; }
  
  static String[] getAlgorithms(final String propertyName) {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return Security.getProperty(propertyName); }
        });
    String[] arrayOfString = null;
    if (str != null && !str.isEmpty()) {
      if (str.length() >= 2 && str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"')
        str = str.substring(1, str.length() - 1); 
      arrayOfString = str.split(",");
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString[b] = arrayOfString[b].trim(); 
    } 
    if (arrayOfString == null)
      arrayOfString = new String[0]; 
    return arrayOfString;
  }
  
  static boolean checkAlgorithm(String[] paramArrayOfString, String paramString, AlgorithmDecomposer paramAlgorithmDecomposer) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("No algorithm name specified"); 
    Set set = null;
    for (String str : paramArrayOfString) {
      if (str != null && !str.isEmpty()) {
        if (str.equalsIgnoreCase(paramString))
          return false; 
        if (set == null)
          set = paramAlgorithmDecomposer.decompose(paramString); 
        for (String str1 : set) {
          if (str.equalsIgnoreCase(str1))
            return false; 
        } 
      } 
    } 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\AbstractAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */