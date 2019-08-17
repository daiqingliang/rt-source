package com.sun.media.sound;

public final class AudioSynthesizerPropertyInfo {
  public String name;
  
  public String description = null;
  
  public Object value = null;
  
  public Class valueClass = null;
  
  public Object[] choices = null;
  
  public AudioSynthesizerPropertyInfo(String paramString, Object paramObject) {
    this.name = paramString;
    if (paramObject instanceof Class) {
      this.valueClass = (Class)paramObject;
    } else {
      this.value = paramObject;
      if (paramObject != null)
        this.valueClass = paramObject.getClass(); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\AudioSynthesizerPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */