package com.sun.media.sound;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Soundbank;
import javax.sound.midi.spi.SoundbankReader;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class AudioFileSoundbankReader extends SoundbankReader {
  public Soundbank getSoundbank(URL paramURL) throws InvalidMidiDataException, IOException {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(paramURL);
      Soundbank soundbank = getSoundbank(audioInputStream);
      audioInputStream.close();
      return soundbank;
    } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
      return null;
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public Soundbank getSoundbank(InputStream paramInputStream) throws InvalidMidiDataException, IOException {
    paramInputStream.mark(512);
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(paramInputStream);
      Soundbank soundbank = getSoundbank(audioInputStream);
      if (soundbank != null)
        return soundbank; 
    } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
    
    } catch (IOException iOException) {}
    paramInputStream.reset();
    return null;
  }
  
  public Soundbank getSoundbank(AudioInputStream paramAudioInputStream) throws InvalidMidiDataException, IOException {
    try {
      byte[] arrayOfByte;
      if (paramAudioInputStream.getFrameLength() == -1L) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] arrayOfByte1 = new byte[1024 - 1024 % paramAudioInputStream.getFormat().getFrameSize()];
        int i;
        while ((i = paramAudioInputStream.read(arrayOfByte1)) != -1)
          byteArrayOutputStream.write(arrayOfByte1, 0, i); 
        paramAudioInputStream.close();
        arrayOfByte = byteArrayOutputStream.toByteArray();
      } else {
        arrayOfByte = new byte[(int)(paramAudioInputStream.getFrameLength() * paramAudioInputStream.getFormat().getFrameSize())];
        (new DataInputStream(paramAudioInputStream)).readFully(arrayOfByte);
      } 
      ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(new ModelByteBuffer(arrayOfByte), paramAudioInputStream.getFormat(), -4800.0F);
      ModelPerformer modelPerformer = new ModelPerformer();
      modelPerformer.getOscillators().add(modelByteBufferWavetable);
      SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
      SimpleInstrument simpleInstrument = new SimpleInstrument();
      simpleInstrument.add(modelPerformer);
      simpleSoundbank.addInstrument(simpleInstrument);
      return simpleSoundbank;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public Soundbank getSoundbank(File paramFile) throws InvalidMidiDataException, IOException {
    try {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(paramFile);
      audioInputStream.close();
      ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(new ModelByteBuffer(paramFile, 0L, paramFile.length()), -4800.0F);
      ModelPerformer modelPerformer = new ModelPerformer();
      modelPerformer.getOscillators().add(modelByteBufferWavetable);
      SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
      SimpleInstrument simpleInstrument = new SimpleInstrument();
      simpleInstrument.add(modelPerformer);
      simpleSoundbank.addInstrument(simpleInstrument);
      return simpleSoundbank;
    } catch (UnsupportedAudioFileException unsupportedAudioFileException) {
      return null;
    } catch (IOException iOException) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AudioFileSoundbankReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */