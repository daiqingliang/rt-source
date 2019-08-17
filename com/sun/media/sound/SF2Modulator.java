package com.sun.media.sound;

public final class SF2Modulator {
  public static final int SOURCE_NONE = 0;
  
  public static final int SOURCE_NOTE_ON_VELOCITY = 2;
  
  public static final int SOURCE_NOTE_ON_KEYNUMBER = 3;
  
  public static final int SOURCE_POLY_PRESSURE = 10;
  
  public static final int SOURCE_CHANNEL_PRESSURE = 13;
  
  public static final int SOURCE_PITCH_WHEEL = 14;
  
  public static final int SOURCE_PITCH_SENSITIVITY = 16;
  
  public static final int SOURCE_MIDI_CONTROL = 128;
  
  public static final int SOURCE_DIRECTION_MIN_MAX = 0;
  
  public static final int SOURCE_DIRECTION_MAX_MIN = 256;
  
  public static final int SOURCE_POLARITY_UNIPOLAR = 0;
  
  public static final int SOURCE_POLARITY_BIPOLAR = 512;
  
  public static final int SOURCE_TYPE_LINEAR = 0;
  
  public static final int SOURCE_TYPE_CONCAVE = 1024;
  
  public static final int SOURCE_TYPE_CONVEX = 2048;
  
  public static final int SOURCE_TYPE_SWITCH = 3072;
  
  public static final int TRANSFORM_LINEAR = 0;
  
  public static final int TRANSFORM_ABSOLUTE = 2;
  
  int sourceOperator;
  
  int destinationOperator;
  
  short amount;
  
  int amountSourceOperator;
  
  int transportOperator;
  
  public short getAmount() { return this.amount; }
  
  public void setAmount(short paramShort) { this.amount = paramShort; }
  
  public int getAmountSourceOperator() { return this.amountSourceOperator; }
  
  public void setAmountSourceOperator(int paramInt) { this.amountSourceOperator = paramInt; }
  
  public int getTransportOperator() { return this.transportOperator; }
  
  public void setTransportOperator(int paramInt) { this.transportOperator = paramInt; }
  
  public int getDestinationOperator() { return this.destinationOperator; }
  
  public void setDestinationOperator(int paramInt) { this.destinationOperator = paramInt; }
  
  public int getSourceOperator() { return this.sourceOperator; }
  
  public void setSourceOperator(int paramInt) { this.sourceOperator = paramInt; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\SF2Modulator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */