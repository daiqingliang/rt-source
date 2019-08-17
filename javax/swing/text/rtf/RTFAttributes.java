package javax.swing.text.rtf;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

class RTFAttributes {
  static RTFAttribute[] attributes;
  
  static Dictionary<String, RTFAttribute> attributesByKeyword() {
    Hashtable hashtable = new Hashtable(attributes.length);
    for (RTFAttribute rTFAttribute : attributes)
      hashtable.put(rTFAttribute.rtfName(), rTFAttribute); 
    return hashtable;
  }
  
  static  {
    Vector vector = new Vector();
    byte b1 = 0;
    byte b2 = 1;
    byte b3 = 2;
    byte b4 = 3;
    byte b5 = 4;
    Boolean bool1;
    Boolean bool2 = (bool1 = Boolean.valueOf(true)).valueOf(false);
    vector.addElement(new BooleanAttribute(b1, StyleConstants.Italic, "i"));
    vector.addElement(new BooleanAttribute(b1, StyleConstants.Bold, "b"));
    vector.addElement(new BooleanAttribute(b1, StyleConstants.Underline, "ul"));
    vector.addElement(NumericAttribute.NewTwips(b2, StyleConstants.LeftIndent, "li", 0.0F, 0));
    vector.addElement(NumericAttribute.NewTwips(b2, StyleConstants.RightIndent, "ri", 0.0F, 0));
    vector.addElement(NumericAttribute.NewTwips(b2, StyleConstants.FirstLineIndent, "fi", 0.0F, 0));
    vector.addElement(new AssertiveAttribute(b2, StyleConstants.Alignment, "ql", 0));
    vector.addElement(new AssertiveAttribute(b2, StyleConstants.Alignment, "qr", 2));
    vector.addElement(new AssertiveAttribute(b2, StyleConstants.Alignment, "qc", 1));
    vector.addElement(new AssertiveAttribute(b2, StyleConstants.Alignment, "qj", 3));
    vector.addElement(NumericAttribute.NewTwips(b2, StyleConstants.SpaceAbove, "sa", 0));
    vector.addElement(NumericAttribute.NewTwips(b2, StyleConstants.SpaceBelow, "sb", 0));
    vector.addElement(new AssertiveAttribute(b5, "tab_alignment", "tqr", 1));
    vector.addElement(new AssertiveAttribute(b5, "tab_alignment", "tqc", 2));
    vector.addElement(new AssertiveAttribute(b5, "tab_alignment", "tqdec", 4));
    vector.addElement(new AssertiveAttribute(b5, "tab_leader", "tldot", 1));
    vector.addElement(new AssertiveAttribute(b5, "tab_leader", "tlhyph", 2));
    vector.addElement(new AssertiveAttribute(b5, "tab_leader", "tlul", 3));
    vector.addElement(new AssertiveAttribute(b5, "tab_leader", "tlth", 4));
    vector.addElement(new AssertiveAttribute(b5, "tab_leader", "tleq", 5));
    vector.addElement(new BooleanAttribute(b1, "caps", "caps"));
    vector.addElement(new BooleanAttribute(b1, "outl", "outl"));
    vector.addElement(new BooleanAttribute(b1, "scaps", "scaps"));
    vector.addElement(new BooleanAttribute(b1, "shad", "shad"));
    vector.addElement(new BooleanAttribute(b1, "v", "v"));
    vector.addElement(new BooleanAttribute(b1, "strike", "strike"));
    vector.addElement(new BooleanAttribute(b1, "deleted", "deleted"));
    vector.addElement(new AssertiveAttribute(b4, "saveformat", "defformat", "RTF"));
    vector.addElement(new AssertiveAttribute(b4, "landscape", "landscape"));
    vector.addElement(NumericAttribute.NewTwips(b4, "paperw", "paperw", 12240));
    vector.addElement(NumericAttribute.NewTwips(b4, "paperh", "paperh", 15840));
    vector.addElement(NumericAttribute.NewTwips(b4, "margl", "margl", 1800));
    vector.addElement(NumericAttribute.NewTwips(b4, "margr", "margr", 1800));
    vector.addElement(NumericAttribute.NewTwips(b4, "margt", "margt", 1440));
    vector.addElement(NumericAttribute.NewTwips(b4, "margb", "margb", 1440));
    vector.addElement(NumericAttribute.NewTwips(b4, "gutter", "gutter", 0));
    vector.addElement(new AssertiveAttribute(b2, "widowctrl", "nowidctlpar", bool2));
    vector.addElement(new AssertiveAttribute(b2, "widowctrl", "widctlpar", bool1));
    vector.addElement(new AssertiveAttribute(b4, "widowctrl", "widowctrl", bool1));
    RTFAttribute[] arrayOfRTFAttribute = new RTFAttribute[vector.size()];
    vector.copyInto(arrayOfRTFAttribute);
    attributes = arrayOfRTFAttribute;
  }
  
  static class AssertiveAttribute extends GenericAttribute implements RTFAttribute {
    Object swingValue = Boolean.valueOf(true);
    
    public AssertiveAttribute(int param1Int, Object param1Object, String param1String) { super(param1Int, param1Object, param1String); }
    
    public AssertiveAttribute(int param1Int, Object param1Object1, String param1String, Object param1Object2) { super(param1Int, param1Object1, param1String); }
    
    public AssertiveAttribute(int param1Int1, Object param1Object, String param1String, int param1Int2) { super(param1Int1, param1Object, param1String); }
    
    public boolean set(MutableAttributeSet param1MutableAttributeSet) {
      if (this.swingValue == null) {
        param1MutableAttributeSet.removeAttribute(this.swingName);
      } else {
        param1MutableAttributeSet.addAttribute(this.swingName, this.swingValue);
      } 
      return true;
    }
    
    public boolean set(MutableAttributeSet param1MutableAttributeSet, int param1Int) { return false; }
    
    public boolean setDefault(MutableAttributeSet param1MutableAttributeSet) {
      param1MutableAttributeSet.removeAttribute(this.swingName);
      return true;
    }
    
    public boolean writeValue(Object param1Object, RTFGenerator param1RTFGenerator, boolean param1Boolean) throws IOException {
      if (param1Object == null)
        return !param1Boolean; 
      if (param1Object.equals(this.swingValue)) {
        param1RTFGenerator.writeControlWord(this.rtfName);
        return true;
      } 
      return !param1Boolean;
    }
  }
  
  static class BooleanAttribute extends GenericAttribute implements RTFAttribute {
    boolean rtfDefault;
    
    boolean swingDefault;
    
    protected static final Boolean True;
    
    protected static final Boolean False = (True = Boolean.valueOf(true)).valueOf(false);
    
    public BooleanAttribute(int param1Int, Object param1Object, String param1String, boolean param1Boolean1, boolean param1Boolean2) {
      super(param1Int, param1Object, param1String);
      this.swingDefault = param1Boolean1;
      this.rtfDefault = param1Boolean2;
    }
    
    public BooleanAttribute(int param1Int, Object param1Object, String param1String) {
      super(param1Int, param1Object, param1String);
      this.swingDefault = false;
      this.rtfDefault = false;
    }
    
    public boolean set(MutableAttributeSet param1MutableAttributeSet) {
      param1MutableAttributeSet.addAttribute(this.swingName, True);
      return true;
    }
    
    public boolean set(MutableAttributeSet param1MutableAttributeSet, int param1Int) {
      Boolean bool = (param1Int != 0) ? True : False;
      param1MutableAttributeSet.addAttribute(this.swingName, bool);
      return true;
    }
    
    public boolean setDefault(MutableAttributeSet param1MutableAttributeSet) {
      if (this.swingDefault != this.rtfDefault || param1MutableAttributeSet.getAttribute(this.swingName) != null)
        param1MutableAttributeSet.addAttribute(this.swingName, Boolean.valueOf(this.rtfDefault)); 
      return true;
    }
    
    public boolean writeValue(Object param1Object, RTFGenerator param1RTFGenerator, boolean param1Boolean) throws IOException {
      Boolean bool;
      if (param1Object == null) {
        bool = Boolean.valueOf(this.swingDefault);
      } else {
        bool = (Boolean)param1Object;
      } 
      if (param1Boolean || bool.booleanValue() != this.rtfDefault)
        if (bool.booleanValue()) {
          param1RTFGenerator.writeControlWord(this.rtfName);
        } else {
          param1RTFGenerator.writeControlWord(this.rtfName, 0);
        }  
      return true;
    }
  }
  
  static abstract class GenericAttribute {
    int domain;
    
    Object swingName;
    
    String rtfName;
    
    protected GenericAttribute(int param1Int, Object param1Object, String param1String) {
      this.domain = param1Int;
      this.swingName = param1Object;
      this.rtfName = param1String;
    }
    
    public int domain() { return this.domain; }
    
    public Object swingName() { return this.swingName; }
    
    public String rtfName() { return this.rtfName; }
    
    abstract boolean set(MutableAttributeSet param1MutableAttributeSet);
    
    abstract boolean set(MutableAttributeSet param1MutableAttributeSet, int param1Int);
    
    abstract boolean setDefault(MutableAttributeSet param1MutableAttributeSet);
    
    public boolean write(AttributeSet param1AttributeSet, RTFGenerator param1RTFGenerator, boolean param1Boolean) throws IOException { return writeValue(param1AttributeSet.getAttribute(this.swingName), param1RTFGenerator, param1Boolean); }
    
    public boolean writeValue(Object param1Object, RTFGenerator param1RTFGenerator, boolean param1Boolean) throws IOException { return false; }
  }
  
  static class NumericAttribute extends GenericAttribute implements RTFAttribute {
    int rtfDefault;
    
    Number swingDefault;
    
    float scale;
    
    protected NumericAttribute(int param1Int, Object param1Object, String param1String) {
      super(param1Int, param1Object, param1String);
      this.rtfDefault = 0;
      this.swingDefault = null;
      this.scale = 1.0F;
    }
    
    public NumericAttribute(int param1Int1, Object param1Object, String param1String, int param1Int2, int param1Int3) { this(param1Int1, param1Object, param1String, Integer.valueOf(param1Int2), param1Int3, 1.0F); }
    
    public NumericAttribute(int param1Int1, Object param1Object, String param1String, Number param1Number, int param1Int2, float param1Float) {
      super(param1Int1, param1Object, param1String);
      this.swingDefault = param1Number;
      this.rtfDefault = param1Int2;
      this.scale = param1Float;
    }
    
    public static NumericAttribute NewTwips(int param1Int1, Object param1Object, String param1String, float param1Float, int param1Int2) { return new NumericAttribute(param1Int1, param1Object, param1String, new Float(param1Float), param1Int2, 20.0F); }
    
    public static NumericAttribute NewTwips(int param1Int1, Object param1Object, String param1String, int param1Int2) { return new NumericAttribute(param1Int1, param1Object, param1String, null, param1Int2, 20.0F); }
    
    public boolean set(MutableAttributeSet param1MutableAttributeSet) { return false; }
    
    public boolean set(MutableAttributeSet param1MutableAttributeSet, int param1Int) {
      Float float;
      if (this.scale == 1.0F) {
        float = Integer.valueOf(param1Int);
      } else {
        float = new Float(param1Int / this.scale);
      } 
      param1MutableAttributeSet.addAttribute(this.swingName, float);
      return true;
    }
    
    public boolean setDefault(MutableAttributeSet param1MutableAttributeSet) {
      Number number = (Number)param1MutableAttributeSet.getAttribute(this.swingName);
      if (number == null)
        number = this.swingDefault; 
      if (number != null && ((this.scale == 1.0F && number.intValue() == this.rtfDefault) || Math.round(number.floatValue() * this.scale) == this.rtfDefault))
        return true; 
      set(param1MutableAttributeSet, this.rtfDefault);
      return true;
    }
    
    public boolean writeValue(Object param1Object, RTFGenerator param1RTFGenerator, boolean param1Boolean) throws IOException {
      Number number = (Number)param1Object;
      if (number == null)
        number = this.swingDefault; 
      if (number == null)
        return true; 
      int i = Math.round(number.floatValue() * this.scale);
      if (param1Boolean || i != this.rtfDefault)
        param1RTFGenerator.writeControlWord(this.rtfName, i); 
      return true;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\RTFAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */