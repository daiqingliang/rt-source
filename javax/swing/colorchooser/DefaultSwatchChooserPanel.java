package javax.swing.colorchooser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

class DefaultSwatchChooserPanel extends AbstractColorChooserPanel {
  SwatchPanel swatchPanel;
  
  RecentSwatchPanel recentSwatchPanel;
  
  MouseListener mainSwatchListener;
  
  MouseListener recentSwatchListener;
  
  private KeyListener mainSwatchKeyListener;
  
  private KeyListener recentSwatchKeyListener;
  
  public DefaultSwatchChooserPanel() { setInheritsPopupMenu(true); }
  
  public String getDisplayName() { return UIManager.getString("ColorChooser.swatchesNameText", getLocale()); }
  
  public int getMnemonic() { return getInt("ColorChooser.swatchesMnemonic", -1); }
  
  public int getDisplayedMnemonicIndex() { return getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1); }
  
  public Icon getSmallDisplayIcon() { return null; }
  
  public Icon getLargeDisplayIcon() { return null; }
  
  public void installChooserPanel(JColorChooser paramJColorChooser) { super.installChooserPanel(paramJColorChooser); }
  
  protected void buildChooser() {
    String str = UIManager.getString("ColorChooser.swatchesRecentText", getLocale());
    GridBagLayout gridBagLayout = new GridBagLayout();
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    JPanel jPanel1 = new JPanel(gridBagLayout);
    this.swatchPanel = new MainSwatchPanel();
    this.swatchPanel.putClientProperty("AccessibleName", getDisplayName());
    this.swatchPanel.setInheritsPopupMenu(true);
    this.recentSwatchPanel = new RecentSwatchPanel();
    this.recentSwatchPanel.putClientProperty("AccessibleName", str);
    this.mainSwatchKeyListener = new MainSwatchKeyListener(null);
    this.mainSwatchListener = new MainSwatchListener();
    this.swatchPanel.addMouseListener(this.mainSwatchListener);
    this.swatchPanel.addKeyListener(this.mainSwatchKeyListener);
    this.recentSwatchListener = new RecentSwatchListener();
    this.recentSwatchKeyListener = new RecentSwatchKeyListener(null);
    this.recentSwatchPanel.addMouseListener(this.recentSwatchListener);
    this.recentSwatchPanel.addKeyListener(this.recentSwatchKeyListener);
    JPanel jPanel2 = new JPanel(new BorderLayout());
    CompoundBorder compoundBorder = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
    jPanel2.setBorder(compoundBorder);
    jPanel2.add(this.swatchPanel, "Center");
    gridBagConstraints.anchor = 25;
    gridBagConstraints.gridwidth = 1;
    gridBagConstraints.gridheight = 2;
    Insets insets = gridBagConstraints.insets;
    gridBagConstraints.insets = new Insets(0, 0, 0, 10);
    jPanel1.add(jPanel2, gridBagConstraints);
    gridBagConstraints.insets = insets;
    this.recentSwatchPanel.setInheritsPopupMenu(true);
    JPanel jPanel3 = new JPanel(new BorderLayout());
    jPanel3.setBorder(compoundBorder);
    jPanel3.setInheritsPopupMenu(true);
    jPanel3.add(this.recentSwatchPanel, "Center");
    JLabel jLabel = new JLabel(str);
    jLabel.setLabelFor(this.recentSwatchPanel);
    gridBagConstraints.gridwidth = 0;
    gridBagConstraints.gridheight = 1;
    gridBagConstraints.weighty = 1.0D;
    jPanel1.add(jLabel, gridBagConstraints);
    gridBagConstraints.weighty = 0.0D;
    gridBagConstraints.gridheight = 0;
    gridBagConstraints.insets = new Insets(0, 0, 0, 2);
    jPanel1.add(jPanel3, gridBagConstraints);
    jPanel1.setInheritsPopupMenu(true);
    add(jPanel1);
  }
  
  public void uninstallChooserPanel(JColorChooser paramJColorChooser) {
    super.uninstallChooserPanel(paramJColorChooser);
    this.swatchPanel.removeMouseListener(this.mainSwatchListener);
    this.swatchPanel.removeKeyListener(this.mainSwatchKeyListener);
    this.recentSwatchPanel.removeMouseListener(this.recentSwatchListener);
    this.recentSwatchPanel.removeKeyListener(this.recentSwatchKeyListener);
    this.swatchPanel = null;
    this.recentSwatchPanel = null;
    this.mainSwatchListener = null;
    this.mainSwatchKeyListener = null;
    this.recentSwatchListener = null;
    this.recentSwatchKeyListener = null;
    removeAll();
  }
  
  public void updateChooser() {}
  
  private class MainSwatchKeyListener extends KeyAdapter {
    private MainSwatchKeyListener() {}
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (32 == param1KeyEvent.getKeyCode()) {
        Color color = DefaultSwatchChooserPanel.this.swatchPanel.getSelectedColor();
        DefaultSwatchChooserPanel.this.setSelectedColor(color);
        DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(color);
      } 
    }
  }
  
  class MainSwatchListener extends MouseAdapter implements Serializable {
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (DefaultSwatchChooserPanel.this.isEnabled()) {
        Color color = DefaultSwatchChooserPanel.this.swatchPanel.getColorForLocation(param1MouseEvent.getX(), param1MouseEvent.getY());
        DefaultSwatchChooserPanel.this.setSelectedColor(color);
        DefaultSwatchChooserPanel.this.swatchPanel.setSelectedColorFromLocation(param1MouseEvent.getX(), param1MouseEvent.getY());
        DefaultSwatchChooserPanel.this.recentSwatchPanel.setMostRecentColor(color);
        DefaultSwatchChooserPanel.this.swatchPanel.requestFocusInWindow();
      } 
    }
  }
  
  private class RecentSwatchKeyListener extends KeyAdapter {
    private RecentSwatchKeyListener() {}
    
    public void keyPressed(KeyEvent param1KeyEvent) {
      if (32 == param1KeyEvent.getKeyCode()) {
        Color color = DefaultSwatchChooserPanel.this.recentSwatchPanel.getSelectedColor();
        DefaultSwatchChooserPanel.this.setSelectedColor(color);
      } 
    }
  }
  
  class RecentSwatchListener extends MouseAdapter implements Serializable {
    public void mousePressed(MouseEvent param1MouseEvent) {
      if (DefaultSwatchChooserPanel.this.isEnabled()) {
        Color color = DefaultSwatchChooserPanel.this.recentSwatchPanel.getColorForLocation(param1MouseEvent.getX(), param1MouseEvent.getY());
        DefaultSwatchChooserPanel.this.recentSwatchPanel.setSelectedColorFromLocation(param1MouseEvent.getX(), param1MouseEvent.getY());
        DefaultSwatchChooserPanel.this.setSelectedColor(color);
        DefaultSwatchChooserPanel.this.recentSwatchPanel.requestFocusInWindow();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\colorchooser\DefaultSwatchChooserPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */