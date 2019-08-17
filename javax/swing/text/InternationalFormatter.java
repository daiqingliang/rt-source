package javax.swing.text;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.AttributedCharacterIterator;
import java.text.Format;
import java.text.Format.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFormattedTextField;

public class InternationalFormatter extends DefaultFormatter {
  private static final Format.Field[] EMPTY_FIELD_ARRAY = new Format.Field[0];
  
  private Format format;
  
  private Comparable max;
  
  private Comparable min;
  
  private BitSet literalMask;
  
  private AttributedCharacterIterator iterator;
  
  private boolean validMask;
  
  private String string;
  
  private boolean ignoreDocumentMutate;
  
  public InternationalFormatter() { setOverwriteMode(false); }
  
  public InternationalFormatter(Format paramFormat) {
    this();
    setFormat(paramFormat);
  }
  
  public void setFormat(Format paramFormat) { this.format = paramFormat; }
  
  public Format getFormat() { return this.format; }
  
  public void setMinimum(Comparable paramComparable) {
    if (getValueClass() == null && paramComparable != null)
      setValueClass(paramComparable.getClass()); 
    this.min = paramComparable;
  }
  
  public Comparable getMinimum() { return this.min; }
  
  public void setMaximum(Comparable paramComparable) {
    if (getValueClass() == null && paramComparable != null)
      setValueClass(paramComparable.getClass()); 
    this.max = paramComparable;
  }
  
  public Comparable getMaximum() { return this.max; }
  
  public void install(JFormattedTextField paramJFormattedTextField) {
    super.install(paramJFormattedTextField);
    updateMaskIfNecessary();
    positionCursorAtInitialLocation();
  }
  
  public String valueToString(Object paramObject) throws ParseException {
    if (paramObject == null)
      return ""; 
    Format format1 = getFormat();
    return (format1 == null) ? paramObject.toString() : format1.format(paramObject);
  }
  
  public Object stringToValue(String paramString) throws ParseException {
    Object object = stringToValue(paramString, getFormat());
    if (object != null && getValueClass() != null && !getValueClass().isInstance(object))
      object = super.stringToValue(object.toString()); 
    try {
      if (!isValidValue(object, true))
        throw new ParseException("Value not within min/max range", 0); 
    } catch (ClassCastException classCastException) {
      throw new ParseException("Class cast exception comparing values: " + classCastException, 0);
    } 
    return object;
  }
  
  public Format.Field[] getFields(int paramInt) {
    if (getAllowsInvalid())
      updateMask(); 
    Map map = getAttributes(paramInt);
    if (map != null && map.size() > 0) {
      ArrayList arrayList = new ArrayList();
      arrayList.addAll(map.keySet());
      return (Field[])arrayList.toArray(EMPTY_FIELD_ARRAY);
    } 
    return EMPTY_FIELD_ARRAY;
  }
  
  public Object clone() throws CloneNotSupportedException {
    InternationalFormatter internationalFormatter = (InternationalFormatter)super.clone();
    internationalFormatter.literalMask = null;
    internationalFormatter.iterator = null;
    internationalFormatter.validMask = false;
    internationalFormatter.string = null;
    return internationalFormatter;
  }
  
  protected Action[] getActions() { return getSupportsIncrement() ? new Action[] { new IncrementAction("increment", 1), new IncrementAction("decrement", -1) } : null; }
  
  Object stringToValue(String paramString, Format paramFormat) throws ParseException { return (paramFormat == null) ? paramString : paramFormat.parseObject(paramString); }
  
  boolean isValidValue(Object paramObject, boolean paramBoolean) {
    Comparable comparable1 = getMinimum();
    try {
      if (comparable1 != null && comparable1.compareTo(paramObject) > 0)
        return false; 
    } catch (ClassCastException classCastException) {
      if (paramBoolean)
        throw classCastException; 
      return false;
    } 
    Comparable comparable2 = getMaximum();
    try {
      if (comparable2 != null && comparable2.compareTo(paramObject) < 0)
        return false; 
    } catch (ClassCastException classCastException) {
      if (paramBoolean)
        throw classCastException; 
      return false;
    } 
    return true;
  }
  
  Map<AttributedCharacterIterator.Attribute, Object> getAttributes(int paramInt) {
    if (isValidMask()) {
      AttributedCharacterIterator attributedCharacterIterator = getIterator();
      if (paramInt >= 0 && paramInt <= attributedCharacterIterator.getEndIndex()) {
        attributedCharacterIterator.setIndex(paramInt);
        return attributedCharacterIterator.getAttributes();
      } 
    } 
    return null;
  }
  
  int getAttributeStart(AttributedCharacterIterator.Attribute paramAttribute) {
    if (isValidMask()) {
      AttributedCharacterIterator attributedCharacterIterator = getIterator();
      attributedCharacterIterator.first();
      while (attributedCharacterIterator.current() != Character.MAX_VALUE) {
        if (attributedCharacterIterator.getAttribute(paramAttribute) != null)
          return attributedCharacterIterator.getIndex(); 
        attributedCharacterIterator.next();
      } 
    } 
    return -1;
  }
  
  AttributedCharacterIterator getIterator() { return this.iterator; }
  
  void updateMaskIfNecessary() {
    if (!getAllowsInvalid() && getFormat() != null)
      if (!isValidMask()) {
        updateMask();
      } else {
        String str = getFormattedTextField().getText();
        if (!str.equals(this.string))
          updateMask(); 
      }  
  }
  
  void updateMask() {
    if (getFormat() != null) {
      Document document = getFormattedTextField().getDocument();
      this.validMask = false;
      if (document != null) {
        try {
          this.string = document.getText(0, document.getLength());
        } catch (BadLocationException badLocationException) {
          this.string = null;
        } 
        if (this.string != null)
          try {
            Object object = stringToValue(this.string);
            AttributedCharacterIterator attributedCharacterIterator = getFormat().formatToCharacterIterator(object);
            updateMask(attributedCharacterIterator);
          } catch (ParseException parseException) {
          
          } catch (IllegalArgumentException illegalArgumentException) {
          
          } catch (NullPointerException nullPointerException) {} 
      } 
    } 
  }
  
  int getLiteralCountTo(int paramInt) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramInt; b2++) {
      if (isLiteral(b2))
        b1++; 
    } 
    return b1;
  }
  
  boolean isLiteral(int paramInt) { return (isValidMask() && paramInt < this.string.length()) ? this.literalMask.get(paramInt) : 0; }
  
  char getLiteral(int paramInt) { return (isValidMask() && this.string != null && paramInt < this.string.length()) ? this.string.charAt(paramInt) : 0; }
  
  boolean isNavigatable(int paramInt) { return !isLiteral(paramInt); }
  
  void updateValue(Object paramObject) {
    super.updateValue(paramObject);
    updateMaskIfNecessary();
  }
  
  void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet) throws BadLocationException {
    if (this.ignoreDocumentMutate) {
      paramFilterBypass.replace(paramInt1, paramInt2, paramString, paramAttributeSet);
      return;
    } 
    super.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  private int getNextNonliteralIndex(int paramInt1, int paramInt2) {
    int i = getFormattedTextField().getDocument().getLength();
    while (paramInt1 >= 0 && paramInt1 < i) {
      if (isLiteral(paramInt1)) {
        paramInt1 += paramInt2;
        continue;
      } 
      return paramInt1;
    } 
    return (paramInt2 == -1) ? 0 : i;
  }
  
  boolean canReplace(DefaultFormatter.ReplaceHolder paramReplaceHolder) {
    if (!getAllowsInvalid()) {
      String str = paramReplaceHolder.text;
      int i = (str != null) ? str.length() : 0;
      JFormattedTextField jFormattedTextField = getFormattedTextField();
      if (i == 0 && paramReplaceHolder.length == 1 && jFormattedTextField.getSelectionStart() != paramReplaceHolder.offset) {
        paramReplaceHolder.offset = getNextNonliteralIndex(paramReplaceHolder.offset, -1);
      } else if (getOverwriteMode()) {
        int j = paramReplaceHolder.offset;
        int k = j;
        boolean bool1 = false;
        for (byte b = 0; b < paramReplaceHolder.length; b++) {
          while (isLiteral(j))
            j++; 
          if (j >= this.string.length()) {
            j = k;
            bool1 = true;
            break;
          } 
          k = ++j;
        } 
        if (bool1 || jFormattedTextField.getSelectedText() == null)
          paramReplaceHolder.length = j - paramReplaceHolder.offset; 
      } else if (i > 0) {
        paramReplaceHolder.offset = getNextNonliteralIndex(paramReplaceHolder.offset, 1);
      } else {
        paramReplaceHolder.offset = getNextNonliteralIndex(paramReplaceHolder.offset, -1);
      } 
      ((ExtendedReplaceHolder)paramReplaceHolder).endOffset = paramReplaceHolder.offset;
      ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength = (paramReplaceHolder.text != null) ? paramReplaceHolder.text.length() : 0;
    } else {
      ((ExtendedReplaceHolder)paramReplaceHolder).endOffset = paramReplaceHolder.offset;
      ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength = (paramReplaceHolder.text != null) ? paramReplaceHolder.text.length() : 0;
    } 
    boolean bool = super.canReplace(paramReplaceHolder);
    if (bool && !getAllowsInvalid())
      ((ExtendedReplaceHolder)paramReplaceHolder).resetFromValue(this); 
    return bool;
  }
  
  boolean replace(DefaultFormatter.ReplaceHolder paramReplaceHolder) {
    int i = -1;
    byte b = 1;
    int j = -1;
    if (paramReplaceHolder.length > 0 && (paramReplaceHolder.text == null || paramReplaceHolder.text.length() == 0) && (getFormattedTextField().getSelectionStart() != paramReplaceHolder.offset || paramReplaceHolder.length > 1))
      b = -1; 
    if (!getAllowsInvalid()) {
      if ((paramReplaceHolder.text == null || paramReplaceHolder.text.length() == 0) && paramReplaceHolder.length > 0) {
        i = getFormattedTextField().getSelectionStart();
      } else {
        i = paramReplaceHolder.offset;
      } 
      j = getLiteralCountTo(i);
    } 
    if (super.replace(paramReplaceHolder)) {
      if (i != -1) {
        int k = ((ExtendedReplaceHolder)paramReplaceHolder).endOffset;
        k += ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength;
        repositionCursor(j, k, b);
      } else {
        i = ((ExtendedReplaceHolder)paramReplaceHolder).endOffset;
        if (b == 1)
          i += ((ExtendedReplaceHolder)paramReplaceHolder).endTextLength; 
        repositionCursor(i, b);
      } 
      return true;
    } 
    return false;
  }
  
  private void repositionCursor(int paramInt1, int paramInt2, int paramInt3) {
    int i = getLiteralCountTo(paramInt2);
    if (i != paramInt2) {
      paramInt2 -= paramInt1;
      for (byte b = 0; b < paramInt2; b++) {
        if (isLiteral(b))
          paramInt2++; 
      } 
    } 
    repositionCursor(paramInt2, 1);
  }
  
  char getBufferedChar(int paramInt) { return (isValidMask() && this.string != null && paramInt < this.string.length()) ? this.string.charAt(paramInt) : 0; }
  
  boolean isValidMask() { return this.validMask; }
  
  boolean isLiteral(Map paramMap) { return (paramMap == null || paramMap.size() == 0); }
  
  private void updateMask(AttributedCharacterIterator paramAttributedCharacterIterator) {
    if (paramAttributedCharacterIterator != null) {
      this.validMask = true;
      this.iterator = paramAttributedCharacterIterator;
      if (this.literalMask == null) {
        this.literalMask = new BitSet();
      } else {
        for (int i = this.literalMask.length() - 1; i >= 0; i--)
          this.literalMask.clear(i); 
      } 
      paramAttributedCharacterIterator.first();
      while (paramAttributedCharacterIterator.current() != Character.MAX_VALUE) {
        Map map = paramAttributedCharacterIterator.getAttributes();
        boolean bool = isLiteral(map);
        int i = paramAttributedCharacterIterator.getIndex();
        int j = paramAttributedCharacterIterator.getRunLimit();
        while (i < j) {
          if (bool) {
            this.literalMask.set(i);
          } else {
            this.literalMask.clear(i);
          } 
          i++;
        } 
        paramAttributedCharacterIterator.setIndex(i);
      } 
    } 
  }
  
  boolean canIncrement(Object paramObject, int paramInt) { return (paramObject != null); }
  
  void selectField(Object paramObject, int paramInt) {
    AttributedCharacterIterator attributedCharacterIterator = getIterator();
    if (attributedCharacterIterator != null && paramObject instanceof AttributedCharacterIterator.Attribute) {
      AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)paramObject;
      attributedCharacterIterator.first();
      while (attributedCharacterIterator.current() != Character.MAX_VALUE) {
        while (attributedCharacterIterator.getAttribute(attribute) == null && attributedCharacterIterator.next() != Character.MAX_VALUE);
        if (attributedCharacterIterator.current() != Character.MAX_VALUE) {
          int i = attributedCharacterIterator.getRunLimit(attribute);
          if (--paramInt <= 0) {
            getFormattedTextField().select(attributedCharacterIterator.getIndex(), i);
            break;
          } 
          attributedCharacterIterator.setIndex(i);
          attributedCharacterIterator.next();
        } 
      } 
    } 
  }
  
  Object getAdjustField(int paramInt, Map paramMap) { return null; }
  
  private int getFieldTypeCountTo(Object paramObject, int paramInt) {
    AttributedCharacterIterator attributedCharacterIterator = getIterator();
    byte b = 0;
    if (attributedCharacterIterator != null && paramObject instanceof AttributedCharacterIterator.Attribute) {
      AttributedCharacterIterator.Attribute attribute = (AttributedCharacterIterator.Attribute)paramObject;
      attributedCharacterIterator.first();
      while (attributedCharacterIterator.getIndex() < paramInt) {
        while (attributedCharacterIterator.getAttribute(attribute) == null && attributedCharacterIterator.next() != Character.MAX_VALUE);
        if (attributedCharacterIterator.current() != Character.MAX_VALUE) {
          attributedCharacterIterator.setIndex(attributedCharacterIterator.getRunLimit(attribute));
          attributedCharacterIterator.next();
          b++;
        } 
      } 
    } 
    return b;
  }
  
  Object adjustValue(Object paramObject1, Map paramMap, Object paramObject2, int paramInt) throws BadLocationException, ParseException { return null; }
  
  boolean getSupportsIncrement() { return false; }
  
  void resetValue(Object paramObject) {
    Document document = getFormattedTextField().getDocument();
    String str = valueToString(paramObject);
    try {
      this.ignoreDocumentMutate = true;
      document.remove(0, document.getLength());
      document.insertString(0, str, null);
    } finally {
      this.ignoreDocumentMutate = false;
    } 
    updateValue(paramObject);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    updateMaskIfNecessary();
  }
  
  DefaultFormatter.ReplaceHolder getReplaceHolder(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet) {
    if (this.replaceHolder == null)
      this.replaceHolder = new ExtendedReplaceHolder(); 
    return super.getReplaceHolder(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  static class ExtendedReplaceHolder extends DefaultFormatter.ReplaceHolder {
    int endOffset;
    
    int endTextLength;
    
    void resetFromValue(InternationalFormatter param1InternationalFormatter) {
      this.offset = 0;
      try {
        this.text = param1InternationalFormatter.valueToString(this.value);
      } catch (ParseException parseException) {
        this.text = "";
      } 
      this.length = this.fb.getDocument().getLength();
    }
  }
  
  private class IncrementAction extends AbstractAction {
    private int direction;
    
    IncrementAction(String param1String, int param1Int) {
      super(param1String);
      this.direction = param1Int;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      if (InternationalFormatter.this.getFormattedTextField().isEditable()) {
        if (InternationalFormatter.this.getAllowsInvalid())
          InternationalFormatter.this.updateMask(); 
        boolean bool = false;
        if (InternationalFormatter.this.isValidMask()) {
          int i = InternationalFormatter.this.getFormattedTextField().getSelectionStart();
          if (i != -1) {
            AttributedCharacterIterator attributedCharacterIterator = InternationalFormatter.this.getIterator();
            attributedCharacterIterator.setIndex(i);
            Map map = attributedCharacterIterator.getAttributes();
            Object object = InternationalFormatter.this.getAdjustField(i, map);
            if (InternationalFormatter.this.canIncrement(object, i))
              try {
                Object object1 = InternationalFormatter.this.stringToValue(InternationalFormatter.this.getFormattedTextField().getText());
                int j = InternationalFormatter.this.getFieldTypeCountTo(object, i);
                object1 = InternationalFormatter.this.adjustValue(object1, map, object, this.direction);
                if (object1 != null && InternationalFormatter.this.isValidValue(object1, false)) {
                  InternationalFormatter.this.resetValue(object1);
                  InternationalFormatter.this.updateMask();
                  if (InternationalFormatter.this.isValidMask())
                    InternationalFormatter.this.selectField(object, j); 
                  bool = true;
                } 
              } catch (ParseException parseException) {
              
              } catch (BadLocationException badLocationException) {} 
          } 
        } 
        if (!bool)
          InternationalFormatter.this.invalidEdit(); 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\InternationalFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */