package com.sun.beans.editors;

import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

public class ColorEditor extends Panel implements PropertyEditor {
  private static final long serialVersionUID = 1781257185164716054L;
  
  private String[] colorNames = { 
      " ", "white", "lightGray", "gray", "darkGray", "black", "red", "pink", "orange", "yellow", 
      "green", "magenta", "cyan", "blue" };
  
  private Color[] colors = { 
      null, Color.white, Color.lightGray, Color.gray, Color.darkGray, Color.black, Color.red, Color.pink, Color.orange, Color.yellow, 
      Color.green, Color.magenta, Color.cyan, Color.blue };
  
  private Canvas sample;
  
  private int sampleHeight = 20;
  
  private int sampleWidth = 40;
  
  private int hPad = 5;
  
  private int ourWidth;
  
  private Color color;
  
  private TextField text;
  
  private Choice choser;
  
  private PropertyChangeSupport support = new PropertyChangeSupport(this);
  
  public ColorEditor() {
    setLayout(null);
    this.ourWidth = this.hPad;
    Panel panel = new Panel();
    panel.setLayout(null);
    panel.setBackground(Color.black);
    this.sample = new Canvas();
    panel.add(this.sample);
    this.sample.reshape(2, 2, this.sampleWidth, this.sampleHeight);
    add(panel);
    panel.reshape(this.ourWidth, 2, this.sampleWidth + 4, this.sampleHeight + 4);
    this.ourWidth += this.sampleWidth + 4 + this.hPad;
    this.text = new TextField("", 14);
    add(this.text);
    this.text.reshape(this.ourWidth, 0, 100, 30);
    this.ourWidth += 100 + this.hPad;
    this.choser = new Choice();
    boolean bool = false;
    for (byte b = 0; b < this.colorNames.length; b++)
      this.choser.addItem(this.colorNames[b]); 
    add(this.choser);
    this.choser.reshape(this.ourWidth, 0, 100, 30);
    this.ourWidth += 100 + this.hPad;
    resize(this.ourWidth, 40);
  }
  
  public void setValue(Object paramObject) {
    Color color1 = (Color)paramObject;
    changeColor(color1);
  }
  
  public Dimension preferredSize() { return new Dimension(this.ourWidth, 40); }
  
  public boolean keyUp(Event paramEvent, int paramInt) {
    if (paramEvent.target == this.text)
      try {
        setAsText(this.text.getText());
      } catch (IllegalArgumentException illegalArgumentException) {} 
    return false;
  }
  
  public void setAsText(String paramString) throws IllegalArgumentException {
    if (paramString == null) {
      changeColor(null);
      return;
    } 
    int i = paramString.indexOf(',');
    int j = paramString.indexOf(',', i + 1);
    if (i < 0 || j < 0)
      throw new IllegalArgumentException(paramString); 
    try {
      int k = Integer.parseInt(paramString.substring(0, i));
      int m = Integer.parseInt(paramString.substring(i + 1, j));
      int n = Integer.parseInt(paramString.substring(j + 1));
      Color color1 = new Color(k, m, n);
      changeColor(color1);
    } catch (Exception exception) {
      throw new IllegalArgumentException(paramString);
    } 
  }
  
  public boolean action(Event paramEvent, Object paramObject) {
    if (paramEvent.target == this.choser)
      changeColor(this.colors[this.choser.getSelectedIndex()]); 
    return false;
  }
  
  public String getJavaInitializationString() { return (this.color != null) ? ("new java.awt.Color(" + this.color.getRGB() + ",true)") : "null"; }
  
  private void changeColor(Color paramColor) {
    if (paramColor == null) {
      this.color = null;
      this.text.setText("");
      return;
    } 
    this.color = paramColor;
    this.text.setText("" + paramColor.getRed() + "," + paramColor.getGreen() + "," + paramColor.getBlue());
    byte b1 = 0;
    for (byte b2 = 0; b2 < this.colorNames.length; b2++) {
      if (this.color.equals(this.colors[b2]))
        b1 = b2; 
    } 
    this.choser.select(b1);
    this.sample.setBackground(this.color);
    this.sample.repaint();
    this.support.firePropertyChange("", null, null);
  }
  
  public Object getValue() { return this.color; }
  
  public boolean isPaintable() { return true; }
  
  public void paintValue(Graphics paramGraphics, Rectangle paramRectangle) {
    Color color1 = paramGraphics.getColor();
    paramGraphics.setColor(Color.black);
    paramGraphics.drawRect(paramRectangle.x, paramRectangle.y, paramRectangle.width - 3, paramRectangle.height - 3);
    paramGraphics.setColor(this.color);
    paramGraphics.fillRect(paramRectangle.x + 1, paramRectangle.y + 1, paramRectangle.width - 4, paramRectangle.height - 4);
    paramGraphics.setColor(color1);
  }
  
  public String getAsText() { return (this.color != null) ? (this.color.getRed() + "," + this.color.getGreen() + "," + this.color.getBlue()) : null; }
  
  public String[] getTags() { return null; }
  
  public Component getCustomEditor() { return this; }
  
  public boolean supportsCustomEditor() { return true; }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.support.addPropertyChangeListener(paramPropertyChangeListener); }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) { this.support.removePropertyChangeListener(paramPropertyChangeListener); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\beans\editors\ColorEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */