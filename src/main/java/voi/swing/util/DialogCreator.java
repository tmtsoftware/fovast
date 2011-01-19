/*
 * Copyright (c) 2005 Virtual Observatory - India.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

/* Created on May 22, 2007 by vivekananda_moosani */

package voi.swing.util;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * Provides static methods which take a component and return a JDialog which have 
 * parent set to the window of the owner. Owner can be any Component with either
 * a Frame or Dialog as an ancestor. If no frame or dialog is an ancestor then 
 * a dummy frame is created to be an owner
 * 
 * @author vivekananda_moosani
 */
public class DialogCreator
{

    public static JDialog createDialog(Component owner)
    {
        return createDialog(owner, null, false, null);
    }
    
    public static JDialog createDialog(Component owner, boolean modal)
    {
        return createDialog(owner, null, modal, null);
    }
    
    public static JDialog createDialog(Component owner, String title, boolean modal)
    {
        return createDialog(owner, title, modal, null);
    }
    
    public static JDialog createDialog(Component owner, String title, boolean modal, 
            GraphicsConfiguration gc)
    {
        //if owner is null then SwingUtilities.getAncestorOfClass returns null
        //and null instanceof Dialog/Frame returns false.
        // So the dialog creation would 
        // be using new JDialog((Frame)null, title, modal, gc)

        JDialog dialog = null;
        
        Window window = (Window) SwingUtilities.getAncestorOfClass(
                Window.class, owner);

        if(window instanceof Dialog)
            dialog = new JDialog((Dialog)window, title, modal, gc);
        else if(window instanceof Frame)
            dialog = new JDialog((Frame)window, title, modal, gc);
        else //also handles owner == null case
            dialog = new JDialog((Frame)null, title, modal, gc);
        
        dialog.setLocationRelativeTo(owner);
        
        return dialog;
    }

}
