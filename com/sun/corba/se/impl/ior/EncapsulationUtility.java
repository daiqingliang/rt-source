package com.sun.corba.se.impl.ior;

import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.spi.ior.Identifiable;
import com.sun.corba.se.spi.ior.IdentifiableFactoryFinder;
import com.sun.corba.se.spi.ior.WriteContents;
import com.sun.corba.se.spi.orb.ORB;
import java.util.List;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public class EncapsulationUtility {
  public static void readIdentifiableSequence(List paramList, IdentifiableFactoryFinder paramIdentifiableFactoryFinder, InputStream paramInputStream) {
    int i = paramInputStream.read_long();
    for (byte b = 0; b < i; b++) {
      int j = paramInputStream.read_long();
      Identifiable identifiable = paramIdentifiableFactoryFinder.create(j, paramInputStream);
      paramList.add(identifiable);
    } 
  }
  
  public static void writeIdentifiableSequence(List paramList, OutputStream paramOutputStream) {
    paramOutputStream.write_long(paramList.size());
    for (Identifiable identifiable : paramList) {
      paramOutputStream.write_long(identifiable.getId());
      identifiable.write(paramOutputStream);
    } 
  }
  
  public static void writeOutputStream(OutputStream paramOutputStream1, OutputStream paramOutputStream2) {
    byte[] arrayOfByte = ((CDROutputStream)paramOutputStream1).toByteArray();
    paramOutputStream2.write_long(arrayOfByte.length);
    paramOutputStream2.write_octet_array(arrayOfByte, 0, arrayOfByte.length);
  }
  
  public static InputStream getEncapsulationStream(InputStream paramInputStream) {
    byte[] arrayOfByte = readOctets(paramInputStream);
    EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(paramInputStream.orb(), arrayOfByte, arrayOfByte.length);
    encapsInputStream.consumeEndian();
    return encapsInputStream;
  }
  
  public static byte[] readOctets(InputStream paramInputStream) {
    int i = paramInputStream.read_ulong();
    byte[] arrayOfByte = new byte[i];
    paramInputStream.read_octet_array(arrayOfByte, 0, i);
    return arrayOfByte;
  }
  
  public static void writeEncapsulation(WriteContents paramWriteContents, OutputStream paramOutputStream) {
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramOutputStream.orb());
    encapsOutputStream.putEndian();
    paramWriteContents.writeContents(encapsOutputStream);
    writeOutputStream(encapsOutputStream, paramOutputStream);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\EncapsulationUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */