package com.sun.media.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDeviceReceiver;
import javax.sound.midi.MidiDeviceTransmitter;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

abstract class AbstractMidiDevice implements MidiDevice, ReferenceCountingDevice {
  private static final boolean TRACE_TRANSMITTER = false;
  
  private ArrayList<Receiver> receiverList;
  
  private TransmitterList transmitterList;
  
  private final Object traRecLock = new Object();
  
  private final MidiDevice.Info info;
  
  private boolean open = false;
  
  private int openRefCount;
  
  private List openKeepingObjects;
  
  protected long id = 0L;
  
  protected AbstractMidiDevice(MidiDevice.Info paramInfo) {
    this.info = paramInfo;
    this.openRefCount = 0;
  }
  
  public final MidiDevice.Info getDeviceInfo() { return this.info; }
  
  public final void open() throws MidiUnavailableException {
    synchronized (this) {
      this.openRefCount = -1;
      doOpen();
    } 
  }
  
  private void openInternal(Object paramObject) throws MidiUnavailableException {
    synchronized (this) {
      if (this.openRefCount != -1) {
        this.openRefCount++;
        getOpenKeepingObjects().add(paramObject);
      } 
      doOpen();
    } 
  }
  
  private void doOpen() throws MidiUnavailableException {
    synchronized (this) {
      if (!isOpen()) {
        implOpen();
        this.open = true;
      } 
    } 
  }
  
  public final void close() throws MidiUnavailableException {
    synchronized (this) {
      doClose();
      this.openRefCount = 0;
    } 
  }
  
  public final void closeInternal(Object paramObject) throws MidiUnavailableException {
    synchronized (this) {
      if (getOpenKeepingObjects().remove(paramObject) && this.openRefCount > 0) {
        this.openRefCount--;
        if (this.openRefCount == 0)
          doClose(); 
      } 
    } 
  }
  
  public final void doClose() throws MidiUnavailableException {
    synchronized (this) {
      if (isOpen()) {
        implClose();
        this.open = false;
      } 
    } 
  }
  
  public final boolean isOpen() { return this.open; }
  
  protected void implClose() throws MidiUnavailableException {
    synchronized (this.traRecLock) {
      if (this.receiverList != null) {
        for (byte b = 0; b < this.receiverList.size(); b++)
          ((Receiver)this.receiverList.get(b)).close(); 
        this.receiverList.clear();
      } 
      if (this.transmitterList != null)
        this.transmitterList.close(); 
    } 
  }
  
  public long getMicrosecondPosition() { return -1L; }
  
  public final int getMaxReceivers() { return hasReceivers() ? -1 : 0; }
  
  public final int getMaxTransmitters() { return hasTransmitters() ? -1 : 0; }
  
  public final Receiver getReceiver() throws MidiUnavailableException {
    Receiver receiver;
    synchronized (this.traRecLock) {
      receiver = createReceiver();
      getReceiverList().add(receiver);
    } 
    return receiver;
  }
  
  public final List<Receiver> getReceivers() {
    List list;
    synchronized (this.traRecLock) {
      if (this.receiverList == null) {
        list = Collections.unmodifiableList(new ArrayList(0));
      } else {
        list = Collections.unmodifiableList((List)this.receiverList.clone());
      } 
    } 
    return list;
  }
  
  public final Transmitter getTransmitter() throws MidiUnavailableException {
    Transmitter transmitter;
    synchronized (this.traRecLock) {
      transmitter = createTransmitter();
      getTransmitterList().add(transmitter);
    } 
    return transmitter;
  }
  
  public final List<Transmitter> getTransmitters() {
    List list;
    synchronized (this.traRecLock) {
      if (this.transmitterList == null || this.transmitterList.transmitters.size() == 0) {
        list = Collections.unmodifiableList(new ArrayList(0));
      } else {
        list = Collections.unmodifiableList((List)this.transmitterList.transmitters.clone());
      } 
    } 
    return list;
  }
  
  final long getId() { return this.id; }
  
  public final Receiver getReceiverReferenceCounting() throws MidiUnavailableException {
    Receiver receiver;
    synchronized (this.traRecLock) {
      receiver = getReceiver();
      openInternal(receiver);
    } 
    return receiver;
  }
  
  public final Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException {
    Transmitter transmitter;
    synchronized (this.traRecLock) {
      transmitter = getTransmitter();
      openInternal(transmitter);
    } 
    return transmitter;
  }
  
  private List getOpenKeepingObjects() {
    if (this.openKeepingObjects == null)
      this.openKeepingObjects = new ArrayList(); 
    return this.openKeepingObjects;
  }
  
  private List<Receiver> getReceiverList() {
    synchronized (this.traRecLock) {
      if (this.receiverList == null)
        this.receiverList = new ArrayList(); 
    } 
    return this.receiverList;
  }
  
  protected boolean hasReceivers() { return false; }
  
  protected Receiver createReceiver() throws MidiUnavailableException { throw new MidiUnavailableException("MIDI IN receiver not available"); }
  
  final TransmitterList getTransmitterList() {
    synchronized (this.traRecLock) {
      if (this.transmitterList == null)
        this.transmitterList = new TransmitterList(); 
    } 
    return this.transmitterList;
  }
  
  protected boolean hasTransmitters() { return false; }
  
  protected Transmitter createTransmitter() throws MidiUnavailableException { throw new MidiUnavailableException("MIDI OUT transmitter not available"); }
  
  protected abstract void implOpen() throws MidiUnavailableException;
  
  protected final void finalize() throws MidiUnavailableException { close(); }
  
  abstract class AbstractReceiver implements MidiDeviceReceiver {
    private boolean open = true;
    
    public final void send(MidiMessage param1MidiMessage, long param1Long) {
      if (!this.open)
        throw new IllegalStateException("Receiver is not open"); 
      implSend(param1MidiMessage, param1Long);
    }
    
    abstract void implSend(MidiMessage param1MidiMessage, long param1Long);
    
    public final void close() throws MidiUnavailableException {
      this.open = false;
      synchronized (AbstractMidiDevice.this.traRecLock) {
        AbstractMidiDevice.this.getReceiverList().remove(this);
      } 
      AbstractMidiDevice.this.closeInternal(this);
    }
    
    public final MidiDevice getMidiDevice() { return AbstractMidiDevice.this; }
    
    final boolean isOpen() { return this.open; }
  }
  
  class BasicTransmitter implements MidiDeviceTransmitter {
    private Receiver receiver = null;
    
    AbstractMidiDevice.TransmitterList tlist = null;
    
    private void setTransmitterList(AbstractMidiDevice.TransmitterList param1TransmitterList) { this.tlist = param1TransmitterList; }
    
    public final void setReceiver(Receiver param1Receiver) {
      if (this.tlist != null && this.receiver != param1Receiver) {
        this.tlist.receiverChanged(this, this.receiver, param1Receiver);
        this.receiver = param1Receiver;
      } 
    }
    
    public final Receiver getReceiver() throws MidiUnavailableException { return this.receiver; }
    
    public final void close() throws MidiUnavailableException {
      AbstractMidiDevice.this.closeInternal(this);
      if (this.tlist != null) {
        this.tlist.receiverChanged(this, this.receiver, null);
        this.tlist.remove(this);
        this.tlist = null;
      } 
    }
    
    public final MidiDevice getMidiDevice() { return AbstractMidiDevice.this; }
  }
  
  final class TransmitterList {
    private final ArrayList<Transmitter> transmitters = new ArrayList();
    
    private MidiOutDevice.MidiOutReceiver midiOutReceiver;
    
    private int optimizedReceiverCount = 0;
    
    private void add(Transmitter param1Transmitter) {
      synchronized (this.transmitters) {
        this.transmitters.add(param1Transmitter);
      } 
      if (param1Transmitter instanceof AbstractMidiDevice.BasicTransmitter)
        ((AbstractMidiDevice.BasicTransmitter)param1Transmitter).setTransmitterList(this); 
    }
    
    private void remove(Transmitter param1Transmitter) {
      synchronized (this.transmitters) {
        int i = this.transmitters.indexOf(param1Transmitter);
        if (i >= 0)
          this.transmitters.remove(i); 
      } 
    }
    
    private void receiverChanged(AbstractMidiDevice.BasicTransmitter param1BasicTransmitter, Receiver param1Receiver1, Receiver param1Receiver2) {
      synchronized (this.transmitters) {
        if (this.midiOutReceiver == param1Receiver1)
          this.midiOutReceiver = null; 
        if (param1Receiver2 != null && param1Receiver2 instanceof MidiOutDevice.MidiOutReceiver && this.midiOutReceiver == null)
          this.midiOutReceiver = (MidiOutDevice.MidiOutReceiver)param1Receiver2; 
        this.optimizedReceiverCount = (this.midiOutReceiver != null) ? 1 : 0;
      } 
    }
    
    void close() throws MidiUnavailableException {
      synchronized (this.transmitters) {
        for (byte b = 0; b < this.transmitters.size(); b++)
          ((Transmitter)this.transmitters.get(b)).close(); 
        this.transmitters.clear();
      } 
    }
    
    void sendMessage(int param1Int, long param1Long) {
      try {
        synchronized (this.transmitters) {
          int i = this.transmitters.size();
          if (this.optimizedReceiverCount == i) {
            if (this.midiOutReceiver != null)
              this.midiOutReceiver.sendPackedMidiMessage(param1Int, param1Long); 
          } else {
            for (byte b = 0; b < i; b++) {
              Receiver receiver = ((Transmitter)this.transmitters.get(b)).getReceiver();
              if (receiver != null)
                if (this.optimizedReceiverCount > 0) {
                  if (receiver instanceof MidiOutDevice.MidiOutReceiver) {
                    ((MidiOutDevice.MidiOutReceiver)receiver).sendPackedMidiMessage(param1Int, param1Long);
                  } else {
                    receiver.send(new FastShortMessage(param1Int), param1Long);
                  } 
                } else {
                  receiver.send(new FastShortMessage(param1Int), param1Long);
                }  
            } 
          } 
        } 
      } catch (InvalidMidiDataException invalidMidiDataException) {}
    }
    
    void sendMessage(byte[] param1ArrayOfByte, long param1Long) {
      try {
        synchronized (this.transmitters) {
          int i = this.transmitters.size();
          for (byte b = 0; b < i; b++) {
            Receiver receiver = ((Transmitter)this.transmitters.get(b)).getReceiver();
            if (receiver != null)
              receiver.send(new FastSysexMessage(param1ArrayOfByte), param1Long); 
          } 
        } 
      } catch (InvalidMidiDataException invalidMidiDataException) {
        return;
      } 
    }
    
    void sendMessage(MidiMessage param1MidiMessage, long param1Long) {
      if (param1MidiMessage instanceof FastShortMessage) {
        sendMessage(((FastShortMessage)param1MidiMessage).getPackedMsg(), param1Long);
        return;
      } 
      synchronized (this.transmitters) {
        int i = this.transmitters.size();
        if (this.optimizedReceiverCount == i) {
          if (this.midiOutReceiver != null)
            this.midiOutReceiver.send(param1MidiMessage, param1Long); 
        } else {
          for (byte b = 0; b < i; b++) {
            Receiver receiver = ((Transmitter)this.transmitters.get(b)).getReceiver();
            if (receiver != null)
              receiver.send(param1MidiMessage, param1Long); 
          } 
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AbstractMidiDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */