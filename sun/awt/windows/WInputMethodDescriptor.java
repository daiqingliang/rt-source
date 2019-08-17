package sun.awt.windows;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

final class WInputMethodDescriptor implements InputMethodDescriptor {
  public Locale[] getAvailableLocales() {
    Locale[] arrayOfLocale1 = getAvailableLocalesInternal();
    Locale[] arrayOfLocale2 = new Locale[arrayOfLocale1.length];
    System.arraycopy(arrayOfLocale1, 0, arrayOfLocale2, 0, arrayOfLocale1.length);
    return arrayOfLocale2;
  }
  
  static Locale[] getAvailableLocalesInternal() { return getNativeAvailableLocales(); }
  
  public boolean hasDynamicLocaleList() { return true; }
  
  public String getInputMethodDisplayName(Locale paramLocale1, Locale paramLocale2) {
    String str = "System Input Methods";
    if (Locale.getDefault().equals(paramLocale2))
      str = Toolkit.getProperty("AWT.HostInputMethodDisplayName", str); 
    return str;
  }
  
  public Image getInputMethodIcon(Locale paramLocale) { return null; }
  
  public InputMethod createInputMethod() throws Exception { return new WInputMethod(); }
  
  private static native Locale[] getNativeAvailableLocales();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WInputMethodDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */