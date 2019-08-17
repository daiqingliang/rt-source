package sun.swing.plaf.synth;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public class DefaultSynthStyle extends SynthStyle implements Cloneable {
  private static final Object PENDING = new Object();
  
  private boolean opaque;
  
  private Insets insets;
  
  private StateInfo[] states;
  
  private Map data;
  
  private Font font;
  
  private SynthGraphicsUtils synthGraphics;
  
  private SynthPainter painter;
  
  public DefaultSynthStyle() {}
  
  public DefaultSynthStyle(DefaultSynthStyle paramDefaultSynthStyle) {
    this.opaque = paramDefaultSynthStyle.opaque;
    if (paramDefaultSynthStyle.insets != null)
      this.insets = new Insets(paramDefaultSynthStyle.insets.top, paramDefaultSynthStyle.insets.left, paramDefaultSynthStyle.insets.bottom, paramDefaultSynthStyle.insets.right); 
    if (paramDefaultSynthStyle.states != null) {
      this.states = new StateInfo[paramDefaultSynthStyle.states.length];
      for (int i = paramDefaultSynthStyle.states.length - 1; i >= 0; i--)
        this.states[i] = (StateInfo)paramDefaultSynthStyle.states[i].clone(); 
    } 
    if (paramDefaultSynthStyle.data != null) {
      this.data = new HashMap();
      this.data.putAll(paramDefaultSynthStyle.data);
    } 
    this.font = paramDefaultSynthStyle.font;
    this.synthGraphics = paramDefaultSynthStyle.synthGraphics;
    this.painter = paramDefaultSynthStyle.painter;
  }
  
  public DefaultSynthStyle(Insets paramInsets, boolean paramBoolean, StateInfo[] paramArrayOfStateInfo, Map paramMap) {
    this.insets = paramInsets;
    this.opaque = paramBoolean;
    this.states = paramArrayOfStateInfo;
    this.data = paramMap;
  }
  
  public Color getColor(SynthContext paramSynthContext, ColorType paramColorType) { return getColor(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState(), paramColorType); }
  
  public Color getColor(JComponent paramJComponent, Region paramRegion, int paramInt, ColorType paramColorType) {
    if (!paramRegion.isSubregion() && paramInt == 1) {
      if (paramColorType == ColorType.BACKGROUND)
        return paramJComponent.getBackground(); 
      if (paramColorType == ColorType.FOREGROUND)
        return paramJComponent.getForeground(); 
      if (paramColorType == ColorType.TEXT_FOREGROUND) {
        Color color1 = paramJComponent.getForeground();
        if (!(color1 instanceof javax.swing.plaf.UIResource))
          return color1; 
      } 
    } 
    Color color = getColorForState(paramJComponent, paramRegion, paramInt, paramColorType);
    if (color == null) {
      if (paramColorType == ColorType.BACKGROUND || paramColorType == ColorType.TEXT_BACKGROUND)
        return paramJComponent.getBackground(); 
      if (paramColorType == ColorType.FOREGROUND || paramColorType == ColorType.TEXT_FOREGROUND)
        return paramJComponent.getForeground(); 
    } 
    return color;
  }
  
  protected Color getColorForState(SynthContext paramSynthContext, ColorType paramColorType) { return getColorForState(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState(), paramColorType); }
  
  protected Color getColorForState(JComponent paramJComponent, Region paramRegion, int paramInt, ColorType paramColorType) {
    StateInfo stateInfo = getStateInfo(paramInt);
    Color color;
    if (stateInfo != null && (color = stateInfo.getColor(paramColorType)) != null)
      return color; 
    if (stateInfo == null || stateInfo.getComponentState() != 0) {
      stateInfo = getStateInfo(0);
      if (stateInfo != null)
        return stateInfo.getColor(paramColorType); 
    } 
    return null;
  }
  
  public void setFont(Font paramFont) { this.font = paramFont; }
  
  public Font getFont(SynthContext paramSynthContext) { return getFont(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState()); }
  
  public Font getFont(JComponent paramJComponent, Region paramRegion, int paramInt) {
    if (!paramRegion.isSubregion() && paramInt == 1)
      return paramJComponent.getFont(); 
    Font font1 = paramJComponent.getFont();
    return (font1 != null && !(font1 instanceof javax.swing.plaf.UIResource)) ? font1 : getFontForState(paramJComponent, paramRegion, paramInt);
  }
  
  protected Font getFontForState(JComponent paramJComponent, Region paramRegion, int paramInt) {
    if (paramJComponent == null)
      return this.font; 
    StateInfo stateInfo = getStateInfo(paramInt);
    Font font1;
    if (stateInfo != null && (font1 = stateInfo.getFont()) != null)
      return font1; 
    if (stateInfo == null || stateInfo.getComponentState() != 0) {
      stateInfo = getStateInfo(0);
      if (stateInfo != null && (font1 = stateInfo.getFont()) != null)
        return font1; 
    } 
    return this.font;
  }
  
  protected Font getFontForState(SynthContext paramSynthContext) { return getFontForState(paramSynthContext.getComponent(), paramSynthContext.getRegion(), paramSynthContext.getComponentState()); }
  
  public void setGraphicsUtils(SynthGraphicsUtils paramSynthGraphicsUtils) { this.synthGraphics = paramSynthGraphicsUtils; }
  
  public SynthGraphicsUtils getGraphicsUtils(SynthContext paramSynthContext) { return (this.synthGraphics == null) ? super.getGraphicsUtils(paramSynthContext) : this.synthGraphics; }
  
  public void setInsets(Insets paramInsets) { this.insets = paramInsets; }
  
  public Insets getInsets(SynthContext paramSynthContext, Insets paramInsets) {
    if (paramInsets == null)
      paramInsets = new Insets(0, 0, 0, 0); 
    if (this.insets != null) {
      paramInsets.left = this.insets.left;
      paramInsets.right = this.insets.right;
      paramInsets.top = this.insets.top;
      paramInsets.bottom = this.insets.bottom;
    } else {
      paramInsets.left = paramInsets.right = paramInsets.top = paramInsets.bottom = 0;
    } 
    return paramInsets;
  }
  
  public void setPainter(SynthPainter paramSynthPainter) { this.painter = paramSynthPainter; }
  
  public SynthPainter getPainter(SynthContext paramSynthContext) { return this.painter; }
  
  public void setOpaque(boolean paramBoolean) { this.opaque = paramBoolean; }
  
  public boolean isOpaque(SynthContext paramSynthContext) { return this.opaque; }
  
  public void setData(Map paramMap) { this.data = paramMap; }
  
  public Map getData() { return this.data; }
  
  public Object get(SynthContext paramSynthContext, Object paramObject) {
    StateInfo stateInfo = getStateInfo(paramSynthContext.getComponentState());
    if (stateInfo != null && stateInfo.getData() != null && getKeyFromData(stateInfo.getData(), paramObject) != null)
      return getKeyFromData(stateInfo.getData(), paramObject); 
    stateInfo = getStateInfo(0);
    return (stateInfo != null && stateInfo.getData() != null && getKeyFromData(stateInfo.getData(), paramObject) != null) ? getKeyFromData(stateInfo.getData(), paramObject) : ((getKeyFromData(this.data, paramObject) != null) ? getKeyFromData(this.data, paramObject) : getDefaultValue(paramSynthContext, paramObject));
  }
  
  private Object getKeyFromData(Map paramMap, Object paramObject) {
    Object object = null;
    if (paramMap != null) {
      synchronized (paramMap) {
        object = paramMap.get(paramObject);
      } 
      while (object == PENDING) {
        synchronized (paramMap) {
          try {
            paramMap.wait();
          } catch (InterruptedException interruptedException) {}
          object = paramMap.get(paramObject);
        } 
      } 
      if (object instanceof UIDefaults.LazyValue) {
        synchronized (paramMap) {
          paramMap.put(paramObject, PENDING);
        } 
        object = ((UIDefaults.LazyValue)object).createValue(null);
        synchronized (paramMap) {
          paramMap.put(paramObject, object);
          paramMap.notifyAll();
        } 
      } 
    } 
    return object;
  }
  
  public Object getDefaultValue(SynthContext paramSynthContext, Object paramObject) { return super.get(paramSynthContext, paramObject); }
  
  public Object clone() {
    DefaultSynthStyle defaultSynthStyle;
    try {
      defaultSynthStyle = (DefaultSynthStyle)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return null;
    } 
    if (this.states != null) {
      defaultSynthStyle.states = new StateInfo[this.states.length];
      for (int i = this.states.length - 1; i >= 0; i--)
        defaultSynthStyle.states[i] = (StateInfo)this.states[i].clone(); 
    } 
    if (this.data != null) {
      defaultSynthStyle.data = new HashMap();
      defaultSynthStyle.data.putAll(this.data);
    } 
    return defaultSynthStyle;
  }
  
  public DefaultSynthStyle addTo(DefaultSynthStyle paramDefaultSynthStyle) {
    if (this.insets != null)
      paramDefaultSynthStyle.insets = this.insets; 
    if (this.font != null)
      paramDefaultSynthStyle.font = this.font; 
    if (this.painter != null)
      paramDefaultSynthStyle.painter = this.painter; 
    if (this.synthGraphics != null)
      paramDefaultSynthStyle.synthGraphics = this.synthGraphics; 
    paramDefaultSynthStyle.opaque = this.opaque;
    if (this.states != null)
      if (paramDefaultSynthStyle.states == null) {
        paramDefaultSynthStyle.states = new StateInfo[this.states.length];
        for (int i = this.states.length - 1; i >= 0; i--) {
          if (this.states[i] != null)
            paramDefaultSynthStyle.states[i] = (StateInfo)this.states[i].clone(); 
        } 
      } else {
        int i = 0;
        int j = 0;
        int k = paramDefaultSynthStyle.states.length;
        for (int m = this.states.length - 1; m >= 0; m--) {
          int n = this.states[m].getComponentState();
          boolean bool = false;
          for (int i1 = k - 1 - j; i1 >= 0; i1--) {
            if (n == paramDefaultSynthStyle.states[i1].getComponentState()) {
              paramDefaultSynthStyle.states[i1] = this.states[m].addTo(paramDefaultSynthStyle.states[i1]);
              StateInfo stateInfo = paramDefaultSynthStyle.states[k - 1 - j];
              paramDefaultSynthStyle.states[k - 1 - j] = paramDefaultSynthStyle.states[i1];
              paramDefaultSynthStyle.states[i1] = stateInfo;
              j++;
              bool = true;
              break;
            } 
          } 
          if (!bool)
            i++; 
        } 
        if (i != 0) {
          StateInfo[] arrayOfStateInfo = new StateInfo[i + k];
          int n = k;
          System.arraycopy(paramDefaultSynthStyle.states, 0, arrayOfStateInfo, 0, k);
          for (int i1 = this.states.length - 1; i1 >= 0; i1--) {
            int i2 = this.states[i1].getComponentState();
            boolean bool = false;
            for (int i3 = k - 1; i3 >= 0; i3--) {
              if (i2 == paramDefaultSynthStyle.states[i3].getComponentState()) {
                bool = true;
                break;
              } 
            } 
            if (!bool)
              arrayOfStateInfo[n++] = (StateInfo)this.states[i1].clone(); 
          } 
          paramDefaultSynthStyle.states = arrayOfStateInfo;
        } 
      }  
    if (this.data != null) {
      if (paramDefaultSynthStyle.data == null)
        paramDefaultSynthStyle.data = new HashMap(); 
      paramDefaultSynthStyle.data.putAll(this.data);
    } 
    return paramDefaultSynthStyle;
  }
  
  public void setStateInfo(StateInfo[] paramArrayOfStateInfo) { this.states = paramArrayOfStateInfo; }
  
  public StateInfo[] getStateInfo() { return this.states; }
  
  public StateInfo getStateInfo(int paramInt) {
    if (this.states != null) {
      int i = 0;
      int j = -1;
      int k = -1;
      if (paramInt == 0) {
        for (int n = this.states.length - 1; n >= 0; n--) {
          if (this.states[n].getComponentState() == 0)
            return this.states[n]; 
        } 
        return null;
      } 
      for (int m = this.states.length - 1; m >= 0; m--) {
        int n = this.states[m].getComponentState();
        if (n == 0) {
          if (k == -1)
            k = m; 
        } else if ((paramInt & n) == n) {
          int i1 = n;
          i1 -= ((0xAAAAAAAA & i1) >>> 1);
          i1 = (i1 & 0x33333333) + (i1 >>> 2 & 0x33333333);
          i1 = i1 + (i1 >>> 4) & 0xF0F0F0F;
          i1 += (i1 >>> 8);
          i1 += (i1 >>> 16);
          i1 &= 0xFF;
          if (i1 > i) {
            j = m;
            i = i1;
          } 
        } 
      } 
      if (j != -1)
        return this.states[j]; 
      if (k != -1)
        return this.states[k]; 
    } 
    return null;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(super.toString()).append(',');
    stringBuffer.append("data=").append(this.data).append(',');
    stringBuffer.append("font=").append(this.font).append(',');
    stringBuffer.append("insets=").append(this.insets).append(',');
    stringBuffer.append("synthGraphics=").append(this.synthGraphics).append(',');
    stringBuffer.append("painter=").append(this.painter).append(',');
    StateInfo[] arrayOfStateInfo = getStateInfo();
    if (arrayOfStateInfo != null) {
      stringBuffer.append("states[");
      for (StateInfo stateInfo : arrayOfStateInfo)
        stringBuffer.append(stateInfo.toString()).append(','); 
      stringBuffer.append(']').append(',');
    } 
    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
    return stringBuffer.toString();
  }
  
  public static class StateInfo {
    private Map data;
    
    private Font font;
    
    private Color[] colors;
    
    private int state;
    
    public StateInfo() {}
    
    public StateInfo(int param1Int, Font param1Font, Color[] param1ArrayOfColor) {
      this.state = param1Int;
      this.font = param1Font;
      this.colors = param1ArrayOfColor;
    }
    
    public StateInfo(StateInfo param1StateInfo) {
      this.state = param1StateInfo.state;
      this.font = param1StateInfo.font;
      if (param1StateInfo.data != null) {
        if (this.data == null)
          this.data = new HashMap(); 
        this.data.putAll(param1StateInfo.data);
      } 
      if (param1StateInfo.colors != null) {
        this.colors = new Color[param1StateInfo.colors.length];
        System.arraycopy(param1StateInfo.colors, 0, this.colors, 0, param1StateInfo.colors.length);
      } 
    }
    
    public Map getData() { return this.data; }
    
    public void setData(Map param1Map) { this.data = param1Map; }
    
    public void setFont(Font param1Font) { this.font = param1Font; }
    
    public Font getFont() { return this.font; }
    
    public void setColors(Color[] param1ArrayOfColor) { this.colors = param1ArrayOfColor; }
    
    public Color[] getColors() { return this.colors; }
    
    public Color getColor(ColorType param1ColorType) {
      if (this.colors != null) {
        int i = param1ColorType.getID();
        if (i < this.colors.length)
          return this.colors[i]; 
      } 
      return null;
    }
    
    public StateInfo addTo(StateInfo param1StateInfo) {
      if (this.font != null)
        param1StateInfo.font = this.font; 
      if (this.data != null) {
        if (param1StateInfo.data == null)
          param1StateInfo.data = new HashMap(); 
        param1StateInfo.data.putAll(this.data);
      } 
      if (this.colors != null)
        if (param1StateInfo.colors == null) {
          param1StateInfo.colors = new Color[this.colors.length];
          System.arraycopy(this.colors, 0, param1StateInfo.colors, 0, this.colors.length);
        } else {
          if (param1StateInfo.colors.length < this.colors.length) {
            Color[] arrayOfColor = param1StateInfo.colors;
            param1StateInfo.colors = new Color[this.colors.length];
            System.arraycopy(arrayOfColor, 0, param1StateInfo.colors, 0, arrayOfColor.length);
          } 
          for (int i = this.colors.length - 1; i >= 0; i--) {
            if (this.colors[i] != null)
              param1StateInfo.colors[i] = this.colors[i]; 
          } 
        }  
      return param1StateInfo;
    }
    
    public void setComponentState(int param1Int) { this.state = param1Int; }
    
    public int getComponentState() { return this.state; }
    
    private int getMatchCount(int param1Int) {
      param1Int &= this.state;
      param1Int -= ((0xAAAAAAAA & param1Int) >>> 1);
      param1Int = (param1Int & 0x33333333) + (param1Int >>> 2 & 0x33333333);
      param1Int = param1Int + (param1Int >>> 4) & 0xF0F0F0F;
      param1Int += (param1Int >>> 8);
      param1Int += (param1Int >>> 16);
      return param1Int & 0xFF;
    }
    
    public Object clone() { return new StateInfo(this); }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append(super.toString()).append(',');
      stringBuffer.append("state=").append(Integer.toString(this.state)).append(',');
      stringBuffer.append("font=").append(this.font).append(',');
      if (this.colors != null)
        stringBuffer.append("colors=").append(Arrays.asList(this.colors)).append(','); 
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\plaf\synth\DefaultSynthStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */