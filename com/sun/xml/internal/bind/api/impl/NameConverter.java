package com.sun.xml.internal.bind.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.lang.model.SourceVersion;

public interface NameConverter {
  public static final NameConverter standard = new Standard();
  
  public static final NameConverter jaxrpcCompatible = new Standard() {
      protected boolean isPunct(char param1Char) { return (param1Char == '.' || param1Char == '-' || param1Char == ';' || param1Char == '·' || param1Char == '·' || param1Char == '۝' || param1Char == '۞'); }
      
      protected boolean isLetter(char param1Char) { return (super.isLetter(param1Char) || param1Char == '_'); }
      
      protected int classify(char param1Char) { return (param1Char == '_') ? 2 : super.classify(param1Char); }
    };
  
  public static final NameConverter smart = new Standard() {
      public String toConstantName(String param1String) {
        String str = super.toConstantName(param1String);
        return !SourceVersion.isKeyword(str) ? str : ('_' + str);
      }
    };
  
  String toClassName(String paramString);
  
  String toInterfaceName(String paramString);
  
  String toPropertyName(String paramString);
  
  String toConstantName(String paramString);
  
  String toVariableName(String paramString);
  
  String toPackageName(String paramString);
  
  public static class Standard extends NameUtil implements NameConverter {
    public String toClassName(String param1String) { return toMixedCaseName(toWordList(param1String), true); }
    
    public String toVariableName(String param1String) { return toMixedCaseName(toWordList(param1String), false); }
    
    public String toInterfaceName(String param1String) { return toClassName(param1String); }
    
    public String toPropertyName(String param1String) {
      String str = toClassName(param1String);
      if (str.equals("Class"))
        str = "Clazz"; 
      return str;
    }
    
    public String toConstantName(String param1String) { return super.toConstantName(param1String); }
    
    public String toPackageName(String param1String) {
      int i = param1String.indexOf(':');
      String str1 = "";
      if (i >= 0) {
        str1 = param1String.substring(0, i);
        if (str1.equalsIgnoreCase("http") || str1.equalsIgnoreCase("urn"))
          param1String = param1String.substring(i + 1); 
      } 
      ArrayList arrayList1 = tokenize(param1String, "/: ");
      if (arrayList1.size() == 0)
        return null; 
      if (arrayList1.size() > 1) {
        String str = (String)arrayList1.get(arrayList1.size() - 1);
        i = str.lastIndexOf('.');
        if (i > 0) {
          str = str.substring(0, i);
          arrayList1.set(arrayList1.size() - 1, str);
        } 
      } 
      String str2 = (String)arrayList1.get(0);
      i = str2.indexOf(':');
      if (i >= 0)
        str2 = str2.substring(0, i); 
      ArrayList arrayList2 = reverse(tokenize(str2, str1.equals("urn") ? ".-" : "."));
      if (((String)arrayList2.get(arrayList2.size() - 1)).equalsIgnoreCase("www"))
        arrayList2.remove(arrayList2.size() - 1); 
      arrayList1.addAll(1, arrayList2);
      arrayList1.remove(0);
      for (byte b = 0; b < arrayList1.size(); b++) {
        String str = (String)arrayList1.get(b);
        str = removeIllegalIdentifierChars(str);
        if (SourceVersion.isKeyword(str.toLowerCase()))
          str = '_' + str; 
        arrayList1.set(b, str.toLowerCase());
      } 
      return combine(arrayList1, '.');
    }
    
    private static String removeIllegalIdentifierChars(String param1String) {
      StringBuilder stringBuilder = new StringBuilder(param1String.length() + 1);
      for (byte b = 0; b < param1String.length(); b++) {
        char c = param1String.charAt(b);
        if (b == 0 && !Character.isJavaIdentifierStart(c))
          stringBuilder.append('_'); 
        if (!Character.isJavaIdentifierPart(c)) {
          stringBuilder.append('_');
        } else {
          stringBuilder.append(c);
        } 
      } 
      return stringBuilder.toString();
    }
    
    private static ArrayList<String> tokenize(String param1String1, String param1String2) {
      StringTokenizer stringTokenizer = new StringTokenizer(param1String1, param1String2);
      ArrayList arrayList = new ArrayList();
      while (stringTokenizer.hasMoreTokens())
        arrayList.add(stringTokenizer.nextToken()); 
      return arrayList;
    }
    
    private static <T> ArrayList<T> reverse(List<T> param1List) {
      ArrayList arrayList = new ArrayList();
      for (int i = param1List.size() - 1; i >= 0; i--)
        arrayList.add(param1List.get(i)); 
      return arrayList;
    }
    
    private static String combine(List param1List, char param1Char) {
      StringBuilder stringBuilder = new StringBuilder(param1List.get(0).toString());
      for (byte b = 1; b < param1List.size(); b++) {
        stringBuilder.append(param1Char);
        stringBuilder.append(param1List.get(b));
      } 
      return stringBuilder.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\api\impl\NameConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */