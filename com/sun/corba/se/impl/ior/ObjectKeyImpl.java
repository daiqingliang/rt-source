package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.ObjectId;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public class ObjectKeyImpl implements ObjectKey {
  private ObjectKeyTemplate oktemp;
  
  private ObjectId id;
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (!(paramObject instanceof ObjectKeyImpl))
      return false; 
    ObjectKeyImpl objectKeyImpl = (ObjectKeyImpl)paramObject;
    return (this.oktemp.equals(objectKeyImpl.oktemp) && this.id.equals(objectKeyImpl.id));
  }
  
  public int hashCode() { return this.oktemp.hashCode() ^ this.id.hashCode(); }
  
  public ObjectKeyTemplate getTemplate() { return this.oktemp; }
  
  public ObjectId getId() { return this.id; }
  
  public ObjectKeyImpl(ObjectKeyTemplate paramObjectKeyTemplate, ObjectId paramObjectId) {
    this.oktemp = paramObjectKeyTemplate;
    this.id = paramObjectId;
  }
  
  public void write(OutputStream paramOutputStream) { this.oktemp.write(this.id, paramOutputStream); }
  
  public byte[] getBytes(ORB paramORB) {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramORB);
    write(encapsOutputStream);
    return encapsOutputStream.toByteArray();
  }
  
  public CorbaServerRequestDispatcher getServerRequestDispatcher(ORB paramORB) { return this.oktemp.getServerRequestDispatcher(paramORB, this.id); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectKeyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */