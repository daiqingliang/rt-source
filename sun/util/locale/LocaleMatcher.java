package sun.util.locale;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class LocaleMatcher {
  public static List<Locale> filter(List<Locale.LanguageRange> paramList, Collection<Locale> paramCollection, Locale.FilteringMode paramFilteringMode) {
    if (paramList.isEmpty() || paramCollection.isEmpty())
      return new ArrayList(); 
    ArrayList arrayList1 = new ArrayList();
    for (Locale locale : paramCollection)
      arrayList1.add(locale.toLanguageTag()); 
    List list = filterTags(paramList, arrayList1, paramFilteringMode);
    ArrayList arrayList2 = new ArrayList(list.size());
    for (String str : list)
      arrayList2.add(Locale.forLanguageTag(str)); 
    return arrayList2;
  }
  
  public static List<String> filterTags(List<Locale.LanguageRange> paramList, Collection<String> paramCollection, Locale.FilteringMode paramFilteringMode) {
    if (paramList.isEmpty() || paramCollection.isEmpty())
      return new ArrayList(); 
    if (paramFilteringMode == Locale.FilteringMode.EXTENDED_FILTERING)
      return filterExtended(paramList, paramCollection); 
    ArrayList arrayList = new ArrayList();
    for (Locale.LanguageRange languageRange : paramList) {
      String str = languageRange.getRange();
      if (str.startsWith("*-") || str.indexOf("-*") != -1) {
        if (paramFilteringMode == Locale.FilteringMode.AUTOSELECT_FILTERING)
          return filterExtended(paramList, paramCollection); 
        if (paramFilteringMode == Locale.FilteringMode.MAP_EXTENDED_RANGES) {
          if (str.charAt(0) == '*') {
            str = "*";
          } else {
            str = str.replaceAll("-[*]", "");
          } 
          arrayList.add(new Locale.LanguageRange(str, languageRange.getWeight()));
          continue;
        } 
        if (paramFilteringMode == Locale.FilteringMode.REJECT_EXTENDED_RANGES)
          throw new IllegalArgumentException("An extended range \"" + str + "\" found in REJECT_EXTENDED_RANGES mode."); 
        continue;
      } 
      arrayList.add(languageRange);
    } 
    return filterBasic(arrayList, paramCollection);
  }
  
  private static List<String> filterBasic(List<Locale.LanguageRange> paramList, Collection<String> paramCollection) {
    ArrayList arrayList = new ArrayList();
    for (Locale.LanguageRange languageRange : paramList) {
      String str = languageRange.getRange();
      if (str.equals("*"))
        return new ArrayList(paramCollection); 
      for (String str1 : paramCollection) {
        str1 = str1.toLowerCase();
        if (str1.startsWith(str)) {
          int i = str.length();
          if ((str1.length() == i || str1.charAt(i) == '-') && !arrayList.contains(str1))
            arrayList.add(str1); 
        } 
      } 
    } 
    return arrayList;
  }
  
  private static List<String> filterExtended(List<Locale.LanguageRange> paramList, Collection<String> paramCollection) {
    ArrayList arrayList = new ArrayList();
    for (Locale.LanguageRange languageRange : paramList) {
      String str = languageRange.getRange();
      if (str.equals("*"))
        return new ArrayList(paramCollection); 
      String[] arrayOfString = str.split("-");
      for (String str1 : paramCollection) {
        str1 = str1.toLowerCase();
        String[] arrayOfString1 = str1.split("-");
        if (!arrayOfString[0].equals(arrayOfString1[0]) && !arrayOfString[0].equals("*"))
          continue; 
        byte b1 = 1;
        for (byte b2 = 1; b1 < arrayOfString.length && b2 < arrayOfString1.length; b2++) {
          if (arrayOfString[b1].equals("*")) {
            b1++;
            continue;
          } 
          if (arrayOfString[b1].equals(arrayOfString1[b2])) {
            b1++;
            b2++;
            continue;
          } 
          if (arrayOfString1[b2].length() == 1 && !arrayOfString1[b2].equals("*"))
            break; 
        } 
        if (arrayOfString.length == b1 && !arrayList.contains(str1))
          arrayList.add(str1); 
      } 
    } 
    return arrayList;
  }
  
  public static Locale lookup(List<Locale.LanguageRange> paramList, Collection<Locale> paramCollection) {
    if (paramList.isEmpty() || paramCollection.isEmpty())
      return null; 
    ArrayList arrayList = new ArrayList();
    for (Locale locale : paramCollection)
      arrayList.add(locale.toLanguageTag()); 
    String str = lookupTag(paramList, arrayList);
    return (str == null) ? null : Locale.forLanguageTag(str);
  }
  
  public static String lookupTag(List<Locale.LanguageRange> paramList, Collection<String> paramCollection) {
    if (paramList.isEmpty() || paramCollection.isEmpty())
      return null; 
    for (Locale.LanguageRange languageRange : paramList) {
      String str1 = languageRange.getRange();
      if (str1.equals("*"))
        continue; 
      for (String str2 = str1.replaceAll("\\x2A", "\\\\p{Alnum}*"); str2.length() > 0; str2 = "") {
        for (String str : paramCollection) {
          str = str.toLowerCase();
          if (str.matches(str2))
            return str; 
        } 
        int i = str2.lastIndexOf('-');
        if (i >= 0) {
          str2 = str2.substring(0, i);
          if (str2.lastIndexOf('-') == str2.length() - 2)
            str2 = str2.substring(0, str2.length() - 2); 
          continue;
        } 
      } 
    } 
    return null;
  }
  
  public static List<Locale.LanguageRange> parse(String paramString) {
    paramString = paramString.replaceAll(" ", "").toLowerCase();
    if (paramString.startsWith("accept-language:"))
      paramString = paramString.substring(16); 
    String[] arrayOfString = paramString.split(",");
    ArrayList arrayList1 = new ArrayList(arrayOfString.length);
    ArrayList arrayList2 = new ArrayList();
    int i = 0;
    for (String str1 : arrayOfString) {
      double d;
      String str2;
      int j;
      if ((j = str1.indexOf(";q=")) == -1) {
        str2 = str1;
        d = 1.0D;
      } else {
        str2 = str1.substring(0, j);
        j += 3;
        try {
          d = Double.parseDouble(str1.substring(j));
        } catch (Exception exception) {
          throw new IllegalArgumentException("weight=\"" + str1.substring(j) + "\" for language range \"" + str2 + "\"");
        } 
        if (d < 0.0D || d > 1.0D)
          throw new IllegalArgumentException("weight=" + d + " for language range \"" + str2 + "\". It must be between " + 0.0D + " and " + 1.0D + "."); 
      } 
      if (!arrayList2.contains(str2)) {
        Locale.LanguageRange languageRange = new Locale.LanguageRange(str2, d);
        j = i;
        for (int k = 0; k < i; k++) {
          if (((Locale.LanguageRange)arrayList1.get(k)).getWeight() < d) {
            j = k;
            break;
          } 
        } 
        arrayList1.add(j, languageRange);
        i++;
        arrayList2.add(str2);
        String str;
        if ((str = getEquivalentForRegionAndVariant(str2)) != null && !arrayList2.contains(str)) {
          arrayList1.add(j + 1, new Locale.LanguageRange(str, d));
          i++;
          arrayList2.add(str);
        } 
        String[] arrayOfString1;
        if ((arrayOfString1 = getEquivalentsForLanguage(str2)) != null)
          for (String str3 : arrayOfString1) {
            if (!arrayList2.contains(str3)) {
              arrayList1.add(j + 1, new Locale.LanguageRange(str3, d));
              i++;
              arrayList2.add(str3);
            } 
            str = getEquivalentForRegionAndVariant(str3);
            if (str != null && !arrayList2.contains(str)) {
              arrayList1.add(j + 1, new Locale.LanguageRange(str, d));
              i++;
              arrayList2.add(str);
            } 
          }  
      } 
    } 
    return arrayList1;
  }
  
  private static String[] getEquivalentsForLanguage(String paramString) {
    for (String str = paramString; str.length() > 0; str = str.substring(0, i)) {
      if (LocaleEquivalentMaps.singleEquivMap.containsKey(str)) {
        String str1 = (String)LocaleEquivalentMaps.singleEquivMap.get(str);
        return new String[] { paramString.replaceFirst(str, str1) };
      } 
      if (LocaleEquivalentMaps.multiEquivsMap.containsKey(str)) {
        String[] arrayOfString = (String[])LocaleEquivalentMaps.multiEquivsMap.get(str);
        for (byte b = 0; b < arrayOfString.length; b++)
          arrayOfString[b] = paramString.replaceFirst(str, arrayOfString[b]); 
        return arrayOfString;
      } 
      int i = str.lastIndexOf('-');
      if (i == -1)
        break; 
    } 
    return null;
  }
  
  private static String getEquivalentForRegionAndVariant(String paramString) {
    int i = getExtentionKeyIndex(paramString);
    for (String str : LocaleEquivalentMaps.regionVariantEquivMap.keySet()) {
      int j;
      if ((j = paramString.indexOf(str)) == -1 || (i != Integer.MIN_VALUE && j > i))
        continue; 
      int k = j + str.length();
      if (paramString.length() == k || paramString.charAt(k) == '-')
        return paramString.replaceFirst(str, (String)LocaleEquivalentMaps.regionVariantEquivMap.get(str)); 
    } 
    return null;
  }
  
  private static int getExtentionKeyIndex(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    int i = Integer.MIN_VALUE;
    for (int j = 1; j < arrayOfChar.length; j++) {
      if (arrayOfChar[j] == '-') {
        if (j - i == 2)
          return i; 
        i = j;
      } 
    } 
    return Integer.MIN_VALUE;
  }
  
  public static List<Locale.LanguageRange> mapEquivalents(List<Locale.LanguageRange> paramList, Map<String, List<String>> paramMap) {
    if (paramList.isEmpty())
      return new ArrayList(); 
    if (paramMap == null || paramMap.isEmpty())
      return new ArrayList(paramList); 
    HashMap hashMap = new HashMap();
    for (String str : paramMap.keySet())
      hashMap.put(str.toLowerCase(), str); 
    ArrayList arrayList = new ArrayList();
    for (Locale.LanguageRange languageRange : paramList) {
      String str1 = languageRange.getRange();
      String str2 = str1;
      boolean bool = false;
      while (str2.length() > 0) {
        if (hashMap.containsKey(str2)) {
          bool = true;
          List list = (List)paramMap.get(hashMap.get(str2));
          if (list != null) {
            int j = str2.length();
            for (String str : list)
              arrayList.add(new Locale.LanguageRange(str.toLowerCase() + str1.substring(j), languageRange.getWeight())); 
          } 
          break;
        } 
        int i = str2.lastIndexOf('-');
        if (i == -1)
          break; 
        str2 = str2.substring(0, i);
      } 
      if (!bool)
        arrayList.add(languageRange); 
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\LocaleMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */