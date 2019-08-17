package com.sun.xml.internal.bind.v2.bytecode;

import com.sun.xml.internal.bind.Util;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ClassTailor {
  private static final Logger logger = Util.getClassLogger();
  
  public static String toVMClassName(Class paramClass) {
    assert !paramClass.isPrimitive();
    return paramClass.isArray() ? toVMTypeName(paramClass) : paramClass.getName().replace('.', '/');
  }
  
  public static String toVMTypeName(Class paramClass) {
    if (paramClass.isArray())
      return '[' + toVMTypeName(paramClass.getComponentType()); 
    if (paramClass.isPrimitive()) {
      if (paramClass == boolean.class)
        return "Z"; 
      if (paramClass == char.class)
        return "C"; 
      if (paramClass == byte.class)
        return "B"; 
      if (paramClass == double.class)
        return "D"; 
      if (paramClass == float.class)
        return "F"; 
      if (paramClass == int.class)
        return "I"; 
      if (paramClass == long.class)
        return "J"; 
      if (paramClass == short.class)
        return "S"; 
      throw new IllegalArgumentException(paramClass.getName());
    } 
    return 'L' + paramClass.getName().replace('.', '/') + ';';
  }
  
  public static byte[] tailor(Class paramClass, String paramString, String... paramVarArgs) {
    String str = toVMClassName(paramClass);
    return tailor(SecureLoader.getClassClassLoader(paramClass).getResourceAsStream(str + ".class"), str, paramString, paramVarArgs);
  }
  
  public static byte[] tailor(InputStream paramInputStream, String paramString1, String paramString2, String... paramVarArgs) {
    DataInputStream dataInputStream = new DataInputStream(paramInputStream);
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      long l = dataInputStream.readLong();
      dataOutputStream.writeLong(l);
      short s = dataInputStream.readShort();
      dataOutputStream.writeShort(s);
      for (byte b = 0; b < s; b++) {
        String str;
        byte b1 = dataInputStream.readByte();
        dataOutputStream.writeByte(b1);
        switch (b1) {
          case 0:
            break;
          case 1:
            str = dataInputStream.readUTF();
            if (str.equals(paramString1)) {
              str = paramString2;
            } else {
              for (boolean bool = false; bool < paramVarArgs.length; bool += true) {
                if (str.equals(paramVarArgs[bool])) {
                  str = paramVarArgs[bool + true];
                  break;
                } 
              } 
            } 
            dataOutputStream.writeUTF(str);
            break;
          case 3:
          case 4:
            dataOutputStream.writeInt(dataInputStream.readInt());
            break;
          case 5:
          case 6:
            b++;
            dataOutputStream.writeLong(dataInputStream.readLong());
            break;
          case 7:
          case 8:
            dataOutputStream.writeShort(dataInputStream.readShort());
            break;
          case 9:
          case 10:
          case 11:
          case 12:
            dataOutputStream.writeInt(dataInputStream.readInt());
            break;
          default:
            throw new IllegalArgumentException("Unknown constant type " + b1);
        } 
      } 
      byte[] arrayOfByte = new byte[512];
      int i;
      while ((i = dataInputStream.read(arrayOfByte)) > 0)
        dataOutputStream.write(arrayOfByte, 0, i); 
      dataInputStream.close();
      dataOutputStream.close();
      return byteArrayOutputStream.toByteArray();
    } catch (IOException iOException) {
      logger.log(Level.WARNING, "failed to tailor", iOException);
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\bytecode\ClassTailor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */