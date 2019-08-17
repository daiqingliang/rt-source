package com.sun.corba.se.impl.ior;

import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA_2_3.portable.InputStream;

interface Handler {
  ObjectKeyTemplate handle(int paramInt1, int paramInt2, InputStream paramInputStream, OctetSeqHolder paramOctetSeqHolder);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */