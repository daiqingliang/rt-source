package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Hashtable;

class LegacyHookGetFields extends ObjectInputStream.GetField {
  private Hashtable fields = null;
  
  LegacyHookGetFields(Hashtable paramHashtable) { this.fields = paramHashtable; }
  
  public ObjectStreamClass getObjectStreamClass() { return null; }
  
  public boolean defaulted(String paramString) throws IOException, IllegalArgumentException { return !this.fields.containsKey(paramString); }
  
  public boolean get(String paramString, boolean paramBoolean) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramBoolean : ((Boolean)this.fields.get(paramString)).booleanValue(); }
  
  public char get(String paramString, char paramChar) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramChar : ((Character)this.fields.get(paramString)).charValue(); }
  
  public byte get(String paramString, byte paramByte) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramByte : ((Byte)this.fields.get(paramString)).byteValue(); }
  
  public short get(String paramString, short paramShort) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramShort : ((Short)this.fields.get(paramString)).shortValue(); }
  
  public int get(String paramString, int paramInt) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramInt : ((Integer)this.fields.get(paramString)).intValue(); }
  
  public long get(String paramString, long paramLong) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramLong : ((Long)this.fields.get(paramString)).longValue(); }
  
  public float get(String paramString, float paramFloat) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramFloat : ((Float)this.fields.get(paramString)).floatValue(); }
  
  public double get(String paramString, double paramDouble) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramDouble : ((Double)this.fields.get(paramString)).doubleValue(); }
  
  public Object get(String paramString, Object paramObject) throws IOException, IllegalArgumentException { return defaulted(paramString) ? paramObject : this.fields.get(paramString); }
  
  public String toString() { return this.fields.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\LegacyHookGetFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */