package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.TypeCodeImpl;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.Any;
import org.omg.CORBA.Context;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA_2_3.portable.InputStream;

public class WrapperInputStream extends InputStream implements TypeCodeReader {
  private CDRInputStream stream;
  
  private Map typeMap = null;
  
  private int startPos = 0;
  
  public WrapperInputStream(CDRInputStream paramCDRInputStream) {
    this.stream = paramCDRInputStream;
    this.startPos = this.stream.getPosition();
  }
  
  public int read() throws IOException { return this.stream.read(); }
  
  public int read(byte[] paramArrayOfByte) throws IOException { return this.stream.read(paramArrayOfByte); }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException { return this.stream.read(paramArrayOfByte, paramInt1, paramInt2); }
  
  public long skip(long paramLong) throws IOException { return this.stream.skip(paramLong); }
  
  public int available() throws IOException { return this.stream.available(); }
  
  public void close() throws IOException { this.stream.close(); }
  
  public void mark(int paramInt) { this.stream.mark(paramInt); }
  
  public void reset() throws IOException { this.stream.reset(); }
  
  public boolean markSupported() { return this.stream.markSupported(); }
  
  public int getPosition() throws IOException { return this.stream.getPosition(); }
  
  public void consumeEndian() throws IOException { this.stream.consumeEndian(); }
  
  public boolean read_boolean() { return this.stream.read_boolean(); }
  
  public char read_char() { return this.stream.read_char(); }
  
  public char read_wchar() { return this.stream.read_wchar(); }
  
  public byte read_octet() { return this.stream.read_octet(); }
  
  public short read_short() { return this.stream.read_short(); }
  
  public short read_ushort() { return this.stream.read_ushort(); }
  
  public int read_long() throws IOException { return this.stream.read_long(); }
  
  public int read_ulong() throws IOException { return this.stream.read_ulong(); }
  
  public long read_longlong() { return this.stream.read_longlong(); }
  
  public long read_ulonglong() { return this.stream.read_ulonglong(); }
  
  public float read_float() { return this.stream.read_float(); }
  
  public double read_double() { return this.stream.read_double(); }
  
  public String read_string() { return this.stream.read_string(); }
  
  public String read_wstring() { return this.stream.read_wstring(); }
  
  public void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) { this.stream.read_boolean_array(paramArrayOfBoolean, paramInt1, paramInt2); }
  
  public void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.stream.read_char_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) { this.stream.read_wchar_array(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { this.stream.read_octet_array(paramArrayOfByte, paramInt1, paramInt2); }
  
  public void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { this.stream.read_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { this.stream.read_ushort_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this.stream.read_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { this.stream.read_ulong_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { this.stream.read_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { this.stream.read_ulonglong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) { this.stream.read_float_array(paramArrayOfFloat, paramInt1, paramInt2); }
  
  public void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) { this.stream.read_double_array(paramArrayOfDouble, paramInt1, paramInt2); }
  
  public Object read_Object() { return this.stream.read_Object(); }
  
  public Serializable read_value() { return this.stream.read_value(); }
  
  public TypeCode read_TypeCode() { return this.stream.read_TypeCode(); }
  
  public Any read_any() { return this.stream.read_any(); }
  
  public Principal read_Principal() { return this.stream.read_Principal(); }
  
  public BigDecimal read_fixed() { return this.stream.read_fixed(); }
  
  public Context read_Context() { return this.stream.read_Context(); }
  
  public ORB orb() { return this.stream.orb(); }
  
  public void addTypeCodeAtPosition(TypeCodeImpl paramTypeCodeImpl, int paramInt) {
    if (this.typeMap == null)
      this.typeMap = new HashMap(16); 
    this.typeMap.put(new Integer(paramInt), paramTypeCodeImpl);
  }
  
  public TypeCodeImpl getTypeCodeAtPosition(int paramInt) { return (this.typeMap == null) ? null : (TypeCodeImpl)this.typeMap.get(new Integer(paramInt)); }
  
  public void setEnclosingInputStream(InputStream paramInputStream) {}
  
  public TypeCodeReader getTopLevelStream() { return this; }
  
  public int getTopLevelPosition() throws IOException { return getPosition() - this.startPos; }
  
  public void performORBVersionSpecificInit() throws IOException { this.stream.performORBVersionSpecificInit(); }
  
  public void resetCodeSetConverters() throws IOException { this.stream.resetCodeSetConverters(); }
  
  public void printTypeMap() throws IOException {
    System.out.println("typeMap = {");
    ArrayList arrayList = new ArrayList(this.typeMap.keySet());
    Collections.sort(arrayList);
    for (Integer integer : arrayList) {
      TypeCodeImpl typeCodeImpl = (TypeCodeImpl)this.typeMap.get(integer);
      System.out.println("  key = " + integer.intValue() + ", value = " + typeCodeImpl.description());
    } 
    System.out.println("}");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\WrapperInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */