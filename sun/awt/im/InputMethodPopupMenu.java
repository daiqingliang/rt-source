package sun.awt.im;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

abstract class InputMethodPopupMenu implements ActionListener {
  static InputMethodPopupMenu getInstance(Component paramComponent, String paramString) { return (paramComponent instanceof javax.swing.JFrame || paramComponent instanceof javax.swing.JDialog) ? new JInputMethodPopupMenu(paramString) : new AWTInputMethodPopupMenu(paramString); }
  
  abstract void show(Component paramComponent, int paramInt1, int paramInt2);
  
  abstract void removeAll();
  
  abstract void addSeparator();
  
  abstract void addToComponent(Component paramComponent);
  
  abstract Object createSubmenu(String paramString);
  
  abstract void add(Object paramObject);
  
  abstract void addMenuItem(String paramString1, String paramString2, String paramString3);
  
  abstract void addMenuItem(Object paramObject, String paramString1, String paramString2, String paramString3);
  
  void addOneInputMethodToMenu(InputMethodLocator paramInputMethodLocator, String paramString) {
    boolean bool;
    InputMethodDescriptor inputMethodDescriptor = paramInputMethodLocator.getDescriptor();
    String str1 = inputMethodDescriptor.getInputMethodDisplayName(null, Locale.getDefault());
    String str2 = paramInputMethodLocator.getActionCommandString();
    Locale[] arrayOfLocale = null;
    try {
      arrayOfLocale = inputMethodDescriptor.getAvailableLocales();
      bool = arrayOfLocale.length;
    } catch (AWTException aWTException) {
      bool = false;
    } 
    if (!bool) {
      addMenuItem(str1, null, paramString);
    } else if (bool == true) {
      if (inputMethodDescriptor.hasDynamicLocaleList()) {
        str1 = inputMethodDescriptor.getInputMethodDisplayName(arrayOfLocale[0], Locale.getDefault());
        str2 = paramInputMethodLocator.deriveLocator(arrayOfLocale[0]).getActionCommandString();
      } 
      addMenuItem(str1, str2, paramString);
    } else {
      Object object = createSubmenu(str1);
      add(object);
      for (byte b = 0; b < bool; b++) {
        Locale locale = arrayOfLocale[b];
        String str3 = getLocaleName(locale);
        String str4 = paramInputMethodLocator.deriveLocator(locale).getActionCommandString();
        addMenuItem(object, str3, str4, paramString);
      } 
    } 
  }
  
  static boolean isSelected(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    if (paramString1.equals(paramString2))
      return true; 
    int i = paramString2.indexOf('\n');
    return (i != -1 && paramString2.substring(0, i).equals(paramString1));
  }
  
  String getLocaleName(Locale paramLocale) {
    String str1 = paramLocale.toString();
    String str2 = Toolkit.getProperty("AWT.InputMethodLanguage." + str1, null);
    if (str2 == null) {
      str2 = paramLocale.getDisplayName();
      if (str2 == null || str2.length() == 0)
        str2 = str1; 
    } 
    return str2;
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    String str = paramActionEvent.getActionCommand();
    ((ExecutableInputMethodManager)InputMethodManager.getInstance()).changeInputMethod(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputMethodPopupMenu.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */