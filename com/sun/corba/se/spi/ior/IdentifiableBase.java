package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class IdentifiableBase implements Identifiable, WriteContents {
  public final void write(OutputStream paramOutputStream) { EncapsulationUtility.writeEncapsulation(this, paramOutputStream); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */