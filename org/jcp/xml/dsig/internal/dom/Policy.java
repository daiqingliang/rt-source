package org.jcp.xml.dsig.internal.dom;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessController;
import java.security.Security;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class Policy {
  private static Set<URI> disallowedAlgs = new HashSet();
  
  private static int maxTrans = Integer.MAX_VALUE;
  
  private static int maxRefs = Integer.MAX_VALUE;
  
  private static Set<String> disallowedRefUriSchemes = new HashSet();
  
  private static Map<String, Integer> minKeyMap = new HashMap();
  
  private static boolean noDuplicateIds = false;
  
  private static boolean noRMLoops = false;
  
  private static void initialize() {
    String str = (String)AccessController.doPrivileged(() -> Security.getProperty("jdk.xml.dsig.secureValidationPolicy"));
    if (str == null || str.isEmpty())
      return; 
    String[] arrayOfString = str.split(",");
    for (String str1 : arrayOfString) {
      byte b;
      String[] arrayOfString1 = str1.split("\\s");
      String str2 = arrayOfString1[0];
      switch (str2) {
        case "disallowAlg":
          if (arrayOfString1.length != 2)
            error(str1); 
          disallowedAlgs.add(URI.create(arrayOfString1[1]));
          break;
        case "maxTransforms":
          if (arrayOfString1.length != 2)
            error(str1); 
          maxTrans = Integer.parseUnsignedInt(arrayOfString1[1]);
          break;
        case "maxReferences":
          if (arrayOfString1.length != 2)
            error(str1); 
          maxRefs = Integer.parseUnsignedInt(arrayOfString1[1]);
          break;
        case "disallowReferenceUriSchemes":
          if (arrayOfString1.length == 1)
            error(str1); 
          for (b = 1; b < arrayOfString1.length; b++) {
            String str3 = arrayOfString1[b];
            disallowedRefUriSchemes.add(str3.toLowerCase(Locale.ROOT));
          } 
          break;
        case "minKeySize":
          if (arrayOfString1.length != 3)
            error(str1); 
          minKeyMap.put(arrayOfString1[1], Integer.valueOf(Integer.parseUnsignedInt(arrayOfString1[2])));
          break;
        case "noDuplicateIds":
          if (arrayOfString1.length != 1)
            error(str1); 
          noDuplicateIds = true;
          break;
        case "noRetrievalMethodLoops":
          if (arrayOfString1.length != 1)
            error(str1); 
          noRMLoops = true;
          break;
        default:
          error(str1);
          break;
      } 
    } 
  }
  
  public static boolean restrictAlg(String paramString) {
    try {
      URI uRI = new URI(paramString);
      return disallowedAlgs.contains(uRI);
    } catch (URISyntaxException uRISyntaxException) {
      return false;
    } 
  }
  
  public static boolean restrictNumTransforms(int paramInt) { return (paramInt > maxTrans); }
  
  public static boolean restrictNumReferences(int paramInt) { return (paramInt > maxRefs); }
  
  public static boolean restrictReferenceUriScheme(String paramString) {
    if (paramString != null) {
      String str = URI.create(paramString).getScheme();
      if (str != null)
        return disallowedRefUriSchemes.contains(str.toLowerCase(Locale.ROOT)); 
    } 
    return false;
  }
  
  public static boolean restrictKey(String paramString, int paramInt) { return (paramInt < ((Integer)minKeyMap.getOrDefault(paramString, Integer.valueOf(0))).intValue()); }
  
  public static boolean restrictDuplicateIds() { return noDuplicateIds; }
  
  public static boolean restrictRetrievalMethodLoops() { return noRMLoops; }
  
  public static Set<URI> disabledAlgs() { return Collections.unmodifiableSet(disallowedAlgs); }
  
  public static int maxTransforms() { return maxTrans; }
  
  public static int maxReferences() { return maxRefs; }
  
  public static Set<String> disabledReferenceUriSchemes() { return Collections.unmodifiableSet(disallowedRefUriSchemes); }
  
  public static int minKeySize(String paramString) { return ((Integer)minKeyMap.getOrDefault(paramString, Integer.valueOf(0))).intValue(); }
  
  private static void error(String paramString) { throw new IllegalArgumentException("Invalid jdk.xml.dsig.secureValidationPolicy entry: " + paramString); }
  
  static  {
    try {
      initialize();
    } catch (Exception exception) {
      throw new SecurityException("Cannot initialize the secure validation policy", exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */