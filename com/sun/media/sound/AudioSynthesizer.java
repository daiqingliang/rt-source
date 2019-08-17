package com.sun.media.sound;

import java.util.Map;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

public interface AudioSynthesizer extends Synthesizer {
  AudioFormat getFormat();
  
  AudioSynthesizerPropertyInfo[] getPropertyInfo(Map<String, Object> paramMap);
  
  void open(SourceDataLine paramSourceDataLine, Map<String, Object> paramMap) throws MidiUnavailableException;
  
  AudioInputStream openStream(AudioFormat paramAudioFormat, Map<String, Object> paramMap) throws MidiUnavailableException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AudioSynthesizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */