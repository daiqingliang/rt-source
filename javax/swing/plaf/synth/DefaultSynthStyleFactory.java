package javax.swing.plaf.synth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import javax.swing.JComponent;
import javax.swing.plaf.FontUIResource;
import sun.swing.BakedArrayList;
import sun.swing.plaf.synth.DefaultSynthStyle;
import sun.swing.plaf.synth.StyleAssociation;

class DefaultSynthStyleFactory extends SynthStyleFactory {
  public static final int NAME = 0;
  
  public static final int REGION = 1;
  
  private List<StyleAssociation> _styles = new ArrayList();
  
  private BakedArrayList _tmpList = new BakedArrayList(5);
  
  private Map<BakedArrayList, SynthStyle> _resolvedStyles = new HashMap();
  
  private SynthStyle _defaultStyle;
  
  public void addStyle(DefaultSynthStyle paramDefaultSynthStyle, String paramString, int paramInt) throws PatternSyntaxException {
    if (paramString == null)
      paramString = ".*"; 
    if (paramInt == 0) {
      this._styles.add(StyleAssociation.createStyleAssociation(paramString, paramDefaultSynthStyle, paramInt));
    } else if (paramInt == 1) {
      this._styles.add(StyleAssociation.createStyleAssociation(paramString.toLowerCase(), paramDefaultSynthStyle, paramInt));
    } 
  }
  
  public SynthStyle getStyle(JComponent paramJComponent, Region paramRegion) {
    BakedArrayList bakedArrayList = this._tmpList;
    bakedArrayList.clear();
    getMatchingStyles(bakedArrayList, paramJComponent, paramRegion);
    if (bakedArrayList.size() == 0)
      return getDefaultStyle(); 
    bakedArrayList.cacheHashCode();
    SynthStyle synthStyle = getCachedStyle(bakedArrayList);
    if (synthStyle == null) {
      synthStyle = mergeStyles(bakedArrayList);
      if (synthStyle != null)
        cacheStyle(bakedArrayList, synthStyle); 
    } 
    return synthStyle;
  }
  
  private SynthStyle getDefaultStyle() {
    if (this._defaultStyle == null) {
      this._defaultStyle = new DefaultSynthStyle();
      ((DefaultSynthStyle)this._defaultStyle).setFont(new FontUIResource("Dialog", 0, 12));
    } 
    return this._defaultStyle;
  }
  
  private void getMatchingStyles(List paramList, JComponent paramJComponent, Region paramRegion) {
    String str1 = paramRegion.getLowerCaseName();
    String str2 = paramJComponent.getName();
    if (str2 == null)
      str2 = ""; 
    for (int i = this._styles.size() - 1; i >= 0; i--) {
      String str;
      StyleAssociation styleAssociation = (StyleAssociation)this._styles.get(i);
      if (styleAssociation.getID() == 0) {
        str = str2;
      } else {
        str = str1;
      } 
      if (styleAssociation.matches(str) && paramList.indexOf(styleAssociation.getStyle()) == -1)
        paramList.add(styleAssociation.getStyle()); 
    } 
  }
  
  private void cacheStyle(List paramList, SynthStyle paramSynthStyle) {
    BakedArrayList bakedArrayList = new BakedArrayList(paramList);
    this._resolvedStyles.put(bakedArrayList, paramSynthStyle);
  }
  
  private SynthStyle getCachedStyle(List paramList) { return (paramList.size() == 0) ? null : (SynthStyle)this._resolvedStyles.get(paramList); }
  
  private SynthStyle mergeStyles(List paramList) {
    int i = paramList.size();
    if (i == 0)
      return null; 
    if (i == 1)
      return (SynthStyle)((DefaultSynthStyle)paramList.get(0)).clone(); 
    DefaultSynthStyle defaultSynthStyle = (DefaultSynthStyle)paramList.get(i - 1);
    defaultSynthStyle = (DefaultSynthStyle)defaultSynthStyle.clone();
    for (int j = i - 2; j >= 0; j--)
      defaultSynthStyle = ((DefaultSynthStyle)paramList.get(j)).addTo(defaultSynthStyle); 
    return defaultSynthStyle;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\synth\DefaultSynthStyleFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */