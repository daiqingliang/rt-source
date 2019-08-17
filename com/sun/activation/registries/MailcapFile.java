package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MailcapFile {
  private Map type_hash = new HashMap();
  
  private Map fallback_hash = new HashMap();
  
  private Map native_commands = new HashMap();
  
  private static boolean addReverse = false;
  
  public MailcapFile(String paramString) throws IOException {
    if (LogSupport.isLoggable())
      LogSupport.log("new MailcapFile: file " + paramString); 
    fileReader = null;
    try {
      fileReader = new FileReader(paramString);
      parse(new BufferedReader(fileReader));
    } finally {
      if (fileReader != null)
        try {
          fileReader.close();
        } catch (IOException iOException) {} 
    } 
  }
  
  public MailcapFile(InputStream paramInputStream) throws IOException {
    if (LogSupport.isLoggable())
      LogSupport.log("new MailcapFile: InputStream"); 
    parse(new BufferedReader(new InputStreamReader(paramInputStream, "iso-8859-1")));
  }
  
  public MailcapFile() {
    if (LogSupport.isLoggable())
      LogSupport.log("new MailcapFile: default"); 
  }
  
  public Map getMailcapList(String paramString) {
    Map map1 = null;
    Map map2 = null;
    map1 = (Map)this.type_hash.get(paramString);
    int i = paramString.indexOf('/');
    String str = paramString.substring(i + 1);
    if (!str.equals("*")) {
      String str1 = paramString.substring(0, i + 1) + "*";
      map2 = (Map)this.type_hash.get(str1);
      if (map2 != null)
        if (map1 != null) {
          map1 = mergeResults(map1, map2);
        } else {
          map1 = map2;
        }  
    } 
    return map1;
  }
  
  public Map getMailcapFallbackList(String paramString) {
    Map map1 = null;
    Map map2 = null;
    map1 = (Map)this.fallback_hash.get(paramString);
    int i = paramString.indexOf('/');
    String str = paramString.substring(i + 1);
    if (!str.equals("*")) {
      String str1 = paramString.substring(0, i + 1) + "*";
      map2 = (Map)this.fallback_hash.get(str1);
      if (map2 != null)
        if (map1 != null) {
          map1 = mergeResults(map1, map2);
        } else {
          map1 = map2;
        }  
    } 
    return map1;
  }
  
  public String[] getMimeTypes() {
    HashSet hashSet = new HashSet(this.type_hash.keySet());
    hashSet.addAll(this.fallback_hash.keySet());
    hashSet.addAll(this.native_commands.keySet());
    null = new String[hashSet.size()];
    return (String[])hashSet.toArray(null);
  }
  
  public String[] getNativeCommands(String paramString) {
    String[] arrayOfString = null;
    List list = (List)this.native_commands.get(paramString.toLowerCase(Locale.ENGLISH));
    if (list != null) {
      arrayOfString = new String[list.size()];
      arrayOfString = (String[])list.toArray(arrayOfString);
    } 
    return arrayOfString;
  }
  
  private Map mergeResults(Map paramMap1, Map paramMap2) {
    Iterator iterator = paramMap2.keySet().iterator();
    HashMap hashMap = new HashMap(paramMap1);
    while (iterator.hasNext()) {
      String str = (String)iterator.next();
      List list1 = (List)hashMap.get(str);
      if (list1 == null) {
        hashMap.put(str, paramMap2.get(str));
        continue;
      } 
      List list2 = (List)paramMap2.get(str);
      list1 = new ArrayList(list1);
      list1.addAll(list2);
      hashMap.put(str, list1);
    } 
    return hashMap;
  }
  
  public void appendToMailcap(String paramString) throws IOException {
    if (LogSupport.isLoggable())
      LogSupport.log("appendToMailcap: " + paramString); 
    try {
      parse(new StringReader(paramString));
    } catch (IOException iOException) {}
  }
  
  private void parse(Reader paramReader) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(paramReader);
    String str1 = null;
    String str2 = null;
    while ((str1 = bufferedReader.readLine()) != null) {
      str1 = str1.trim();
      try {
        if (str1.charAt(0) == '#')
          continue; 
        if (str1.charAt(str1.length() - 1) == '\\') {
          if (str2 != null) {
            str2 = str2 + str1.substring(0, str1.length() - 1);
            continue;
          } 
          str2 = str1.substring(0, str1.length() - 1);
          continue;
        } 
        if (str2 != null) {
          str2 = str2 + str1;
          try {
            parseLine(str2);
          } catch (MailcapParseException mailcapParseException) {}
          str2 = null;
          continue;
        } 
        try {
          parseLine(str1);
        } catch (MailcapParseException mailcapParseException) {}
      } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {}
    } 
  }
  
  protected void parseLine(String paramString) throws IOException {
    MailcapTokenizer mailcapTokenizer = new MailcapTokenizer(paramString);
    mailcapTokenizer.setIsAutoquoting(false);
    if (LogSupport.isLoggable())
      LogSupport.log("parse: " + paramString); 
    int i = mailcapTokenizer.nextToken();
    if (i != 2)
      reportParseError(2, i, mailcapTokenizer.getCurrentTokenValue()); 
    String str1 = mailcapTokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
    String str2 = "*";
    i = mailcapTokenizer.nextToken();
    if (i != 47 && i != 59)
      reportParseError(47, 59, i, mailcapTokenizer.getCurrentTokenValue()); 
    if (i == 47) {
      i = mailcapTokenizer.nextToken();
      if (i != 2)
        reportParseError(2, i, mailcapTokenizer.getCurrentTokenValue()); 
      str2 = mailcapTokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
      i = mailcapTokenizer.nextToken();
    } 
    String str3 = str1 + "/" + str2;
    if (LogSupport.isLoggable())
      LogSupport.log("  Type: " + str3); 
    LinkedHashMap linkedHashMap = new LinkedHashMap();
    if (i != 59)
      reportParseError(59, i, mailcapTokenizer.getCurrentTokenValue()); 
    mailcapTokenizer.setIsAutoquoting(true);
    i = mailcapTokenizer.nextToken();
    mailcapTokenizer.setIsAutoquoting(false);
    if (i != 2 && i != 59)
      reportParseError(2, 59, i, mailcapTokenizer.getCurrentTokenValue()); 
    if (i == 2) {
      List list = (List)this.native_commands.get(str3);
      if (list == null) {
        list = new ArrayList();
        list.add(paramString);
        this.native_commands.put(str3, list);
      } else {
        list.add(paramString);
      } 
    } 
    if (i != 59)
      i = mailcapTokenizer.nextToken(); 
    if (i == 59) {
      boolean bool = false;
      do {
        i = mailcapTokenizer.nextToken();
        if (i != 2)
          reportParseError(2, i, mailcapTokenizer.getCurrentTokenValue()); 
        String str4 = mailcapTokenizer.getCurrentTokenValue().toLowerCase(Locale.ENGLISH);
        i = mailcapTokenizer.nextToken();
        if (i != 61 && i != 59 && i != 5)
          reportParseError(61, 59, 5, i, mailcapTokenizer.getCurrentTokenValue()); 
        if (i != 61)
          continue; 
        mailcapTokenizer.setIsAutoquoting(true);
        i = mailcapTokenizer.nextToken();
        mailcapTokenizer.setIsAutoquoting(false);
        if (i != 2)
          reportParseError(2, i, mailcapTokenizer.getCurrentTokenValue()); 
        String str5 = mailcapTokenizer.getCurrentTokenValue();
        if (str4.startsWith("x-java-")) {
          String str = str4.substring(7);
          if (str.equals("fallback-entry") && str5.equalsIgnoreCase("true")) {
            bool = true;
          } else {
            if (LogSupport.isLoggable())
              LogSupport.log("    Command: " + str + ", Class: " + str5); 
            List list = (List)linkedHashMap.get(str);
            if (list == null) {
              list = new ArrayList();
              linkedHashMap.put(str, list);
            } 
            if (addReverse) {
              list.add(0, str5);
            } else {
              list.add(str5);
            } 
          } 
        } 
        i = mailcapTokenizer.nextToken();
      } while (i == 59);
      Map map1 = bool ? this.fallback_hash : this.type_hash;
      Map map2 = (Map)map1.get(str3);
      if (map2 == null) {
        map1.put(str3, linkedHashMap);
      } else {
        if (LogSupport.isLoggable())
          LogSupport.log("Merging commands for type " + str3); 
        for (String str : map2.keySet()) {
          List list1 = (List)map2.get(str);
          List list2 = (List)linkedHashMap.get(str);
          if (list2 == null)
            continue; 
          for (String str4 : list2) {
            if (!list1.contains(str4)) {
              if (addReverse) {
                list1.add(0, str4);
                continue;
              } 
              list1.add(str4);
            } 
          } 
        } 
        for (String str : linkedHashMap.keySet()) {
          if (map2.containsKey(str))
            continue; 
          List list = (List)linkedHashMap.get(str);
          map2.put(str, list);
        } 
      } 
    } else if (i != 5) {
      reportParseError(5, 59, i, mailcapTokenizer.getCurrentTokenValue());
    } 
  }
  
  protected static void reportParseError(int paramInt1, int paramInt2, String paramString) throws MailcapParseException { throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(paramInt2) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + " token."); }
  
  protected static void reportParseError(int paramInt1, int paramInt2, int paramInt3, String paramString) throws MailcapParseException { throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(paramInt3) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + " or a " + MailcapTokenizer.nameForToken(paramInt2) + " token."); }
  
  protected static void reportParseError(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString) throws MailcapParseException {
    if (LogSupport.isLoggable())
      LogSupport.log("PARSE ERROR: Encountered a " + MailcapTokenizer.nameForToken(paramInt4) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + ", a " + MailcapTokenizer.nameForToken(paramInt2) + ", or a " + MailcapTokenizer.nameForToken(paramInt3) + " token."); 
    throw new MailcapParseException("Encountered a " + MailcapTokenizer.nameForToken(paramInt4) + " token (" + paramString + ") while expecting a " + MailcapTokenizer.nameForToken(paramInt1) + ", a " + MailcapTokenizer.nameForToken(paramInt2) + ", or a " + MailcapTokenizer.nameForToken(paramInt3) + " token.");
  }
  
  static  {
    try {
      addReverse = Boolean.getBoolean("javax.activation.addreverse");
    } catch (Throwable throwable) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\activation\registries\MailcapFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */