package javax.swing.plaf.basic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import javax.swing.AbstractListModel;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.filechooser.FileSystemView;
import sun.awt.shell.ShellFolder;

public class BasicDirectoryModel extends AbstractListModel<Object> implements PropertyChangeListener {
  private JFileChooser filechooser = null;
  
  private Vector<File> fileCache = new Vector(50);
  
  private LoadFilesThread loadThread = null;
  
  private Vector<File> files = null;
  
  private Vector<File> directories = null;
  
  private int fetchID = 0;
  
  private PropertyChangeSupport changeSupport;
  
  private boolean busy = false;
  
  public BasicDirectoryModel(JFileChooser paramJFileChooser) {
    this.filechooser = paramJFileChooser;
    validateFileCache();
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "directoryChanged" || str == "fileViewChanged" || str == "fileFilterChanged" || str == "FileHidingChanged" || str == "fileSelectionChanged") {
      validateFileCache();
    } else if ("UI".equals(str)) {
      Object object = paramPropertyChangeEvent.getOldValue();
      if (object instanceof BasicFileChooserUI) {
        BasicFileChooserUI basicFileChooserUI = (BasicFileChooserUI)object;
        BasicDirectoryModel basicDirectoryModel = basicFileChooserUI.getModel();
        if (basicDirectoryModel != null)
          basicDirectoryModel.invalidateFileCache(); 
      } 
    } else if ("JFileChooserDialogIsClosingProperty".equals(str)) {
      invalidateFileCache();
    } 
  }
  
  public void invalidateFileCache() {
    if (this.loadThread != null) {
      this.loadThread.interrupt();
      this.loadThread.cancelRunnables();
      this.loadThread = null;
    } 
  }
  
  public Vector<File> getDirectories() {
    synchronized (this.fileCache) {
      if (this.directories != null)
        return this.directories; 
      Vector vector = getFiles();
      return this.directories;
    } 
  }
  
  public Vector<File> getFiles() {
    synchronized (this.fileCache) {
      if (this.files != null)
        return this.files; 
      this.files = new Vector();
      this.directories = new Vector();
      this.directories.addElement(this.filechooser.getFileSystemView().createFileObject(this.filechooser.getCurrentDirectory(), ".."));
      for (byte b = 0; b < getSize(); b++) {
        File file = (File)this.fileCache.get(b);
        if (this.filechooser.isTraversable(file)) {
          this.directories.add(file);
        } else {
          this.files.add(file);
        } 
      } 
      return this.files;
    } 
  }
  
  public void validateFileCache() {
    File file = this.filechooser.getCurrentDirectory();
    if (file == null)
      return; 
    if (this.loadThread != null) {
      this.loadThread.interrupt();
      this.loadThread.cancelRunnables();
    } 
    setBusy(true, ++this.fetchID);
    this.loadThread = new LoadFilesThread(file, this.fetchID);
    this.loadThread.start();
  }
  
  public boolean renameFile(File paramFile1, File paramFile2) {
    synchronized (this.fileCache) {
      if (paramFile1.renameTo(paramFile2)) {
        validateFileCache();
        return true;
      } 
      return false;
    } 
  }
  
  public void fireContentsChanged() { fireContentsChanged(this, 0, getSize() - 1); }
  
  public int getSize() { return this.fileCache.size(); }
  
  public boolean contains(Object paramObject) { return this.fileCache.contains(paramObject); }
  
  public int indexOf(Object paramObject) { return this.fileCache.indexOf(paramObject); }
  
  public Object getElementAt(int paramInt) { return this.fileCache.get(paramInt); }
  
  public void intervalAdded(ListDataEvent paramListDataEvent) {}
  
  public void intervalRemoved(ListDataEvent paramListDataEvent) {}
  
  protected void sort(Vector<? extends File> paramVector) { ShellFolder.sort(paramVector); }
  
  protected boolean lt(File paramFile1, File paramFile2) {
    int i = paramFile1.getName().toLowerCase().compareTo(paramFile2.getName().toLowerCase());
    return (i != 0) ? ((i < 0)) : ((paramFile1.getName().compareTo(paramFile2.getName()) < 0));
  }
  
  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport == null)
      this.changeSupport = new PropertyChangeSupport(this); 
    this.changeSupport.addPropertyChangeListener(paramPropertyChangeListener);
  }
  
  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener) {
    if (this.changeSupport != null)
      this.changeSupport.removePropertyChangeListener(paramPropertyChangeListener); 
  }
  
  public PropertyChangeListener[] getPropertyChangeListeners() { return (this.changeSupport == null) ? new PropertyChangeListener[0] : this.changeSupport.getPropertyChangeListeners(); }
  
  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2) {
    if (this.changeSupport != null)
      this.changeSupport.firePropertyChange(paramString, paramObject1, paramObject2); 
  }
  
  private void setBusy(final boolean busy, int paramInt) {
    if (paramInt == this.fetchID) {
      boolean bool = this.busy;
      this.busy = paramBoolean;
      if (this.changeSupport != null && paramBoolean != bool)
        SwingUtilities.invokeLater(new Runnable() {
              public void run() { BasicDirectoryModel.this.firePropertyChange("busy", Boolean.valueOf(!busy), Boolean.valueOf(busy)); }
            }); 
    } 
  }
  
  class DoChangeContents implements Runnable {
    private List<File> addFiles;
    
    private List<File> remFiles;
    
    private boolean doFire = true;
    
    private int fid;
    
    private int addStart = 0;
    
    private int remStart = 0;
    
    public DoChangeContents(List<File> param1List1, int param1Int1, List<File> param1List2, int param1Int2, int param1Int3) {
      this.addFiles = param1List1;
      this.addStart = param1Int1;
      this.remFiles = param1List2;
      this.remStart = param1Int2;
      this.fid = param1Int3;
    }
    
    void cancel() { this.doFire = false; }
    
    public void run() {
      if (BasicDirectoryModel.this.fetchID == this.fid && this.doFire) {
        int i = (this.remFiles == null) ? 0 : this.remFiles.size();
        int j = (this.addFiles == null) ? 0 : this.addFiles.size();
        synchronized (BasicDirectoryModel.this.fileCache) {
          if (i)
            BasicDirectoryModel.this.fileCache.removeAll(this.remFiles); 
          if (j)
            BasicDirectoryModel.this.fileCache.addAll(this.addStart, this.addFiles); 
          BasicDirectoryModel.this.files = null;
          BasicDirectoryModel.this.directories = null;
        } 
        if (i && !j) {
          BasicDirectoryModel.this.fireIntervalRemoved(BasicDirectoryModel.this, this.remStart, this.remStart + i - 1);
        } else if (j && i == 0 && this.addStart + j <= BasicDirectoryModel.this.fileCache.size()) {
          BasicDirectoryModel.this.fireIntervalAdded(BasicDirectoryModel.this, this.addStart, this.addStart + j - 1);
        } else {
          BasicDirectoryModel.this.fireContentsChanged();
        } 
      } 
    }
  }
  
  class LoadFilesThread extends Thread {
    File currentDirectory = null;
    
    int fid;
    
    Vector<BasicDirectoryModel.DoChangeContents> runnables = new Vector(10);
    
    public LoadFilesThread(File param1File, int param1Int) {
      super("Basic L&F File Loading Thread");
      this.currentDirectory = param1File;
      this.fid = param1Int;
    }
    
    public void run() {
      run0();
      BasicDirectoryModel.this.setBusy(false, this.fid);
    }
    
    public void run0() {
      FileSystemView fileSystemView = BasicDirectoryModel.this.filechooser.getFileSystemView();
      if (isInterrupted())
        return; 
      File[] arrayOfFile = fileSystemView.getFiles(this.currentDirectory, BasicDirectoryModel.this.filechooser.isFileHidingEnabled());
      if (isInterrupted())
        return; 
      final Vector newFileCache = new Vector();
      Vector vector2 = new Vector();
      for (File file : arrayOfFile) {
        if (BasicDirectoryModel.this.filechooser.accept(file)) {
          boolean bool = BasicDirectoryModel.this.filechooser.isTraversable(file);
          if (bool) {
            vector1.addElement(file);
          } else if (BasicDirectoryModel.this.filechooser.isFileSelectionEnabled()) {
            vector2.addElement(file);
          } 
          if (isInterrupted())
            return; 
        } 
      } 
      BasicDirectoryModel.this.sort(vector1);
      BasicDirectoryModel.this.sort(vector2);
      vector1.addAll(vector2);
      BasicDirectoryModel.DoChangeContents doChangeContents = (BasicDirectoryModel.DoChangeContents)ShellFolder.invoke(new Callable<BasicDirectoryModel.DoChangeContents>() {
            public BasicDirectoryModel.DoChangeContents call() {
              int i = newFileCache.size();
              int j = BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.size();
              if (i > j) {
                int k = j;
                int m = i;
                for (int n = 0; n < j; n++) {
                  if (!((File)newFileCache.get(n)).equals(BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.get(n))) {
                    k = n;
                    for (int i1 = n; i1 < i; i1++) {
                      if (((File)newFileCache.get(i1)).equals(BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.get(n))) {
                        m = i1;
                        break;
                      } 
                    } 
                    break;
                  } 
                } 
                if (k >= 0 && m > k && newFileCache.subList(m, i).equals(BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.subList(k, j)))
                  return BasicDirectoryModel.LoadFilesThread.this.isInterrupted() ? null : new BasicDirectoryModel.DoChangeContents(BasicDirectoryModel.LoadFilesThread.this.this$0, newFileCache.subList(k, m), k, null, 0, BasicDirectoryModel.LoadFilesThread.this.fid); 
              } else if (i < j) {
                byte b = -1;
                int k = -1;
                for (int m = 0; m < i; m++) {
                  if (!((File)newFileCache.get(m)).equals(BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.get(m))) {
                    b = m;
                    k = m + j - i;
                    break;
                  } 
                } 
                if (b >= 0 && k > b && BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.subList(k, j).equals(newFileCache.subList(b, i)))
                  return BasicDirectoryModel.LoadFilesThread.this.isInterrupted() ? null : new BasicDirectoryModel.DoChangeContents(BasicDirectoryModel.LoadFilesThread.this.this$0, null, 0, new Vector(BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.subList(b, k)), b, BasicDirectoryModel.LoadFilesThread.this.fid); 
              } 
              if (!BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache.equals(newFileCache)) {
                if (BasicDirectoryModel.LoadFilesThread.this.isInterrupted())
                  BasicDirectoryModel.LoadFilesThread.this.cancelRunnables(BasicDirectoryModel.LoadFilesThread.this.runnables); 
                return new BasicDirectoryModel.DoChangeContents(BasicDirectoryModel.LoadFilesThread.this.this$0, newFileCache, 0, BasicDirectoryModel.LoadFilesThread.this.this$0.fileCache, 0, BasicDirectoryModel.LoadFilesThread.this.fid);
              } 
              return null;
            }
          });
      if (doChangeContents != null) {
        this.runnables.addElement(doChangeContents);
        SwingUtilities.invokeLater(doChangeContents);
      } 
    }
    
    public void cancelRunnables(Vector<BasicDirectoryModel.DoChangeContents> param1Vector) {
      for (BasicDirectoryModel.DoChangeContents doChangeContents : param1Vector)
        doChangeContents.cancel(); 
    }
    
    public void cancelRunnables() { cancelRunnables(this.runnables); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\plaf\basic\BasicDirectoryModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */