package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MailcapFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MailcapCommandMap extends CommandMap {
  private MailcapFile[] DB;
  
  private static final int PROG = 0;
  
  public MailcapCommandMap() {
    ArrayList arrayList = new ArrayList(5);
    MailcapFile mailcapFile = null;
    arrayList.add(null);
    LogSupport.log("MailcapCommandMap: load HOME");
    try {
      String str = System.getProperty("user.home");
      if (str != null) {
        String str1 = str + File.separator + ".mailcap";
        mailcapFile = loadFile(str1);
        if (mailcapFile != null)
          arrayList.add(mailcapFile); 
      } 
    } catch (SecurityException securityException) {}
    LogSupport.log("MailcapCommandMap: load SYS");
    try {
      String str = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mailcap";
      mailcapFile = loadFile(str);
      if (mailcapFile != null)
        arrayList.add(mailcapFile); 
    } catch (SecurityException securityException) {}
    LogSupport.log("MailcapCommandMap: load JAR");
    loadAllResources(arrayList, "META-INF/mailcap");
    LogSupport.log("MailcapCommandMap: load DEF");
    mailcapFile = loadResource("/META-INF/mailcap.default");
    if (mailcapFile != null)
      arrayList.add(mailcapFile); 
    this.DB = new MailcapFile[arrayList.size()];
    this.DB = (MailcapFile[])arrayList.toArray(this.DB);
  }
  
  private MailcapFile loadResource(String paramString) {
    inputStream = null;
    try {
      inputStream = SecuritySupport.getResourceAsStream(getClass(), paramString);
      if (inputStream != null) {
        MailcapFile mailcapFile = new MailcapFile(inputStream);
        if (LogSupport.isLoggable())
          LogSupport.log("MailcapCommandMap: successfully loaded mailcap file: " + paramString); 
        return mailcapFile;
      } 
      if (LogSupport.isLoggable())
        LogSupport.log("MailcapCommandMap: not loading mailcap file: " + paramString); 
    } catch (IOException iOException) {
      if (LogSupport.isLoggable())
        LogSupport.log("MailcapCommandMap: can't load " + paramString, iOException); 
    } catch (SecurityException securityException) {
      if (LogSupport.isLoggable())
        LogSupport.log("MailcapCommandMap: can't load " + paramString, securityException); 
    } finally {
      try {
        if (inputStream != null)
          inputStream.close(); 
      } catch (IOException iOException) {}
    } 
    return null;
  }
  
  private void loadAllResources(List paramList, String paramString) {
    boolean bool = false;
    try {
      URL[] arrayOfURL;
      ClassLoader classLoader = null;
      classLoader = SecuritySupport.getContextClassLoader();
      if (classLoader == null)
        classLoader = getClass().getClassLoader(); 
      if (classLoader != null) {
        arrayOfURL = SecuritySupport.getResources(classLoader, paramString);
      } else {
        arrayOfURL = SecuritySupport.getSystemResources(paramString);
      } 
      if (arrayOfURL != null) {
        if (LogSupport.isLoggable())
          LogSupport.log("MailcapCommandMap: getResources"); 
        for (byte b = 0; b < arrayOfURL.length; b++) {
          URL uRL = arrayOfURL[b];
          inputStream = null;
          if (LogSupport.isLoggable())
            LogSupport.log("MailcapCommandMap: URL " + uRL); 
          try {
            inputStream = SecuritySupport.openStream(uRL);
            if (inputStream != null) {
              paramList.add(new MailcapFile(inputStream));
              bool = true;
              if (LogSupport.isLoggable())
                LogSupport.log("MailcapCommandMap: successfully loaded mailcap file from URL: " + uRL); 
            } else if (LogSupport.isLoggable()) {
              LogSupport.log("MailcapCommandMap: not loading mailcap file from URL: " + uRL);
            } 
          } catch (IOException iOException) {
            if (LogSupport.isLoggable())
              LogSupport.log("MailcapCommandMap: can't load " + uRL, iOException); 
          } catch (SecurityException securityException) {
            if (LogSupport.isLoggable())
              LogSupport.log("MailcapCommandMap: can't load " + uRL, securityException); 
          } finally {
            try {
              if (inputStream != null)
                inputStream.close(); 
            } catch (IOException iOException) {}
          } 
        } 
      } 
    } catch (Exception exception) {
      if (LogSupport.isLoggable())
        LogSupport.log("MailcapCommandMap: can't load " + paramString, exception); 
    } 
    if (!bool) {
      if (LogSupport.isLoggable())
        LogSupport.log("MailcapCommandMap: !anyLoaded"); 
      MailcapFile mailcapFile = loadResource("/" + paramString);
      if (mailcapFile != null)
        paramList.add(mailcapFile); 
    } 
  }
  
  private MailcapFile loadFile(String paramString) {
    MailcapFile mailcapFile = null;
    try {
      mailcapFile = new MailcapFile(paramString);
    } catch (IOException iOException) {}
    return mailcapFile;
  }
  
  public MailcapCommandMap(String paramString) throws IOException {
    this();
    if (LogSupport.isLoggable())
      LogSupport.log("MailcapCommandMap: load PROG from " + paramString); 
    if (this.DB[false] == null)
      this.DB[0] = new MailcapFile(paramString); 
  }
  
  public MailcapCommandMap(InputStream paramInputStream) {
    this();
    LogSupport.log("MailcapCommandMap: load PROG");
    if (this.DB[false] == null)
      try {
        this.DB[0] = new MailcapFile(paramInputStream);
      } catch (IOException iOException) {} 
  }
  
  public CommandInfo[] getPreferredCommands(String paramString) {
    ArrayList arrayList = new ArrayList();
    if (paramString != null)
      paramString = paramString.toLowerCase(Locale.ENGLISH); 
    byte b;
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        Map map = this.DB[b].getMailcapList(paramString);
        if (map != null)
          appendPrefCmdsToList(map, arrayList); 
      } 
    } 
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        Map map = this.DB[b].getMailcapFallbackList(paramString);
        if (map != null)
          appendPrefCmdsToList(map, arrayList); 
      } 
    } 
    null = new CommandInfo[arrayList.size()];
    return (CommandInfo[])arrayList.toArray(null);
  }
  
  private void appendPrefCmdsToList(Map paramMap, List paramList) {
    for (String str : paramMap.keySet()) {
      if (!checkForVerb(paramList, str)) {
        List list = (List)paramMap.get(str);
        String str1 = (String)list.get(0);
        paramList.add(new CommandInfo(str, str1));
      } 
    } 
  }
  
  private boolean checkForVerb(List paramList, String paramString) {
    Iterator iterator = paramList.iterator();
    while (iterator.hasNext()) {
      String str = ((CommandInfo)iterator.next()).getCommandName();
      if (str.equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public CommandInfo[] getAllCommands(String paramString) {
    ArrayList arrayList = new ArrayList();
    if (paramString != null)
      paramString = paramString.toLowerCase(Locale.ENGLISH); 
    byte b;
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        Map map = this.DB[b].getMailcapList(paramString);
        if (map != null)
          appendCmdsToList(map, arrayList); 
      } 
    } 
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        Map map = this.DB[b].getMailcapFallbackList(paramString);
        if (map != null)
          appendCmdsToList(map, arrayList); 
      } 
    } 
    null = new CommandInfo[arrayList.size()];
    return (CommandInfo[])arrayList.toArray(null);
  }
  
  private void appendCmdsToList(Map paramMap, List paramList) {
    for (String str : paramMap.keySet()) {
      List list = (List)paramMap.get(str);
      for (String str1 : list)
        paramList.add(new CommandInfo(str, str1)); 
    } 
  }
  
  public CommandInfo getCommand(String paramString1, String paramString2) {
    if (paramString1 != null)
      paramString1 = paramString1.toLowerCase(Locale.ENGLISH); 
    byte b;
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        Map map = this.DB[b].getMailcapList(paramString1);
        if (map != null) {
          List list = (List)map.get(paramString2);
          if (list != null) {
            String str = (String)list.get(0);
            if (str != null)
              return new CommandInfo(paramString2, str); 
          } 
        } 
      } 
    } 
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        Map map = this.DB[b].getMailcapFallbackList(paramString1);
        if (map != null) {
          List list = (List)map.get(paramString2);
          if (list != null) {
            String str = (String)list.get(0);
            if (str != null)
              return new CommandInfo(paramString2, str); 
          } 
        } 
      } 
    } 
    return null;
  }
  
  public void addMailcap(String paramString) throws IOException {
    LogSupport.log("MailcapCommandMap: add to PROG");
    if (this.DB[false] == null)
      this.DB[0] = new MailcapFile(); 
    this.DB[0].appendToMailcap(paramString);
  }
  
  public DataContentHandler createDataContentHandler(String paramString) {
    if (LogSupport.isLoggable())
      LogSupport.log("MailcapCommandMap: createDataContentHandler for " + paramString); 
    if (paramString != null)
      paramString = paramString.toLowerCase(Locale.ENGLISH); 
    byte b;
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        if (LogSupport.isLoggable())
          LogSupport.log("  search DB #" + b); 
        Map map = this.DB[b].getMailcapList(paramString);
        if (map != null) {
          List list = (List)map.get("content-handler");
          if (list != null) {
            String str = (String)list.get(0);
            DataContentHandler dataContentHandler = getDataContentHandler(str);
            if (dataContentHandler != null)
              return dataContentHandler; 
          } 
        } 
      } 
    } 
    for (b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        if (LogSupport.isLoggable())
          LogSupport.log("  search fallback DB #" + b); 
        Map map = this.DB[b].getMailcapFallbackList(paramString);
        if (map != null) {
          List list = (List)map.get("content-handler");
          if (list != null) {
            String str = (String)list.get(0);
            DataContentHandler dataContentHandler = getDataContentHandler(str);
            if (dataContentHandler != null)
              return dataContentHandler; 
          } 
        } 
      } 
    } 
    return null;
  }
  
  private DataContentHandler getDataContentHandler(String paramString) {
    if (LogSupport.isLoggable())
      LogSupport.log("    got content-handler"); 
    if (LogSupport.isLoggable())
      LogSupport.log("      class " + paramString); 
    try {
      ClassLoader classLoader = null;
      classLoader = SecuritySupport.getContextClassLoader();
      if (classLoader == null)
        classLoader = getClass().getClassLoader(); 
      Class clazz = null;
      try {
        clazz = classLoader.loadClass(paramString);
      } catch (Exception exception) {
        clazz = Class.forName(paramString);
      } 
      if (clazz != null)
        return (DataContentHandler)clazz.newInstance(); 
    } catch (IllegalAccessException illegalAccessException) {
      if (LogSupport.isLoggable())
        LogSupport.log("Can't load DCH " + paramString, illegalAccessException); 
    } catch (ClassNotFoundException classNotFoundException) {
      if (LogSupport.isLoggable())
        LogSupport.log("Can't load DCH " + paramString, classNotFoundException); 
    } catch (InstantiationException instantiationException) {
      if (LogSupport.isLoggable())
        LogSupport.log("Can't load DCH " + paramString, instantiationException); 
    } 
    return null;
  }
  
  public String[] getMimeTypes() {
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        String[] arrayOfString = this.DB[b].getMimeTypes();
        if (arrayOfString != null)
          for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
            if (!arrayList.contains(arrayOfString[b1]))
              arrayList.add(arrayOfString[b1]); 
          }  
      } 
    } 
    null = new String[arrayList.size()];
    return (String[])arrayList.toArray(null);
  }
  
  public String[] getNativeCommands(String paramString) {
    ArrayList arrayList = new ArrayList();
    if (paramString != null)
      paramString = paramString.toLowerCase(Locale.ENGLISH); 
    for (byte b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        String[] arrayOfString = this.DB[b].getNativeCommands(paramString);
        if (arrayOfString != null)
          for (byte b1 = 0; b1 < arrayOfString.length; b1++) {
            if (!arrayList.contains(arrayOfString[b1]))
              arrayList.add(arrayOfString[b1]); 
          }  
      } 
    } 
    null = new String[arrayList.size()];
    return (String[])arrayList.toArray(null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\MailcapCommandMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */