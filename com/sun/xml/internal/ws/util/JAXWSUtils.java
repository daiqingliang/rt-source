package com.sun.xml.internal.ws.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;

public final class JAXWSUtils {
  public static String getUUID() { return UUID.randomUUID().toString(); }
  
  public static String getFileOrURLName(String paramString) {
    try {
      return escapeSpace((new URL(paramString)).toExternalForm());
    } catch (MalformedURLException malformedURLException) {
      return (new File(paramString)).getCanonicalFile().toURL().toExternalForm();
    } catch (Exception exception) {
      return paramString;
    } 
  }
  
  public static URL getFileOrURL(String paramString) throws IOException {
    try {
      URL uRL = new URL(paramString);
      String str = String.valueOf(uRL.getProtocol()).toLowerCase();
      return (str.equals("http") || str.equals("https")) ? new URL(uRL.toURI().toASCIIString()) : uRL;
    } catch (URISyntaxException uRISyntaxException) {
      return (new File(paramString)).toURL();
    } catch (MalformedURLException malformedURLException) {
      return (new File(paramString)).toURL();
    } 
  }
  
  public static URL getEncodedURL(String paramString) throws IOException {
    URL uRL = new URL(paramString);
    String str = String.valueOf(uRL.getProtocol()).toLowerCase();
    if (str.equals("http") || str.equals("https"))
      try {
        return new URL(uRL.toURI().toASCIIString());
      } catch (URISyntaxException uRISyntaxException) {
        MalformedURLException malformedURLException = new MalformedURLException(uRISyntaxException.getMessage());
        malformedURLException.initCause(uRISyntaxException);
        throw malformedURLException;
      }  
    return uRL;
  }
  
  private static String escapeSpace(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < paramString.length(); b++) {
      if (paramString.charAt(b) == ' ') {
        stringBuilder.append("%20");
      } else {
        stringBuilder.append(paramString.charAt(b));
      } 
    } 
    return stringBuilder.toString();
  }
  
  public static String absolutize(String paramString) {
    try {
      URL uRL = (new File(".")).getCanonicalFile().toURL();
      return (new URL(uRL, paramString)).toExternalForm();
    } catch (IOException iOException) {
      return paramString;
    } 
  }
  
  public static void checkAbsoluteness(String paramString) {
    try {
      new URL(paramString);
    } catch (MalformedURLException malformedURLException) {
      try {
        new URI(paramString);
      } catch (URISyntaxException uRISyntaxException) {
        throw new IllegalArgumentException("system ID '" + paramString + "' isn't absolute", uRISyntaxException);
      } 
    } 
  }
  
  public static boolean matchQNames(QName paramQName1, QName paramQName2) {
    if (paramQName1 == null || paramQName2 == null)
      return false; 
    if (paramQName2.getNamespaceURI().equals(paramQName1.getNamespaceURI())) {
      String str = paramQName2.getLocalPart().replaceAll("\\*", ".*");
      return Pattern.matches(str, paramQName1.getLocalPart());
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\JAXWSUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */