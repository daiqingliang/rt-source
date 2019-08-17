package javax.swing.plaf.basic;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.swing.plaf.UIResource;
import sun.awt.datatransfer.DataTransferer;

class BasicTransferable implements Transferable, UIResource {
  protected String plainData;
  
  protected String htmlData;
  
  private static DataFlavor[] htmlFlavors;
  
  private static DataFlavor[] stringFlavors;
  
  private static DataFlavor[] plainFlavors;
  
  public BasicTransferable(String paramString1, String paramString2) {
    this.plainData = paramString1;
    this.htmlData = paramString2;
  }
  
  public DataFlavor[] getTransferDataFlavors() {
    DataFlavor[] arrayOfDataFlavor1 = getRicherFlavors();
    int i = (arrayOfDataFlavor1 != null) ? arrayOfDataFlavor1.length : 0;
    int j = isHTMLSupported() ? htmlFlavors.length : 0;
    int k = isPlainSupported() ? plainFlavors.length : 0;
    int m = isPlainSupported() ? stringFlavors.length : 0;
    int n = i + j + k + m;
    DataFlavor[] arrayOfDataFlavor2 = new DataFlavor[n];
    int i1 = 0;
    if (i > 0) {
      System.arraycopy(arrayOfDataFlavor1, 0, arrayOfDataFlavor2, i1, i);
      i1 += i;
    } 
    if (j > 0) {
      System.arraycopy(htmlFlavors, 0, arrayOfDataFlavor2, i1, j);
      i1 += j;
    } 
    if (k > 0) {
      System.arraycopy(plainFlavors, 0, arrayOfDataFlavor2, i1, k);
      i1 += k;
    } 
    if (m > 0) {
      System.arraycopy(stringFlavors, 0, arrayOfDataFlavor2, i1, m);
      i1 += m;
    } 
    return arrayOfDataFlavor2;
  }
  
  public boolean isDataFlavorSupported(DataFlavor paramDataFlavor) {
    DataFlavor[] arrayOfDataFlavor = getTransferDataFlavors();
    for (byte b = 0; b < arrayOfDataFlavor.length; b++) {
      if (arrayOfDataFlavor[b].equals(paramDataFlavor))
        return true; 
    } 
    return false;
  }
  
  public Object getTransferData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException {
    DataFlavor[] arrayOfDataFlavor = getRicherFlavors();
    if (isRicherFlavor(paramDataFlavor))
      return getRicherData(paramDataFlavor); 
    if (isHTMLFlavor(paramDataFlavor)) {
      String str = getHTMLData();
      str = (str == null) ? "" : str;
      if (String.class.equals(paramDataFlavor.getRepresentationClass()))
        return str; 
      if (java.io.Reader.class.equals(paramDataFlavor.getRepresentationClass()))
        return new StringReader(str); 
      if (InputStream.class.equals(paramDataFlavor.getRepresentationClass()))
        return createInputStream(paramDataFlavor, str); 
    } else if (isPlainFlavor(paramDataFlavor)) {
      String str = getPlainData();
      str = (str == null) ? "" : str;
      if (String.class.equals(paramDataFlavor.getRepresentationClass()))
        return str; 
      if (java.io.Reader.class.equals(paramDataFlavor.getRepresentationClass()))
        return new StringReader(str); 
      if (InputStream.class.equals(paramDataFlavor.getRepresentationClass()))
        return createInputStream(paramDataFlavor, str); 
    } else if (isStringFlavor(paramDataFlavor)) {
      null = getPlainData();
      return (null == null) ? "" : null;
    } 
    throw new UnsupportedFlavorException(paramDataFlavor);
  }
  
  private InputStream createInputStream(DataFlavor paramDataFlavor, String paramString) throws IOException, UnsupportedFlavorException {
    String str = DataTransferer.getTextCharset(paramDataFlavor);
    if (str == null)
      throw new UnsupportedFlavorException(paramDataFlavor); 
    return new ByteArrayInputStream(paramString.getBytes(str));
  }
  
  protected boolean isRicherFlavor(DataFlavor paramDataFlavor) {
    DataFlavor[] arrayOfDataFlavor = getRicherFlavors();
    int i = (arrayOfDataFlavor != null) ? arrayOfDataFlavor.length : 0;
    for (byte b = 0; b < i; b++) {
      if (arrayOfDataFlavor[b].equals(paramDataFlavor))
        return true; 
    } 
    return false;
  }
  
  protected DataFlavor[] getRicherFlavors() { return null; }
  
  protected Object getRicherData(DataFlavor paramDataFlavor) throws UnsupportedFlavorException, IOException { return null; }
  
  protected boolean isHTMLFlavor(DataFlavor paramDataFlavor) {
    DataFlavor[] arrayOfDataFlavor = htmlFlavors;
    for (byte b = 0; b < arrayOfDataFlavor.length; b++) {
      if (arrayOfDataFlavor[b].equals(paramDataFlavor))
        return true; 
    } 
    return false;
  }
  
  protected boolean isHTMLSupported() { return (this.htmlData != null); }
  
  protected String getHTMLData() { return this.htmlData; }
  
  protected boolean isPlainFlavor(DataFlavor paramDataFlavor) {
    DataFlavor[] arrayOfDataFlavor = plainFlavors;
    for (byte b = 0; b < arrayOfDataFlavor.length; b++) {
      if (arrayOfDataFlavor[b].equals(paramDataFlavor))
        return true; 
    } 
    return false;
  }
  
  protected boolean isPlainSupported() { return (this.plainData != null); }
  
  protected String getPlainData() { return this.plainData; }
  
  protected boolean isStringFlavor(DataFlavor paramDataFlavor) {
    DataFlavor[] arrayOfDataFlavor = stringFlavors;
    for (byte b = 0; b < arrayOfDataFlavor.length; b++) {
      if (arrayOfDataFlavor[b].equals(paramDataFlavor))
        return true; 
    } 
    return false;
  }
  
  static  {
    try {
      htmlFlavors = new DataFlavor[3];
      htmlFlavors[0] = new DataFlavor("text/html;class=java.lang.String");
      htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader");
      htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");
      plainFlavors = new DataFlavor[3];
      plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String");
      plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
      plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");
      stringFlavors = new DataFlavor[2];
      stringFlavors[0] = new DataFlavor("application/x-java-jvm-local-objectref;class=java.lang.String");
      stringFlavors[1] = DataFlavor.stringFlavor;
    } catch (ClassNotFoundException classNotFoundException) {
      System.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicTransferable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */