package sun.audio;

import com.sun.media.sound.DataPusher;
import com.sun.media.sound.Toolkit;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioDevice {
  private boolean DEBUG = false;
  
  private Hashtable clipStreams = new Hashtable();
  
  private Vector infos = new Vector();
  
  private boolean playing = false;
  
  private Mixer mixer = null;
  
  public static final AudioDevice device = new AudioDevice();
  
  private void startSampled(AudioInputStream paramAudioInputStream, InputStream paramInputStream) throws UnsupportedAudioFileException, LineUnavailableException {
    Info info = null;
    DataPusher dataPusher = null;
    DataLine.Info info1 = null;
    SourceDataLine sourceDataLine = null;
    paramAudioInputStream = Toolkit.getPCMConvertedAudioInputStream(paramAudioInputStream);
    if (paramAudioInputStream == null)
      return; 
    info1 = new DataLine.Info(SourceDataLine.class, paramAudioInputStream.getFormat());
    if (!AudioSystem.isLineSupported(info1))
      return; 
    sourceDataLine = (SourceDataLine)AudioSystem.getLine(info1);
    dataPusher = new DataPusher(sourceDataLine, paramAudioInputStream);
    info = new Info(null, paramInputStream, dataPusher);
    this.infos.addElement(info);
    dataPusher.start();
  }
  
  private void startMidi(InputStream paramInputStream1, InputStream paramInputStream2) throws InvalidMidiDataException, MidiUnavailableException {
    Sequencer sequencer = null;
    Info info = null;
    sequencer = MidiSystem.getSequencer();
    sequencer.open();
    try {
      sequencer.setSequence(paramInputStream1);
    } catch (IOException iOException) {
      throw new InvalidMidiDataException(iOException.getMessage());
    } 
    info = new Info(sequencer, paramInputStream2, null);
    this.infos.addElement(info);
    sequencer.addMetaEventListener(info);
    sequencer.start();
  }
  
  public void openChannel(InputStream paramInputStream) {
    if (this.DEBUG) {
      System.out.println("AudioDevice: openChannel");
      System.out.println("input stream =" + paramInputStream);
    } 
    Info info = null;
    for (byte b = 0; b < this.infos.size(); b++) {
      info = (Info)this.infos.elementAt(b);
      if (info.in == paramInputStream)
        return; 
    } 
    AudioInputStream audioInputStream = null;
    if (paramInputStream instanceof AudioStream) {
      if (((AudioStream)paramInputStream).midiformat != null) {
        try {
          startMidi(((AudioStream)paramInputStream).stream, paramInputStream);
        } catch (Exception exception) {
          return;
        } 
      } else if (((AudioStream)paramInputStream).ais != null) {
        try {
          startSampled(((AudioStream)paramInputStream).ais, paramInputStream);
        } catch (Exception exception) {
          return;
        } 
      } 
    } else if (paramInputStream instanceof AudioDataStream) {
      if (paramInputStream instanceof ContinuousAudioDataStream) {
        try {
          AudioInputStream audioInputStream1 = new AudioInputStream(paramInputStream, (((AudioDataStream)paramInputStream).getAudioData()).format, -1L);
          startSampled(audioInputStream1, paramInputStream);
        } catch (Exception exception) {
          return;
        } 
      } else {
        try {
          AudioInputStream audioInputStream1 = new AudioInputStream(paramInputStream, (((AudioDataStream)paramInputStream).getAudioData()).format, (((AudioDataStream)paramInputStream).getAudioData()).buffer.length);
          startSampled(audioInputStream1, paramInputStream);
        } catch (Exception exception) {
          return;
        } 
      } 
    } else {
      BufferedInputStream bufferedInputStream = new BufferedInputStream(paramInputStream, 1024);
      try {
        try {
          audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
        } catch (IOException iOException) {
          return;
        } 
        startSampled(audioInputStream, paramInputStream);
      } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
        try {
          try {
            MidiFileFormat midiFileFormat = MidiSystem.getMidiFileFormat(bufferedInputStream);
          } catch (IOException iOException) {
            return;
          } 
          startMidi(bufferedInputStream, paramInputStream);
        } catch (InvalidMidiDataException invalidMidiDataException) {
          AudioFormat audioFormat = new AudioFormat(AudioFormat.Encoding.ULAW, 8000.0F, 8, 1, 1, 8000.0F, true);
          try {
            AudioInputStream audioInputStream1 = new AudioInputStream(bufferedInputStream, audioFormat, -1L);
            startSampled(audioInputStream1, paramInputStream);
          } catch (UnsupportedAudioFileException unsupportedAudioFileException1) {
            return;
          } catch (LineUnavailableException lineUnavailableException) {
            return;
          } 
        } catch (MidiUnavailableException midiUnavailableException) {
          return;
        } 
      } catch (LineUnavailableException lineUnavailableException) {
        return;
      } 
    } 
    notify();
  }
  
  public void closeChannel(InputStream paramInputStream) {
    if (this.DEBUG)
      System.out.println("AudioDevice.closeChannel"); 
    if (paramInputStream == null)
      return; 
    for (byte b = 0; b < this.infos.size(); b++) {
      Info info = (Info)this.infos.elementAt(b);
      if (info.in == paramInputStream)
        if (info.sequencer != null) {
          info.sequencer.stop();
          this.infos.removeElement(info);
        } else if (info.datapusher != null) {
          info.datapusher.stop();
          this.infos.removeElement(info);
        }  
    } 
    notify();
  }
  
  public void open() {}
  
  public void close() {}
  
  public void play() {
    if (this.DEBUG)
      System.out.println("exiting play()"); 
  }
  
  public void closeStreams() {
    for (byte b = 0; b < this.infos.size(); b++) {
      Info info = (Info)this.infos.elementAt(b);
      if (info.sequencer != null) {
        info.sequencer.stop();
        info.sequencer.close();
        this.infos.removeElement(info);
      } else if (info.datapusher != null) {
        info.datapusher.stop();
        this.infos.removeElement(info);
      } 
    } 
    if (this.DEBUG)
      System.err.println("Audio Device: Streams all closed."); 
    this.clipStreams = new Hashtable();
    this.infos = new Vector();
  }
  
  public int openChannels() { return this.infos.size(); }
  
  void setVerbose(boolean paramBoolean) { this.DEBUG = paramBoolean; }
  
  final class Info implements MetaEventListener {
    final Sequencer sequencer;
    
    final InputStream in;
    
    final DataPusher datapusher;
    
    Info(Sequencer param1Sequencer, InputStream param1InputStream, DataPusher param1DataPusher) {
      this.sequencer = param1Sequencer;
      this.in = param1InputStream;
      this.datapusher = param1DataPusher;
    }
    
    public void meta(MetaMessage param1MetaMessage) {
      if (param1MetaMessage.getType() == 47 && this.sequencer != null)
        this.sequencer.close(); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */