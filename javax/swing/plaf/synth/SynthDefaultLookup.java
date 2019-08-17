package javax.swing.plaf.synth;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import sun.swing.DefaultLookup;

class SynthDefaultLookup extends DefaultLookup {
  public Object getDefault(JComponent paramJComponent, ComponentUI paramComponentUI, String paramString) {
    if (!(paramComponentUI instanceof SynthUI))
      return super.getDefault(paramJComponent, paramComponentUI, paramString); 
    SynthContext synthContext = ((SynthUI)paramComponentUI).getContext(paramJComponent);
    Object object = synthContext.getStyle().get(synthContext, paramString);
    synthContext.dispose();
    return object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\SynthDefaultLookup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */