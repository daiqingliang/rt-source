package sun.print;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;
import sun.security.action.GetPropertyAction;

public class Win32PrintServiceLookup extends PrintServiceLookup {
  private String defaultPrinter;
  
  private PrintService defaultPrintService;
  
  private String[] printers;
  
  private PrintService[] printServices;
  
  private static Win32PrintServiceLookup win32PrintLUS;
  
  public static Win32PrintServiceLookup getWin32PrintLUS() {
    if (win32PrintLUS == null)
      PrintServiceLookup.lookupDefaultPrintService(); 
    return win32PrintLUS;
  }
  
  public Win32PrintServiceLookup() {
    if (win32PrintLUS == null) {
      win32PrintLUS = this;
      String str = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
      if (str != null && str.startsWith("Windows 98"))
        return; 
      PrinterChangeListener printerChangeListener = new PrinterChangeListener();
      printerChangeListener.setDaemon(true);
      printerChangeListener.start();
    } 
  }
  
  public PrintService[] getPrintServices() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    if (this.printServices == null)
      refreshServices(); 
    return this.printServices;
  }
  
  private void refreshServices() {
    this.printers = getAllPrinterNames();
    if (this.printers == null) {
      this.printServices = new PrintService[0];
      return;
    } 
    PrintService[] arrayOfPrintService = new PrintService[this.printers.length];
    PrintService printService = getDefaultPrintService();
    byte b;
    for (b = 0; b < this.printers.length; b++) {
      if (printService != null && this.printers[b].equals(printService.getName())) {
        arrayOfPrintService[b] = printService;
      } else if (this.printServices == null) {
        arrayOfPrintService[b] = new Win32PrintService(this.printers[b]);
      } else {
        byte b1;
        for (b1 = 0; b1 < this.printServices.length; b1++) {
          if (this.printServices[b1] != null && this.printers[b].equals(this.printServices[b1].getName())) {
            arrayOfPrintService[b] = this.printServices[b1];
            this.printServices[b1] = null;
            break;
          } 
        } 
        if (b1 == this.printServices.length)
          arrayOfPrintService[b] = new Win32PrintService(this.printers[b]); 
      } 
    } 
    if (this.printServices != null)
      for (b = 0; b < this.printServices.length; b++) {
        if (this.printServices[b] instanceof Win32PrintService && !this.printServices[b].equals(this.defaultPrintService))
          ((Win32PrintService)this.printServices[b]).invalidateService(); 
      }  
    this.printServices = arrayOfPrintService;
  }
  
  public PrintService getPrintServiceByName(String paramString) {
    if (paramString == null || paramString.equals(""))
      return null; 
    PrintService[] arrayOfPrintService = getPrintServices();
    for (byte b = 0; b < arrayOfPrintService.length; b++) {
      if (arrayOfPrintService[b].getName().equals(paramString))
        return arrayOfPrintService[b]; 
    } 
    return null;
  }
  
  boolean matchingService(PrintService paramPrintService, PrintServiceAttributeSet paramPrintServiceAttributeSet) {
    if (paramPrintServiceAttributeSet != null) {
      Attribute[] arrayOfAttribute = paramPrintServiceAttributeSet.toArray();
      for (byte b = 0; b < arrayOfAttribute.length; b++) {
        PrintServiceAttribute printServiceAttribute = paramPrintService.getAttribute(arrayOfAttribute[b].getCategory());
        if (printServiceAttribute == null || !printServiceAttribute.equals(arrayOfAttribute[b]))
          return false; 
      } 
    } 
    return true;
  }
  
  public PrintService[] getPrintServices(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    HashPrintRequestAttributeSet hashPrintRequestAttributeSet = null;
    HashPrintServiceAttributeSet hashPrintServiceAttributeSet = null;
    if (paramAttributeSet != null && !paramAttributeSet.isEmpty()) {
      hashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
      hashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
      Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
      for (byte b1 = 0; b1 < arrayOfAttribute.length; b1++) {
        if (arrayOfAttribute[b1] instanceof javax.print.attribute.PrintRequestAttribute) {
          hashPrintRequestAttributeSet.add(arrayOfAttribute[b1]);
        } else if (arrayOfAttribute[b1] instanceof PrintServiceAttribute) {
          hashPrintServiceAttributeSet.add(arrayOfAttribute[b1]);
        } 
      } 
    } 
    PrintService[] arrayOfPrintService = null;
    if (hashPrintServiceAttributeSet != null && hashPrintServiceAttributeSet.get(PrinterName.class) != null) {
      PrinterName printerName = (PrinterName)hashPrintServiceAttributeSet.get(PrinterName.class);
      PrintService printService = getPrintServiceByName(printerName.getValue());
      if (printService == null || !matchingService(printService, hashPrintServiceAttributeSet)) {
        arrayOfPrintService = new PrintService[0];
      } else {
        arrayOfPrintService = new PrintService[1];
        arrayOfPrintService[0] = printService;
      } 
    } else {
      arrayOfPrintService = getPrintServices();
    } 
    if (arrayOfPrintService.length == 0)
      return arrayOfPrintService; 
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < arrayOfPrintService.length; b++) {
      try {
        if (arrayOfPrintService[b].getUnsupportedAttributes(paramDocFlavor, hashPrintRequestAttributeSet) == null)
          arrayList.add(arrayOfPrintService[b]); 
      } catch (IllegalArgumentException illegalArgumentException) {}
    } 
    arrayOfPrintService = new PrintService[arrayList.size()];
    return (PrintService[])arrayList.toArray(arrayOfPrintService);
  }
  
  public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] paramArrayOfDocFlavor, AttributeSet paramAttributeSet) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    return new MultiDocPrintService[0];
  }
  
  public PrintService getDefaultPrintService() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    this.defaultPrinter = getDefaultPrinterName();
    if (this.defaultPrinter == null)
      return null; 
    if (this.defaultPrintService != null && this.defaultPrintService.getName().equals(this.defaultPrinter))
      return this.defaultPrintService; 
    this.defaultPrintService = null;
    if (this.printServices != null)
      for (byte b = 0; b < this.printServices.length; b++) {
        if (this.defaultPrinter.equals(this.printServices[b].getName())) {
          this.defaultPrintService = this.printServices[b];
          break;
        } 
      }  
    if (this.defaultPrintService == null)
      this.defaultPrintService = new Win32PrintService(this.defaultPrinter); 
    return this.defaultPrintService;
  }
  
  private native String getDefaultPrinterName();
  
  private native String[] getAllPrinterNames();
  
  private native long notifyFirstPrinterChange(String paramString);
  
  private native void notifyClosePrinterChange(long paramLong);
  
  private native int notifyPrinterChange(long paramLong);
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("awt");
            return null;
          }
        });
  }
  
  class PrinterChangeListener extends Thread {
    long chgObj;
    
    PrinterChangeListener() { this.chgObj = this$0.notifyFirstPrinterChange(null); }
    
    public void run() {
      if (this.chgObj != -1L)
        while (true) {
          if (Win32PrintServiceLookup.this.notifyPrinterChange(this.chgObj) != 0)
            try {
              Win32PrintServiceLookup.this.refreshServices();
              continue;
            } catch (SecurityException securityException) {
              break;
            }  
          Win32PrintServiceLookup.this.notifyClosePrinterChange(this.chgObj);
          break;
        }  
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\Win32PrintServiceLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */