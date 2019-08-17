package sun.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import sun.awt.OSInfo;
import sun.awt.shell.ShellFolder;

public class WindowsPlacesBar extends JToolBar implements ActionListener, PropertyChangeListener {
  JFileChooser fc;
  
  JToggleButton[] buttons;
  
  ButtonGroup buttonGroup;
  
  File[] files;
  
  final Dimension buttonSize;
  
  public WindowsPlacesBar(JFileChooser paramJFileChooser, boolean paramBoolean) {
    super(1);
    this.fc = paramJFileChooser;
    setFloatable(false);
    putClientProperty("JToolBar.isRollover", Boolean.TRUE);
    boolean bool = (OSInfo.getOSType() == OSInfo.OSType.WINDOWS && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_XP) >= 0) ? 1 : 0;
    if (paramBoolean) {
      this.buttonSize = new Dimension(83, 69);
      putClientProperty("XPStyle.subAppName", "placesbar");
      setBorder(new EmptyBorder(1, 1, 1, 1));
    } else {
      this.buttonSize = new Dimension(83, bool ? 65 : 54);
      setBorder(new BevelBorder(1, UIManager.getColor("ToolBar.highlight"), UIManager.getColor("ToolBar.background"), UIManager.getColor("ToolBar.darkShadow"), UIManager.getColor("ToolBar.shadow")));
    } 
    Color color = new Color(UIManager.getColor("ToolBar.shadow").getRGB());
    setBackground(color);
    FileSystemView fileSystemView = paramJFileChooser.getFileSystemView();
    this.files = (File[])ShellFolder.get("fileChooserShortcutPanelFolders");
    this.buttons = new JToggleButton[this.files.length];
    this.buttonGroup = new ButtonGroup();
    for (byte b = 0; b < this.files.length; b++) {
      Icon icon;
      if (fileSystemView.isFileSystemRoot(this.files[b]))
        this.files[b] = fileSystemView.createFileObject(this.files[b].getAbsolutePath()); 
      String str = fileSystemView.getSystemDisplayName(this.files[b]);
      int i = str.lastIndexOf(File.separatorChar);
      if (i >= 0 && i < str.length() - 1)
        str = str.substring(i + 1); 
      if (this.files[b] instanceof ShellFolder) {
        ShellFolder shellFolder = (ShellFolder)this.files[b];
        Image image = shellFolder.getIcon(true);
        if (image == null)
          image = (Image)ShellFolder.get("shell32LargeIcon 1"); 
        icon = (image == null) ? null : new ImageIcon(image, shellFolder.getFolderType());
      } else {
        icon = fileSystemView.getSystemIcon(this.files[b]);
      } 
      this.buttons[b] = new JToggleButton(str, icon);
      if (paramBoolean) {
        this.buttons[b].putClientProperty("XPStyle.subAppName", "placesbar");
      } else {
        Color color1 = new Color(UIManager.getColor("List.selectionForeground").getRGB());
        this.buttons[b].setContentAreaFilled(false);
        this.buttons[b].setForeground(color1);
      } 
      this.buttons[b].setMargin(new Insets(3, 2, 1, 2));
      this.buttons[b].setFocusPainted(false);
      this.buttons[b].setIconTextGap(0);
      this.buttons[b].setHorizontalTextPosition(0);
      this.buttons[b].setVerticalTextPosition(3);
      this.buttons[b].setAlignmentX(0.5F);
      this.buttons[b].setPreferredSize(this.buttonSize);
      this.buttons[b].setMaximumSize(this.buttonSize);
      this.buttons[b].addActionListener(this);
      add(this.buttons[b]);
      if (b < this.files.length - 1 && paramBoolean)
        add(Box.createRigidArea(new Dimension(1, 1))); 
      this.buttonGroup.add(this.buttons[b]);
    } 
    doDirectoryChanged(paramJFileChooser.getCurrentDirectory());
  }
  
  protected void doDirectoryChanged(File paramFile) {
    for (byte b = 0; b < this.buttons.length; b++) {
      JToggleButton jToggleButton = this.buttons[b];
      if (this.files[b].equals(paramFile)) {
        jToggleButton.setSelected(true);
        break;
      } 
      if (jToggleButton.isSelected()) {
        this.buttonGroup.remove(jToggleButton);
        jToggleButton.setSelected(false);
        this.buttonGroup.add(jToggleButton);
      } 
    } 
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "directoryChanged")
      doDirectoryChanged(this.fc.getCurrentDirectory()); 
  }
  
  public void actionPerformed(ActionEvent paramActionEvent) {
    JToggleButton jToggleButton = (JToggleButton)paramActionEvent.getSource();
    for (byte b = 0; b < this.buttons.length; b++) {
      if (jToggleButton == this.buttons[b]) {
        this.fc.setCurrentDirectory(this.files[b]);
        break;
      } 
    } 
  }
  
  public Dimension getPreferredSize() {
    Dimension dimension1 = getMinimumSize();
    Dimension dimension2 = super.getPreferredSize();
    int i = dimension1.height;
    if (this.buttons != null && this.buttons.length > 0 && this.buttons.length < 5) {
      JToggleButton jToggleButton = this.buttons[0];
      if (jToggleButton != null) {
        int j = 5 * ((jToggleButton.getPreferredSize()).height + 1);
        if (j > i)
          i = j; 
      } 
    } 
    if (i > dimension2.height)
      dimension2 = new Dimension(dimension2.width, i); 
    return dimension2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\WindowsPlacesBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */