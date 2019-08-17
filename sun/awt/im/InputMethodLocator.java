package sun.awt.im;

import java.awt.AWTException;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

final class InputMethodLocator {
  private InputMethodDescriptor descriptor;
  
  private ClassLoader loader;
  
  private Locale locale;
  
  InputMethodLocator(InputMethodDescriptor paramInputMethodDescriptor, ClassLoader paramClassLoader, Locale paramLocale) {
    if (paramInputMethodDescriptor == null)
      throw new NullPointerException("descriptor can't be null"); 
    this.descriptor = paramInputMethodDescriptor;
    this.loader = paramClassLoader;
    this.locale = paramLocale;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    InputMethodLocator inputMethodLocator = (InputMethodLocator)paramObject;
    return !this.descriptor.getClass().equals(inputMethodLocator.descriptor.getClass()) ? false : (((this.loader == null && inputMethodLocator.loader != null) || (this.loader != null && !this.loader.equals(inputMethodLocator.loader))) ? false : (!((this.locale == null && inputMethodLocator.locale != null) || (this.locale != null && !this.locale.equals(inputMethodLocator.locale)))));
  }
  
  public int hashCode() {
    int i = this.descriptor.hashCode();
    if (this.loader != null)
      i |= this.loader.hashCode() << 10; 
    if (this.locale != null)
      i |= this.locale.hashCode() << 20; 
    return i;
  }
  
  InputMethodDescriptor getDescriptor() { return this.descriptor; }
  
  ClassLoader getClassLoader() { return this.loader; }
  
  Locale getLocale() { return this.locale; }
  
  boolean isLocaleAvailable(Locale paramLocale) {
    try {
      Locale[] arrayOfLocale = this.descriptor.getAvailableLocales();
      for (byte b = 0; b < arrayOfLocale.length; b++) {
        if (arrayOfLocale[b].equals(paramLocale))
          return true; 
      } 
    } catch (AWTException aWTException) {}
    return false;
  }
  
  InputMethodLocator deriveLocator(Locale paramLocale) { return (paramLocale == this.locale) ? this : new InputMethodLocator(this.descriptor, this.loader, paramLocale); }
  
  boolean sameInputMethod(InputMethodLocator paramInputMethodLocator) { return (paramInputMethodLocator == this) ? true : ((paramInputMethodLocator == null) ? false : (!this.descriptor.getClass().equals(paramInputMethodLocator.descriptor.getClass()) ? false : (!((this.loader == null && paramInputMethodLocator.loader != null) || (this.loader != null && !this.loader.equals(paramInputMethodLocator.loader)))))); }
  
  String getActionCommandString() {
    String str = this.descriptor.getClass().getName();
    return (this.locale == null) ? str : (str + "\n" + this.locale.toString());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\im\InputMethodLocator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */