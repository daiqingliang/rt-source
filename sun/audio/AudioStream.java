package sun.audio;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiFileFormat;
import javax.sound.midi.MidiSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioStream extends FilterInputStream {
  AudioInputStream ais = null;
  
  AudioFormat format = null;
  
  MidiFileFormat midiformat = null;
  
  InputStream stream = null;
  
  public AudioStream(InputStream paramInputStream) throws IOException {
    super(paramInputStream);
    this.stream = paramInputStream;
    if (!paramInputStream.markSupported())
      this.stream = new BufferedInputStream(paramInputStream, 1024); 
    try {
      this.ais = AudioSystem.getAudioInputStream(this.stream);
      this.format = this.ais.getFormat();
      this.in = this.ais;
    } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
      try {
        this.midiformat = MidiSystem.getMidiFileFormat(this.stream);
      } catch (InvalidMidiDataException invalidMidiDataException) {
        throw new IOException("could not create audio stream from input stream");
      } 
    } 
  }
  
  public AudioData getData() throws IOException {
    int i = getLength();
    if (i < 1048576) {
      byte[] arrayOfByte = new byte[i];
      try {
        this.ais.read(arrayOfByte, 0, i);
      } catch (IOException iOException) {
        throw new IOException("Could not create AudioData Object");
      } 
      return new AudioData(this.format, arrayOfByte);
    } 
    throw new IOException("could not create AudioData object");
  }
  
  public int getLength() { return (this.ais != null && this.format != null) ? (int)(this.ais.getFrameLength() * this.ais.getFormat().getFrameSize()) : ((this.midiformat != null) ? this.midiformat.getByteLength() : -1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\audio\AudioStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */