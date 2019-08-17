package com.sun.media.sound;

public final class DLSModulator {
  public static final int CONN_DST_NONE = 0;
  
  public static final int CONN_DST_GAIN = 1;
  
  public static final int CONN_DST_PITCH = 3;
  
  public static final int CONN_DST_PAN = 4;
  
  public static final int CONN_DST_LFO_FREQUENCY = 260;
  
  public static final int CONN_DST_LFO_STARTDELAY = 261;
  
  public static final int CONN_DST_EG1_ATTACKTIME = 518;
  
  public static final int CONN_DST_EG1_DECAYTIME = 519;
  
  public static final int CONN_DST_EG1_RELEASETIME = 521;
  
  public static final int CONN_DST_EG1_SUSTAINLEVEL = 522;
  
  public static final int CONN_DST_EG2_ATTACKTIME = 778;
  
  public static final int CONN_DST_EG2_DECAYTIME = 779;
  
  public static final int CONN_DST_EG2_RELEASETIME = 781;
  
  public static final int CONN_DST_EG2_SUSTAINLEVEL = 782;
  
  public static final int CONN_DST_KEYNUMBER = 5;
  
  public static final int CONN_DST_LEFT = 16;
  
  public static final int CONN_DST_RIGHT = 17;
  
  public static final int CONN_DST_CENTER = 18;
  
  public static final int CONN_DST_LEFTREAR = 19;
  
  public static final int CONN_DST_RIGHTREAR = 20;
  
  public static final int CONN_DST_LFE_CHANNEL = 21;
  
  public static final int CONN_DST_CHORUS = 128;
  
  public static final int CONN_DST_REVERB = 129;
  
  public static final int CONN_DST_VIB_FREQUENCY = 276;
  
  public static final int CONN_DST_VIB_STARTDELAY = 277;
  
  public static final int CONN_DST_EG1_DELAYTIME = 523;
  
  public static final int CONN_DST_EG1_HOLDTIME = 524;
  
  public static final int CONN_DST_EG1_SHUTDOWNTIME = 525;
  
  public static final int CONN_DST_EG2_DELAYTIME = 783;
  
  public static final int CONN_DST_EG2_HOLDTIME = 784;
  
  public static final int CONN_DST_FILTER_CUTOFF = 1280;
  
  public static final int CONN_DST_FILTER_Q = 1281;
  
  public static final int CONN_SRC_NONE = 0;
  
  public static final int CONN_SRC_LFO = 1;
  
  public static final int CONN_SRC_KEYONVELOCITY = 2;
  
  public static final int CONN_SRC_KEYNUMBER = 3;
  
  public static final int CONN_SRC_EG1 = 4;
  
  public static final int CONN_SRC_EG2 = 5;
  
  public static final int CONN_SRC_PITCHWHEEL = 6;
  
  public static final int CONN_SRC_CC1 = 129;
  
  public static final int CONN_SRC_CC7 = 135;
  
  public static final int CONN_SRC_CC10 = 138;
  
  public static final int CONN_SRC_CC11 = 139;
  
  public static final int CONN_SRC_RPN0 = 256;
  
  public static final int CONN_SRC_RPN1 = 257;
  
  public static final int CONN_SRC_RPN2 = 258;
  
  public static final int CONN_SRC_POLYPRESSURE = 7;
  
  public static final int CONN_SRC_CHANNELPRESSURE = 8;
  
  public static final int CONN_SRC_VIBRATO = 9;
  
  public static final int CONN_SRC_MONOPRESSURE = 10;
  
  public static final int CONN_SRC_CC91 = 219;
  
  public static final int CONN_SRC_CC93 = 221;
  
  public static final int CONN_TRN_NONE = 0;
  
  public static final int CONN_TRN_CONCAVE = 1;
  
  public static final int CONN_TRN_CONVEX = 2;
  
  public static final int CONN_TRN_SWITCH = 3;
  
  public static final int DST_FORMAT_CB = 1;
  
  public static final int DST_FORMAT_CENT = 1;
  
  public static final int DST_FORMAT_TIMECENT = 2;
  
  public static final int DST_FORMAT_PERCENT = 3;
  
  int source;
  
  int control;
  
  int destination;
  
  int transform;
  
  int scale;
  
  int version = 1;
  
  public int getControl() { return this.control; }
  
  public void setControl(int paramInt) { this.control = paramInt; }
  
  public static int getDestinationFormat(int paramInt) { return (paramInt == 1) ? 1 : ((paramInt == 3) ? 1 : ((paramInt == 4) ? 3 : ((paramInt == 260) ? 1 : ((paramInt == 261) ? 2 : ((paramInt == 518) ? 2 : ((paramInt == 519) ? 2 : ((paramInt == 521) ? 2 : ((paramInt == 522) ? 3 : ((paramInt == 778) ? 2 : ((paramInt == 779) ? 2 : ((paramInt == 781) ? 2 : ((paramInt == 782) ? 3 : ((paramInt == 5) ? 1 : ((paramInt == 16) ? 1 : ((paramInt == 17) ? 1 : ((paramInt == 18) ? 1 : ((paramInt == 19) ? 1 : ((paramInt == 20) ? 1 : ((paramInt == 21) ? 1 : ((paramInt == 128) ? 3 : ((paramInt == 129) ? 3 : ((paramInt == 276) ? 1 : ((paramInt == 277) ? 2 : ((paramInt == 523) ? 2 : ((paramInt == 524) ? 2 : ((paramInt == 525) ? 2 : ((paramInt == 783) ? 2 : ((paramInt == 784) ? 2 : ((paramInt == 1280) ? 1 : ((paramInt == 1281) ? 1 : -1)))))))))))))))))))))))))))))); }
  
  public static String getDestinationName(int paramInt) { return (paramInt == 1) ? "gain" : ((paramInt == 3) ? "pitch" : ((paramInt == 4) ? "pan" : ((paramInt == 260) ? "lfo1.freq" : ((paramInt == 261) ? "lfo1.delay" : ((paramInt == 518) ? "eg1.attack" : ((paramInt == 519) ? "eg1.decay" : ((paramInt == 521) ? "eg1.release" : ((paramInt == 522) ? "eg1.sustain" : ((paramInt == 778) ? "eg2.attack" : ((paramInt == 779) ? "eg2.decay" : ((paramInt == 781) ? "eg2.release" : ((paramInt == 782) ? "eg2.sustain" : ((paramInt == 5) ? "keynumber" : ((paramInt == 16) ? "left" : ((paramInt == 17) ? "right" : ((paramInt == 18) ? "center" : ((paramInt == 19) ? "leftrear" : ((paramInt == 20) ? "rightrear" : ((paramInt == 21) ? "lfe_channel" : ((paramInt == 128) ? "chorus" : ((paramInt == 129) ? "reverb" : ((paramInt == 276) ? "vib.freq" : ((paramInt == 277) ? "vib.delay" : ((paramInt == 523) ? "eg1.delay" : ((paramInt == 524) ? "eg1.hold" : ((paramInt == 525) ? "eg1.shutdown" : ((paramInt == 783) ? "eg2.delay" : ((paramInt == 784) ? "eg.2hold" : ((paramInt == 1280) ? "filter.cutoff" : ((paramInt == 1281) ? "filter.q" : null)))))))))))))))))))))))))))))); }
  
  public static String getSourceName(int paramInt) { return (paramInt == 0) ? "none" : ((paramInt == 1) ? "lfo" : ((paramInt == 2) ? "keyonvelocity" : ((paramInt == 3) ? "keynumber" : ((paramInt == 4) ? "eg1" : ((paramInt == 5) ? "eg2" : ((paramInt == 6) ? "pitchweel" : ((paramInt == 129) ? "cc1" : ((paramInt == 135) ? "cc7" : ((paramInt == 138) ? "c10" : ((paramInt == 139) ? "cc11" : ((paramInt == 7) ? "polypressure" : ((paramInt == 8) ? "channelpressure" : ((paramInt == 9) ? "vibrato" : ((paramInt == 10) ? "monopressure" : ((paramInt == 219) ? "cc91" : ((paramInt == 221) ? "cc93" : null)))))))))))))))); }
  
  public int getDestination() { return this.destination; }
  
  public void setDestination(int paramInt) { this.destination = paramInt; }
  
  public int getScale() { return this.scale; }
  
  public void setScale(int paramInt) { this.scale = paramInt; }
  
  public int getSource() { return this.source; }
  
  public void setSource(int paramInt) { this.source = paramInt; }
  
  public int getVersion() { return this.version; }
  
  public void setVersion(int paramInt) { this.version = paramInt; }
  
  public int getTransform() { return this.transform; }
  
  public void setTransform(int paramInt) { this.transform = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\DLSModulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */