package com.sun.beans.editors;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public class FontEditor extends Panel implements PropertyEditor {
  private static final long serialVersionUID = 6732704486002715933L;
  
  private Font font;
  
  private Toolkit toolkit;
  
  private String sampleText = "Abcde...";
  
  private Label sample;
  
  private Choice familyChoser;
  
  private Choice styleChoser;
  
  private Choice sizeChoser;
  
  private String[] fonts;
  
  private String[] styleNames = { "plain", "bold", "italic" };
  
  private int[] styles = { 0, 1, 2 };
  
  private int[] pointSizes = { 3, 5, 8, 10, 12, 14, 18, 24, 36, 48 };
  
  private PropertyChangeSupport support = new PropertyChangeSupport(this);
  
  public FontEditor() {
    setLayout(null);
    this.toolkit = Toolkit.getDefaultToolkit();
    this.fonts = this.toolkit.getFontList();
    this.familyChoser = new Choice();
    byte b;
    for (b = 0; b < this.fonts.length; b++)
      this.familyChoser.addItem(this.fonts[b]); 
    add(this.familyChoser);
    this.familyChoser.reshape(20, 5, 100, 30);
    this.styleChoser = new Choice();
    for (b = 0; b < this.styleNames.length; b++)
      this.styleChoser.addItem(this.styleNames[b]); 
    add(this.styleChoser);
    this.styleChoser.reshape(145, 5, 70, 30);
    this.sizeChoser = new Choice();
    for (b = 0; b < this.pointSizes.length; b++)
      this.sizeChoser.addItem("" + this.pointSizes[b]); 
    add(this.sizeChoser);
    this.sizeChoser.reshape(220, 5, 70, 30);
    resize(300, 40);
  }
  
  public Dimension preferredSize() { return new Dimension(300, 40); }
  
  public void setValue(Object paramObject) {
    this.font = (Font)paramObject;
    if (this.font == null)
      return; 
    changeFont(this.font);
    byte b;
    for (b = 0; b < this.fonts.length; b++) {
      if (this.fonts[b].equals(this.font.getFamily())) {
        this.familyChoser.select(b);
        break;
      } 
    } 
    for (b = 0; b < this.styleNames.length; b++) {
      if (this.font.getStyle() == this.styles[b]) {
        this.styleChoser.select(b);
        break;
      } 
    } 
    for (b = 0; b < this.pointSizes.length; b++) {
      if (this.font.getSize() <= this.pointSizes[b]) {
        this.sizeChoser.select(b);
        break;
      } 
    } 
  }
  
  private void changeFont(Font paramFont) {
    this.font = paramFont;
    if (this.sample != null)
      remove(this.sample); 
    this.sample = new Label(this.sampleText);
    this.sample.setFont(this.font);
    add(this.sample);
    Container container = getParent();
    if (container != null) {
      container.invalidate();
      container.layout();
    } 
    invalidate();
    layout();
    repaint();
    this.support.firePropertyChange("", null, null);
  }
  
  public Object getValue() { return this.font; }
  
  public String getJavaInitializationString() { return (this.font == null) ? "null" : ("new java.awt.Font(\"" + this.font.getName() + "\", " + this.font.getStyle() + ", " + this.font.getSize() + ")"); }
  
  public boolean action(Event paramEvent, Object paramObject) {
    String str = this.familyChoser.getSelectedItem();
    int i = this.styles[this.styleChoser.getSelectedIndex()];
    int j = this.pointSizes[this.sizeChoser.getSelectedIndex()];
    try {
      Font font1 = new Font(str, i, j);
      changeFont(font1);
    } catch (Exception exception) {
      System.err.println("Couldn't create font " + str + "-" + this.styleNames[i] + "-" + j);
    } 
    return false;
  }
  
  public boolean isPaintable() { return true; }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {
    Font font1 = paramGraphics.getFont();
    paramGraphics.setFont(this.font);
    FontMetrics fontMetrics = paramGraphics.getFontMetrics();
    int i = (paramRectangle.height - fontMetrics.getAscent()) / 2;
    paramGraphics.drawString(this.sampleText, 0, paramRectangle.height - i);
    paramGraphics.setFont(font1);
  }
  
  public String getAsText() {
    if (this.font == null)
      return null; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.font.getName());
    stringBuilder.append(' ');
    boolean bool1 = this.font.isBold();
    if (bool1)
      stringBuilder.append("BOLD"); 
    boolean bool2 = this.font.isItalic();
    if (bool2)
      stringBuilder.append("ITALIC"); 
    if (bool1 || bool2)
      stringBuilder.append(' '); 
    stringBuilder.append(this.font.getSize());
    return stringBuilder.toString();
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException { setValue((paramString == null) ? null : Font.decode(paramString)); }
  
  public String[] getTags() { return null; }
  
  public Component getCustomEditor() { return this; }
  
  public boolean supportsCustomEditor() { return true; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.support.addPropertyChangeListener(paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.support.removePropertyChangeListener(paramPropertyChangeListener); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\FontEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */