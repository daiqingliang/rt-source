package com.sun.media.sound;

import java.util.Vector;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;

final class PortMixer extends AbstractMixer {
  private static final int SRC_UNKNOWN = 1;
  
  private static final int SRC_MICROPHONE = 2;
  
  private static final int SRC_LINE_IN = 3;
  
  private static final int SRC_COMPACT_DISC = 4;
  
  private static final int SRC_MASK = 255;
  
  private static final int DST_UNKNOWN = 256;
  
  private static final int DST_SPEAKER = 512;
  
  private static final int DST_HEADPHONE = 768;
  
  private static final int DST_LINE_OUT = 1024;
  
  private static final int DST_MASK = 65280;
  
  private Port.Info[] portInfos;
  
  private PortMixerPort[] ports;
  
  private long id = 0L;
  
  PortMixer(PortMixerProvider.PortMixerInfo paramPortMixerInfo) {
    super(paramPortMixerInfo, null, null, null);
    int i = 0;
    byte b1 = 0;
    byte b2 = 0;
    try {
      try {
        this.id = nOpen(getMixerIndex());
        if (this.id != 0L) {
          i = nGetPortCount(this.id);
          if (i < 0)
            i = 0; 
        } 
      } catch (Exception exception) {}
      this.portInfos = new Port.Info[i];
      for (byte b = 0; b < i; b++) {
        int j = nGetPortType(this.id, b);
        b1 += (((j & 0xFF) != 0) ? 1 : 0);
        b2 += (((j & 0xFF00) != 0) ? 1 : 0);
        this.portInfos[b] = getPortInfo(b, j);
      } 
    } finally {
      if (this.id != 0L)
        nClose(this.id); 
      this.id = 0L;
    } 
    this.sourceLineInfo = new Port.Info[b1];
    this.targetLineInfo = new Port.Info[b2];
    b1 = 0;
    b2 = 0;
    for (byte b3 = 0; b3 < i; b3++) {
      if (this.portInfos[b3].isSource()) {
        this.sourceLineInfo[b1++] = this.portInfos[b3];
      } else {
        this.targetLineInfo[b2++] = this.portInfos[b3];
      } 
    } 
  }
  
  public Line getLine(Line.Info paramInfo) throws LineUnavailableException {
    Line.Info info = getLineInfo(paramInfo);
    if (info != null && info instanceof Port.Info)
      for (byte b = 0; b < this.portInfos.length; b++) {
        if (info.equals(this.portInfos[b]))
          return getPort(b); 
      }  
    throw new IllegalArgumentException("Line unsupported: " + paramInfo);
  }
  
  public int getMaxLines(Line.Info paramInfo) {
    Line.Info info = getLineInfo(paramInfo);
    return (info == null) ? 0 : ((info instanceof Port.Info) ? 1 : 0);
  }
  
  protected void implOpen() throws LineUnavailableException { this.id = nOpen(getMixerIndex()); }
  
  protected void implClose() throws LineUnavailableException {
    long l = this.id;
    this.id = 0L;
    nClose(l);
    if (this.ports != null)
      for (byte b = 0; b < this.ports.length; b++) {
        if (this.ports[b] != null)
          this.ports[b].disposeControls(); 
      }  
  }
  
  protected void implStart() throws LineUnavailableException {}
  
  protected void implStop() throws LineUnavailableException {}
  
  private Port.Info getPortInfo(int paramInt1, int paramInt2) {
    switch (paramInt2) {
      case 1:
        return new PortInfo(nGetPortName(getID(), paramInt1), true, null);
      case 2:
        return Port.Info.MICROPHONE;
      case 3:
        return Port.Info.LINE_IN;
      case 4:
        return Port.Info.COMPACT_DISC;
      case 256:
        return new PortInfo(nGetPortName(getID(), paramInt1), false, null);
      case 512:
        return Port.Info.SPEAKER;
      case 768:
        return Port.Info.HEADPHONE;
      case 1024:
        return Port.Info.LINE_OUT;
    } 
    return null;
  }
  
  int getMixerIndex() { return ((PortMixerProvider.PortMixerInfo)getMixerInfo()).getIndex(); }
  
  Port getPort(int paramInt) {
    if (this.ports == null)
      this.ports = new PortMixerPort[this.portInfos.length]; 
    if (this.ports[paramInt] == null) {
      this.ports[paramInt] = new PortMixerPort(this.portInfos[paramInt], this, paramInt, null);
      return this.ports[paramInt];
    } 
    return this.ports[paramInt];
  }
  
  long getID() { return this.id; }
  
  private static native long nOpen(int paramInt) throws LineUnavailableException;
  
  private static native void nClose(long paramLong);
  
  private static native int nGetPortCount(long paramLong);
  
  private static native int nGetPortType(long paramLong, int paramInt);
  
  private static native String nGetPortName(long paramLong, int paramInt);
  
  private static native void nGetControls(long paramLong, int paramInt, Vector paramVector);
  
  private static native void nControlSetIntValue(long paramLong, int paramInt);
  
  private static native int nControlGetIntValue(long paramLong);
  
  private static native void nControlSetFloatValue(long paramLong, float paramFloat);
  
  private static native float nControlGetFloatValue(long paramLong);
  
  private static final class BoolCtrl extends BooleanControl {
    private final long controlID;
    
    private boolean closed = false;
    
    private static BooleanControl.Type createType(String param1String) {
      if (param1String.equals("Mute"))
        return BooleanControl.Type.MUTE; 
      if (param1String.equals("Select"));
      return new BCT(param1String, null);
    }
    
    private BoolCtrl(long param1Long, String param1String) { this(param1Long, createType(param1String)); }
    
    private BoolCtrl(long param1Long, BooleanControl.Type param1Type) {
      super(param1Type, false);
      this.controlID = param1Long;
    }
    
    public void setValue(boolean param1Boolean) {
      if (!this.closed)
        PortMixer.nControlSetIntValue(this.controlID, param1Boolean ? 1 : 0); 
    }
    
    public boolean getValue() { return !this.closed ? ((PortMixer.nControlGetIntValue(this.controlID) != 0)) : false; }
    
    private static final class BCT extends BooleanControl.Type {
      private BCT(String param2String) { super(param2String); }
    }
  }
  
  private static final class CompCtrl extends CompoundControl {
    private CompCtrl(String param1String, Control[] param1ArrayOfControl) { super(new CCT(param1String, null), param1ArrayOfControl); }
    
    private static final class CCT extends CompoundControl.Type {
      private CCT(String param2String) { super(param2String); }
    }
  }
  
  private static final class FloatCtrl extends FloatControl {
    private final long controlID;
    
    private boolean closed = false;
    
    private static final FloatControl.Type[] FLOAT_CONTROL_TYPES = { null, FloatControl.Type.BALANCE, FloatControl.Type.MASTER_GAIN, FloatControl.Type.PAN, FloatControl.Type.VOLUME };
    
    private FloatCtrl(long param1Long, String param1String1, float param1Float1, float param1Float2, float param1Float3, String param1String2) { this(param1Long, new FCT(param1String1, null), param1Float1, param1Float2, param1Float3, param1String2); }
    
    private FloatCtrl(long param1Long, int param1Int, float param1Float1, float param1Float2, float param1Float3, String param1String) { this(param1Long, FLOAT_CONTROL_TYPES[param1Int], param1Float1, param1Float2, param1Float3, param1String); }
    
    private FloatCtrl(long param1Long, FloatControl.Type param1Type, float param1Float1, float param1Float2, float param1Float3, String param1String) {
      super(param1Type, param1Float1, param1Float2, param1Float3, 1000, param1Float1, param1String);
      this.controlID = param1Long;
    }
    
    public void setValue(float param1Float) {
      if (!this.closed)
        PortMixer.nControlSetFloatValue(this.controlID, param1Float); 
    }
    
    public float getValue() { return !this.closed ? PortMixer.nControlGetFloatValue(this.controlID) : getMinimum(); }
    
    private static final class FCT extends FloatControl.Type {
      private FCT(String param2String) { super(param2String); }
    }
  }
  
  private static final class PortInfo extends Port.Info {
    private PortInfo(String param1String, boolean param1Boolean) { super(Port.class, param1String, param1Boolean); }
  }
  
  private static final class PortMixerPort extends AbstractLine implements Port {
    private final int portIndex;
    
    private long id;
    
    private PortMixerPort(Port.Info param1Info, PortMixer param1PortMixer, int param1Int) {
      super(param1Info, param1PortMixer, null);
      this.portIndex = param1Int;
    }
    
    void implOpen() throws LineUnavailableException {
      long l = ((PortMixer)this.mixer).getID();
      if (this.id == 0L || l != this.id || this.controls.length == 0) {
        this.id = l;
        Vector vector = new Vector();
        synchronized (vector) {
          PortMixer.nGetControls(this.id, this.portIndex, vector);
          this.controls = new Control[vector.size()];
          for (byte b = 0; b < this.controls.length; b++)
            this.controls[b] = (Control)vector.elementAt(b); 
        } 
      } else {
        enableControls(this.controls, true);
      } 
    }
    
    private void enableControls(Control[] param1ArrayOfControl, boolean param1Boolean) {
      for (byte b = 0; b < param1ArrayOfControl.length; b++) {
        if (param1ArrayOfControl[b] instanceof PortMixer.BoolCtrl) {
          ((PortMixer.BoolCtrl)param1ArrayOfControl[b]).closed = !param1Boolean;
        } else if (param1ArrayOfControl[b] instanceof PortMixer.FloatCtrl) {
          ((PortMixer.FloatCtrl)param1ArrayOfControl[b]).closed = !param1Boolean;
        } else if (param1ArrayOfControl[b] instanceof CompoundControl) {
          enableControls(((CompoundControl)param1ArrayOfControl[b]).getMemberControls(), param1Boolean);
        } 
      } 
    }
    
    private void disposeControls() throws LineUnavailableException {
      enableControls(this.controls, false);
      this.controls = new Control[0];
    }
    
    void implClose() throws LineUnavailableException { enableControls(this.controls, false); }
    
    public void open() throws LineUnavailableException {
      synchronized (this.mixer) {
        if (!isOpen()) {
          this.mixer.open(this);
          try {
            implOpen();
            setOpen(true);
          } catch (LineUnavailableException lineUnavailableException) {
            this.mixer.close(this);
            throw lineUnavailableException;
          } 
        } 
      } 
    }
    
    public void close() throws LineUnavailableException {
      synchronized (this.mixer) {
        if (isOpen()) {
          setOpen(false);
          implClose();
          this.mixer.close(this);
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\PortMixer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */