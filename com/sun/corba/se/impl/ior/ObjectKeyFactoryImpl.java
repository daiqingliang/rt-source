package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyFactory;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.orb.ORB;
import java.io.IOException;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class ObjectKeyFactoryImpl implements ObjectKeyFactory {
  public static final int MAGIC_BASE = -1347695874;
  
  public static final int JAVAMAGIC_OLD = -1347695874;
  
  public static final int JAVAMAGIC_NEW = -1347695873;
  
  public static final int JAVAMAGIC_NEWER = -1347695872;
  
  public static final int MAX_MAGIC = -1347695872;
  
  public static final byte JDK1_3_1_01_PATCH_LEVEL = 1;
  
  private final ORB orb;
  
  private IORSystemException wrapper;
  
  private Handler fullKey = new Handler() {
      public ObjectKeyTemplate handle(int param1Int1, int param1Int2, InputStream param1InputStream, OctetSeqHolder param1OctetSeqHolder) {
        OldJIDLObjectKeyTemplate oldJIDLObjectKeyTemplate = null;
        if (param1Int2 >= 32 && param1Int2 <= 63) {
          if (param1Int1 >= -1347695872) {
            oldJIDLObjectKeyTemplate = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream, param1OctetSeqHolder);
          } else {
            OldPOAObjectKeyTemplate oldPOAObjectKeyTemplate = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream, param1OctetSeqHolder);
          } 
        } else if (param1Int2 >= 0 && param1Int2 < 32) {
          if (param1Int1 >= -1347695872) {
            JIDLObjectKeyTemplate jIDLObjectKeyTemplate = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream, param1OctetSeqHolder);
          } else {
            oldJIDLObjectKeyTemplate = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream, param1OctetSeqHolder);
          } 
        } 
        return oldJIDLObjectKeyTemplate;
      }
    };
  
  private Handler oktempOnly = new Handler() {
      public ObjectKeyTemplate handle(int param1Int1, int param1Int2, InputStream param1InputStream, OctetSeqHolder param1OctetSeqHolder) {
        OldJIDLObjectKeyTemplate oldJIDLObjectKeyTemplate = null;
        if (param1Int2 >= 32 && param1Int2 <= 63) {
          if (param1Int1 >= -1347695872) {
            oldJIDLObjectKeyTemplate = new POAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream);
          } else {
            OldPOAObjectKeyTemplate oldPOAObjectKeyTemplate = new OldPOAObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream);
          } 
        } else if (param1Int2 >= 0 && param1Int2 < 32) {
          if (param1Int1 >= -1347695872) {
            JIDLObjectKeyTemplate jIDLObjectKeyTemplate = new JIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream);
          } else {
            oldJIDLObjectKeyTemplate = new OldJIDLObjectKeyTemplate(ObjectKeyFactoryImpl.this.orb, param1Int1, param1Int2, param1InputStream);
          } 
        } 
        return oldJIDLObjectKeyTemplate;
      }
    };
  
  public ObjectKeyFactoryImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = IORSystemException.get(paramORB, "oa.ior");
  }
  
  private boolean validMagic(int paramInt) { return (paramInt >= -1347695874 && paramInt <= -1347695872); }
  
  private ObjectKeyTemplate create(InputStream paramInputStream, Handler paramHandler, OctetSeqHolder paramOctetSeqHolder) {
    ObjectKeyTemplate objectKeyTemplate = null;
    try {
      paramInputStream.mark(0);
      int i = paramInputStream.read_long();
      if (validMagic(i)) {
        int j = paramInputStream.read_long();
        objectKeyTemplate = paramHandler.handle(i, j, paramInputStream, paramOctetSeqHolder);
      } 
    } catch (MARSHAL mARSHAL) {}
    if (objectKeyTemplate == null)
      try {
        paramInputStream.reset();
      } catch (IOException iOException) {} 
    return objectKeyTemplate;
  }
  
  public ObjectKey create(byte[] paramArrayOfByte) {
    OctetSeqHolder octetSeqHolder = new OctetSeqHolder();
    EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, paramArrayOfByte, paramArrayOfByte.length);
    ObjectKeyTemplate objectKeyTemplate = create(encapsInputStream, this.fullKey, octetSeqHolder);
    if (objectKeyTemplate == null)
      objectKeyTemplate = new WireObjectKeyTemplate(encapsInputStream, octetSeqHolder); 
    ObjectIdImpl objectIdImpl = new ObjectIdImpl(octetSeqHolder.value);
    return new ObjectKeyImpl(objectKeyTemplate, objectIdImpl);
  }
  
  public ObjectKeyTemplate createTemplate(InputStream paramInputStream) {
    ObjectKeyTemplate objectKeyTemplate = create(paramInputStream, this.oktempOnly, null);
    if (objectKeyTemplate == null)
      objectKeyTemplate = new WireObjectKeyTemplate(this.orb); 
    return objectKeyTemplate;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\ObjectKeyFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */