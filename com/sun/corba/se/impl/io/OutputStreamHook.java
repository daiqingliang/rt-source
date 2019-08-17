package com.sun.corba.se.impl.io;

import java.io.IOException;
import java.io.NotActiveException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.portable.ValueOutputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class OutputStreamHook extends ObjectOutputStream {
  private HookPutFields putFields = null;
  
  protected byte streamFormatVersion = 1;
  
  protected WriteObjectState writeObjectState = NOT_IN_WRITE_OBJECT;
  
  protected static final WriteObjectState NOT_IN_WRITE_OBJECT = new DefaultState();
  
  protected static final WriteObjectState IN_WRITE_OBJECT = new InWriteObjectState();
  
  protected static final WriteObjectState WROTE_DEFAULT_DATA = new WroteDefaultDataState();
  
  protected static final WriteObjectState WROTE_CUSTOM_DATA = new WroteCustomDataState();
  
  abstract void writeField(ObjectStreamField paramObjectStreamField, Object paramObject) throws IOException;
  
  public void defaultWriteObject() throws IOException {
    this.writeObjectState.defaultWriteObject(this);
    defaultWriteObjectDelegate();
  }
  
  public abstract void defaultWriteObjectDelegate() throws IOException;
  
  public ObjectOutputStream.PutField putFields() throws IOException {
    if (this.putFields == null)
      this.putFields = new HookPutFields(null); 
    return this.putFields;
  }
  
  public byte getStreamFormatVersion() { return this.streamFormatVersion; }
  
  abstract ObjectStreamField[] getFieldsNoCopy();
  
  public void writeFields() throws IOException {
    this.writeObjectState.defaultWriteObject(this);
    if (this.putFields != null) {
      this.putFields.write(this);
    } else {
      throw new NotActiveException("no current PutField object");
    } 
  }
  
  abstract OutputStream getOrbStream();
  
  protected abstract void beginOptionalCustomData() throws IOException;
  
  protected void setState(WriteObjectState paramWriteObjectState) { this.writeObjectState = paramWriteObjectState; }
  
  protected static class DefaultState extends WriteObjectState {
    public void enterWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException { param1OutputStreamHook.setState(OutputStreamHook.IN_WRITE_OBJECT); }
  }
  
  private class HookPutFields extends ObjectOutputStream.PutField {
    private Map<String, Object> fields = new HashMap();
    
    private HookPutFields() throws IOException {}
    
    public void put(String param1String, boolean param1Boolean) { this.fields.put(param1String, new Boolean(param1Boolean)); }
    
    public void put(String param1String, char param1Char) { this.fields.put(param1String, new Character(param1Char)); }
    
    public void put(String param1String, byte param1Byte) { this.fields.put(param1String, new Byte(param1Byte)); }
    
    public void put(String param1String, short param1Short) { this.fields.put(param1String, new Short(param1Short)); }
    
    public void put(String param1String, int param1Int) { this.fields.put(param1String, new Integer(param1Int)); }
    
    public void put(String param1String, long param1Long) { this.fields.put(param1String, new Long(param1Long)); }
    
    public void put(String param1String, float param1Float) { this.fields.put(param1String, new Float(param1Float)); }
    
    public void put(String param1String, double param1Double) { this.fields.put(param1String, new Double(param1Double)); }
    
    public void put(String param1String, Object param1Object) { this.fields.put(param1String, param1Object); }
    
    public void write(ObjectOutput param1ObjectOutput) throws IOException {
      OutputStreamHook outputStreamHook = (OutputStreamHook)param1ObjectOutput;
      ObjectStreamField[] arrayOfObjectStreamField = outputStreamHook.getFieldsNoCopy();
      for (byte b = 0; b < arrayOfObjectStreamField.length; b++) {
        Object object = this.fields.get(arrayOfObjectStreamField[b].getName());
        outputStreamHook.writeField(arrayOfObjectStreamField[b], object);
      } 
    }
  }
  
  protected static class InWriteObjectState extends WriteObjectState {
    public void enterWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException { throw new IOException("Internal state failure: Entered writeObject twice"); }
    
    public void exitWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {
      param1OutputStreamHook.getOrbStream().write_boolean(false);
      if (param1OutputStreamHook.getStreamFormatVersion() == 2)
        param1OutputStreamHook.getOrbStream().write_long(0); 
      param1OutputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
    }
    
    public void defaultWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {
      param1OutputStreamHook.getOrbStream().write_boolean(true);
      param1OutputStreamHook.setState(OutputStreamHook.WROTE_DEFAULT_DATA);
    }
    
    public void writeData(OutputStreamHook param1OutputStreamHook) throws IOException {
      param1OutputStreamHook.getOrbStream().write_boolean(false);
      param1OutputStreamHook.beginOptionalCustomData();
      param1OutputStreamHook.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
    }
  }
  
  protected static class WriteObjectState {
    public void enterWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {}
    
    public void exitWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {}
    
    public void defaultWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {}
    
    public void writeData(OutputStreamHook param1OutputStreamHook) throws IOException {}
  }
  
  protected static class WroteCustomDataState extends InWriteObjectState {
    public void exitWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {
      if (param1OutputStreamHook.getStreamFormatVersion() == 2)
        ((ValueOutputStream)param1OutputStreamHook.getOrbStream()).end_value(); 
      param1OutputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
    }
    
    public void defaultWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException { throw new IOException("Cannot call defaultWriteObject/writeFields after writing custom data in RMI-IIOP"); }
    
    public void writeData(OutputStreamHook param1OutputStreamHook) throws IOException {}
  }
  
  protected static class WroteDefaultDataState extends InWriteObjectState {
    public void exitWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException {
      if (param1OutputStreamHook.getStreamFormatVersion() == 2)
        param1OutputStreamHook.getOrbStream().write_long(0); 
      param1OutputStreamHook.setState(OutputStreamHook.NOT_IN_WRITE_OBJECT);
    }
    
    public void defaultWriteObject(OutputStreamHook param1OutputStreamHook) throws IOException { throw new IOException("Called defaultWriteObject/writeFields twice"); }
    
    public void writeData(OutputStreamHook param1OutputStreamHook) throws IOException {
      param1OutputStreamHook.beginOptionalCustomData();
      param1OutputStreamHook.setState(OutputStreamHook.WROTE_CUSTOM_DATA);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\OutputStreamHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */