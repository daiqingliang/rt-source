package com.sun.corba.se.impl.io;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotActiveException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ValueInputStream;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class InputStreamHook extends ObjectInputStream {
  static final OMGSystemException omgWrapper = OMGSystemException.get("rpc.encoding");
  
  static final UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  
  protected ReadObjectState readObjectState = DEFAULT_STATE;
  
  protected static final ReadObjectState DEFAULT_STATE = new DefaultState();
  
  protected static final ReadObjectState IN_READ_OBJECT_OPT_DATA = new InReadObjectOptionalDataState();
  
  protected static final ReadObjectState IN_READ_OBJECT_NO_MORE_OPT_DATA = new InReadObjectNoMoreOptionalDataState();
  
  protected static final ReadObjectState IN_READ_OBJECT_DEFAULTS_SENT = new InReadObjectDefaultsSentState();
  
  protected static final ReadObjectState NO_READ_OBJECT_DEFAULTS_SENT = new NoReadObjectDefaultsSentState();
  
  protected static final ReadObjectState IN_READ_OBJECT_REMOTE_NOT_CUSTOM_MARSHALED = new InReadObjectRemoteDidNotUseWriteObjectState();
  
  protected static final ReadObjectState IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM = new InReadObjectPastDefaultsRemoteDidNotUseWOState();
  
  public void defaultReadObject() throws IOException {
    this.readObjectState.beginDefaultReadObject(this);
    defaultReadObjectDelegate();
    this.readObjectState.endDefaultReadObject(this);
  }
  
  abstract void defaultReadObjectDelegate() throws IOException;
  
  abstract void readFields(Map paramMap) throws InvalidClassException, StreamCorruptedException, ClassNotFoundException, IOException;
  
  public ObjectInputStream.GetField readFields() throws IOException, ClassNotFoundException, NotActiveException {
    HashMap hashMap = new HashMap();
    readFields(hashMap);
    this.readObjectState.endDefaultReadObject(this);
    return new HookGetFields(hashMap);
  }
  
  protected void setState(ReadObjectState paramReadObjectState) { this.readObjectState = paramReadObjectState; }
  
  protected abstract byte getStreamFormatVersion();
  
  abstract InputStream getOrbStream();
  
  protected void throwOptionalDataIncompatibleException() throws IOException { throw omgWrapper.rmiiiopOptionalDataIncompatible2(); }
  
  protected static class DefaultState extends ReadObjectState {
    public void beginUnmarshalCustomValue(InputStreamHook param1InputStreamHook, boolean param1Boolean1, boolean param1Boolean2) throws IOException {
      if (param1Boolean2) {
        if (param1Boolean1) {
          param1InputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_DEFAULTS_SENT);
        } else {
          try {
            if (param1InputStreamHook.getStreamFormatVersion() == 2)
              ((ValueInputStream)param1InputStreamHook.getOrbStream()).start_value(); 
          } catch (Exception exception) {}
          param1InputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
        } 
      } else if (param1Boolean1) {
        param1InputStreamHook.setState(InputStreamHook.NO_READ_OBJECT_DEFAULTS_SENT);
      } else {
        throw new StreamCorruptedException("No default data sent");
      } 
    }
  }
  
  private class HookGetFields extends ObjectInputStream.GetField {
    private Map fields = null;
    
    HookGetFields(Map param1Map) { this.fields = param1Map; }
    
    public ObjectStreamClass getObjectStreamClass() { return null; }
    
    public boolean defaulted(String param1String) throws IOException, IllegalArgumentException { return !this.fields.containsKey(param1String); }
    
    public boolean get(String param1String, boolean param1Boolean) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Boolean : ((Boolean)this.fields.get(param1String)).booleanValue(); }
    
    public char get(String param1String, char param1Char) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Char : ((Character)this.fields.get(param1String)).charValue(); }
    
    public byte get(String param1String, byte param1Byte) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Byte : ((Byte)this.fields.get(param1String)).byteValue(); }
    
    public short get(String param1String, short param1Short) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Short : ((Short)this.fields.get(param1String)).shortValue(); }
    
    public int get(String param1String, int param1Int) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Int : ((Integer)this.fields.get(param1String)).intValue(); }
    
    public long get(String param1String, long param1Long) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Long : ((Long)this.fields.get(param1String)).longValue(); }
    
    public float get(String param1String, float param1Float) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Float : ((Float)this.fields.get(param1String)).floatValue(); }
    
    public double get(String param1String, double param1Double) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Double : ((Double)this.fields.get(param1String)).doubleValue(); }
    
    public Object get(String param1String, Object param1Object) throws IOException, IllegalArgumentException { return defaulted(param1String) ? param1Object : this.fields.get(param1String); }
    
    public String toString() { return this.fields.toString(); }
  }
  
  protected static class InReadObjectDefaultsSentState extends ReadObjectState {
    public void beginUnmarshalCustomValue(InputStreamHook param1InputStreamHook, boolean param1Boolean1, boolean param1Boolean2) throws IOException { throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue(); }
    
    public void endUnmarshalCustomValue(InputStreamHook param1InputStreamHook) {
      if (param1InputStreamHook.getStreamFormatVersion() == 2) {
        ((ValueInputStream)param1InputStreamHook.getOrbStream()).start_value();
        ((ValueInputStream)param1InputStreamHook.getOrbStream()).end_value();
      } 
      param1InputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
    }
    
    public void endDefaultReadObject(InputStreamHook param1InputStreamHook) {
      if (param1InputStreamHook.getStreamFormatVersion() == 2)
        ((ValueInputStream)param1InputStreamHook.getOrbStream()).start_value(); 
      param1InputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_OPT_DATA);
    }
    
    public void readData(InputStreamHook param1InputStreamHook) {
      ORB oRB = param1InputStreamHook.getOrbStream().orb();
      if (oRB == null || !(oRB instanceof ORB))
        throw new StreamCorruptedException("Default data must be read first"); 
      ORBVersion oRBVersion = ((ORB)oRB).getORBVersion();
      if (ORBVersionFactory.getPEORB().compareTo(oRBVersion) <= 0 || oRBVersion.equals(ORBVersionFactory.getFOREIGN()))
        throw new StreamCorruptedException("Default data must be read first"); 
    }
  }
  
  protected static class InReadObjectNoMoreOptionalDataState extends InReadObjectOptionalDataState {
    public void readData(InputStreamHook param1InputStreamHook) { param1InputStreamHook.throwOptionalDataIncompatibleException(); }
  }
  
  protected static class InReadObjectOptionalDataState extends ReadObjectState {
    public void beginUnmarshalCustomValue(InputStreamHook param1InputStreamHook, boolean param1Boolean1, boolean param1Boolean2) throws IOException { throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue(); }
    
    public void endUnmarshalCustomValue(InputStreamHook param1InputStreamHook) {
      if (param1InputStreamHook.getStreamFormatVersion() == 2)
        ((ValueInputStream)param1InputStreamHook.getOrbStream()).end_value(); 
      param1InputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
    }
    
    public void beginDefaultReadObject(InputStreamHook param1InputStreamHook) { throw new StreamCorruptedException("Default data not sent or already read/passed"); }
  }
  
  protected static class InReadObjectPastDefaultsRemoteDidNotUseWOState extends ReadObjectState {
    public void beginUnmarshalCustomValue(InputStreamHook param1InputStreamHook, boolean param1Boolean1, boolean param1Boolean2) throws IOException { throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue(); }
    
    public void beginDefaultReadObject(InputStreamHook param1InputStreamHook) { throw new StreamCorruptedException("Default data already read"); }
    
    public void readData(InputStreamHook param1InputStreamHook) { param1InputStreamHook.throwOptionalDataIncompatibleException(); }
  }
  
  protected static class InReadObjectRemoteDidNotUseWriteObjectState extends ReadObjectState {
    public void beginUnmarshalCustomValue(InputStreamHook param1InputStreamHook, boolean param1Boolean1, boolean param1Boolean2) throws IOException { throw InputStreamHook.utilWrapper.badBeginUnmarshalCustomValue(); }
    
    public void endDefaultReadObject(InputStreamHook param1InputStreamHook) { param1InputStreamHook.setState(InputStreamHook.IN_READ_OBJECT_PAST_DEFAULTS_REMOTE_NOT_CUSTOM); }
    
    public void readData(InputStreamHook param1InputStreamHook) { param1InputStreamHook.throwOptionalDataIncompatibleException(); }
  }
  
  protected static class NoReadObjectDefaultsSentState extends ReadObjectState {
    public void endUnmarshalCustomValue(InputStreamHook param1InputStreamHook) {
      if (param1InputStreamHook.getStreamFormatVersion() == 2) {
        ((ValueInputStream)param1InputStreamHook.getOrbStream()).start_value();
        ((ValueInputStream)param1InputStreamHook.getOrbStream()).end_value();
      } 
      param1InputStreamHook.setState(InputStreamHook.DEFAULT_STATE);
    }
  }
  
  protected static class ReadObjectState {
    public void beginUnmarshalCustomValue(InputStreamHook param1InputStreamHook, boolean param1Boolean1, boolean param1Boolean2) throws IOException {}
    
    public void endUnmarshalCustomValue(InputStreamHook param1InputStreamHook) {}
    
    public void beginDefaultReadObject(InputStreamHook param1InputStreamHook) {}
    
    public void endDefaultReadObject(InputStreamHook param1InputStreamHook) {}
    
    public void readData(InputStreamHook param1InputStreamHook) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\InputStreamHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */