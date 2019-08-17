package com.sun.nio.file;

import java.nio.file.WatchEvent;

public static enum SensitivityWatchEventModifier implements WatchEvent.Modifier {
  HIGH(2),
  MEDIUM(10),
  LOW(30);
  
  private final int sensitivity;
  
  public int sensitivityValueInSeconds() { return this.sensitivity; }
  
  SensitivityWatchEventModifier(int paramInt1) { this.sensitivity = paramInt1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\nio\file\SensitivityWatchEventModifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */