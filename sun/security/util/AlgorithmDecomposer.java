package sun.security.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AlgorithmDecomposer {
  private static final Pattern transPattern;
  
  private static final Pattern pattern = (transPattern = Pattern.compile("/")).compile("with|and", 2);
  
  private static Set<String> decomposeImpl(String paramString) {
    String[] arrayOfString = transPattern.split(paramString);
    HashSet hashSet = new HashSet();
    for (String str : arrayOfString) {
      if (str != null && str.length() != 0) {
        String[] arrayOfString1 = pattern.split(str);
        for (String str1 : arrayOfString1) {
          if (str1 != null && str1.length() != 0)
            hashSet.add(str1); 
        } 
      } 
    } 
    return hashSet;
  }
  
  public Set<String> decompose(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return new HashSet(); 
    Set set = decomposeImpl(paramString);
    if (set.contains("SHA1") && !set.contains("SHA-1"))
      set.add("SHA-1"); 
    if (set.contains("SHA-1") && !set.contains("SHA1"))
      set.add("SHA1"); 
    if (set.contains("SHA224") && !set.contains("SHA-224"))
      set.add("SHA-224"); 
    if (set.contains("SHA-224") && !set.contains("SHA224"))
      set.add("SHA224"); 
    if (set.contains("SHA256") && !set.contains("SHA-256"))
      set.add("SHA-256"); 
    if (set.contains("SHA-256") && !set.contains("SHA256"))
      set.add("SHA256"); 
    if (set.contains("SHA384") && !set.contains("SHA-384"))
      set.add("SHA-384"); 
    if (set.contains("SHA-384") && !set.contains("SHA384"))
      set.add("SHA384"); 
    if (set.contains("SHA512") && !set.contains("SHA-512"))
      set.add("SHA-512"); 
    if (set.contains("SHA-512") && !set.contains("SHA512"))
      set.add("SHA512"); 
    return set;
  }
  
  public static Collection<String> getAliases(String paramString) {
    String[] arrayOfString;
    if (paramString.equalsIgnoreCase("DH") || paramString.equalsIgnoreCase("DiffieHellman")) {
      arrayOfString = new String[] { "DH", "DiffieHellman" };
    } else {
      arrayOfString = new String[] { paramString };
    } 
    return Arrays.asList(arrayOfString);
  }
  
  private static void hasLoop(Set<String> paramSet, String paramString1, String paramString2) {
    if (paramSet.contains(paramString1)) {
      if (!paramSet.contains(paramString2))
        paramSet.add(paramString2); 
      paramSet.remove(paramString1);
    } 
  }
  
  public static Set<String> decomposeOneHash(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return new HashSet(); 
    Set set = decomposeImpl(paramString);
    hasLoop(set, "SHA-1", "SHA1");
    hasLoop(set, "SHA-224", "SHA224");
    hasLoop(set, "SHA-256", "SHA256");
    hasLoop(set, "SHA-384", "SHA384");
    hasLoop(set, "SHA-512", "SHA512");
    return set;
  }
  
  public static String hashName(String paramString) { return paramString.replace("-", ""); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\securit\\util\AlgorithmDecomposer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */