package java.awt.font;

import java.io.InvalidObjectException;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import java.util.Map;

public final class TextAttribute extends AttributedCharacterIterator.Attribute {
  private static final Map<String, TextAttribute> instanceMap = new HashMap(29);
  
  static final long serialVersionUID = 7744112784117861702L;
  
  public static final TextAttribute FAMILY = new TextAttribute("family");
  
  public static final TextAttribute WEIGHT = new TextAttribute("weight");
  
  public static final Float WEIGHT_EXTRA_LIGHT;
  
  public static final Float WEIGHT_LIGHT;
  
  public static final Float WEIGHT_DEMILIGHT;
  
  public static final Float WEIGHT_REGULAR;
  
  public static final Float WEIGHT_SEMIBOLD;
  
  public static final Float WEIGHT_MEDIUM;
  
  public static final Float WEIGHT_DEMIBOLD;
  
  public static final Float WEIGHT_BOLD;
  
  public static final Float WEIGHT_HEAVY;
  
  public static final Float WEIGHT_EXTRABOLD;
  
  public static final Float WEIGHT_ULTRABOLD = (WEIGHT_EXTRABOLD = (WEIGHT_HEAVY = (WEIGHT_BOLD = (WEIGHT_DEMIBOLD = (WEIGHT_MEDIUM = (WEIGHT_SEMIBOLD = (WEIGHT_REGULAR = (WEIGHT_DEMILIGHT = (WEIGHT_LIGHT = (WEIGHT_EXTRA_LIGHT = Float.valueOf(0.5F)).valueOf(0.75F)).valueOf(0.875F)).valueOf(1.0F)).valueOf(1.25F)).valueOf(1.5F)).valueOf(1.75F)).valueOf(2.0F)).valueOf(2.25F)).valueOf(2.5F)).valueOf(2.75F);
  
  public static final TextAttribute WIDTH = new TextAttribute("width");
  
  public static final Float WIDTH_CONDENSED;
  
  public static final Float WIDTH_SEMI_CONDENSED;
  
  public static final Float WIDTH_REGULAR;
  
  public static final Float WIDTH_SEMI_EXTENDED;
  
  public static final Float WIDTH_EXTENDED = (WIDTH_SEMI_EXTENDED = (WIDTH_REGULAR = (WIDTH_SEMI_CONDENSED = (WIDTH_CONDENSED = Float.valueOf(0.75F)).valueOf(0.875F)).valueOf(1.0F)).valueOf(1.25F)).valueOf(1.5F);
  
  public static final TextAttribute POSTURE = new TextAttribute("posture");
  
  public static final Float POSTURE_REGULAR;
  
  public static final Float POSTURE_OBLIQUE = (POSTURE_REGULAR = Float.valueOf(0.0F)).valueOf(0.2F);
  
  public static final TextAttribute SIZE = new TextAttribute("size");
  
  public static final TextAttribute TRANSFORM = new TextAttribute("transform");
  
  public static final TextAttribute SUPERSCRIPT = new TextAttribute("superscript");
  
  public static final Integer SUPERSCRIPT_SUPER;
  
  public static final Integer SUPERSCRIPT_SUB = (SUPERSCRIPT_SUPER = Integer.valueOf(1)).valueOf(-1);
  
  public static final TextAttribute FONT = new TextAttribute("font");
  
  public static final TextAttribute CHAR_REPLACEMENT = new TextAttribute("char_replacement");
  
  public static final TextAttribute FOREGROUND = new TextAttribute("foreground");
  
  public static final TextAttribute BACKGROUND = new TextAttribute("background");
  
  public static final TextAttribute UNDERLINE = new TextAttribute("underline");
  
  public static final Integer UNDERLINE_ON = Integer.valueOf(0);
  
  public static final TextAttribute STRIKETHROUGH = new TextAttribute("strikethrough");
  
  public static final Boolean STRIKETHROUGH_ON = Boolean.TRUE;
  
  public static final TextAttribute RUN_DIRECTION = new TextAttribute("run_direction");
  
  public static final Boolean RUN_DIRECTION_LTR = Boolean.FALSE;
  
  public static final Boolean RUN_DIRECTION_RTL = Boolean.TRUE;
  
  public static final TextAttribute BIDI_EMBEDDING = new TextAttribute("bidi_embedding");
  
  public static final TextAttribute JUSTIFICATION = new TextAttribute("justification");
  
  public static final Float JUSTIFICATION_FULL;
  
  public static final Float JUSTIFICATION_NONE = (JUSTIFICATION_FULL = Float.valueOf(1.0F)).valueOf(0.0F);
  
  public static final TextAttribute INPUT_METHOD_HIGHLIGHT = new TextAttribute("input method highlight");
  
  public static final TextAttribute INPUT_METHOD_UNDERLINE = new TextAttribute("input method underline");
  
  public static final Integer UNDERLINE_LOW_ONE_PIXEL;
  
  public static final Integer UNDERLINE_LOW_TWO_PIXEL;
  
  public static final Integer UNDERLINE_LOW_DOTTED;
  
  public static final Integer UNDERLINE_LOW_GRAY;
  
  public static final Integer UNDERLINE_LOW_DASHED = (UNDERLINE_LOW_GRAY = (UNDERLINE_LOW_DOTTED = (UNDERLINE_LOW_TWO_PIXEL = (UNDERLINE_LOW_ONE_PIXEL = Integer.valueOf(1)).valueOf(2)).valueOf(3)).valueOf(4)).valueOf(5);
  
  public static final TextAttribute SWAP_COLORS = new TextAttribute("swap_colors");
  
  public static final Boolean SWAP_COLORS_ON = Boolean.TRUE;
  
  public static final TextAttribute NUMERIC_SHAPING = new TextAttribute("numeric_shaping");
  
  public static final TextAttribute KERNING = new TextAttribute("kerning");
  
  public static final Integer KERNING_ON = Integer.valueOf(1);
  
  public static final TextAttribute LIGATURES = new TextAttribute("ligatures");
  
  public static final Integer LIGATURES_ON = Integer.valueOf(1);
  
  public static final TextAttribute TRACKING = new TextAttribute("tracking");
  
  public static final Float TRACKING_TIGHT;
  
  public static final Float TRACKING_LOOSE = (TRACKING_TIGHT = Float.valueOf(-0.04F)).valueOf(0.04F);
  
  protected TextAttribute(String paramString) {
    super(paramString);
    if (getClass() == TextAttribute.class)
      instanceMap.put(paramString, this); 
  }
  
  protected Object readResolve() throws InvalidObjectException {
    if (getClass() != TextAttribute.class)
      throw new InvalidObjectException("subclass didn't correctly implement readResolve"); 
    TextAttribute textAttribute = (TextAttribute)instanceMap.get(getName());
    if (textAttribute != null)
      return textAttribute; 
    throw new InvalidObjectException("unknown attribute name");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TextAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */