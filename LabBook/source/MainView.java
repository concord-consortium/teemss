package org.concord.LabBook;

import org.concord.waba.extra.ui.*;
import org.concord.waba.extra.event.*;

public interface MainView
{
    public void addMenu(LabObjectView source, Menu menu);

    public void delMenu(LabObjectView source, Menu menu);

	public void addFileMenuItems(String [] items, ActionListener source);

	public void removeFileMenuItems(String [] items, ActionListener source);

	public String [] getCreateNames();

	public void createObj(String name, LObjDictionaryView dView);

	public void showFullWindowObj(boolean edit, LObjDictionary dict,  LabObjectPtr ptr);

	public void closeTopWindowView();
}
