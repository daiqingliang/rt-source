package sun.applet;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.print.attribute.HashPrintRequestAttributeSet;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.misc.Ref;

public class AppletViewer extends Frame implements AppletContext, Printable {
  private static String defaultSaveFile = "Applet.ser";
  
  AppletViewerPanel panel;
  
  Label label;
  
  PrintStream statusMsgStream;
  
  AppletViewerFactory factory;
  
  private static Map audioClips = new HashMap();
  
  private static Map imageRefs = new HashMap();
  
  static Vector appletPanels = new Vector();
  
  static Hashtable systemParam = new Hashtable();
  
  static AppletProps props;
  
  static int c;
  
  private static int x;
  
  private static int y;
  
  private static final int XDELTA = 30;
  
  private static final int YDELTA = 30;
  
  static String encoding;
  
  private static AppletMessageHandler amh;
  
  public AppletViewer(int paramInt1, int paramInt2, URL paramURL, Hashtable paramHashtable, PrintStream paramPrintStream, AppletViewerFactory paramAppletViewerFactory) {
    this.factory = paramAppletViewerFactory;
    this.statusMsgStream = paramPrintStream;
    setTitle(amh.getMessage("tool.title", paramHashtable.get("code")));
    MenuBar menuBar = paramAppletViewerFactory.getBaseMenuBar();
    Menu menu = new Menu(amh.getMessage("menu.applet"));
    addMenuItem(menu, "menuitem.restart");
    addMenuItem(menu, "menuitem.reload");
    addMenuItem(menu, "menuitem.stop");
    addMenuItem(menu, "menuitem.save");
    addMenuItem(menu, "menuitem.start");
    addMenuItem(menu, "menuitem.clone");
    menu.add(new MenuItem("-"));
    addMenuItem(menu, "menuitem.tag");
    addMenuItem(menu, "menuitem.info");
    addMenuItem(menu, "menuitem.edit").disable();
    addMenuItem(menu, "menuitem.encoding");
    menu.add(new MenuItem("-"));
    addMenuItem(menu, "menuitem.print");
    menu.add(new MenuItem("-"));
    addMenuItem(menu, "menuitem.props");
    menu.add(new MenuItem("-"));
    addMenuItem(menu, "menuitem.close");
    if (paramAppletViewerFactory.isStandalone())
      addMenuItem(menu, "menuitem.quit"); 
    menuBar.add(menu);
    setMenuBar(menuBar);
    add("Center", this.panel = new AppletViewerPanel(paramURL, paramHashtable));
    add("South", this.label = new Label(amh.getMessage("label.hello")));
    this.panel.init();
    appletPanels.addElement(this.panel);
    pack();
    move(paramInt1, paramInt2);
    setVisible(true);
    WindowAdapter windowAdapter = new WindowAdapter() {
        public void windowClosing(WindowEvent param1WindowEvent) { AppletViewer.this.appletClose(); }
        
        public void windowIconified(WindowEvent param1WindowEvent) { AppletViewer.this.appletStop(); }
        
        public void windowDeiconified(WindowEvent param1WindowEvent) { AppletViewer.this.appletStart(); }
      };
    addWindowListener(windowAdapter);
    class AppletEventListener implements AppletListener {
      final Frame frame;
      
      public AppletEventListener(Frame param1Frame) { this.frame = param1Frame; }
      
      public void appletStateChanged(AppletEvent param1AppletEvent) {
        Applet applet;
        AppletPanel appletPanel = (AppletPanel)param1AppletEvent.getSource();
        switch (param1AppletEvent.getID()) {
          case 51234:
            if (appletPanel != null) {
              AppletViewer.this.resize(AppletViewer.this.preferredSize());
              AppletViewer.this.validate();
            } 
            break;
          case 51236:
            applet = appletPanel.getApplet();
            if (applet != null) {
              AppletPanel.changeFrameAppContext(this.frame, SunToolkit.targetToAppContext(applet));
              break;
            } 
            AppletPanel.changeFrameAppContext(this.frame, AppContext.getAppContext());
            break;
        } 
      }
    };
    this.panel.addAppletListener(new AppletEventListener(this));
    showStatus(amh.getMessage("status.start"));
    initEventQueue();
  }
  
  public MenuItem addMenuItem(Menu paramMenu, String paramString) {
    MenuItem menuItem = new MenuItem(amh.getMessage(paramString));
    menuItem.addActionListener(new UserActionListener(null));
    return paramMenu.add(menuItem);
  }
  
  private void initEventQueue() {
    String str = System.getProperty("appletviewer.send.event");
    if (str == null) {
      this.panel.sendEvent(1);
      this.panel.sendEvent(2);
      this.panel.sendEvent(3);
    } else {
      String[] arrayOfString = splitSeparator(",", str);
      for (byte b = 0; b < arrayOfString.length; b++) {
        System.out.println("Adding event to queue: " + arrayOfString[b]);
        if (arrayOfString[b].equals("dispose")) {
          this.panel.sendEvent(0);
        } else if (arrayOfString[b].equals("load")) {
          this.panel.sendEvent(1);
        } else if (arrayOfString[b].equals("init")) {
          this.panel.sendEvent(2);
        } else if (arrayOfString[b].equals("start")) {
          this.panel.sendEvent(3);
        } else if (arrayOfString[b].equals("stop")) {
          this.panel.sendEvent(4);
        } else if (arrayOfString[b].equals("destroy")) {
          this.panel.sendEvent(5);
        } else if (arrayOfString[b].equals("quit")) {
          this.panel.sendEvent(6);
        } else if (arrayOfString[b].equals("error")) {
          this.panel.sendEvent(7);
        } else {
          System.out.println("Unrecognized event name: " + arrayOfString[b]);
        } 
      } 
      while (!this.panel.emptyEventQueue());
      appletSystemExit();
    } 
  }
  
  private String[] splitSeparator(String paramString1, String paramString2) {
    Vector vector = new Vector();
    int i = 0;
    int j = 0;
    while ((j = paramString2.indexOf(paramString1, i)) != -1) {
      vector.addElement(paramString2.substring(i, j));
      i = j + 1;
    } 
    vector.addElement(paramString2.substring(i));
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public AudioClip getAudioClip(URL paramURL) {
    checkConnect(paramURL);
    synchronized (audioClips) {
      AudioClip audioClip = (AudioClip)audioClips.get(paramURL);
      if (audioClip == null)
        audioClips.put(paramURL, audioClip = new AppletAudioClip(paramURL)); 
      return audioClip;
    } 
  }
  
  public Image getImage(URL paramURL) { return getCachedImage(paramURL); }
  
  static Image getCachedImage(URL paramURL) { return (Image)getCachedImageRef(paramURL).get(); }
  
  static Ref getCachedImageRef(URL paramURL) {
    synchronized (imageRefs) {
      AppletImageRef appletImageRef = (AppletImageRef)imageRefs.get(paramURL);
      if (appletImageRef == null) {
        appletImageRef = new AppletImageRef(paramURL);
        imageRefs.put(paramURL, appletImageRef);
      } 
      return appletImageRef;
    } 
  }
  
  static void flushImageCache() { imageRefs.clear(); }
  
  public Applet getApplet(String paramString) {
    AppletSecurity appletSecurity = (AppletSecurity)System.getSecurityManager();
    paramString = paramString.toLowerCase();
    SocketPermission socketPermission = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
    Enumeration enumeration = appletPanels.elements();
    while (enumeration.hasMoreElements()) {
      AppletPanel appletPanel = (AppletPanel)enumeration.nextElement();
      String str = appletPanel.getParameter("name");
      if (str != null)
        str = str.toLowerCase(); 
      if (paramString.equals(str) && appletPanel.getDocumentBase().equals(this.panel.getDocumentBase())) {
        SocketPermission socketPermission1 = new SocketPermission(appletPanel.getCodeBase().getHost(), "connect");
        if (socketPermission.implies(socketPermission1))
          return appletPanel.applet; 
      } 
    } 
    return null;
  }
  
  public Enumeration getApplets() {
    AppletSecurity appletSecurity = (AppletSecurity)System.getSecurityManager();
    Vector vector = new Vector();
    SocketPermission socketPermission = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
    Enumeration enumeration = appletPanels.elements();
    while (enumeration.hasMoreElements()) {
      AppletPanel appletPanel = (AppletPanel)enumeration.nextElement();
      if (appletPanel.getDocumentBase().equals(this.panel.getDocumentBase())) {
        SocketPermission socketPermission1 = new SocketPermission(appletPanel.getCodeBase().getHost(), "connect");
        if (socketPermission.implies(socketPermission1))
          vector.addElement(appletPanel.applet); 
      } 
    } 
    return vector.elements();
  }
  
  public void showDocument(URL paramURL) {}
  
  public void showDocument(URL paramURL, String paramString) {}
  
  public void showStatus(String paramString) { this.label.setText(paramString); }
  
  public void setStream(String paramString, InputStream paramInputStream) throws IOException {}
  
  public InputStream getStream(String paramString) { return null; }
  
  public Iterator getStreamKeys() { return null; }
  
  public static void printTag(PrintStream paramPrintStream, Hashtable paramHashtable) {
    paramPrintStream.print("<applet");
    String str = (String)paramHashtable.get("codebase");
    if (str != null)
      paramPrintStream.print(" codebase=\"" + str + "\""); 
    str = (String)paramHashtable.get("code");
    if (str == null)
      str = "applet.class"; 
    paramPrintStream.print(" code=\"" + str + "\"");
    str = (String)paramHashtable.get("width");
    if (str == null)
      str = "150"; 
    paramPrintStream.print(" width=" + str);
    str = (String)paramHashtable.get("height");
    if (str == null)
      str = "100"; 
    paramPrintStream.print(" height=" + str);
    str = (String)paramHashtable.get("name");
    if (str != null)
      paramPrintStream.print(" name=\"" + str + "\""); 
    paramPrintStream.println(">");
    int i = paramHashtable.size();
    String[] arrayOfString = new String[i];
    i = 0;
    Enumeration enumeration = paramHashtable.keys();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      int j;
      for (j = 0; j < i && arrayOfString[j].compareTo(str1) < 0; j++);
      System.arraycopy(arrayOfString, j, arrayOfString, j + 1, i - j);
      arrayOfString[j] = str1;
      i++;
    } 
    for (byte b = 0; b < i; b++) {
      String str1 = arrayOfString[b];
      if (systemParam.get(str1) == null)
        paramPrintStream.println("<param name=" + str1 + " value=\"" + paramHashtable.get(str1) + "\">"); 
    } 
    paramPrintStream.println("</applet>");
  }
  
  public void updateAtts() {
    Dimension dimension = this.panel.size();
    Insets insets = this.panel.insets();
    this.panel.atts.put("width", Integer.toString(dimension.width - insets.left + insets.right));
    this.panel.atts.put("height", Integer.toString(dimension.height - insets.top + insets.bottom));
  }
  
  void appletRestart() {
    this.panel.sendEvent(4);
    this.panel.sendEvent(5);
    this.panel.sendEvent(2);
    this.panel.sendEvent(3);
  }
  
  void appletReload() {
    this.panel.sendEvent(4);
    this.panel.sendEvent(5);
    this.panel.sendEvent(0);
    AppletPanel.flushClassLoader(this.panel.getClassLoaderCacheKey());
    try {
      this.panel.joinAppletThread();
      this.panel.release();
    } catch (InterruptedException interruptedException) {
      return;
    } 
    this.panel.createAppletThread();
    this.panel.sendEvent(1);
    this.panel.sendEvent(2);
    this.panel.sendEvent(3);
  }
  
  void appletSave() { AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            AppletViewer.this.panel.sendEvent(4);
            FileDialog fileDialog = new FileDialog(AppletViewer.this, amh.getMessage("appletsave.filedialogtitle"), 1);
            fileDialog.setDirectory(System.getProperty("user.dir"));
            fileDialog.setFile(defaultSaveFile);
            fileDialog.show();
            String str1 = fileDialog.getFile();
            if (str1 == null) {
              AppletViewer.this.panel.sendEvent(3);
              return null;
            } 
            String str2 = fileDialog.getDirectory();
            File file = new File(str2, str1);
            try(FileOutputStream null = new FileOutputStream(file); BufferedOutputStream null = new BufferedOutputStream(fileOutputStream); ObjectOutputStream null = new ObjectOutputStream(bufferedOutputStream)) {
              AppletViewer.this.showStatus(amh.getMessage("appletsave.err1", this.this$0.panel.applet.toString(), file.toString()));
              objectOutputStream.writeObject(this.this$0.panel.applet);
            } catch (IOException iOException) {
              System.err.println(amh.getMessage("appletsave.err2", iOException));
            } finally {
              AppletViewer.this.panel.sendEvent(3);
            } 
            return null;
          }
        }); }
  
  void appletClone() {
    Point point = location();
    updateAtts();
    this.factory.createAppletViewer(point.x + 30, point.y + 30, this.panel.documentURL, (Hashtable)this.panel.atts.clone());
  }
  
  void appletTag() {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    updateAtts();
    printTag(new PrintStream(byteArrayOutputStream), this.panel.atts);
    showStatus(amh.getMessage("applettag"));
    Point point = location();
    new TextFrame(point.x + 30, point.y + 30, amh.getMessage("applettag.textframe"), byteArrayOutputStream.toString());
  }
  
  void appletInfo() {
    String str = this.panel.applet.getAppletInfo();
    if (str == null)
      str = amh.getMessage("appletinfo.applet"); 
    str = str + "\n\n";
    String[][] arrayOfString = this.panel.applet.getParameterInfo();
    if (arrayOfString != null) {
      for (byte b = 0; b < arrayOfString.length; b++)
        str = str + arrayOfString[b][0] + " -- " + arrayOfString[b][1] + " -- " + arrayOfString[b][2] + "\n"; 
    } else {
      str = str + amh.getMessage("appletinfo.param");
    } 
    Point point = location();
    new TextFrame(point.x + 30, point.y + 30, amh.getMessage("appletinfo.textframe"), str);
  }
  
  void appletCharacterEncoding() { showStatus(amh.getMessage("appletencoding", encoding)); }
  
  void appletEdit() {}
  
  void appletPrint() {
    PrinterJob printerJob = PrinterJob.getPrinterJob();
    if (printerJob != null) {
      HashPrintRequestAttributeSet hashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
      if (printerJob.printDialog(hashPrintRequestAttributeSet)) {
        printerJob.setPrintable(this);
        try {
          printerJob.print(hashPrintRequestAttributeSet);
          this.statusMsgStream.println(amh.getMessage("appletprint.finish"));
        } catch (PrinterException printerException) {
          this.statusMsgStream.println(amh.getMessage("appletprint.fail"));
        } 
      } else {
        this.statusMsgStream.println(amh.getMessage("appletprint.cancel"));
      } 
    } else {
      this.statusMsgStream.println(amh.getMessage("appletprint.fail"));
    } 
  }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) {
    if (paramInt > 0)
      return 1; 
    Graphics2D graphics2D = (Graphics2D)paramGraphics;
    graphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
    this.panel.applet.printAll(paramGraphics);
    return 0;
  }
  
  public static void networkProperties() {
    if (props == null)
      props = new AppletProps(); 
    props.addNotify();
    props.setVisible(true);
  }
  
  void appletStart() { this.panel.sendEvent(3); }
  
  void appletStop() { this.panel.sendEvent(4); }
  
  private void appletShutdown(AppletPanel paramAppletPanel) {
    paramAppletPanel.sendEvent(4);
    paramAppletPanel.sendEvent(5);
    paramAppletPanel.sendEvent(0);
    paramAppletPanel.sendEvent(6);
  }
  
  void appletClose() {
    AppletViewerPanel appletViewerPanel = this.panel;
    (new Thread(new Runnable(this, appletViewerPanel) {
          public void run() {
            AppletViewer.this.appletShutdown(p);
            AppletViewer.appletPanels.removeElement(p);
            AppletViewer.this.dispose();
            if (AppletViewer.countApplets() == 0)
              AppletViewer.this.appletSystemExit(); 
          }
        })).start();
  }
  
  private void appletSystemExit() {
    if (this.factory.isStandalone())
      System.exit(0); 
  }
  
  protected void appletQuit() { (new Thread(new Runnable(this) {
          public void run() {
            Enumeration enumeration = AppletViewer.appletPanels.elements();
            while (enumeration.hasMoreElements()) {
              AppletPanel appletPanel = (AppletPanel)enumeration.nextElement();
              AppletViewer.this.appletShutdown(appletPanel);
            } 
            AppletViewer.this.appletSystemExit();
          }
        })).start(); }
  
  public void processUserAction(ActionEvent paramActionEvent) {
    String str = ((MenuItem)paramActionEvent.getSource()).getLabel();
    if (amh.getMessage("menuitem.restart").equals(str)) {
      appletRestart();
      return;
    } 
    if (amh.getMessage("menuitem.reload").equals(str)) {
      appletReload();
      return;
    } 
    if (amh.getMessage("menuitem.clone").equals(str)) {
      appletClone();
      return;
    } 
    if (amh.getMessage("menuitem.stop").equals(str)) {
      appletStop();
      return;
    } 
    if (amh.getMessage("menuitem.save").equals(str)) {
      appletSave();
      return;
    } 
    if (amh.getMessage("menuitem.start").equals(str)) {
      appletStart();
      return;
    } 
    if (amh.getMessage("menuitem.tag").equals(str)) {
      appletTag();
      return;
    } 
    if (amh.getMessage("menuitem.info").equals(str)) {
      appletInfo();
      return;
    } 
    if (amh.getMessage("menuitem.encoding").equals(str)) {
      appletCharacterEncoding();
      return;
    } 
    if (amh.getMessage("menuitem.edit").equals(str)) {
      appletEdit();
      return;
    } 
    if (amh.getMessage("menuitem.print").equals(str)) {
      appletPrint();
      return;
    } 
    if (amh.getMessage("menuitem.props").equals(str)) {
      networkProperties();
      return;
    } 
    if (amh.getMessage("menuitem.close").equals(str)) {
      appletClose();
      return;
    } 
    if (this.factory.isStandalone() && amh.getMessage("menuitem.quit").equals(str)) {
      appletQuit();
      return;
    } 
  }
  
  public static int countApplets() { return appletPanels.size(); }
  
  public static void skipSpace(Reader paramReader) throws IOException {
    while (c >= 0 && (c == 32 || c == 9 || c == 10 || c == 13))
      c = paramReader.read(); 
  }
  
  public static String scanIdentifier(Reader paramReader) throws IOException {
    StringBuffer stringBuffer = new StringBuffer();
    while ((c >= 97 && c <= 122) || (c >= 65 && c <= 90) || (c >= 48 && c <= 57) || c == 95) {
      stringBuffer.append((char)c);
      c = paramReader.read();
    } 
    return stringBuffer.toString();
  }
  
  public static Hashtable scanTag(Reader paramReader) throws IOException {
    Hashtable hashtable = new Hashtable();
    skipSpace(paramReader);
    while (c >= 0 && c != 62) {
      String str1 = scanIdentifier(paramReader);
      String str2 = "";
      skipSpace(paramReader);
      if (c == 61) {
        int i = -1;
        c = paramReader.read();
        skipSpace(paramReader);
        if (c == 39 || c == 34) {
          i = c;
          c = paramReader.read();
        } 
        StringBuffer stringBuffer = new StringBuffer();
        while (c > 0 && ((i < 0 && c != 32 && c != 9 && c != 10 && c != 13 && c != 62) || (i >= 0 && c != i))) {
          stringBuffer.append((char)c);
          c = paramReader.read();
        } 
        if (c == i)
          c = paramReader.read(); 
        skipSpace(paramReader);
        str2 = stringBuffer.toString();
      } 
      if (!str2.equals(""))
        hashtable.put(str1.toLowerCase(Locale.ENGLISH), str2); 
      while (c != 62 && c >= 0 && (c < 97 || c > 122) && (c < 65 || c > 90) && (c < 48 || c > 57) && c != 95)
        c = paramReader.read(); 
    } 
    return hashtable;
  }
  
  private static Reader makeReader(InputStream paramInputStream) {
    if (encoding != null)
      try {
        return new BufferedReader(new InputStreamReader(paramInputStream, encoding));
      } catch (IOException iOException) {} 
    InputStreamReader inputStreamReader = new InputStreamReader(paramInputStream);
    encoding = inputStreamReader.getEncoding();
    return new BufferedReader(inputStreamReader);
  }
  
  public static void parse(URL paramURL, String paramString) {
    encoding = paramString;
    parse(paramURL, System.out, new StdAppletViewerFactory());
  }
  
  public static void parse(URL paramURL) { parse(paramURL, System.out, new StdAppletViewerFactory()); }
  
  public static void parse(URL paramURL, PrintStream paramPrintStream, AppletViewerFactory paramAppletViewerFactory) throws IOException {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    String str1 = amh.getMessage("parse.warning.requiresname");
    String str2 = amh.getMessage("parse.warning.paramoutside");
    String str3 = amh.getMessage("parse.warning.applet.requirescode");
    String str4 = amh.getMessage("parse.warning.applet.requiresheight");
    String str5 = amh.getMessage("parse.warning.applet.requireswidth");
    String str6 = amh.getMessage("parse.warning.object.requirescode");
    String str7 = amh.getMessage("parse.warning.object.requiresheight");
    String str8 = amh.getMessage("parse.warning.object.requireswidth");
    String str9 = amh.getMessage("parse.warning.embed.requirescode");
    String str10 = amh.getMessage("parse.warning.embed.requiresheight");
    String str11 = amh.getMessage("parse.warning.embed.requireswidth");
    String str12 = amh.getMessage("parse.warning.appnotLongersupported");
    URLConnection uRLConnection = paramURL.openConnection();
    Reader reader = makeReader(uRLConnection.getInputStream());
    paramURL = uRLConnection.getURL();
    byte b = 1;
    Hashtable hashtable = null;
    while (true) {
      c = reader.read();
      if (c == -1)
        break; 
      if (c == 60) {
        c = reader.read();
        if (c == 47) {
          c = reader.read();
          String str13 = scanIdentifier(reader);
          if (str13.equalsIgnoreCase("applet") || str13.equalsIgnoreCase("object") || str13.equalsIgnoreCase("embed")) {
            if (bool2 && hashtable.get("code") == null && hashtable.get("object") == null) {
              paramPrintStream.println(str6);
              hashtable = null;
            } 
            if (hashtable != null) {
              paramAppletViewerFactory.createAppletViewer(x, y, paramURL, hashtable);
              x += 30;
              y += 30;
              Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
              if (x > dimension.width - 300 || y > dimension.height - 300) {
                x = 0;
                y = 2 * b * 30;
                b++;
              } 
            } 
            hashtable = null;
            bool1 = false;
            bool2 = false;
            bool3 = false;
          } 
          continue;
        } 
        String str = scanIdentifier(reader);
        if (str.equalsIgnoreCase("param")) {
          Hashtable hashtable1 = scanTag(reader);
          String str13 = (String)hashtable1.get("name");
          if (str13 == null) {
            paramPrintStream.println(str1);
            continue;
          } 
          String str14 = (String)hashtable1.get("value");
          if (str14 == null) {
            paramPrintStream.println(str1);
            continue;
          } 
          if (hashtable != null) {
            hashtable.put(str13.toLowerCase(), str14);
            continue;
          } 
          paramPrintStream.println(str2);
          continue;
        } 
        if (str.equalsIgnoreCase("applet")) {
          bool1 = true;
          hashtable = scanTag(reader);
          if (hashtable.get("code") == null && hashtable.get("object") == null) {
            paramPrintStream.println(str3);
            hashtable = null;
            continue;
          } 
          if (hashtable.get("width") == null) {
            paramPrintStream.println(str5);
            hashtable = null;
            continue;
          } 
          if (hashtable.get("height") == null) {
            paramPrintStream.println(str4);
            hashtable = null;
          } 
          continue;
        } 
        if (str.equalsIgnoreCase("object")) {
          bool2 = true;
          hashtable = scanTag(reader);
          if (hashtable.get("codebase") != null)
            hashtable.remove("codebase"); 
          if (hashtable.get("width") == null) {
            paramPrintStream.println(str8);
            hashtable = null;
            continue;
          } 
          if (hashtable.get("height") == null) {
            paramPrintStream.println(str7);
            hashtable = null;
          } 
          continue;
        } 
        if (str.equalsIgnoreCase("embed")) {
          bool3 = true;
          hashtable = scanTag(reader);
          if (hashtable.get("code") == null && hashtable.get("object") == null) {
            paramPrintStream.println(str9);
            hashtable = null;
            continue;
          } 
          if (hashtable.get("width") == null) {
            paramPrintStream.println(str11);
            hashtable = null;
            continue;
          } 
          if (hashtable.get("height") == null) {
            paramPrintStream.println(str10);
            hashtable = null;
          } 
          continue;
        } 
        if (str.equalsIgnoreCase("app")) {
          paramPrintStream.println(str12);
          Hashtable hashtable1 = scanTag(reader);
          str = (String)hashtable1.get("class");
          if (str != null) {
            hashtable1.remove("class");
            hashtable1.put("code", str + ".class");
          } 
          str = (String)hashtable1.get("src");
          if (str != null) {
            hashtable1.remove("src");
            hashtable1.put("codebase", str);
          } 
          if (hashtable1.get("width") == null)
            hashtable1.put("width", "100"); 
          if (hashtable1.get("height") == null)
            hashtable1.put("height", "100"); 
          printTag(paramPrintStream, hashtable1);
          paramPrintStream.println();
        } 
      } 
    } 
    reader.close();
  }
  
  @Deprecated
  public static void main(String[] paramArrayOfString) { Main.main(paramArrayOfString); }
  
  private static void checkConnect(URL paramURL) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        Permission permission = paramURL.openConnection().getPermission();
        if (permission != null) {
          securityManager.checkPermission(permission);
        } else {
          securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
        } 
      } catch (IOException iOException) {
        securityManager.checkConnect(paramURL.getHost(), paramURL.getPort());
      }  
  }
  
  static  {
    systemParam.put("codebase", "codebase");
    systemParam.put("code", "code");
    systemParam.put("alt", "alt");
    systemParam.put("width", "width");
    systemParam.put("height", "height");
    systemParam.put("align", "align");
    systemParam.put("vspace", "vspace");
    systemParam.put("hspace", "hspace");
    x = 0;
    y = 0;
    encoding = null;
    amh = new AppletMessageHandler("appletviewer");
  }
  
  private final class UserActionListener implements ActionListener {
    private UserActionListener() {}
    
    public void actionPerformed(ActionEvent param1ActionEvent) { AppletViewer.this.processUserAction(param1ActionEvent); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletViewer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */