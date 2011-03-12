/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

/**
 *
 */

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


public class MyTitledColorBorderPanel extends JPanel
{
    JPanel tempPane;
    public MyTitledColorBorderPanel(JPanel innerPane, Color backColor, String borderText)
    {
        // set properties of tempPanel
        tempPane = new JPanel (new BorderLayout ());
        tempPane.setBackground (backColor);
        //TODO: NEW-UI
        tempPane.setBorder (new TitledBorder (new LineBorder (new Color (125, 145, 160)),
        borderText));

        // create the tempPane variables
        tempPane.add (new JLabel (" "), BorderLayout.WEST);
        tempPane.add (new JLabel (" "), BorderLayout.EAST);
        tempPane.add (innerPane, BorderLayout.CENTER);

        setLayout (new BorderLayout ());
        setBackground (backColor);
        add (new JLabel (" "), BorderLayout.SOUTH);
        add (new JLabel (" "), BorderLayout.WEST);
        add (new JLabel (" "), BorderLayout.EAST);
        add (tempPane, BorderLayout.CENTER);

    } 


    /*
     * Creates a titled color border panel aroind a given panel
     * @inputs :
     * i) panel around which border is required
     * ii) Background color
     * iii) Line colour (i.e. border color)
     * iv) Text around the border
     */
    public MyTitledColorBorderPanel (JPanel innerPane, Color backColor, Color lineColor, String borderText)
    {
        // set properties of tempPanel

        tempPane = new JPanel (new BorderLayout ());
        tempPane.setBackground (backColor);
        tempPane.setBorder (new TitledBorder (new LineBorder (lineColor),
                borderText));
        // create the tempPane variables

        tempPane.add (new JLabel (" "), BorderLayout.WEST);
        tempPane.add (new JLabel (" "), BorderLayout.EAST);
        tempPane.add (innerPane, BorderLayout.CENTER);


        setLayout (new BorderLayout ());
        setBackground (backColor);
        add (new JLabel (" "), BorderLayout.SOUTH);
        add (new JLabel (" "), BorderLayout.WEST);
        add (new JLabel (" "), BorderLayout.EAST);
        add (tempPane, BorderLayout.CENTER);

    }


} 
