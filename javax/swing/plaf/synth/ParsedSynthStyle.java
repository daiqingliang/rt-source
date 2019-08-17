package javax.swing.plaf.synth;

import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import sun.swing.plaf.synth.DefaultSynthStyle;

class ParsedSynthStyle extends DefaultSynthStyle {
  private static SynthPainter DELEGATING_PAINTER_INSTANCE = new DelegatingPainter(null);
  
  private PainterInfo[] _painters;
  
  private static PainterInfo[] mergePainterInfo(PainterInfo[] paramArrayOfPainterInfo1, PainterInfo[] paramArrayOfPainterInfo2) {
    if (paramArrayOfPainterInfo1 == null)
      return paramArrayOfPainterInfo2; 
    if (paramArrayOfPainterInfo2 == null)
      return paramArrayOfPainterInfo1; 
    int i = paramArrayOfPainterInfo1.length;
    int j = paramArrayOfPainterInfo2.length;
    int k = 0;
    PainterInfo[] arrayOfPainterInfo = new PainterInfo[i + j];
    System.arraycopy(paramArrayOfPainterInfo1, 0, arrayOfPainterInfo, 0, i);
    for (int m = 0; m < j; m++) {
      boolean bool = false;
      for (byte b = 0; b < i - k; b++) {
        if (paramArrayOfPainterInfo2[m].equalsPainter(paramArrayOfPainterInfo1[b])) {
          arrayOfPainterInfo[b] = paramArrayOfPainterInfo2[m];
          k++;
          bool = true;
          break;
        } 
      } 
      if (!bool)
        arrayOfPainterInfo[i + m - k] = paramArrayOfPainterInfo2[m]; 
    } 
    if (k > 0) {
      PainterInfo[] arrayOfPainterInfo1 = arrayOfPainterInfo;
      arrayOfPainterInfo = new PainterInfo[arrayOfPainterInfo.length - k];
      System.arraycopy(arrayOfPainterInfo1, 0, arrayOfPainterInfo, 0, arrayOfPainterInfo.length);
    } 
    return arrayOfPainterInfo;
  }
  
  public ParsedSynthStyle() {}
  
  public ParsedSynthStyle(DefaultSynthStyle paramDefaultSynthStyle) {
    super(paramDefaultSynthStyle);
    if (paramDefaultSynthStyle instanceof ParsedSynthStyle) {
      ParsedSynthStyle parsedSynthStyle = (ParsedSynthStyle)paramDefaultSynthStyle;
      if (parsedSynthStyle._painters != null)
        this._painters = parsedSynthStyle._painters; 
    } 
  }
  
  public SynthPainter getPainter(SynthContext paramSynthContext) { return DELEGATING_PAINTER_INSTANCE; }
  
  public void setPainters(PainterInfo[] paramArrayOfPainterInfo) { this._painters = paramArrayOfPainterInfo; }
  
  public DefaultSynthStyle addTo(DefaultSynthStyle paramDefaultSynthStyle) {
    if (!(paramDefaultSynthStyle instanceof ParsedSynthStyle))
      paramDefaultSynthStyle = new ParsedSynthStyle(paramDefaultSynthStyle); 
    ParsedSynthStyle parsedSynthStyle;
    parsedSynthStyle._painters = (parsedSynthStyle = (ParsedSynthStyle)super.addTo(paramDefaultSynthStyle)).mergePainterInfo(parsedSynthStyle._painters, this._painters);
    return parsedSynthStyle;
  }
  
  private SynthPainter getBestPainter(SynthContext paramSynthContext, String paramString, int paramInt) {
    StateInfo stateInfo = (StateInfo)getStateInfo(paramSynthContext.getComponentState());
    SynthPainter synthPainter;
    return (stateInfo != null && (synthPainter = getBestPainter(stateInfo.getPainters(), paramString, paramInt)) != null) ? synthPainter : (((synthPainter = getBestPainter(this._painters, paramString, paramInt)) != null) ? synthPainter : SynthPainter.NULL_PAINTER);
  }
  
  private SynthPainter getBestPainter(PainterInfo[] paramArrayOfPainterInfo, String paramString, int paramInt) {
    if (paramArrayOfPainterInfo != null) {
      SynthPainter synthPainter1 = null;
      SynthPainter synthPainter2 = null;
      for (int i = paramArrayOfPainterInfo.length - 1; i >= 0; i--) {
        PainterInfo painterInfo = paramArrayOfPainterInfo[i];
        if (painterInfo.getMethod() == paramString) {
          if (painterInfo.getDirection() == paramInt)
            return painterInfo.getPainter(); 
          if (synthPainter2 == null && painterInfo.getDirection() == -1)
            synthPainter2 = painterInfo.getPainter(); 
        } else if (synthPainter1 == null && painterInfo.getMethod() == null) {
          synthPainter1 = painterInfo.getPainter();
        } 
      } 
      return (synthPainter2 != null) ? synthPainter2 : synthPainter1;
    } 
    return null;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(super.toString());
    if (this._painters != null) {
      stringBuffer.append(",painters=[");
      for (byte b = 0; b < this._painters.length; b++)
        stringBuffer.append(this._painters[b].toString()); 
      stringBuffer.append("]");
    } 
    return stringBuffer.toString();
  }
  
  private static class AggregatePainter extends SynthPainter {
    private List<SynthPainter> painters = new LinkedList();
    
    AggregatePainter(SynthPainter param1SynthPainter) { this.painters.add(param1SynthPainter); }
    
    void addPainter(SynthPainter param1SynthPainter) {
      if (param1SynthPainter != null)
        this.painters.add(param1SynthPainter); 
    }
    
    public void paintArrowButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintArrowButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintArrowButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintArrowButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintArrowButtonForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintArrowButtonForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintCheckBoxMenuItemBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintCheckBoxMenuItemBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintCheckBoxMenuItemBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintCheckBoxMenuItemBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintCheckBoxBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintCheckBoxBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintCheckBoxBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintCheckBoxBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintColorChooserBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintColorChooserBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintColorChooserBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintColorChooserBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintComboBoxBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintComboBoxBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintComboBoxBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintComboBoxBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintDesktopIconBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintDesktopIconBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintDesktopIconBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintDesktopIconBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintDesktopPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintDesktopPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintDesktopPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintDesktopPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintEditorPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintEditorPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintEditorPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintEditorPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintFileChooserBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintFileChooserBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintFileChooserBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintFileChooserBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintFormattedTextFieldBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintFormattedTextFieldBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintFormattedTextFieldBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintFormattedTextFieldBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintInternalFrameTitlePaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintInternalFrameTitlePaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintInternalFrameTitlePaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintInternalFrameTitlePaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintInternalFrameBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintInternalFrameBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintInternalFrameBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintInternalFrameBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintLabelBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintLabelBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintLabelBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintLabelBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintListBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintListBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintListBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintListBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintMenuBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintMenuBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintMenuBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintMenuBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintMenuItemBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintMenuItemBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintMenuItemBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintMenuItemBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintMenuBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintMenuBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintMenuBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintMenuBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintOptionPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintOptionPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintOptionPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintOptionPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintPanelBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintPanelBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintPanelBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintPanelBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintPasswordFieldBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintPasswordFieldBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintPasswordFieldBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintPasswordFieldBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintPopupMenuBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintPopupMenuBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintPopupMenuBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintPopupMenuBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintProgressBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintProgressBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintProgressBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintProgressBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintProgressBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintProgressBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintProgressBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintProgressBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintProgressBarForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintProgressBarForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintRadioButtonMenuItemBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintRadioButtonMenuItemBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintRadioButtonMenuItemBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintRadioButtonMenuItemBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintRadioButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintRadioButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintRadioButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintRadioButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintRootPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintRootPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintRootPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintRootPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintScrollBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintScrollBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintScrollBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintScrollBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintScrollBarThumbBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarThumbBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintScrollBarThumbBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarThumbBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintScrollBarTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintScrollBarTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintScrollBarTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintScrollBarTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollBarTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintScrollPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintScrollPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintScrollPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSeparatorBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSeparatorBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSeparatorBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSeparatorBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSeparatorBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSeparatorBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSeparatorBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSeparatorBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSeparatorForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSeparatorForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSliderBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSliderBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSliderBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSliderBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSliderThumbBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderThumbBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSliderThumbBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderThumbBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSliderTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSliderTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSliderTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSliderTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSliderTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSpinnerBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSpinnerBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSpinnerBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSpinnerBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSplitPaneDividerBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSplitPaneDividerBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSplitPaneDividerBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSplitPaneDividerBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSplitPaneDividerForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSplitPaneDividerForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSplitPaneDragDivider(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSplitPaneDragDivider(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintSplitPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSplitPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintSplitPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintSplitPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTabbedPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTabbedPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabAreaBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabAreaBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabAreaBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabAreaBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintTabbedPaneTabBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintTabbedPaneTabBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6); 
    }
    
    public void paintTabbedPaneTabBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintTabbedPaneTabBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneTabBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6); 
    }
    
    public void paintTabbedPaneContentBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneContentBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTabbedPaneContentBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTabbedPaneContentBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTableHeaderBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTableHeaderBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTableHeaderBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTableHeaderBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTableBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTableBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTableBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTableBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTextAreaBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTextAreaBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTextAreaBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTextAreaBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTextPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTextPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTextPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTextPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTextFieldBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTextFieldBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTextFieldBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTextFieldBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToggleButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToggleButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToggleButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToggleButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintToolBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintToolBarContentBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarContentBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarContentBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarContentBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintToolBarContentBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarContentBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarContentBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarContentBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintToolBarDragWindowBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarDragWindowBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarDragWindowBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarDragWindowBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintToolBarDragWindowBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarDragWindowBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolBarDragWindowBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolBarDragWindowBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); 
    }
    
    public void paintToolTipBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolTipBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintToolTipBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintToolTipBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTreeBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTreeBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTreeBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTreeBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTreeCellBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTreeCellBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTreeCellBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTreeCellBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintTreeCellFocus(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintTreeCellFocus(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintViewportBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintViewportBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    public void paintViewportBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      for (SynthPainter synthPainter : this.painters)
        synthPainter.paintViewportBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
  }
  
  private static class DelegatingPainter extends SynthPainter {
    private DelegatingPainter() {}
    
    private static SynthPainter getPainter(SynthContext param1SynthContext, String param1String, int param1Int) { return ((ParsedSynthStyle)param1SynthContext.getStyle()).getBestPainter(param1SynthContext, param1String, param1Int); }
    
    public void paintArrowButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "arrowbuttonbackground", -1).paintArrowButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintArrowButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "arrowbuttonborder", -1).paintArrowButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintArrowButtonForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "arrowbuttonforeground", param1Int5).paintArrowButtonForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "buttonbackground", -1).paintButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "buttonborder", -1).paintButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintCheckBoxMenuItemBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "checkboxmenuitembackground", -1).paintCheckBoxMenuItemBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintCheckBoxMenuItemBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "checkboxmenuitemborder", -1).paintCheckBoxMenuItemBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintCheckBoxBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "checkboxbackground", -1).paintCheckBoxBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintCheckBoxBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "checkboxborder", -1).paintCheckBoxBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintColorChooserBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "colorchooserbackground", -1).paintColorChooserBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintColorChooserBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "colorchooserborder", -1).paintColorChooserBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintComboBoxBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "comboboxbackground", -1).paintComboBoxBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintComboBoxBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "comboboxborder", -1).paintComboBoxBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintDesktopIconBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "desktopiconbackground", -1).paintDesktopIconBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintDesktopIconBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "desktopiconborder", -1).paintDesktopIconBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintDesktopPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "desktoppanebackground", -1).paintDesktopPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintDesktopPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "desktoppaneborder", -1).paintDesktopPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintEditorPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "editorpanebackground", -1).paintEditorPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintEditorPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "editorpaneborder", -1).paintEditorPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintFileChooserBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "filechooserbackground", -1).paintFileChooserBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintFileChooserBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "filechooserborder", -1).paintFileChooserBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintFormattedTextFieldBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "formattedtextfieldbackground", -1).paintFormattedTextFieldBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintFormattedTextFieldBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "formattedtextfieldborder", -1).paintFormattedTextFieldBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintInternalFrameTitlePaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "internalframetitlepanebackground", -1).paintInternalFrameTitlePaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintInternalFrameTitlePaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "internalframetitlepaneborder", -1).paintInternalFrameTitlePaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintInternalFrameBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "internalframebackground", -1).paintInternalFrameBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintInternalFrameBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "internalframeborder", -1).paintInternalFrameBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintLabelBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "labelbackground", -1).paintLabelBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintLabelBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "labelborder", -1).paintLabelBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintListBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "listbackground", -1).paintListBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintListBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "listborder", -1).paintListBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintMenuBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "menubarbackground", -1).paintMenuBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintMenuBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "menubarborder", -1).paintMenuBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintMenuItemBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "menuitembackground", -1).paintMenuItemBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintMenuItemBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "menuitemborder", -1).paintMenuItemBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintMenuBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "menubackground", -1).paintMenuBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintMenuBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "menuborder", -1).paintMenuBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintOptionPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "optionpanebackground", -1).paintOptionPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintOptionPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "optionpaneborder", -1).paintOptionPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintPanelBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "panelbackground", -1).paintPanelBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintPanelBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "panelborder", -1).paintPanelBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintPasswordFieldBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "passwordfieldbackground", -1).paintPasswordFieldBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintPasswordFieldBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "passwordfieldborder", -1).paintPasswordFieldBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintPopupMenuBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "popupmenubackground", -1).paintPopupMenuBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintPopupMenuBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "popupmenuborder", -1).paintPopupMenuBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintProgressBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "progressbarbackground", -1).paintProgressBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintProgressBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "progressbarbackground", param1Int5).paintProgressBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintProgressBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "progressbarborder", -1).paintProgressBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintProgressBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "progressbarborder", param1Int5).paintProgressBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintProgressBarForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "progressbarforeground", param1Int5).paintProgressBarForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintRadioButtonMenuItemBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "radiobuttonmenuitembackground", -1).paintRadioButtonMenuItemBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintRadioButtonMenuItemBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "radiobuttonmenuitemborder", -1).paintRadioButtonMenuItemBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintRadioButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "radiobuttonbackground", -1).paintRadioButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintRadioButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "radiobuttonborder", -1).paintRadioButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintRootPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "rootpanebackground", -1).paintRootPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintRootPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "rootpaneborder", -1).paintRootPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintScrollBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "scrollbarbackground", -1).paintScrollBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintScrollBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "scrollbarbackground", param1Int5).paintScrollBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintScrollBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "scrollbarborder", -1).paintScrollBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintScrollBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "scrollbarborder", param1Int5).paintScrollBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintScrollBarThumbBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "scrollbarthumbbackground", param1Int5).paintScrollBarThumbBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintScrollBarThumbBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "scrollbarthumbborder", param1Int5).paintScrollBarThumbBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintScrollBarTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "scrollbartrackbackground", -1).paintScrollBarTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintScrollBarTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "scrollbartrackbackground", param1Int5).paintScrollBarTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintScrollBarTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "scrollbartrackborder", -1).paintScrollBarTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintScrollBarTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "scrollbartrackborder", param1Int5).paintScrollBarTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintScrollPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "scrollpanebackground", -1).paintScrollPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintScrollPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "scrollpaneborder", -1).paintScrollPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSeparatorBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "separatorbackground", -1).paintSeparatorBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSeparatorBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "separatorbackground", param1Int5).paintSeparatorBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSeparatorBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "separatorborder", -1).paintSeparatorBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSeparatorBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "separatorborder", param1Int5).paintSeparatorBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSeparatorForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "separatorforeground", param1Int5).paintSeparatorForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSliderBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "sliderbackground", -1).paintSliderBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSliderBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "sliderbackground", param1Int5).paintSliderBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSliderBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "sliderborder", -1).paintSliderBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSliderBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "sliderborder", param1Int5).paintSliderBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSliderThumbBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "sliderthumbbackground", param1Int5).paintSliderThumbBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSliderThumbBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "sliderthumbborder", param1Int5).paintSliderThumbBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSliderTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "slidertrackbackground", -1).paintSliderTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSliderTrackBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "slidertrackbackground", param1Int5).paintSliderTrackBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSliderTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "slidertrackborder", -1).paintSliderTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSliderTrackBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "slidertrackborder", param1Int5).paintSliderTrackBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSpinnerBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "spinnerbackground", -1).paintSpinnerBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSpinnerBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "spinnerborder", -1).paintSpinnerBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSplitPaneDividerBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "splitpanedividerbackground", -1).paintSplitPaneDividerBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSplitPaneDividerBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "splitpanedividerbackground", param1Int5).paintSplitPaneDividerBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSplitPaneDividerForeground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "splitpanedividerforeground", param1Int5).paintSplitPaneDividerForeground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSplitPaneDragDivider(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "splitpanedragdivider", param1Int5).paintSplitPaneDragDivider(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintSplitPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "splitpanebackground", -1).paintSplitPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintSplitPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "splitpaneborder", -1).paintSplitPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTabbedPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tabbedpanebackground", -1).paintTabbedPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTabbedPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tabbedpaneborder", -1).paintTabbedPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tabbedpanetabareabackground", -1).paintTabbedPaneTabAreaBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTabbedPaneTabAreaBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "tabbedpanetabareabackground", param1Int5).paintTabbedPaneTabAreaBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tabbedpanetabareaborder", -1).paintTabbedPaneTabAreaBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTabbedPaneTabAreaBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "tabbedpanetabareaborder", param1Int5).paintTabbedPaneTabAreaBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintTabbedPaneTabBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "tabbedpanetabbackground", -1).paintTabbedPaneTabBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintTabbedPaneTabBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) { getPainter(param1SynthContext, "tabbedpanetabbackground", param1Int6).paintTabbedPaneTabBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6); }
    
    public void paintTabbedPaneTabBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "tabbedpanetabborder", -1).paintTabbedPaneTabBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintTabbedPaneTabBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5, int param1Int6) { getPainter(param1SynthContext, "tabbedpanetabborder", param1Int6).paintTabbedPaneTabBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5, param1Int6); }
    
    public void paintTabbedPaneContentBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tabbedpanecontentbackground", -1).paintTabbedPaneContentBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTabbedPaneContentBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tabbedpanecontentborder", -1).paintTabbedPaneContentBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTableHeaderBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tableheaderbackground", -1).paintTableHeaderBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTableHeaderBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tableheaderborder", -1).paintTableHeaderBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTableBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tablebackground", -1).paintTableBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTableBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tableborder", -1).paintTableBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTextAreaBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "textareabackground", -1).paintTextAreaBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTextAreaBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "textareaborder", -1).paintTextAreaBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTextPaneBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "textpanebackground", -1).paintTextPaneBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTextPaneBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "textpaneborder", -1).paintTextPaneBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTextFieldBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "textfieldbackground", -1).paintTextFieldBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTextFieldBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "textfieldborder", -1).paintTextFieldBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToggleButtonBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "togglebuttonbackground", -1).paintToggleButtonBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToggleButtonBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "togglebuttonborder", -1).paintToggleButtonBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "toolbarbackground", -1).paintToolBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "toolbarbackground", param1Int5).paintToolBarBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintToolBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "toolbarborder", -1).paintToolBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "toolbarborder", param1Int5).paintToolBarBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintToolBarContentBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "toolbarcontentbackground", -1).paintToolBarContentBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarContentBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "toolbarcontentbackground", param1Int5).paintToolBarContentBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintToolBarContentBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "toolbarcontentborder", -1).paintToolBarContentBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarContentBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "toolbarcontentborder", param1Int5).paintToolBarContentBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintToolBarDragWindowBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "toolbardragwindowbackground", -1).paintToolBarDragWindowBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarDragWindowBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "toolbardragwindowbackground", param1Int5).paintToolBarDragWindowBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintToolBarDragWindowBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "toolbardragwindowborder", -1).paintToolBarDragWindowBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolBarDragWindowBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int param1Int5) { getPainter(param1SynthContext, "toolbardragwindowborder", param1Int5).paintToolBarDragWindowBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4, param1Int5); }
    
    public void paintToolTipBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tooltipbackground", -1).paintToolTipBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintToolTipBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "tooltipborder", -1).paintToolTipBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTreeBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "treebackground", -1).paintTreeBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTreeBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "treeborder", -1).paintTreeBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTreeCellBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "treecellbackground", -1).paintTreeCellBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTreeCellBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "treecellborder", -1).paintTreeCellBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintTreeCellFocus(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "treecellfocus", -1).paintTreeCellFocus(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintViewportBackground(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "viewportbackground", -1).paintViewportBackground(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void paintViewportBorder(SynthContext param1SynthContext, Graphics param1Graphics, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { getPainter(param1SynthContext, "viewportborder", -1).paintViewportBorder(param1SynthContext, param1Graphics, param1Int1, param1Int2, param1Int3, param1Int4); }
  }
  
  static class PainterInfo {
    private String _method;
    
    private SynthPainter _painter;
    
    private int _direction;
    
    PainterInfo(String param1String, SynthPainter param1SynthPainter, int param1Int) {
      if (param1String != null)
        this._method = param1String.intern(); 
      this._painter = param1SynthPainter;
      this._direction = param1Int;
    }
    
    void addPainter(SynthPainter param1SynthPainter) {
      if (!(this._painter instanceof ParsedSynthStyle.AggregatePainter))
        this._painter = new ParsedSynthStyle.AggregatePainter(this._painter); 
      ((ParsedSynthStyle.AggregatePainter)this._painter).addPainter(param1SynthPainter);
    }
    
    String getMethod() { return this._method; }
    
    SynthPainter getPainter() { return this._painter; }
    
    int getDirection() { return this._direction; }
    
    boolean equalsPainter(PainterInfo param1PainterInfo) { return (this._method == param1PainterInfo._method && this._direction == param1PainterInfo._direction); }
    
    public String toString() { return "PainterInfo {method=" + this._method + ",direction=" + this._direction + ",painter=" + this._painter + "}"; }
  }
  
  static class StateInfo extends DefaultSynthStyle.StateInfo {
    private ParsedSynthStyle.PainterInfo[] _painterInfo;
    
    public StateInfo() {}
    
    public StateInfo(DefaultSynthStyle.StateInfo param1StateInfo) {
      super(param1StateInfo);
      if (param1StateInfo instanceof StateInfo)
        this._painterInfo = ((StateInfo)param1StateInfo)._painterInfo; 
    }
    
    public void setPainters(ParsedSynthStyle.PainterInfo[] param1ArrayOfPainterInfo) { this._painterInfo = param1ArrayOfPainterInfo; }
    
    public ParsedSynthStyle.PainterInfo[] getPainters() { return this._painterInfo; }
    
    public Object clone() { return new StateInfo(this); }
    
    public DefaultSynthStyle.StateInfo addTo(DefaultSynthStyle.StateInfo param1StateInfo) {
      if (!(param1StateInfo instanceof StateInfo)) {
        param1StateInfo = new StateInfo(param1StateInfo);
      } else {
        param1StateInfo = super.addTo(param1StateInfo);
        StateInfo stateInfo = (StateInfo)param1StateInfo;
        stateInfo._painterInfo = ParsedSynthStyle.mergePainterInfo(stateInfo._painterInfo, this._painterInfo);
      } 
      return param1StateInfo;
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer(super.toString());
      stringBuffer.append(",painters=[");
      if (this._painterInfo != null)
        for (byte b = 0; b < this._painterInfo.length; b++)
          stringBuffer.append("    ").append(this._painterInfo[b].toString());  
      stringBuffer.append("]");
      return stringBuffer.toString();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\ParsedSynthStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */