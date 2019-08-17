package java.awt.print;

import java.awt.AWTError;
import java.awt.HeadlessException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

public abstract class PrinterJob {
  public static PrinterJob getPrinterJob() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    return (PrinterJob)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str = System.getProperty("java.awt.printerjob", null);
            try {
              return (PrinterJob)Class.forName(str).newInstance();
            } catch (ClassNotFoundException classNotFoundException) {
              throw new AWTError("PrinterJob not found: " + str);
            } catch (InstantiationException instantiationException) {
              throw new AWTError("Could not instantiate PrinterJob: " + str);
            } catch (IllegalAccessException illegalAccessException) {
              throw new AWTError("Could not access PrinterJob: " + str);
            } 
          }
        });
  }
  
  public static PrintService[] lookupPrintServices() { return PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null); }
  
  public static StreamPrintServiceFactory[] lookupStreamPrintServices(String paramString) { return StreamPrintServiceFactory.lookupStreamPrintServiceFactories(DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramString); }
  
  public PrintService getPrintService() { return null; }
  
  public void setPrintService(PrintService paramPrintService) throws PrinterException { throw new PrinterException("Setting a service is not supported on this class"); }
  
  public abstract void setPrintable(Printable paramPrintable);
  
  public abstract void setPrintable(Printable paramPrintable, PageFormat paramPageFormat);
  
  public abstract void setPageable(Pageable paramPageable) throws NullPointerException;
  
  public abstract boolean printDialog() throws HeadlessException;
  
  public boolean printDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws HeadlessException {
    if (paramPrintRequestAttributeSet == null)
      throw new NullPointerException("attributes"); 
    return printDialog();
  }
  
  public abstract PageFormat pageDialog(PageFormat paramPageFormat) throws HeadlessException;
  
  public PageFormat pageDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws HeadlessException {
    if (paramPrintRequestAttributeSet == null)
      throw new NullPointerException("attributes"); 
    return pageDialog(defaultPage());
  }
  
  public abstract PageFormat defaultPage(PageFormat paramPageFormat) throws HeadlessException;
  
  public PageFormat defaultPage() { return defaultPage(new PageFormat()); }
  
  public PageFormat getPageFormat(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws HeadlessException {
    PrintService printService = getPrintService();
    null = defaultPage();
    if (printService == null || paramPrintRequestAttributeSet == null)
      return null; 
    Media media = (Media)paramPrintRequestAttributeSet.get(Media.class);
    MediaPrintableArea mediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
    OrientationRequested orientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    if (media == null && mediaPrintableArea == null && orientationRequested == null)
      return null; 
    Paper paper = null.getPaper();
    if (mediaPrintableArea == null && media != null && printService.isAttributeCategorySupported(MediaPrintableArea.class)) {
      Object object = printService.getSupportedAttributeValues(MediaPrintableArea.class, null, paramPrintRequestAttributeSet);
      if (object instanceof MediaPrintableArea[] && (MediaPrintableArea[])object.length > 0)
        mediaPrintableArea = (MediaPrintableArea[])object[0]; 
    } 
    if (media != null && printService.isAttributeValueSupported(media, null, paramPrintRequestAttributeSet) && media instanceof MediaSizeName) {
      MediaSizeName mediaSizeName = (MediaSizeName)media;
      MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
      if (mediaSize != null) {
        double d1 = 72.0D;
        double d2 = mediaSize.getX(25400) * d1;
        double d3 = mediaSize.getY(25400) * d1;
        paper.setSize(d2, d3);
        if (mediaPrintableArea == null)
          paper.setImageableArea(d1, d1, d2 - 2.0D * d1, d3 - 2.0D * d1); 
      } 
    } 
    if (mediaPrintableArea != null && printService.isAttributeValueSupported(mediaPrintableArea, null, paramPrintRequestAttributeSet)) {
      float[] arrayOfFloat = mediaPrintableArea.getPrintableArea(25400);
      for (byte b = 0; b < arrayOfFloat.length; b++)
        arrayOfFloat[b] = arrayOfFloat[b] * 72.0F; 
      paper.setImageableArea(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
    } 
    if (orientationRequested != null && printService.isAttributeValueSupported(orientationRequested, null, paramPrintRequestAttributeSet)) {
      byte b;
      if (orientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
        b = 2;
      } else if (orientationRequested.equals(OrientationRequested.LANDSCAPE)) {
        b = 0;
      } else {
        b = 1;
      } 
      null.setOrientation(b);
    } 
    null.setPaper(paper);
    return validatePage(null);
  }
  
  public abstract PageFormat validatePage(PageFormat paramPageFormat) throws HeadlessException;
  
  public abstract void print();
  
  public void print(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrinterException { print(); }
  
  public abstract void setCopies(int paramInt);
  
  public abstract int getCopies();
  
  public abstract String getUserName();
  
  public abstract void setJobName(String paramString);
  
  public abstract String getJobName();
  
  public abstract void cancel();
  
  public abstract boolean isCancelled() throws HeadlessException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\print\PrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */