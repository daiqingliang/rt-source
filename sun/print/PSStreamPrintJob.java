package sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
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
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

public class PSStreamPrintJob implements CancelablePrintJob {
  private Vector jobListeners;
  
  private Vector attrListeners;
  
  private Vector listenedAttributeSets;
  
  private PSStreamPrintService service;
  
  private boolean fidelity;
  
  private boolean printing = false;
  
  private boolean printReturned = false;
  
  private PrintRequestAttributeSet reqAttrSet = null;
  
  private PrintJobAttributeSet jobAttrSet = null;
  
  private PrinterJob job;
  
  private Doc doc;
  
  private InputStream instream = null;
  
  private Reader reader = null;
  
  private String jobName = "Java Printing";
  
  private int copies = 1;
  
  private MediaSize mediaSize = MediaSize.NA.LETTER;
  
  private OrientationRequested orient = OrientationRequested.PORTRAIT;
  
  PSStreamPrintJob(PSStreamPrintService paramPSStreamPrintService) { this.service = paramPSStreamPrintService; }
  
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
    synchronized (this) {
      if (this.jobListeners != null) {
        PrintJobEvent printJobEvent = new PrintJobEvent(this, paramInt);
        for (byte b = 0; b < this.jobListeners.size(); b++) {
          PrintJobListener printJobListener = (PrintJobListener)this.jobListeners.elementAt(b);
          switch (paramInt) {
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
            case 102:
              printJobListener.printJobCompleted(printJobEvent);
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
    this.doc = paramDoc;
    DocFlavor docFlavor = paramDoc.getDocFlavor();
    try {
      object = paramDoc.getPrintData();
    } catch (IOException iOException) {
      notifyEvent(103);
      throw new PrintException("can't get print data: " + iOException.toString());
    } 
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
        printableJob(new ImagePrinter(this.instream), this.reqAttrSet);
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
        printableJob(new ImagePrinter((URL)object), this.reqAttrSet);
        return;
      } catch (ClassCastException classCastException) {
        notifyEvent(103);
        throw new PrintException(classCastException);
      }  
    if (str.equals("java.awt.print.Pageable"))
      try {
        pageableJob((Pageable)paramDoc.getPrintData(), this.reqAttrSet);
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
        printableJob((Printable)paramDoc.getPrintData(), this.reqAttrSet);
        return;
      } catch (ClassCastException classCastException) {
        notifyEvent(103);
        throw new PrintException(classCastException);
      } catch (IOException iOException) {
        notifyEvent(103);
        throw new PrintException(iOException);
      }  
    notifyEvent(103);
    throw new PrintException("unrecognized class: " + str);
  }
  
  public void printableJob(Printable paramPrintable, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrintException {
    try {
      synchronized (this) {
        if (this.job != null)
          throw new PrintException("already printing"); 
        this.job = new PSPrinterJob();
      } 
      this.job.setPrintService(getPrintService());
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
      this.job.print(paramPrintRequestAttributeSet);
      notifyEvent(102);
      return;
    } catch (PrinterException printerException) {
      notifyEvent(103);
      throw new PrintException(printerException);
    } finally {
      this.printReturned = true;
    } 
  }
  
  public void pageableJob(Pageable paramPageable, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrintException {
    try {
      synchronized (this) {
        if (this.job != null)
          throw new PrintException("already printing"); 
        this.job = new PSPrinterJob();
      } 
      this.job.setPrintService(getPrintService());
      this.job.setPageable(paramPageable);
      this.job.print(paramPrintRequestAttributeSet);
      notifyEvent(102);
      return;
    } catch (PrinterException printerException) {
      notifyEvent(103);
      throw new PrintException(printerException);
    } finally {
      this.printReturned = true;
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
      if (clazz == JobName.class) {
        this.jobName = ((JobName)attribute).getValue();
      } else if (clazz == Copies.class) {
        this.copies = ((Copies)attribute).getValue();
      } else if (clazz == javax.print.attribute.standard.Media.class) {
        if (attribute instanceof MediaSizeName && this.service.isAttributeValueSupported(attribute, null, null))
          this.mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)attribute); 
      } else if (clazz == OrientationRequested.class) {
        this.orient = (OrientationRequested)attribute;
      } 
    } 
  }
  
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PSStreamPrintJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */