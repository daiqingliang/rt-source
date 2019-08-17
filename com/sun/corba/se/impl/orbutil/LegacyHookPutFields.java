package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

class LegacyHookPutFields extends ObjectOutputStream.PutField {
  private Hashtable fields = new Hashtable();
  
  public void put(String paramString, boolean paramBoolean) { this.fields.put(paramString, new Boolean(paramBoolean)); }
  
  public void put(String paramString, char paramChar) { this.fields.put(paramString, new Character(paramChar)); }
  
  public void put(String paramString, byte paramByte) { this.fields.put(paramString, new Byte(paramByte)); }
  
  public void put(String paramString, short paramShort) { this.fields.put(paramString, new Short(paramShort)); }
  
  public void put(String paramString, int paramInt) { this.fields.put(paramString, new Integer(paramInt)); }
  
  public void put(String paramString, long paramLong) { this.fields.put(paramString, new Long(paramLong)); }
  
  public void put(String paramString, float paramFloat) { this.fields.put(paramString, new Float(paramFloat)); }
  
  public void put(String paramString, double paramDouble) { this.fields.put(paramString, new Double(paramDouble)); }
  
  public void put(String paramString, Object paramObject) { this.fields.put(paramString, paramObject); }
  
  public void write(ObjectOutput paramObjectOutput) throws IOException { paramObjectOutput.writeObject(this.fields); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\orbutil\LegacyHookPutFields.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */