package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.CodeSetsComponent;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CodeSetsComponentImpl extends TaggedComponentBase implements CodeSetsComponent {
  CodeSetComponentInfo csci;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof CodeSetsComponentImpl))
      return false; 
    CodeSetsComponentImpl codeSetsComponentImpl = (CodeSetsComponentImpl)paramObject;
    return this.csci.equals(codeSetsComponentImpl.csci);
  }
  
  public int hashCode() { return this.csci.hashCode(); }
  
  public String toString() { return "CodeSetsComponentImpl[csci=" + this.csci + "]"; }
  
  public CodeSetsComponentImpl() { this.csci = new CodeSetComponentInfo(); }
  
  public CodeSetsComponentImpl(InputStream paramInputStream) {
    this.csci = new CodeSetComponentInfo();
    this.csci.read((MarshalInputStream)paramInputStream);
  }
  
  public CodeSetsComponentImpl(ORB paramORB) {
    if (paramORB == null) {
      this.csci = new CodeSetComponentInfo();
    } else {
      this.csci = paramORB.getORBData().getCodeSetComponentInfo();
    } 
  }
  
  public CodeSetComponentInfo getCodeSetComponentInfo() { return this.csci; }
  
  public void writeContents(OutputStream paramOutputStream) { this.csci.write((MarshalOutputStream)paramOutputStream); }
  
  public int getId() { return 1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\CodeSetsComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */