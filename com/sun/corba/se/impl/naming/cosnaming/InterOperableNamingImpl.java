package com.sun.corba.se.impl.naming.cosnaming;

import java.io.StringWriter;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

public class InterOperableNamingImpl {
  public String convertToString(NameComponent[] paramArrayOfNameComponent) {
    String str = convertNameComponentToString(paramArrayOfNameComponent[0]);
    for (byte b = 1; b < paramArrayOfNameComponent.length; b++) {
      String str1 = convertNameComponentToString(paramArrayOfNameComponent[b]);
      if (str1 != null)
        str = str + "/" + convertNameComponentToString(paramArrayOfNameComponent[b]); 
    } 
    return str;
  }
  
  private String convertNameComponentToString(NameComponent paramNameComponent) {
    if ((paramNameComponent.id == null || paramNameComponent.id.length() == 0) && (paramNameComponent.kind == null || paramNameComponent.kind.length() == 0))
      return "."; 
    if (paramNameComponent.id == null || paramNameComponent.id.length() == 0) {
      String str = addEscape(paramNameComponent.kind);
      return "." + str;
    } 
    if (paramNameComponent.kind == null || paramNameComponent.kind.length() == 0)
      return addEscape(paramNameComponent.id); 
    String str1 = addEscape(paramNameComponent.id);
    String str2 = addEscape(paramNameComponent.kind);
    return str1 + "." + str2;
  }
  
  private String addEscape(String paramString) {
    StringBuffer stringBuffer;
    if (paramString != null && (paramString.indexOf('.') != -1 || paramString.indexOf('/') != -1)) {
      stringBuffer = new StringBuffer();
      for (byte b = 0; b < paramString.length(); b++) {
        char c = paramString.charAt(b);
        if (c != '.' && c != '/') {
          stringBuffer.append(c);
        } else {
          stringBuffer.append('\\');
          stringBuffer.append(c);
        } 
      } 
    } else {
      return paramString;
    } 
    return new String(stringBuffer);
  }
  
  public NameComponent[] convertToNameComponent(String paramString) throws InvalidName {
    String[] arrayOfString = breakStringToNameComponents(paramString);
    if (arrayOfString == null || arrayOfString.length == 0)
      return null; 
    NameComponent[] arrayOfNameComponent = new NameComponent[arrayOfString.length];
    for (byte b = 0; b < arrayOfString.length; b++)
      arrayOfNameComponent[b] = createNameComponentFromString(arrayOfString[b]); 
    return arrayOfNameComponent;
  }
  
  private String[] breakStringToNameComponents(String paramString) {
    int[] arrayOfInt = new int[100];
    byte b = 0;
    int i = 0;
    while (i <= paramString.length()) {
      arrayOfInt[b] = paramString.indexOf('/', i);
      if (arrayOfInt[b] == -1) {
        i = paramString.length() + 1;
        continue;
      } 
      if (arrayOfInt[b] > 0 && paramString.charAt(arrayOfInt[b] - 1) == '\\') {
        i = arrayOfInt[b] + 1;
        arrayOfInt[b] = -1;
        continue;
      } 
      i = arrayOfInt[b] + 1;
      b++;
    } 
    if (b == 0) {
      String[] arrayOfString = new String[1];
      arrayOfString[0] = paramString;
      return arrayOfString;
    } 
    if (b != 0)
      b++; 
    return StringComponentsFromIndices(arrayOfInt, b, paramString);
  }
  
  private String[] StringComponentsFromIndices(int[] paramArrayOfInt, int paramInt, String paramString) {
    String[] arrayOfString = new String[paramInt];
    int i = 0;
    int j = paramArrayOfInt[0];
    for (int k = 0; k < paramInt; k++) {
      arrayOfString[k] = paramString.substring(i, j);
      if (paramArrayOfInt[k] < paramString.length() - 1 && paramArrayOfInt[k] != -1) {
        i = paramArrayOfInt[k] + 1;
      } else {
        i = 0;
        k = paramInt;
      } 
      if (k + 1 < paramArrayOfInt.length && paramArrayOfInt[k + 1] < paramString.length() - 1 && paramArrayOfInt[k + 1] != -1) {
        j = paramArrayOfInt[k + 1];
      } else {
        k = paramInt;
      } 
      if (i != 0 && k == paramInt)
        arrayOfString[paramInt - 1] = paramString.substring(i); 
    } 
    return arrayOfString;
  }
  
  private NameComponent createNameComponentFromString(String paramString) throws InvalidName {
    String str1 = null;
    String str2 = null;
    if (paramString == null || paramString.length() == 0 || paramString.endsWith("."))
      throw new InvalidName(); 
    int i = paramString.indexOf('.', 0);
    if (i == -1) {
      str1 = paramString;
    } else if (i == 0) {
      if (paramString.length() != 1)
        str2 = paramString.substring(1); 
    } else if (paramString.charAt(i - 1) != '\\') {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } else {
      boolean bool = false;
      while (i < paramString.length() && bool != true) {
        i = paramString.indexOf('.', i + 1);
        if (i > 0) {
          if (paramString.charAt(i - 1) != '\\')
            bool = true; 
          continue;
        } 
        i = paramString.length();
      } 
      if (bool == true) {
        str1 = paramString.substring(0, i);
        str2 = paramString.substring(i + 1);
      } else {
        str1 = paramString;
      } 
    } 
    str1 = cleanEscapeCharacter(str1);
    str2 = cleanEscapeCharacter(str2);
    if (str1 == null)
      str1 = ""; 
    if (str2 == null)
      str2 = ""; 
    return new NameComponent(str1, str2);
  }
  
  private String cleanEscapeCharacter(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return paramString; 
    int i = paramString.indexOf('\\');
    if (i == 0)
      return paramString; 
    StringBuffer stringBuffer1 = new StringBuffer(paramString);
    StringBuffer stringBuffer2 = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = stringBuffer1.charAt(b);
      if (c != '\\') {
        stringBuffer2.append(c);
      } else if (b + 1 < paramString.length()) {
        char c1 = stringBuffer1.charAt(b + 1);
        if (Character.isLetterOrDigit(c1))
          stringBuffer2.append(c); 
      } 
    } 
    return new String(stringBuffer2);
  }
  
  public String createURLBasedAddress(String paramString1, String paramString2) throws InvalidAddress {
    null = null;
    if (paramString1 == null || paramString1.length() == 0)
      throw new InvalidAddress(); 
    return "corbaname:" + paramString1 + "#" + encode(paramString2);
  }
  
  private String encode(String paramString) {
    StringWriter stringWriter = new StringWriter();
    boolean bool = false;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (Character.isLetterOrDigit(c)) {
        stringWriter.write(c);
      } else if (c == ';' || c == '/' || c == '?' || c == ':' || c == '@' || c == '&' || c == '=' || c == '+' || c == '$' || c == ';' || c == '-' || c == '_' || c == '.' || c == '!' || c == '~' || c == '*' || c == ' ' || c == '(' || c == ')') {
        stringWriter.write(c);
      } else {
        stringWriter.write(37);
        String str = Integer.toHexString(c);
        stringWriter.write(str);
      } 
    } 
    return stringWriter.toString();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\cosnaming\InterOperableNamingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */