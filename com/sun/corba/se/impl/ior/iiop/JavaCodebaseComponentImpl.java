package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.JavaCodebaseComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class JavaCodebaseComponentImpl extends TaggedComponentBase implements JavaCodebaseComponent {
  private String URLs;
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof JavaCodebaseComponentImpl))
      return false; 
    JavaCodebaseComponentImpl javaCodebaseComponentImpl = (JavaCodebaseComponentImpl)paramObject;
    return this.URLs.equals(javaCodebaseComponentImpl.getURLs());
  }
  
  public int hashCode() { return this.URLs.hashCode(); }
  
  public String toString() { return "JavaCodebaseComponentImpl[URLs=" + this.URLs + "]"; }
  
  public String getURLs() { return this.URLs; }
  
  public JavaCodebaseComponentImpl(String paramString) { this.URLs = paramString; }
  
  public void writeContents(OutputStream paramOutputStream) { paramOutputStream.write_string(this.URLs); }
  
  public int getId() { return 25; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\JavaCodebaseComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */