package javax.swing.plaf.synth;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.JComponent;

public class SynthContext {
  private static final Queue<SynthContext> queue = new ConcurrentLinkedQueue();
  
  private JComponent component;
  
  private Region region;
  
  private SynthStyle style;
  
  private int state;
  
  static SynthContext getContext(JComponent paramJComponent, SynthStyle paramSynthStyle, int paramInt) { return getContext(paramJComponent, SynthLookAndFeel.getRegion(paramJComponent), paramSynthStyle, paramInt); }
  
  static SynthContext getContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt) {
    SynthContext synthContext = (SynthContext)queue.poll();
    if (synthContext == null)
      synthContext = new SynthContext(); 
    synthContext.reset(paramJComponent, paramRegion, paramSynthStyle, paramInt);
    return synthContext;
  }
  
  static void releaseContext(SynthContext paramSynthContext) { queue.offer(paramSynthContext); }
  
  SynthContext() {}
  
  public SynthContext(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt) {
    if (paramJComponent == null || paramRegion == null || paramSynthStyle == null)
      throw new NullPointerException("You must supply a non-null component, region and style"); 
    reset(paramJComponent, paramRegion, paramSynthStyle, paramInt);
  }
  
  public JComponent getComponent() { return this.component; }
  
  public Region getRegion() { return this.region; }
  
  boolean isSubregion() { return getRegion().isSubregion(); }
  
  void setStyle(SynthStyle paramSynthStyle) { this.style = paramSynthStyle; }
  
  public SynthStyle getStyle() { return this.style; }
  
  void setComponentState(int paramInt) { this.state = paramInt; }
  
  public int getComponentState() { return this.state; }
  
  void reset(JComponent paramJComponent, Region paramRegion, SynthStyle paramSynthStyle, int paramInt) {
    this.component = paramJComponent;
    this.region = paramRegion;
    this.style = paramSynthStyle;
    this.state = paramInt;
  }
  
  void dispose() {
    this.component = null;
    this.style = null;
    releaseContext(this);
  }
  
  SynthPainter getPainter() {
    SynthPainter synthPainter = getStyle().getPainter(this);
    return (synthPainter != null) ? synthPainter : SynthPainter.NULL_PAINTER;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */