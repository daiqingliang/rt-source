package sun.awt.im;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.im.spi.InputMethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import sun.awt.AppContext;
import sun.awt.InputMethodSupport;
import sun.awt.SunToolkit;

class ExecutableInputMethodManager extends InputMethodManager implements Runnable {
  private InputContext currentInputContext;
  
  private String triggerMenuString;
  
  private InputMethodPopupMenu selectionMenu;
  
  private static String selectInputMethodMenuTitle;
  
  private InputMethodLocator hostAdapterLocator;
  
  private int javaInputMethodCount;
  
  private Vector<InputMethodLocator> javaInputMethodLocatorList;
  
  private Component requestComponent;
  
  private InputContext requestInputContext;
  
  private static final String preferredIMNode = "/sun/awt/im/preferredInputMethod";
  
  private static final String descriptorKey = "descriptor";
  
  private Hashtable<String, InputMethodLocator> preferredLocatorCache = new Hashtable();
  
  private Preferences userRoot;
  
  ExecutableInputMethodManager() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    try {
      if (toolkit instanceof InputMethodSupport) {
        InputMethodDescriptor inputMethodDescriptor = ((InputMethodSupport)toolkit).getInputMethodAdapterDescriptor();
        if (inputMethodDescriptor != null)
          this.hostAdapterLocator = new InputMethodLocator(inputMethodDescriptor, null, null); 
      } 
    } catch (AWTException aWTException) {}
    this.javaInputMethodLocatorList = new Vector();
    initializeInputMethodLocatorList();
  }
  
  void initialize() {
    selectInputMethodMenuTitle = Toolkit.getProperty("AWT.InputMethodSelectionMenu", "Select Input Method");
    this.triggerMenuString = selectInputMethodMenuTitle;
  }
  
  public void run() {
    while (!hasMultipleInputMethods()) {
      try {
        synchronized (this) {
          wait();
        } 
      } catch (InterruptedException interruptedException) {}
    } 
    while (true) {
      waitForChangeRequest();
      initializeInputMethodLocatorList();
      try {
        if (this.requestComponent != null) {
          showInputMethodMenuOnRequesterEDT(this.requestComponent);
          continue;
        } 
        EventQueue.invokeAndWait(new Runnable() {
              public void run() { ExecutableInputMethodManager.this.showInputMethodMenu(); }
            });
      } catch (InterruptedException interruptedException) {
      
      } catch (InvocationTargetException invocationTargetException) {}
    } 
  }
  
  private void showInputMethodMenuOnRequesterEDT(Component paramComponent) throws InterruptedException, InvocationTargetException {
    if (paramComponent == null)
      return; 
    class AWTInvocationLock {};
    AWTInvocationLock aWTInvocationLock = new AWTInvocationLock();
    InvocationEvent invocationEvent = new InvocationEvent(paramComponent, new Runnable(this) {
          public void run() { ExecutableInputMethodManager.this.showInputMethodMenu(); }
        },  aWTInvocationLock, true);
    AppContext appContext = SunToolkit.targetToAppContext(paramComponent);
    synchronized (aWTInvocationLock) {
      SunToolkit.postEvent(appContext, invocationEvent);
      while (!invocationEvent.isDispatched())
        aWTInvocationLock.wait(); 
    } 
    Throwable throwable = invocationEvent.getThrowable();
    if (throwable != null)
      throw new InvocationTargetException(throwable); 
  }
  
  void setInputContext(InputContext paramInputContext) {
    if (this.currentInputContext == null || paramInputContext != null);
    this.currentInputContext = paramInputContext;
  }
  
  public void notifyChangeRequest(Component paramComponent) throws InterruptedException, InvocationTargetException {
    if (!(paramComponent instanceof java.awt.Frame) && !(paramComponent instanceof java.awt.Dialog))
      return; 
    if (this.requestComponent != null)
      return; 
    this.requestComponent = paramComponent;
    notify();
  }
  
  public void notifyChangeRequestByHotKey(Component paramComponent) throws InterruptedException, InvocationTargetException {
    while (!(paramComponent instanceof java.awt.Frame) && !(paramComponent instanceof java.awt.Dialog)) {
      if (paramComponent == null)
        return; 
      paramComponent = paramComponent.getParent();
    } 
    notifyChangeRequest(paramComponent);
  }
  
  public String getTriggerMenuString() { return this.triggerMenuString; }
  
  boolean hasMultipleInputMethods() { return ((this.hostAdapterLocator != null && this.javaInputMethodCount > 0) || this.javaInputMethodCount > 1); }
  
  private void waitForChangeRequest() {
    try {
      while (this.requestComponent == null)
        wait(); 
    } catch (InterruptedException interruptedException) {}
  }
  
  private void initializeInputMethodLocatorList() {
    synchronized (this.javaInputMethodLocatorList) {
      this.javaInputMethodLocatorList.clear();
      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              public Object run() {
                for (InputMethodDescriptor inputMethodDescriptor : ServiceLoader.loadInstalled(InputMethodDescriptor.class)) {
                  ClassLoader classLoader = inputMethodDescriptor.getClass().getClassLoader();
                  ExecutableInputMethodManager.this.javaInputMethodLocatorList.add(new InputMethodLocator(inputMethodDescriptor, classLoader, null));
                } 
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        privilegedActionException.printStackTrace();
      } 
      this.javaInputMethodCount = this.javaInputMethodLocatorList.size();
    } 
    if (hasMultipleInputMethods()) {
      if (this.userRoot == null)
        this.userRoot = getUserRoot(); 
    } else {
      this.triggerMenuString = null;
    } 
  }
  
  private void showInputMethodMenu() {
    if (!hasMultipleInputMethods()) {
      this.requestComponent = null;
      return;
    } 
    this.selectionMenu = InputMethodPopupMenu.getInstance(this.requestComponent, selectInputMethodMenuTitle);
    this.selectionMenu.removeAll();
    String str = getCurrentSelection();
    if (this.hostAdapterLocator != null) {
      this.selectionMenu.addOneInputMethodToMenu(this.hostAdapterLocator, str);
      this.selectionMenu.addSeparator();
    } 
    for (byte b = 0; b < this.javaInputMethodLocatorList.size(); b++) {
      InputMethodLocator inputMethodLocator = (InputMethodLocator)this.javaInputMethodLocatorList.get(b);
      this.selectionMenu.addOneInputMethodToMenu(inputMethodLocator, str);
    } 
    synchronized (this) {
      this.selectionMenu.addToComponent(this.requestComponent);
      this.requestInputContext = this.currentInputContext;
      this.selectionMenu.show(this.requestComponent, 60, 80);
      this.requestComponent = null;
    } 
  }
  
  private String getCurrentSelection() {
    InputContext inputContext = this.currentInputContext;
    if (inputContext != null) {
      InputMethodLocator inputMethodLocator = inputContext.getInputMethodLocator();
      if (inputMethodLocator != null)
        return inputMethodLocator.getActionCommandString(); 
    } 
    return null;
  }
  
  void changeInputMethod(String paramString) {
    InputMethodLocator inputMethodLocator = null;
    String str1 = paramString;
    String str2 = null;
    int i = paramString.indexOf('\n');
    if (i != -1) {
      str2 = paramString.substring(i + 1);
      str1 = paramString.substring(0, i);
    } 
    if (this.hostAdapterLocator.getActionCommandString().equals(str1)) {
      inputMethodLocator = this.hostAdapterLocator;
    } else {
      for (byte b = 0; b < this.javaInputMethodLocatorList.size(); b++) {
        InputMethodLocator inputMethodLocator1 = (InputMethodLocator)this.javaInputMethodLocatorList.get(b);
        String str = inputMethodLocator1.getActionCommandString();
        if (str.equals(str1)) {
          inputMethodLocator = inputMethodLocator1;
          break;
        } 
      } 
    } 
    if (inputMethodLocator != null && str2 != null) {
      String str3 = "";
      String str4 = "";
      String str5 = "";
      int j = str2.indexOf('_');
      if (j == -1) {
        str3 = str2;
      } else {
        str3 = str2.substring(0, j);
        int k = j + 1;
        j = str2.indexOf('_', k);
        if (j == -1) {
          str4 = str2.substring(k);
        } else {
          str4 = str2.substring(k, j);
          str5 = str2.substring(j + 1);
        } 
      } 
      Locale locale = new Locale(str3, str4, str5);
      inputMethodLocator = inputMethodLocator.deriveLocator(locale);
    } 
    if (inputMethodLocator == null)
      return; 
    if (this.requestInputContext != null) {
      this.requestInputContext.changeInputMethod(inputMethodLocator);
      this.requestInputContext = null;
      putPreferredInputMethod(inputMethodLocator);
    } 
  }
  
  InputMethodLocator findInputMethod(Locale paramLocale) {
    InputMethodLocator inputMethodLocator = getPreferredInputMethod(paramLocale);
    if (inputMethodLocator != null)
      return inputMethodLocator; 
    if (this.hostAdapterLocator != null && this.hostAdapterLocator.isLocaleAvailable(paramLocale))
      return this.hostAdapterLocator.deriveLocator(paramLocale); 
    initializeInputMethodLocatorList();
    for (byte b = 0; b < this.javaInputMethodLocatorList.size(); b++) {
      InputMethodLocator inputMethodLocator1 = (InputMethodLocator)this.javaInputMethodLocatorList.get(b);
      if (inputMethodLocator1.isLocaleAvailable(paramLocale))
        return inputMethodLocator1.deriveLocator(paramLocale); 
    } 
    return null;
  }
  
  Locale getDefaultKeyboardLocale() {
    Toolkit toolkit = Toolkit.getDefaultToolkit();
    return (toolkit instanceof InputMethodSupport) ? ((InputMethodSupport)toolkit).getDefaultKeyboardLocale() : Locale.getDefault();
  }
  
  private InputMethodLocator getPreferredInputMethod(Locale paramLocale) {
    InputMethodLocator inputMethodLocator = null;
    if (!hasMultipleInputMethods())
      return null; 
    inputMethodLocator = (InputMethodLocator)this.preferredLocatorCache.get(paramLocale.toString().intern());
    if (inputMethodLocator != null)
      return inputMethodLocator; 
    String str1 = findPreferredInputMethodNode(paramLocale);
    String str2 = readPreferredInputMethod(str1);
    if (str2 != null) {
      if (this.hostAdapterLocator != null && this.hostAdapterLocator.getDescriptor().getClass().getName().equals(str2)) {
        Locale locale = getAdvertisedLocale(this.hostAdapterLocator, paramLocale);
        if (locale != null) {
          inputMethodLocator = this.hostAdapterLocator.deriveLocator(locale);
          this.preferredLocatorCache.put(paramLocale.toString().intern(), inputMethodLocator);
        } 
        return inputMethodLocator;
      } 
      for (byte b = 0; b < this.javaInputMethodLocatorList.size(); b++) {
        InputMethodLocator inputMethodLocator1 = (InputMethodLocator)this.javaInputMethodLocatorList.get(b);
        InputMethodDescriptor inputMethodDescriptor = inputMethodLocator1.getDescriptor();
        if (inputMethodDescriptor.getClass().getName().equals(str2)) {
          Locale locale = getAdvertisedLocale(inputMethodLocator1, paramLocale);
          if (locale != null) {
            inputMethodLocator = inputMethodLocator1.deriveLocator(locale);
            this.preferredLocatorCache.put(paramLocale.toString().intern(), inputMethodLocator);
          } 
          return inputMethodLocator;
        } 
      } 
      writePreferredInputMethod(str1, null);
    } 
    return null;
  }
  
  private String findPreferredInputMethodNode(Locale paramLocale) {
    if (this.userRoot == null)
      return null; 
    for (String str = "/sun/awt/im/preferredInputMethod/" + createLocalePath(paramLocale); !str.equals("/sun/awt/im/preferredInputMethod"); str = str.substring(0, str.lastIndexOf('/'))) {
      try {
        if (this.userRoot.nodeExists(str) && readPreferredInputMethod(str) != null)
          return str; 
      } catch (BackingStoreException backingStoreException) {}
    } 
    return null;
  }
  
  private String readPreferredInputMethod(String paramString) { return (this.userRoot == null || paramString == null) ? null : this.userRoot.node(paramString).get("descriptor", null); }
  
  private void putPreferredInputMethod(InputMethodLocator paramInputMethodLocator) {
    InputMethodDescriptor inputMethodDescriptor = paramInputMethodLocator.getDescriptor();
    Locale locale = paramInputMethodLocator.getLocale();
    if (locale == null)
      try {
        Locale[] arrayOfLocale = inputMethodDescriptor.getAvailableLocales();
        if (arrayOfLocale.length == 1) {
          locale = arrayOfLocale[0];
        } else {
          return;
        } 
      } catch (AWTException aWTException) {
        return;
      }  
    if (locale.equals(Locale.JAPAN))
      locale = Locale.JAPANESE; 
    if (locale.equals(Locale.KOREA))
      locale = Locale.KOREAN; 
    if (locale.equals(new Locale("th", "TH")))
      locale = new Locale("th"); 
    String str = "/sun/awt/im/preferredInputMethod/" + createLocalePath(locale);
    writePreferredInputMethod(str, inputMethodDescriptor.getClass().getName());
    this.preferredLocatorCache.put(locale.toString().intern(), paramInputMethodLocator.deriveLocator(locale));
  }
  
  private String createLocalePath(Locale paramLocale) {
    String str1 = paramLocale.getLanguage();
    String str2 = paramLocale.getCountry();
    String str3 = paramLocale.getVariant();
    String str4 = null;
    if (!str3.equals("")) {
      str4 = "_" + str1 + "/_" + str2 + "/_" + str3;
    } else if (!str2.equals("")) {
      str4 = "_" + str1 + "/_" + str2;
    } else {
      str4 = "_" + str1;
    } 
    return str4;
  }
  
  private void writePreferredInputMethod(String paramString1, String paramString2) {
    if (this.userRoot != null) {
      Preferences preferences = this.userRoot.node(paramString1);
      if (paramString2 != null) {
        preferences.put("descriptor", paramString2);
      } else {
        preferences.remove("descriptor");
      } 
    } 
  }
  
  private Preferences getUserRoot() { return (Preferences)AccessController.doPrivileged(new PrivilegedAction<Preferences>() {
          public Preferences run() { return Preferences.userRoot(); }
        }); }
  
  private Locale getAdvertisedLocale(InputMethodLocator paramInputMethodLocator, Locale paramLocale) {
    Locale locale = null;
    if (paramInputMethodLocator.isLocaleAvailable(paramLocale)) {
      locale = paramLocale;
    } else if (paramLocale.getLanguage().equals("ja")) {
      if (paramInputMethodLocator.isLocaleAvailable(Locale.JAPAN)) {
        locale = Locale.JAPAN;
      } else if (paramInputMethodLocator.isLocaleAvailable(Locale.JAPANESE)) {
        locale = Locale.JAPANESE;
      } 
    } else if (paramLocale.getLanguage().equals("ko")) {
      if (paramInputMethodLocator.isLocaleAvailable(Locale.KOREA)) {
        locale = Locale.KOREA;
      } else if (paramInputMethodLocator.isLocaleAvailable(Locale.KOREAN)) {
        locale = Locale.KOREAN;
      } 
    } else if (paramLocale.getLanguage().equals("th")) {
      if (paramInputMethodLocator.isLocaleAvailable(new Locale("th", "TH"))) {
        locale = new Locale("th", "TH");
      } else if (paramInputMethodLocator.isLocaleAvailable(new Locale("th"))) {
        locale = new Locale("th");
      } 
    } 
    return locale;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\ExecutableInputMethodManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */