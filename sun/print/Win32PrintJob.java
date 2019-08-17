package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.Vector;
import javax.print.CancelablePrintJob;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashPrintJobAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import sun.awt.windows.WPrinterJob;

public class Win32PrintJob implements CancelablePrintJob {
  private Vector jobListeners;
  
  private Vector attrListeners;
  
  private Vector listenedAttributeSets;
  
  private Win32PrintService service;
  
  private boolean fidelity;
  
  private boolean printing = false;
  
  private boolean printReturned = false;
  
  private PrintRequestAttributeSet reqAttrSet = null;
  
  private PrintJobAttributeSet jobAttrSet = null;
  
  private PrinterJob job;
  
  private Doc doc;
  
  private String mDestination = null;
  
  private InputStream instream = null;
  
  private Reader reader = null;
  
  private String jobName = "Java Printing";
  
  private int copies = 0;
  
  private MediaSizeName mediaName = null;
  
  private MediaSize mediaSize = null;
  
  private OrientationRequested orient = null;
  
  private long hPrintJob;
  
  private static final int PRINTBUFFERLEN = 8192;
  
  Win32PrintJob(Win32PrintService paramWin32PrintService) { this.service = paramWin32PrintService; }
  
  public PrintService getPrintService() { return this.service; }
  
  public PrintJobAttributeSet getAttributes() {
    synchronized (this) {
      if (this.jobAttrSet == null) {
        HashPrintJobAttributeSet hashPrintJobAttributeSet = new HashPrintJobAttributeSet();
        return AttributeSetUtilities.unmodifiableView(hashPrintJobAttributeSet);
      } 
      return this.jobAttrSet;
    } 
  }
  
  public void addPrintJobListener(PrintJobListener paramPrintJobListener) {
    synchronized (this) {
      if (paramPrintJobListener == null)
        return; 
      if (this.jobListeners == null)
        this.jobListeners = new Vector(); 
      this.jobListeners.add(paramPrintJobListener);
    } 
  }
  
  public void removePrintJobListener(PrintJobListener paramPrintJobListener) {
    synchronized (this) {
      if (paramPrintJobListener == null || this.jobListeners == null)
        return; 
      this.jobListeners.remove(paramPrintJobListener);
      if (this.jobListeners.isEmpty())
        this.jobListeners = null; 
    } 
  }
  
  private void closeDataStreams() {
    if (this.doc == null)
      return; 
    Object object = null;
    try {
      object = this.doc.getPrintData();
    } catch (IOException iOException) {
      return;
    } 
    if (this.instream != null) {
      try {
        this.instream.close();
      } catch (IOException iOException) {
      
      } finally {
        this.instream = null;
      } 
    } else if (this.reader != null) {
      try {
        this.reader.close();
      } catch (IOException iOException) {
      
      } finally {
        this.reader = null;
      } 
    } else if (object instanceof InputStream) {
      try {
        ((InputStream)object).close();
      } catch (IOException iOException) {}
    } else if (object instanceof Reader) {
      try {
        ((Reader)object).close();
      } catch (IOException iOException) {}
    } 
  }
  
  private void notifyEvent(int paramInt) {
    switch (paramInt) {
      case 101:
      case 102:
      case 103:
      case 105:
      case 106:
        closeDataStreams();
        break;
    } 
    synchronized (this) {
      if (this.jobListeners != null) {
        PrintJobEvent printJobEvent = new PrintJobEvent(this, paramInt);
        for (byte b = 0; b < this.jobListeners.size(); b++) {
          PrintJobListener printJobListener = (PrintJobListener)this.jobListeners.elementAt(b);
          switch (paramInt) {
            case 102:
              printJobListener.printJobCompleted(printJobEvent);
              break;
            case 101:
              printJobListener.printJobCanceled(printJobEvent);
              break;
            case 103:
              printJobListener.printJobFailed(printJobEvent);
              break;
            case 106:
              printJobListener.printDataTransferCompleted(printJobEvent);
              break;
            case 105:
              printJobListener.printJobNoMoreEvents(printJobEvent);
              break;
          } 
        } 
      } 
    } 
  }
  
  public void addPrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener, PrintJobAttributeSet paramPrintJobAttributeSet) {
    synchronized (this) {
      if (paramPrintJobAttributeListener == null)
        return; 
      if (this.attrListeners == null) {
        this.attrListeners = new Vector();
        this.listenedAttributeSets = new Vector();
      } 
      this.attrListeners.add(paramPrintJobAttributeListener);
      if (paramPrintJobAttributeSet == null)
        paramPrintJobAttributeSet = new HashPrintJobAttributeSet(); 
      this.listenedAttributeSets.add(paramPrintJobAttributeSet);
    } 
  }
  
  public void removePrintJobAttributeListener(PrintJobAttributeListener paramPrintJobAttributeListener) {
    synchronized (this) {
      if (paramPrintJobAttributeListener == null || this.attrListeners == null)
        return; 
      int i = this.attrListeners.indexOf(paramPrintJobAttributeListener);
      if (i == -1)
        return; 
      this.attrListeners.remove(i);
      this.listenedAttributeSets.remove(i);
      if (this.attrListeners.isEmpty()) {
        this.attrListeners = null;
        this.listenedAttributeSets = null;
      } 
    } 
  }
  
  public void print(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrintException {
    Object object;
    synchronized (this) {
      if (this.printing)
        throw new PrintException("already printing"); 
      this.printing = true;
    } 
    PrinterState printerState = (PrinterState)this.service.getAttribute(PrinterState.class);
    if (printerState == PrinterState.STOPPED) {
      PrinterStateReasons printerStateReasons = (PrinterStateReasons)this.service.getAttribute(PrinterStateReasons.class);
      if (printerStateReasons != null && printerStateReasons.containsKey(PrinterStateReason.SHUTDOWN))
        throw new PrintException("PrintService is no longer available."); 
    } 
    if ((PrinterIsAcceptingJobs)this.service.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS)
      throw new PrintException("Printer is not accepting job."); 
    this.doc = paramDoc;
    DocFlavor docFlavor = paramDoc.getDocFlavor();
    try {
      object = paramDoc.getPrintData();
    } catch (IOException iOException) {
      notifyEvent(103);
      throw new PrintException("can't get print data: " + iOException.toString());
    } 
    if (object == null)
      throw new PrintException("Null print data."); 
    if (docFlavor == null || !this.service.isDocFlavorSupported(docFlavor)) {
      notifyEvent(103);
      throw new PrintJobFlavorException("invalid flavor", docFlavor);
    } 
    initializeAttributeSets(paramDoc, paramPrintRequestAttributeSet);
    getAttributeValues(docFlavor);
    String str = docFlavor.getRepresentationClassName();
    if (docFlavor.equals(DocFlavor.INPUT_STREAM.GIF) || docFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) || docFlavor.equals(DocFlavor.INPUT_STREAM.PNG) || docFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) || docFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) || docFlavor.equals(DocFlavor.BYTE_ARRAY.PNG))
      try {
        this.instream = paramDoc.getStreamForBytes();
        if (this.instream == null) {
          notifyEvent(103);
          throw new PrintException("No stream for data");
        } 
        printableJob(new ImagePrinter(this.instream));
        this.service.wakeNotifier();
        return;
      } catch (ClassCastException classCastException) {
        notifyEvent(103);
        throw new PrintException(classCastException);
      } catch (IOException iOException) {
        notifyEvent(103);
        throw new PrintException(iOException);
      }  
    if (docFlavor.equals(DocFlavor.URL.GIF) || docFlavor.equals(DocFlavor.URL.JPEG) || docFlavor.equals(DocFlavor.URL.PNG))
      try {
        printableJob(new ImagePrinter((URL)object));
        this.service.wakeNotifier();
        return;
      } catch (ClassCastException classCastException) {
        notifyEvent(103);
        throw new PrintException(classCastException);
      }  
    if (str.equals("java.awt.print.Pageable"))
      try {
        pageableJob((Pageable)paramDoc.getPrintData());
        this.service.wakeNotifier();
        return;
      } catch (ClassCastException classCastException) {
        notifyEvent(103);
        throw new PrintException(classCastException);
      } catch (IOException iOException) {
        notifyEvent(103);
        throw new PrintException(iOException);
      }  
    if (str.equals("java.awt.print.Printable"))
      try {
        printableJob((Printable)paramDoc.getPrintData());
        this.service.wakeNotifier();
        return;
      } catch (ClassCastException classCastException) {
        notifyEvent(103);
        throw new PrintException(classCastException);
      } catch (IOException iOException) {
        notifyEvent(103);
        throw new PrintException(iOException);
      }  
    if (str.equals("[B") || str.equals("java.io.InputStream") || str.equals("java.net.URL")) {
      if (str.equals("java.net.URL")) {
        URL uRL = (URL)object;
        try {
          this.instream = uRL.openStream();
        } catch (IOException iOException) {
          notifyEvent(103);
          throw new PrintException(iOException.toString());
        } 
      } else {
        try {
          this.instream = paramDoc.getStreamForBytes();
        } catch (IOException iOException) {
          notifyEvent(103);
          throw new PrintException(iOException.toString());
        } 
      } 
      if (this.instream == null) {
        notifyEvent(103);
        throw new PrintException("No stream for data");
      } 
      if (this.mDestination != null) {
        try {
          FileOutputStream fileOutputStream = new FileOutputStream(this.mDestination);
          byte[] arrayOfByte = new byte[1024];
          int j;
          while ((j = this.instream.read(arrayOfByte, 0, arrayOfByte.length)) >= 0)
            fileOutputStream.write(arrayOfByte, 0, j); 
          fileOutputStream.flush();
          fileOutputStream.close();
        } catch (FileNotFoundException fileNotFoundException) {
          notifyEvent(103);
          throw new PrintException(fileNotFoundException.toString());
        } catch (IOException iOException) {
          notifyEvent(103);
          throw new PrintException(iOException.toString());
        } 
        notifyEvent(106);
        notifyEvent(102);
        this.service.wakeNotifier();
        return;
      } 
      if (!startPrintRawData(this.service.getName(), this.jobName)) {
        notifyEvent(103);
        throw new PrintException("Print job failed to start.");
      } 
      BufferedInputStream bufferedInputStream = new BufferedInputStream(this.instream);
      int i = 0;
      try {
        byte[] arrayOfByte = new byte[8192];
        while ((i = bufferedInputStream.read(arrayOfByte, 0, 8192)) >= 0) {
          if (!printRawData(arrayOfByte, i)) {
            bufferedInputStream.close();
            notifyEvent(103);
            throw new PrintException("Problem while spooling data");
          } 
        } 
        bufferedInputStream.close();
        if (!endPrintRawData()) {
          notifyEvent(103);
          throw new PrintException("Print job failed to close properly.");
        } 
        notifyEvent(106);
      } catch (IOException iOException) {
        notifyEvent(103);
        throw new PrintException(iOException.toString());
      } finally {
        notifyEvent(105);
      } 
    } else {
      notifyEvent(103);
      throw new PrintException("unrecognized class: " + str);
    } 
    this.service.wakeNotifier();
  }
  
  public void printableJob(Printable paramPrintable) throws PrintException {
    try {
      synchronized (this) {
        if (this.job != null)
          throw new PrintException("already printing"); 
        this.job = new WPrinterJob();
      } 
      PrintService printService = getPrintService();
      this.job.setPrintService(printService);
      if (this.copies == 0) {
        Copies copies1 = (Copies)printService.getDefaultAttributeValue(Copies.class);
        this.copies = copies1.getValue();
      } 
      if (this.mediaName == null) {
        Object object = printService.getDefaultAttributeValue(javax.print.attribute.standard.Media.class);
        if (object instanceof MediaSizeName) {
          this.mediaName = (MediaSizeName)object;
          this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
        } 
      } 
      if (this.orient == null)
        this.orient = (OrientationRequested)printService.getDefaultAttributeValue(OrientationRequested.class); 
      this.job.setCopies(this.copies);
      this.job.setJobName(this.jobName);
      PageFormat pageFormat = new PageFormat();
      if (this.mediaSize != null) {
        Paper paper = new Paper();
        paper.setSize(this.mediaSize.getX(25400) * 72.0D, this.mediaSize.getY(25400) * 72.0D);
        paper.setImageableArea(72.0D, 72.0D, paper.getWidth() - 144.0D, paper.getHeight() - 144.0D);
        pageFormat.setPaper(paper);
      } 
      if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
        pageFormat.setOrientation(2);
      } else if (this.orient == OrientationRequested.LANDSCAPE) {
        pageFormat.setOrientation(0);
      } 
      this.job.setPrintable(paramPrintable, pageFormat);
      this.job.print(this.reqAttrSet);
      notifyEvent(106);
      return;
    } catch (PrinterException printerException) {
      notifyEvent(103);
      throw new PrintException(printerException);
    } finally {
      this.printReturned = true;
      notifyEvent(105);
    } 
  }
  
  public void pageableJob(Pageable paramPageable) throws PrintException {
    try {
      synchronized (this) {
        if (this.job != null)
          throw new PrintException("already printing"); 
        this.job = new WPrinterJob();
      } 
      PrintService printService = getPrintService();
      this.job.setPrintService(printService);
      if (this.copies == 0) {
        Copies copies1 = (Copies)printService.getDefaultAttributeValue(Copies.class);
        this.copies = copies1.getValue();
      } 
      this.job.setCopies(this.copies);
      this.job.setJobName(this.jobName);
      this.job.setPageable(paramPageable);
      this.job.print(this.reqAttrSet);
      notifyEvent(106);
      return;
    } catch (PrinterException printerException) {
      notifyEvent(103);
      throw new PrintException(printerException);
    } finally {
      this.printReturned = true;
      notifyEvent(105);
    } 
  }
  
  private void initializeAttributeSets(Doc paramDoc, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrintException {
    this.reqAttrSet = new HashPrintRequestAttributeSet();
    this.jobAttrSet = new HashPrintJobAttributeSet();
    if (paramPrintRequestAttributeSet != null) {
      this.reqAttrSet.addAll(paramPrintRequestAttributeSet);
      Attribute[] arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
      for (byte b = 0; b < arrayOfAttribute.length; b++) {
        if (arrayOfAttribute[b] instanceof javax.print.attribute.PrintJobAttribute)
          this.jobAttrSet.add(arrayOfAttribute[b]); 
      } 
    } 
    DocAttributeSet docAttributeSet = paramDoc.getAttributes();
    if (docAttributeSet != null) {
      Attribute[] arrayOfAttribute = docAttributeSet.toArray();
      for (byte b = 0; b < arrayOfAttribute.length; b++) {
        if (arrayOfAttribute[b] instanceof javax.print.attribute.PrintRequestAttribute)
          this.reqAttrSet.add(arrayOfAttribute[b]); 
        if (arrayOfAttribute[b] instanceof javax.print.attribute.PrintJobAttribute)
          this.jobAttrSet.add(arrayOfAttribute[b]); 
      } 
    } 
    String str = "";
    try {
      str = System.getProperty("user.name");
    } catch (SecurityException securityException) {}
    if (str == null || str.equals("")) {
      RequestingUserName requestingUserName = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
      if (requestingUserName != null) {
        this.jobAttrSet.add(new JobOriginatingUserName(requestingUserName.getValue(), requestingUserName.getLocale()));
      } else {
        this.jobAttrSet.add(new JobOriginatingUserName("", null));
      } 
    } else {
      this.jobAttrSet.add(new JobOriginatingUserName(str, null));
    } 
    if (this.jobAttrSet.get(JobName.class) == null)
      if (docAttributeSet != null && docAttributeSet.get(DocumentName.class) != null) {
        DocumentName documentName = (DocumentName)docAttributeSet.get(DocumentName.class);
        JobName jobName1 = new JobName(documentName.getValue(), documentName.getLocale());
        this.jobAttrSet.add(jobName1);
      } else {
        String str1 = "JPS Job:" + paramDoc;
        try {
          Object object = paramDoc.getPrintData();
          if (object instanceof URL)
            str1 = ((URL)paramDoc.getPrintData()).toString(); 
        } catch (IOException iOException) {}
        JobName jobName1 = new JobName(str1, null);
        this.jobAttrSet.add(jobName1);
      }  
    this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
  }
  
  private void getAttributeValues(DocFlavor paramDocFlavor) throws PrintException {
    if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
      this.fidelity = true;
    } else {
      this.fidelity = false;
    } 
    Attribute[] arrayOfAttribute = this.reqAttrSet.toArray();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      Attribute attribute = arrayOfAttribute[b];
      Class clazz = attribute.getCategory();
      if (this.fidelity == true) {
        if (!this.service.isAttributeCategorySupported(clazz)) {
          notifyEvent(103);
          throw new PrintJobAttributeException("unsupported category: " + clazz, clazz, null);
        } 
        if (!this.service.isAttributeValueSupported(attribute, paramDocFlavor, null)) {
          notifyEvent(103);
          throw new PrintJobAttributeException("unsupported attribute: " + attribute, null, attribute);
        } 
      } 
      if (clazz == Destination.class) {
        URI uRI = ((Destination)attribute).getURI();
        if (!"file".equals(uRI.getScheme())) {
          notifyEvent(103);
          throw new PrintException("Not a file: URI");
        } 
        try {
          this.mDestination = (new File(uRI)).getPath();
        } catch (Exception exception) {
          throw new PrintException(exception);
        } 
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null)
          try {
            securityManager.checkWrite(this.mDestination);
          } catch (SecurityException securityException) {
            notifyEvent(103);
            throw new PrintException(securityException);
          }  
      } else if (clazz == JobName.class) {
        this.jobName = ((JobName)attribute).getValue();
      } else if (clazz == Copies.class) {
        this.copies = ((Copies)attribute).getValue();
      } else if (clazz == javax.print.attribute.standard.Media.class) {
        if (attribute instanceof MediaSizeName) {
          this.mediaName = (MediaSizeName)attribute;
          if (!this.service.isAttributeValueSupported(attribute, null, null))
            this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName); 
        } 
      } else if (clazz == OrientationRequested.class) {
        this.orient = (OrientationRequested)attribute;
      } 
    } 
  }
  
  private native boolean startPrintRawData(String paramString1, String paramString2);
  
  private native boolean printRawData(byte[] paramArrayOfByte, int paramInt);
  
  private native boolean endPrintRawData();
  
  public void cancel() {
    synchronized (this) {
      if (!this.printing)
        throw new PrintException("Job is not yet submitted."); 
      if (this.job != null && !this.printReturned) {
        this.job.cancel();
        notifyEvent(101);
        return;
      } 
      throw new PrintException("Job could not be cancelled.");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\Win32PrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */