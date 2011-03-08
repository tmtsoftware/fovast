/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.gui;

import gnu.jel.DVMap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableRowSorter;
import jsky.util.JavaExpr;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmt.fovast.astro.util.DegreeCoverter;
import org.tmt.fovast.util.Cache;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowSequence;
import uk.ac.starlink.table.StarTable;
import uk.ac.starlink.table.StoragePolicy;
import uk.ac.starlink.table.formats.TstTableBuilder;
import uk.ac.starlink.util.DataSource;
import uk.ac.starlink.votable.TableElement;
import uk.ac.starlink.votable.VOElement;
import uk.ac.starlink.votable.VOElementFactory;
import uk.ac.starlink.votable.VOStarTable;

/**
 *
 * @author Disha_Gujrathi
 */
public class ConeSearchDialog extends DVMap{

    private static Logger logger = LoggerFactory.getLogger(ConeSearchDialog.class);

    JDialog csDialog;

    private JLabel urlLabel;

    private JLabel raLabel;

    private JLabel decLabel;  

    private JLabel radiusLabel;

    private JLabel minMagLabel;

    private JLabel maxMagLabel;

    private JTextField urlTextField;

    private JTextField raTextField;

    private JTextField decTextField;

    private JTextField radiusTextField;

    private JTextField minMagTextField;

    private JTextField maxMagTextField;

    private JButton fetchButton;

    private JButton plotButton;

    private JButton closeButton;

    private JPanel coneSearchParameterPanel;

    private JPanel coneSearchPanel;

    private JPanel tablePanel;

    private JPanel plotPanel;

    private Color QUERY_COLOR;

    private Catalog c;

    private ArrayList<Double> raList = new ArrayList<Double>();

    private ArrayList<Double> decList = new ArrayList<Double>();

    private ArrayList<Float> magList = new ArrayList<Float>();

    public static final String RA_UCD = "POS_EQ_RA_MAIN";

    public static final String DEC_UCD = "POS_EQ_DEC_MAIN";

    public static final String MAG_UCD = "PHOT_MAG_V";

    private String[] _colNames = new String[3];

    private int[] _colIndexes = new int[3];

    private String _cond;

    private JavaExpr _condExpr;

    private int rowCountIndex=0;

    public boolean retrieving=true;

    Class[] classTypes = new Class[3] ;

    private Task worker;

    private Timer _timer;

    StarTable table;

    PlotHandler ph;

    private boolean isCancelledFlag = false;

    private String source;

    MyTableModel tableModel;

    Object[] cells;
    
    private static Cache cache;

    private URL urlToDownload;

    private static int refetch = 0;

    private boolean showPrevious = false;

    private Catalog lastCatalog=null;

    HashMap<String,Object> prop = new HashMap<String, Object>();

    public ConeSearchDialog(String url ,double ra ,double dec ,int verb,
            PlotHandler ph,FovastMainView fmv,String source,Cache cache,Catalog lastCatalog)
            throws MalformedURLException, SAXException, IOException{
        csDialog = new JDialog(fmv.getFrame(),ModalityType.APPLICATION_MODAL);
        this.source = source;
        this.cache = cache;
        if(lastCatalog != null){
            showPrevious = true;
            this.lastCatalog=lastCatalog;
        }
        initComponents(url ,ra ,dec ,verb);
        this.ph = ph;
        csDialog.setVisible(true);
        csDialog.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                  isCancelledFlag = true;
//                  killWorkersAndTimers();
//                  csDialog.setVisible(false);
//                  csDialog.dispose();
//            }
//
//            @Override
//            public void windowClosed(WindowEvent e) {
//                 isCancelledFlag = true;
//                  killWorkersAndTimers();
//                  csDialog.setVisible(false);
//                  csDialog.dispose();
//            }    
            @Override
            public void windowDeactivated(WindowEvent e) {
               isCancelledFlag = true;
                  killWorkersAndTimers();
                  csDialog.setVisible(false);
                  csDialog.dispose();
            }
        });
    }

    public void killWorkersAndTimers() {
        if (worker != null) {
            worker.cancel(true);
            logger.info("worker stopped");
        }
        if (_timer != null) {
            _timer.cancel();
            logger.info("Timer stopped");
        }
    }

    public void loadLastCatalog() throws IOException, Throwable{
         retrieving=false;
        HashMap<String,Object> lastMap = lastCatalog.getProperties();
//        raTextField.setText((String) lastMap.get("ra"));
//        decTextField.setText((String) lastMap.get("dec"));
        radiusTextField.setText((String) lastMap.get("sr"));
        minMagTextField.setText((String) lastMap.get("magMin"));
        maxMagTextField.setText((String) lastMap.get("magMax"));
        _cond=(String) lastMap.get("condition");
        if(_cond != null && !(_cond.equals(""))){
            _compileExpressions();
        }
        StarTable lastTable=lastCatalog.getStarTable();
        String columnNames[] = new String[lastTable.getColumnCount()];
        for(int j = 0;j < lastTable.getColumnCount();j++){
            columnNames[j] = lastTable.getColumnInfo(j).getName();
        }
        tableModel = new MyTableModel(lastTable);
        //tableModel.initiallize((int) table.getRowCount(),table.getColumnCount());
        tableModel.setColNames(columnNames);
        RowSequence res = lastTable.getRowSequence();
        boolean condition = false;
        int tableModelIndex = 0;
        while(res.next()){
            cells = res.getRow();
            if(_cond != null && !(_cond.equals(""))){
                 condition = _condExpr.evalBoolean();
            }
            else
                 condition = true;
            if(condition == true){
            for(int j = 0;j<lastTable.getColumnCount();j++){
                     if(cells[j] != null){
                        tableModel.setValueAt(cells[j], tableModelIndex, j);
                     }
                }
               tableModelIndex++;
            }
        }
        showTable(lastTable);
    }


    private void initComponents(final String url ,final double ra ,final double dec ,final int verb){
        csDialog.setPreferredSize(new Dimension(800, 730));
        csDialog.setMaximumSize(new Dimension(800, 730));
        csDialog.setMinimumSize(new Dimension(800, 730));
        csDialog.setLayout(new BorderLayout());
        c = new Catalog();
        QUERY_COLOR = (new JPanel()).getBackground();
        coneSearchPanel = new JPanel(new BorderLayout());
        coneSearchParameterPanel = new JPanel();
        tablePanel = new JPanel(new BorderLayout());
        plotPanel=new JPanel();
        urlLabel = new JLabel("ConeSearch URL : ");
        raLabel = new JLabel("RA : ");
        decLabel = new JLabel("DEC : ");
        radiusLabel = new JLabel("Search Radius : ");
        if(source.equals("GSC2")){
                minMagLabel = new JLabel("JMag (Brightest): ");
                maxMagLabel = new JLabel("JMag (Faintest): ");
        }else if(source.equals("2MassPsc")){
                minMagLabel = new JLabel("KMag (Brightest): ");
                maxMagLabel = new JLabel("KMag (Faintest): ");
        }else if(source.equals("USNO")){
                minMagLabel = new JLabel("rmag (Brightest): ");
                maxMagLabel = new JLabel("rmag (Faintest): ");
        }
        urlTextField = new JTextField(url,50);
        raTextField = new JTextField(""+ra,15);
        decTextField = new JTextField(""+dec,15);
        radiusTextField = new JTextField("0.6");
        minMagTextField = new JTextField();
        maxMagTextField = new JTextField();
        closeButton = new JButton(" Close ");
        closeButton.setEnabled(true);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isCancelledFlag = true;
                killWorkersAndTimers();
                csDialog.setVisible(false);
                csDialog.dispose();
            }
        });
        plotButton = new JButton(" Plot ");
        plotButton.setEnabled(false);
        plotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String tempString = "";
                    String minMag = "";
                    String maxMag = "";
                    String tempMag = "";
                    if(source.equals("GSC2")){
                        tempMag = "JMag";
                    }else if(source.equals("USNO")){
                        tempMag = "rmag";
                    }else if(source.equals("2MassPsc")){
                        tempMag = "KMag";
                    }
                    if(minMagTextField.getText().equals("") && !(maxMagTextField.getText().equals(""))){
                      tempString=source+"(SearchRadius="+radiusTextField.getText().trim()+
                      ","+tempMag+"<="+maxMagTextField.getText().trim()+")";
                    }else if(maxMagTextField.getText().equals("") && !(minMagTextField.getText().equals(""))){
                      tempString=source+"(SearchRadius="+radiusTextField.getText().trim()+
                      ","+minMagTextField.getText().trim()+"<= "+tempMag+")";
                    }else if(minMagTextField.getText().equals("") && maxMagTextField.getText().equals("")){
                        tempString=source+"(SearchRadius="+radiusTextField.getText().trim()+")";
                    }else{
                   tempString=source+"(SearchRadius="+radiusTextField.getText().trim()+
                      ","+minMagTextField.getText().trim()+"<= "+tempMag+" <="+maxMagTextField.getText().trim()+")";
                    }
                    c.setLabel(tempString);
                    prop.put("ra", raTextField.getText().trim());
                    prop.put("dec",decTextField.getText().trim());
                    prop.put("sr",radiusTextField.getText().trim());
                    prop.put("magMin",minMagTextField.getText().trim());
                    prop.put("magMax", maxMagTextField.getText().trim());
                    prop.put("type",source);
                    ph.addCatalog(c);
                    csDialog.setVisible(false);
                    csDialog.dispose();
                } catch (Throwable ex) {
                    logger.error("ConeSearchDialog:", ex);
                }
            }
        });
        fetchButton = new JButton("  Fetch  ");
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                    try {
                            refetch++;
                            if(refetch == 1){
                                double sr = Double.parseDouble(radiusTextField.getText().trim());
                                fetchClicked(url, ra, dec, verb,sr);
                             }
                             else{
                               killWorkersAndTimers();
                               double sr = Double.parseDouble(radiusTextField.getText().trim());
                               fetchClicked(url, ra, dec, verb,sr);
                             }
                    } catch (MalformedURLException ex) {
                        logger.error("ConeSearchDialog:", ex);
                    } catch (SAXException ex) {
                        logger.error("ConeSearchDialog:", ex);
                    } catch (IOException ex) {
                        logger.error("ConeSearchDialog:", ex);
                    }
             }

        });    
        urlTextField.setEditable(false);
        raTextField.setEditable(false);
        decTextField.setEditable(false);

        urlTextField.setEnabled(false);
        raTextField.setEnabled(false);
        decTextField.setEnabled(false); 
        
        GridBagLayout raDecPanelLayout = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        coneSearchParameterPanel.setLayout(raDecPanelLayout);
        coneSearchParameterPanel.add(urlLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 0, 0);
        coneSearchParameterPanel.add(raLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        coneSearchParameterPanel.add(decLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        coneSearchParameterPanel.add(radiusLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        coneSearchParameterPanel.add(minMagLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        coneSearchParameterPanel.add(maxMagLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        Dimension TextFieldDimension = new Dimension(100, 30);
        gbc.anchor = GridBagConstraints.WEST;
        urlTextField.setPreferredSize(TextFieldDimension);
        urlTextField.setMinimumSize(TextFieldDimension);
        urlTextField.setMaximumSize(TextFieldDimension);
        coneSearchParameterPanel.add(urlTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        raTextField.setPreferredSize(TextFieldDimension);
        raTextField.setMinimumSize(TextFieldDimension);
        raTextField.setMaximumSize(TextFieldDimension);
        coneSearchParameterPanel.add(raTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        decTextField.setPreferredSize(TextFieldDimension);
        decTextField.setMinimumSize(TextFieldDimension);
        decTextField.setMaximumSize(TextFieldDimension);
        coneSearchParameterPanel.add(decTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        radiusTextField.setPreferredSize(TextFieldDimension);
        radiusTextField.setMinimumSize(TextFieldDimension);
        radiusTextField.setMaximumSize(TextFieldDimension);
        coneSearchParameterPanel.add(radiusTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        minMagTextField.setPreferredSize(TextFieldDimension);
        minMagTextField.setMinimumSize(TextFieldDimension);
        minMagTextField.setMaximumSize(TextFieldDimension);
        coneSearchParameterPanel.add(minMagTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        maxMagTextField.setPreferredSize(TextFieldDimension);
        maxMagTextField.setMinimumSize(TextFieldDimension);
        maxMagTextField.setMaximumSize(TextFieldDimension);
        coneSearchParameterPanel.add(maxMagTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1;
        gbc.gridwidth = 4;
        coneSearchParameterPanel.add(fetchButton, gbc);

        csDialog.getContentPane().add(new MyTitledColorBorderPanel(coneSearchPanel, QUERY_COLOR,
                " Cone Search "));
        plotPanel.add(plotButton);
        plotPanel.add(closeButton);
        coneSearchPanel.add(new MyTitledColorBorderPanel(coneSearchParameterPanel,
                QUERY_COLOR,"ConeSearch Parameters"), BorderLayout.NORTH);
        coneSearchPanel.add(new MyTitledColorBorderPanel(tablePanel,
                QUERY_COLOR,"RESULTS"), BorderLayout.CENTER);
        coneSearchPanel.add(plotPanel,BorderLayout.SOUTH);
        if(showPrevious){
            try {
                
                loadLastCatalog();
            } catch (IOException ex) {
                logger.error(null, ex);
                JOptionPane.showMessageDialog(csDialog, "Error while loading plotted table");
            } catch (Throwable ex) {
                logger.error(null, ex);
                JOptionPane.showMessageDialog(csDialog, "Error while loading plotted table");
            }
        }
    }

 /** Implements the DVMap interface */
    @Override
    public String getTypeName(String name) {
        if (name.startsWith("$"))
            name = name.substring(1);
            name=name.trim();

//        for (int i = 0; i < _colNames.length; i++) {
//            if (_colNames[i].equals(name)) {
        for(int i = 0 ;i < table.getColumnCount();i++ ){
            ColumnInfo info = table.getColumnInfo(i);
            if(name.equals(info.getName().trim())){
                String className= info.getContentClass().toString();
                //String className = classTypes[i].toString();
                return className.substring(className.lastIndexOf('.')+1);
            }
        }
        return "Object";
    }


    /** Called by reflection for the DVMap interface to get the value of the named variable of type Float */
    public double getFloatProperty(String name) throws IOException {
        if (name.startsWith("$"))
            name = name.substring(1);

        for(int i = 0 ;i < table.getColumnCount();i++ ){
            ColumnInfo info = table.getColumnInfo(i);
            if(name.equals(info.getName())){
                Object value = cells[i];
                if (value instanceof Float)
                    return (Float) value;
            }
        }
        return 0.0;
    }

    /** Called by reflection for the DVMap interface to get the value of the named variable of type Double */
    public double getDoubleProperty(String name) throws IOException {
        if (name.startsWith("$"))
            name = name.substring(1);

        for(int i = 0 ;i < table.getColumnCount();i++ ){
            ColumnInfo info = table.getColumnInfo(i);
            if(name.equals(info.getName())){
                Object value = cells[i];
                if (value instanceof Double)
                    return (Double) value;
            }
        }
        return 0.0;
    }

    /** Called by reflection for the DVMap interface to get the value of the named variable of type String */
    public String getStringProperty(String name) throws IOException {
        if (name.startsWith("$"))
            name = name.substring(1);

        for(int i = 0 ;i < table.getColumnCount();i++ ){
            ColumnInfo info = table.getColumnInfo(i);
            if(name.equals(info.getName())){
                Object value = cells[i];
                if (value instanceof String)
                    return (String) value;
            }
        }
        return null;
    }

    /** Called by reflection for the DVMap interface to get the value of the named variable of type Object */
    public Object getObjectProperty(String name) throws IOException {
        if (name.startsWith("$"))
            name = name.substring(1);
        for(int i = 0 ;i < table.getColumnCount();i++ ){
            ColumnInfo info = table.getColumnInfo(i);
            if(name.equals(info.getName())){
                return cells[i];
            }
        }
        return null;
    }


    /**
     * Compile any expressions that are based on column values. If the expression is constant,
     * it is not compiled.
     */
    private void _compileExpressions() {

        if (_cond.length() != 0 && _cond != null) {
            try {
                _condExpr = new JavaExpr(_cond, this);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

   
    public void fetchClicked(final String url ,final double ra ,final double dec ,final int verb,final double sr)
            throws MalformedURLException, SAXException, IOException{
        
        raList.clear();
        decList.clear();
        magList.clear();
        plotButton.setEnabled(false);
        if(tableModel != null)
            tableModel.clear();
        
        worker = new Task(FovastApplication.getApplication()) {
            @Override
            protected Object doInBackground() throws Exception {
                if (!isCancelled()) {
                try {
                        double tempSr = sr*60;
                        int raColIndex = -1;
                        int decColIndex = -1;
                        int magColIndex = -1;
                        String urlString = "";
                        if(source.equals("GSC2")){
                            String ra1 = DegreeCoverter.degToHMS(ra);
                            String dec1 = DegreeCoverter.degToDMS(dec);
                            if(minMagTextField.getText().equals("") &&
                                 !(maxMagTextField.getText().equals(""))){
                                    urlString = url + "ra=" + ra1 + "&dec=" + dec1 +
                                     "&r1=0.0&r2=" + tempSr+"&Jmag(Faintest)="
                                     +maxMagTextField.getText().trim()+"&";
                            }else if(maxMagTextField.getText().equals("") &&
                                 !(minMagTextField.getText().equals(""))){
                                    urlString = url + "ra=" + ra1 + "&dec=" + dec1 +
                                     "&r1=0.0&r2=" + tempSr+"&Jmag(Brightest)="
                                     +minMagTextField.getText().trim()+"&";
                            }else if(minMagTextField.getText().equals("") &&
                                  maxMagTextField.getText().equals("")){
                                    urlString = url + "ra=" + ra1 + "&dec=" + dec1 +
                                     "&r1=0.0&r2=" + tempSr+"&";
                            }else{
                                    urlString = url + "ra=" + ra1 + "&dec=" + dec1 +
                                     "&r1=0.0&r2=" + tempSr+"&Jmag(Brightest)="
                                     +minMagTextField.getText().trim()
                                     +"&Jmag(Faintest)="+maxMagTextField.getText().trim()+"&";
                            }
                            logger.info(urlString);
                        }else if(source.equals("2MassPsc")){
                            urlString = url + "&RA=" + ra + "&DEC=" + dec +
                                "&SR=" + sr + "&VERB=" + verb;
                            logger.info(urlString);
                        }else if(source.equals("USNO")){
                            String ra1 =DegreeCoverter.degToHMS(ra);
                            String dec1=DegreeCoverter.degToDMS(dec);
                            if(minMagTextField.getText().equals("") &&
                                 !(maxMagTextField.getText().equals(""))){
                                    urlString = url + ra1 +"+"+ dec1 +
                                     "&radius=0.0," + tempSr +"&mag=,"
                                     +maxMagTextField.getText().trim()
                                     +"&&format=8&sort=mr";
                            }else if(maxMagTextField.getText().equals("") &&
                                 !(minMagTextField.getText().equals(""))){
                                    urlString = url + ra1 +"+"+ dec1 +
                                     "&radius=0.0," + tempSr +"&mag="
                                     +minMagTextField.getText().trim()+",&&format=8&sort=mr";
                            }else if(minMagTextField.getText().equals("") &&
                                 maxMagTextField.getText().equals("")){
                                    urlString = url + ra1 +"+"+ dec1 +
                                     "&radius=0.0," + tempSr +"&&format=8&sort=mr";
                            }else{
                                urlString = url + ra1 +"+"+ dec1 +
                               "&radius=0.0," + tempSr +"&mag="
                               +minMagTextField.getText().trim()+","+maxMagTextField.getText().trim()
                               +"&&format=8&sort=mr";
                            }
                            logger.info(urlString);
                        }                       
                        urlToDownload = new URL(urlString);
                        if (cache.getFile(urlToDownload) == null) {
                            logger.info("Downloading image from " + urlToDownload.toString());
                            cache.save(urlToDownload, null);
                        } else {
                                logger.info("Using image from local cache "
                                        + "instead of from " + urlToDownload.toString());
                                logger.info("Cached image path "
                                        + cache.getFile(urlToDownload).toString());
                        }
                        File cacheFile =  cache.getFile(urlToDownload);                            
                        if(source.equals("2MassPsc")){                          
                            VOElement votable = new VOElementFactory().makeVOElement(cacheFile);
                            NodeList resources = votable.getElementsByTagName("RESOURCE");
                            VOElement resource = (VOElement) resources.item(0);
                            if ((resource.getAttribute("name")).equals("Error")) {
                              logger.error("ConeSearchDialog:"+resource.
                                   getChildByName("TABLE").getAttribute("value"));
                            }
                            if (resource.getChildByName("TABLE").getAttribute("name").equals("Error")) {
                               logger.error("ConeSearchDialog:"+resource.
                                   getChildByName("TABLE").getAttribute("value"));
                            }                           
                            VOElement[] tables = resource.getChildrenByName("TABLE");
                            TableElement tableElement = (TableElement) tables[0];
                            table = new VOStarTable(tableElement);

                            for (int colInd = 0; colInd < table.getColumnCount(); colInd++) {
                                ColumnInfo colInfo = table.getColumnInfo(colInd);
                                if (RA_UCD.equalsIgnoreCase(colInfo.getUCD())) {
                                    raColIndex = colInd;
                                    _colNames[0] = colInfo.getName();
                                    classTypes[0]= colInfo.getContentClass();
                                } else if (DEC_UCD.equalsIgnoreCase(colInfo.getUCD())) {
                                    decColIndex = colInd;
                                    _colNames[1] = colInfo.getName();
                                    classTypes[1]= colInfo.getContentClass();
                                } else if (colInfo.getName().equals("k_m")) {
                                    magColIndex = colInd;
                                    _colNames[2] = colInfo.getName();
                                    classTypes[2]= colInfo.getContentClass();
                                }
                            }
                        }
                        else if(source.equals("GSC2")){
                            table = (StarTable) new TstTableBuilder()
                              .makeStarTable( DataSource.makeDataSource(cacheFile.getAbsolutePath()),
                              true, StoragePolicy.getDefaultPolicy() );
                            raColIndex = 1;
                            decColIndex = 2;
                            magColIndex = 4;
                            ColumnInfo colInfo = table.getColumnInfo(raColIndex);
                            _colNames[0] = colInfo.getName();
                            classTypes[0] = colInfo.getContentClass();
                            colInfo = table.getColumnInfo(decColIndex);
                            _colNames[1] = colInfo.getName();
                            classTypes[1] = colInfo.getContentClass();
                            colInfo = table.getColumnInfo(magColIndex);
                            _colNames[2] = colInfo.getName();
                            classTypes[2] = colInfo.getContentClass();
                        }
                        else if(source.equals("USNO") ){
                            table = (StarTable) new TstTableBuilder()
                              .makeStarTable( DataSource.makeDataSource(cacheFile.getAbsolutePath()),
                              true, StoragePolicy.getDefaultPolicy() );
                            raColIndex = 1;
                            decColIndex = 2;
                            magColIndex = 3;
                            ColumnInfo colInfo = table.getColumnInfo(raColIndex);
                            _colNames[0] = colInfo.getName();
                            classTypes[0] = colInfo.getContentClass();
                            colInfo = table.getColumnInfo(decColIndex);
                            _colNames[1] = colInfo.getName();
                            classTypes[1] = colInfo.getContentClass();
                            colInfo = table.getColumnInfo(magColIndex);
                            _colNames[2] = colInfo.getName();
                            classTypes[2] = colInfo.getContentClass();  
                        }


                        c.setStarTable(table);
                        _colIndexes[0] = raColIndex;
                        _colIndexes[1] = decColIndex;
                        if(source.equals("2MassPsc")){
                            if(minMagTextField.getText().equals("") &&
                                !(maxMagTextField.getText().equals(""))){
                                  _cond = "$k_m<="+maxMagTextField.getText().trim();
                            }else if(maxMagTextField.getText().equals("") &&
                                !(minMagTextField.getText().equals(""))){
                                  _cond = "$k_m>="+minMagTextField.getText().trim();
                            }else if(minMagTextField.getText().equals("") &&
                                maxMagTextField.getText().equals("")){
                                  _cond = "" ;
                            }else{
                                  _cond = minMagTextField.getText().trim()+"<=$k_m && $k_m <="
                                        +maxMagTextField.getText().trim();
                            }
                            _colIndexes[2] = magColIndex;
                            if(_cond!=null && !(_cond.equals("")))
                                _compileExpressions();
                            prop.put("codition", _cond);
                        }
                        if(source.equals("GSC2")){
                            if(minMagTextField.getText().equals("") &&
                                !(maxMagTextField.getText().equals(""))){
                                  _cond = "$Jmag<="+maxMagTextField.getText().trim();
                            }else if(maxMagTextField.getText().equals("") &&
                                !(minMagTextField.getText().equals(""))){
                                  _cond = "$Jmag>="+minMagTextField.getText().trim();
                            }else if(minMagTextField.getText().equals("") &&
                                maxMagTextField.getText().equals("")){
                                  _cond = "" ;
                            }else{
                                  _cond = minMagTextField.getText().trim()+"<=$Jmag && $Jmag <="
                                        +maxMagTextField.getText().trim();
                            }
                            _colIndexes[2] = magColIndex;
                            if(_cond!=null && !(_cond.equals("")))
                                _compileExpressions();
                            prop.put("codition", _cond);
                        }
                        if(source.equals("USNO")){
                            if(minMagTextField.getText().equals("") &&
                                !(maxMagTextField.getText().equals(""))){
                                  _cond = "$r_mag<="+maxMagTextField.getText().trim();
                            }else if(maxMagTextField.getText().equals("") &&
                                !(minMagTextField.getText().equals(""))){
                                  _cond = "$r_mag>="+minMagTextField.getText().trim();
                            }else if(minMagTextField.getText().equals("") &&
                                maxMagTextField.getText().equals("")){
                                  _cond = "" ;
                            }else{
                                  _cond = minMagTextField.getText().trim()+"<=$r_mag && $r_mag <="
                                        +maxMagTextField.getText().trim();
                            }
                            _colIndexes[2] = magColIndex;
                            if(_cond!=null && !(_cond.equals("")))
                                _compileExpressions();
                            prop.put("codition", _cond);
                        }
                        c.setProperties(prop);
                        String columnNames[] = new String[table.getColumnCount()];
                        for(int j = 0;j < table.getColumnCount();j++){
                            columnNames[j] = table.getColumnInfo(j).getName();
                        }
                        tableModel = new MyTableModel(table);
                        //tableModel.initiallize((int) table.getRowCount(),table.getColumnCount());
                        tableModel.setColNames(columnNames);
                        boolean condition = false;
                        RowSequence res = table.getRowSequence();
                        int rowInd = 0;
                        int tableModelIndex = 0;
                        while(res.next()){
                            rowCountIndex = rowInd;
                            cells = res.getRow();
                            if(source.equals("2MassPsc")){
                                if(_cond != null && !(_cond.equals(""))){
                                    condition = _condExpr.evalBoolean();
                                }
                                else
                                    condition = true;
                                if(condition == true){
                                    String str = cells[raColIndex].toString();
                                    Double d1 = Double.parseDouble(str);
                                    raList.add(d1);
                                    str = cells[decColIndex].toString();
                                    d1 = Double.parseDouble(str);
                                    decList.add(d1);
                                    for(int j = 0;j<table.getColumnCount();j++){
                                         if(cells[j] != null){
                                            tableModel.setValueAt(cells[j], tableModelIndex, j);
                                         }
                                    }
                                    tableModelIndex++;
                                }
                            }
                            else if(source.equals("GSC2") || source.equals("USNO")){
                                if(_cond != null && !(_cond.equals(""))){
                                    condition = _condExpr.evalBoolean();
                                }
                                else
                                    condition = true;
                                String str = cells[raColIndex].toString();
                                Double d1 = Double.parseDouble(str);
                                raList.add(d1);
                                str = cells[decColIndex].toString();
                                d1 = Double.parseDouble(str);
                                decList.add(d1);
                                for(int j = 0;j<table.getColumnCount();j++){
                                     if(cells[j] != null){
                                         tableModel.setValueAt(cells[j], tableModelIndex, j);
                                     }
                                }
                                tableModelIndex++;
                            }
                            rowInd++;
                        }
                        Object[][] tempData = new Object[raList.size()][3];
                        for (rowInd = 0; rowInd < raList.size(); rowInd++) {
                                tempData[rowInd][0] = raList.get(rowInd);
                                tempData[rowInd][1] = decList.get(rowInd);
                        }
                        c.setData(tempData);
                        c.setColNames(_colNames);
                    }
//                    catch (MalformedURLException ex) {
//                        logger.error("ConeSearchDialog:", ex);
//                    }catch (SAXException ex) {
//                        logger.error("ConeSearchDialog:", ex);
//                    } catch (IOException ex) {
//                        logger.error("ConeSearchDialog:", ex);
//                    } catch (Throwable ex) {
//                        logger.error("ConeSearchDialog:", ex);
//                    }
                    catch(Throwable th){
                        throw new Exception(th);
                    }
                    finally{}
                }
                else{
                  logger.info("Task cancelled");
                }
                return null;
          }

          @Override
          protected void finished() {
                super.finished();
                if (isCancelled()) {
                    logger.info("[TASK CANCELLED] in finished method");
                    doCleanup();
                }
          }

          @Override
          protected void failed(Throwable cause) {
                super.failed(cause);
                if (!isCancelled()) {
                    logger.warn("Some thing went wrong while loading image", cause);
                    String msg = cause.getMessage();
                    if(msg == null) {
                        JOptionPane.showMessageDialog(csDialog,
                                "Catalog fetch and load failed");
                    }
                    else {
                        if(msg.length() > 100)
                            msg = msg.substring(0, 100);
                        JOptionPane.showMessageDialog(csDialog, msg);
                    }

                    doCleanup();
                }
                else {
                    logger.debug("[TASK CANCELLED] in failed method", cause);
                }
          }

          @Override
          protected void succeeded(Object result) {
                super.succeeded(result);
                if (!isCancelled()) {
                    try {
                        retrieving = false;
                        _timer.cancel();
                        showTable(table);
                        if(!raList.isEmpty() || !decList.isEmpty()){
                          plotButton.setEnabled(true);
                        }
                    } catch (IOException ex) {
                        cache.remove(urlToDownload);
                        logger.error("ConeSearchDialog:", ex);
                    }
                }
                else {
                    if(result != null)
                        logger.debug("[TASK CANCELLED] in success method" + result);
                    else
                        logger.debug("[TASK CANCELLED] in success method");
                }

            }

            private void doCleanup() {
                try {
                    retrieving = false;
                    _timer.cancel();
                    if(tableModel != null)
                        tableModel.clear();
                    showTable(table);
                } catch (IOException ex) {
                    cache.remove(urlToDownload);
                    logger.error("ConeSearchDialog:", ex);
                }
            }
        };
        retrieving = true;
        worker.execute();
        showTable(table);                                 
    }

    //TO-DO:find some way to put column info somewhere.
    public void showTable(final StarTable table) throws IOException{      
        if(retrieving && isCancelledFlag == false) {
            final String labelText = "Retrieving .";
            final JLabel _messageLabel = new JLabel(labelText);
            final JScrollPane scrollPane = new JScrollPane(_messageLabel);
            tablePanel.add(scrollPane);
            tablePanel.revalidate();
            _timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {                    
                    int temp = labelText.length();
                    if (_messageLabel.getText().length() < temp + 5) {
                        _messageLabel.setText(_messageLabel.getText() + ".");
                    } else {
                        _messageLabel.setText("Retrieving .");
                    }                                           
                 }
            
                 public boolean cancel() {
                    return true;
                 }
            };
            _timer.schedule(task, 200, 200);          
        }
        else{ 
            tablePanel.removeAll();           
            JTable displayTable = new JTable(tableModel);
            displayTable.setShowGrid(true);
            String temp = "";            
            displayTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            TableRowSorter sorter = new TableRowSorter(tableModel);
            displayTable.setRowSorter(sorter);
            JScrollPane scrollPane = new JScrollPane(displayTable);
            tablePanel.add(scrollPane);
            tablePanel.revalidate();  
        }
    }

    public static void main(String args[]){        
//        try {
//            ConeSearchDialog csd = new ConeSearchDialog("http://gsss.stsci.edu/webservices/vo/ConeSearch.aspx?CAT=GSC23", 10.6847083, 41.26875, 2);
//        } catch (MalformedURLException ex) {
//        Logger.getLogger(ConeSearchDialog.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SAXException ex) {
//            Logger.getLogger(ConeSearchDialog.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(ConeSearchDialog.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
}
