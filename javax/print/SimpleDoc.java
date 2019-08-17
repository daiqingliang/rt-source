package javax.print;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import sun.reflect.misc.ReflectUtil;

public final class SimpleDoc implements Doc {
  private DocFlavor flavor;
  
  private DocAttributeSet attributes;
  
  private Object printData;
  
  private Reader reader;
  
  private InputStream inStream;
  
  public SimpleDoc(Object paramObject, DocFlavor paramDocFlavor, DocAttributeSet paramDocAttributeSet) {
    if (paramDocFlavor == null || paramObject == null)
      throw new IllegalArgumentException("null argument(s)"); 
    Class clazz = null;
    try {
      String str = paramDocFlavor.getRepresentationClassName();
      ReflectUtil.checkPackageAccess(str);
      clazz = Class.forName(str, false, Thread.currentThread().getContextClassLoader());
    } catch (Throwable throwable) {
      throw new IllegalArgumentException("unknown representation class");
    } 
    if (!clazz.isInstance(paramObject))
      throw new IllegalArgumentException("data is not of declared type"); 
    this.flavor = paramDocFlavor;
    if (paramDocAttributeSet != null)
      this.attributes = AttributeSetUtilities.unmodifiableView(paramDocAttributeSet); 
    this.printData = paramObject;
  }
  
  public DocFlavor getDocFlavor() { return this.flavor; }
  
  public DocAttributeSet getAttributes() { return this.attributes; }
  
  public Object getPrintData() throws IOException { return this.printData; }
  
  public Reader getReaderForText() throws IOException {
    if (this.printData instanceof Reader)
      return (Reader)this.printData; 
    synchronized (this) {
      if (this.reader != null)
        return this.reader; 
      if (this.printData instanceof char[]) {
        this.reader = new CharArrayReader((char[])this.printData);
      } else if (this.printData instanceof String) {
        this.reader = new StringReader((String)this.printData);
      } 
    } 
    return this.reader;
  }
  
  public InputStream getStreamForBytes() throws IOException {
    if (this.printData instanceof InputStream)
      return (InputStream)this.printData; 
    synchronized (this) {
      if (this.inStream != null)
        return this.inStream; 
      if (this.printData instanceof byte[])
        this.inStream = new ByteArrayInputStream((byte[])this.printData); 
    } 
    return this.inStream;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\SimpleDoc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */