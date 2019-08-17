package sun.swing.plaf.synth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.plaf.synth.SynthStyle;

public class StyleAssociation {
  private SynthStyle _style;
  
  private Pattern _pattern;
  
  private Matcher _matcher;
  
  private int _id;
  
  public static StyleAssociation createStyleAssociation(String paramString, SynthStyle paramSynthStyle) throws PatternSyntaxException { return createStyleAssociation(paramString, paramSynthStyle, 0); }
  
  public static StyleAssociation createStyleAssociation(String paramString, SynthStyle paramSynthStyle, int paramInt) throws PatternSyntaxException { return new StyleAssociation(paramString, paramSynthStyle, paramInt); }
  
  private StyleAssociation(String paramString, SynthStyle paramSynthStyle, int paramInt) throws PatternSyntaxException {
    this._style = paramSynthStyle;
    this._pattern = Pattern.compile(paramString);
    this._id = paramInt;
  }
  
  public int getID() { return this._id; }
  
  public boolean matches(CharSequence paramCharSequence) {
    if (this._matcher == null) {
      this._matcher = this._pattern.matcher(paramCharSequence);
    } else {
      this._matcher.reset(paramCharSequence);
    } 
    return this._matcher.matches();
  }
  
  public String getText() { return this._pattern.pattern(); }
  
  public SynthStyle getStyle() { return this._style; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\plaf\synth\StyleAssociation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */