package sun.applet;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.File;
import java.io.FileOutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Properties;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;

class AppletProps extends Frame {
  TextField proxyHost;
  
  TextField proxyPort;
  
  Choice accessMode;
  
  private static AppletMessageHandler amh = new AppletMessageHandler("appletprops");
  
  AppletProps() {
    setTitle(amh.getMessage("title"));
    Panel panel = new Panel();
    panel.setLayout(new GridLayout(0, 2));
    panel.add(new Label(amh.getMessage("label.http.server", "Http proxy server:")));
    panel.add(this.proxyHost = new TextField());
    panel.add(new Label(amh.getMessage("label.http.proxy")));
    panel.add(this.proxyPort = new TextField());
    panel.add(new Label(amh.getMessage("label.class")));
    panel.add(this.accessMode = new Choice());
    this.accessMode.addItem(amh.getMessage("choice.class.item.restricted"));
    this.accessMode.addItem(amh.getMessage("choice.class.item.unrestricted"));
    add("Center", panel);
    panel = new Panel();
    panel.add(new Button(amh.getMessage("button.apply")));
    panel.add(new Button(amh.getMessage("button.reset")));
    panel.add(new Button(amh.getMessage("button.cancel")));
    add("South", panel);
    move(200, 150);
    pack();
    reset();
  }
  
  void reset() {
    AppletSecurity appletSecurity = (AppletSecurity)System.getSecurityManager();
    if (appletSecurity != null)
      appletSecurity.reset(); 
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyHost"));
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("http.proxyPort"));
    Boolean bool = (Boolean)AccessController.doPrivileged(new GetBooleanAction("package.restrict.access.sun"));
    boolean bool1 = bool.booleanValue();
    if (bool1) {
      this.accessMode.select(amh.getMessage("choice.class.item.restricted"));
    } else {
      this.accessMode.select(amh.getMessage("choice.class.item.unrestricted"));
    } 
    if (str1 != null) {
      this.proxyHost.setText(str1);
      this.proxyPort.setText(str2);
    } else {
      this.proxyHost.setText("");
      this.proxyPort.setText("");
    } 
  }
  
  void apply() {
    String str1 = this.proxyHost.getText().trim();
    String str2 = this.proxyPort.getText().trim();
    final Properties props = (Properties)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return System.getProperties(); }
        });
    if (str1.length() != 0) {
      int i = 0;
      try {
        i = Integer.parseInt(str2);
      } catch (NumberFormatException numberFormatException) {}
      if (i <= 0) {
        this.proxyPort.selectAll();
        this.proxyPort.requestFocus();
        (new AppletPropsErrorDialog(this, amh.getMessage("title.invalidproxy"), amh.getMessage("label.invalidproxy"), amh.getMessage("button.ok"))).show();
        return;
      } 
      properties.put("http.proxyHost", str1);
      properties.put("http.proxyPort", str2);
    } else {
      properties.put("http.proxyHost", "");
    } 
    if (amh.getMessage("choice.class.item.restricted").equals(this.accessMode.getSelectedItem())) {
      properties.put("package.restrict.access.sun", "true");
    } else {
      properties.put("package.restrict.access.sun", "false");
    } 
    try {
      reset();
      AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Object run() {
              File file = Main.theUserPropertiesFile;
              FileOutputStream fileOutputStream = new FileOutputStream(file);
              Properties properties = new Properties();
              for (byte b = 0; b < Main.avDefaultUserProps.length; b++) {
                String str = Main.avDefaultUserProps[b][0];
                properties.setProperty(str, props.getProperty(str));
              } 
              properties.store(fileOutputStream, amh.getMessage("prop.store"));
              fileOutputStream.close();
              return null;
            }
          });
      hide();
    } catch (PrivilegedActionException privilegedActionException) {
      System.out.println(amh.getMessage("apply.exception", privilegedActionException.getException()));
      privilegedActionException.printStackTrace();
      reset();
    } 
  }
  
  public boolean action(Event paramEvent, Object paramObject) {
    if (amh.getMessage("button.apply").equals(paramObject)) {
      apply();
      return true;
    } 
    if (amh.getMessage("button.reset").equals(paramObject)) {
      reset();
      return true;
    } 
    if (amh.getMessage("button.cancel").equals(paramObject)) {
      reset();
      hide();
      return true;
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\applet\AppletProps.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */