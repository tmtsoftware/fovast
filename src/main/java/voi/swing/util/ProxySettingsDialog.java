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

package voi.swing.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 
 * @author vivekananda_moosani
 */
public class ProxySettingsDialog
{

    private static Logger _logger = LoggerFactory.getLogger(ProxySettingsDialog.class);

    public static final String PROPERTIES_FILE = "proxy-settings.props";
    public static final String PROXY_HOST = "http.proxyHost";
    public static final String PROXY_PORT = "http.proxyPort";
    public static final String NON_PROXY_HOSTS = "http.nonProxyHosts";
    public static final String USE_PROXY = "httpx.useProxy";
	public static final String PROXY_USER = "httpx.proxyUser";
	public static final String PROXY_PASSWORD = "httpx.proxyPassword";
	public static final String REQUIRES_AUTH = "httpx.requiresAuthentication";
    public static final String SAVE_INFO = "save_info";

    //proxy settings file
    private File _proxySettingsFile;
    
	//saved settings
    private String _storedProxyPort;
    private String _storedProxyHost;
    private String _storedNonProxyHosts;
    private boolean _storedUseProxy;
    private boolean _storedSaveInfo;
    private String _storedUserName;
    private String _storedPassword;
	private boolean _storedRequiresAuthentication;
    
    //current system props
    String systemProxyHost;
    String systemProxyPort;
    String systemNonProxyHosts;
    String systemRequiresAuth;
    String systemProxyUsername;
    String systemProxyPassword;


    //components
    JTextField _proxyHostTextField;
    JTextField _proxyPortTextField;
    JTextField _nonProxyHostsTextField;
    JCheckBox _useProxyCheckBox;
    JCheckBox _saveCheckBox;
    JButton _okButton;
    JButton _cancelButton;
	private JPanel _proxyPanel;
	private JLabel _proxyHostLabel;
	private JLabel _proxyPortLabel;
	private JLabel _nonProxyHostsLabel;
	private JLabel _nonProxyHostsExampleLabel;
	private JCheckBox _requireAuthenticationCheckbox;
	private JLabel _userNameLabel;
	private JTextField _userNameTextField;
	private JLabel _passwordLabel;
	private JPasswordField _passwordTextField;
    private JDialog _dialog;

    /**
     *
     * @param comp - Used to determine parent of the dialog and to set
     *               the relative location dialog
     */
    public ProxySettingsDialog(Component comp, File proxySettingsFile)
    {
        _dialog = DialogCreator.createDialog(comp);
        this._proxySettingsFile = proxySettingsFile;
        //add components
        jbInit();

        systemProxyHost = System.getProperty(ProxySettingsDialog.PROXY_HOST);
        if(systemProxyHost != null && systemProxyHost.trim().length() == 0)
            systemProxyHost = null;
        systemProxyPort = System.getProperty(ProxySettingsDialog.PROXY_PORT);
        if(systemProxyPort != null && systemProxyPort.trim().length() == 0)
            systemProxyPort = null;
        systemNonProxyHosts = System.getProperty(ProxySettingsDialog.NON_PROXY_HOSTS);
        if(systemNonProxyHosts != null && systemNonProxyHosts.trim().length() == 0)
            systemNonProxyHosts = null;
        systemRequiresAuth = System.getProperty(ProxySettingsDialog.REQUIRES_AUTH);
        if(systemRequiresAuth != null && systemRequiresAuth.trim().length() == 0)
            systemRequiresAuth = null;
        systemProxyUsername = System.getProperty(ProxySettingsDialog.PROXY_USER);
        if(systemProxyUsername != null && systemProxyUsername.trim().length() == 0)
            systemProxyUsername = null;
        systemProxyPassword = System.getProperty(ProxySettingsDialog.PROXY_PASSWORD);
        if(systemProxyPassword != null && systemProxyPassword.trim().length() == 0)
            systemProxyPassword = null;
        
        readStoredSettings();
        
        //initialize textfields and check boxes
        if(systemProxyHost == null)
            _proxyHostTextField.setText(_storedProxyHost);
        else
            _proxyHostTextField.setText(systemProxyHost);
        
        if(systemProxyPort == null)
            _proxyPortTextField.setText(_storedProxyPort);
        else
            _proxyPortTextField.setText(systemProxyPort);
        
        if(systemNonProxyHosts == null)
            _nonProxyHostsTextField.setText(_storedNonProxyHosts);
        else
        	_nonProxyHostsTextField.setText(systemNonProxyHosts);

        if(systemProxyUsername == null)
        	_userNameTextField.setText(_storedUserName);
        else
        	_userNameTextField.setText(systemProxyUsername);
        
        if(systemProxyPassword == null)
        	_passwordTextField.setText(_storedPassword);
        else
        	_passwordTextField.setText(systemProxyPassword);
        
        _saveCheckBox.setSelected(_storedSaveInfo);

        boolean b =false;
        if(systemProxyHost != null && systemProxyPort != null)
        {
        	_useProxyCheckBox.setSelected(true);
            b = true;
        }
        else if(_storedUseProxy)
        {
            _useProxyCheckBox.setSelected(true);
            b = true;
        }
        else
        {
            _useProxyCheckBox.setSelected(false);
            b = false;
        }

        if(systemRequiresAuth != null &&
                (new Boolean(systemRequiresAuth) == true)) {
            _requireAuthenticationCheckbox.setSelected(true);
        }
        else if(systemProxyUsername != null && systemProxyPassword != null)
        {
        	_requireAuthenticationCheckbox.setSelected(true);
        }
        else if(systemRequiresAuth == null && _storedRequiresAuthentication)
        {
        	_requireAuthenticationCheckbox.setSelected(true);
        }
        else
        {
        	_requireAuthenticationCheckbox.setSelected(false);
        }
        enableProxyStuff(b);

        //action listeners'
        addListeners();
        
        _dialog.pack();
        //_dialog.setSize(400, 400);
        _dialog.setResizable(false);
        _dialog.setLocationRelativeTo(comp);
        _dialog.setModal(true);
        _dialog.setVisible(true);
    }

    private void enableProxyStuff(boolean b) {
        _proxyHostLabel.setEnabled(b);
        _proxyHostTextField.setEnabled(b);
        _proxyPortLabel.setEnabled(b);
        _proxyPortTextField.setEnabled(b);
        _nonProxyHostsLabel.setEnabled(b);
        _nonProxyHostsExampleLabel.setEnabled(b);
        _nonProxyHostsTextField.setEnabled(b);
        _requireAuthenticationCheckbox.setEnabled(b);
        enableAuthStuff(b && _requireAuthenticationCheckbox.isSelected());
    }

	private void enableAuthStuff(boolean b) {
    	_userNameLabel.setEnabled(b);
    	_userNameTextField.setEnabled(b);
    	_passwordLabel.setEnabled(b);
    	_passwordTextField.setEnabled(b);
	}

	private void addListeners() {
    	
        _useProxyCheckBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
                boolean b = _useProxyCheckBox.isSelected();
                enableProxyStuff(b);
            }
        });
        
        _requireAuthenticationCheckbox.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent ae)
        	{
        		boolean b = _requireAuthenticationCheckbox.isSelected();
        		enableAuthStuff(b);
        	}
        });
        
        _okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                if(_useProxyCheckBox.isSelected())
                {
                    System.setProperty(PROXY_HOST, _proxyHostTextField.getText());
                	System.setProperty(PROXY_PORT, _proxyPortTextField.getText());
                    System.setProperty(NON_PROXY_HOSTS, _nonProxyHostsTextField.getText());
                    if(_requireAuthenticationCheckbox.isSelected())
                    {
                        System.setProperty(REQUIRES_AUTH, "true");
                    	System.setProperty(PROXY_USER, _userNameTextField.getText());
                    	System.setProperty(PROXY_PASSWORD, 
                    			new String(_passwordTextField.getPassword()));
                        Authenticator.setDefault(new SimpleAuthenticator());
                    }
                    else
                    {
                        //TODO: Check if this would work as clearProperty in jdk5.0
                        System.setProperty(REQUIRES_AUTH, "false");
                        if(systemProxyUsername != null)
                            System.setProperty(PROXY_USER, "");
                        if(systemProxyPassword != null)
                            System.setProperty(PROXY_PASSWORD, "");
                        Authenticator.setDefault(null);
                    }
                }
                else
                {
                    
                    if(systemProxyHost != null)
                        System.setProperty(PROXY_HOST, "");
                    if(systemProxyPort != null)
                        System.setProperty(PROXY_PORT, "");
                    if(systemNonProxyHosts != null)
                        System.setProperty(NON_PROXY_HOSTS, "");
                	if(systemProxyUsername != null)
                	    System.setProperty(PROXY_USER, "");
                	if(systemProxyPassword != null)
                	    System.setProperty(PROXY_PASSWORD, "");
                }
                
                OutputStream  os = null;
                try
                {

                    if(!_proxySettingsFile.exists())
                        _proxySettingsFile.createNewFile();
                    
                    Properties props = new Properties();
                    
                    if(_saveCheckBox.isSelected())
                        props.put(SAVE_INFO, "true");
                    else
                        props.put(SAVE_INFO, "false");
                    
                    if(_saveCheckBox.isSelected())
                    {
                        if(_useProxyCheckBox.isSelected())
                            props.put(USE_PROXY, "true");
                        else
                            props.put(USE_PROXY, "false");
                        
                        if(!_proxyHostTextField.getText().trim().equals(""))
                            props.put(PROXY_HOST, _proxyHostTextField.getText());
                        if(!_proxyPortTextField.getText().trim().equals(""))
                            props.put(PROXY_PORT, _proxyPortTextField.getText());
                        
                        if(!_nonProxyHostsTextField.getText().trim().equals(""))
                            props.put(NON_PROXY_HOSTS, _nonProxyHostsTextField.getText());
                        
                        if(_requireAuthenticationCheckbox.isSelected())
                        	props.put(REQUIRES_AUTH, "true");
                        else
                        	props.put(REQUIRES_AUTH, "false");
                        
                        if(!_userNameTextField.getText().trim().equals(""))
                            props.put(PROXY_USER, _userNameTextField.getText());
                        // write password only if not empty. we donot trim passwords
                        //as a password could be spacecharacters even
                        if( !(_passwordTextField.getPassword().length == 0) )
                        {
                            //TODO: Should be using some string encrypter
                            props.put(PROXY_PASSWORD, 
                            		        new String(_passwordTextField.getPassword()));  
                        }
                        
                        os = new FileOutputStream(_proxySettingsFile);
                        props.store(os ,"VOPlot Proxy Settings");
                    }
                    else
                    {
                    	
                    }                    
                }
                catch(Exception ex){
                    System.err.println("Problem storing settings: " +
                    		""+ ex.getMessage() );
                }
                finally
                {
                	try
                	{
                		if(os != null)
                			os.close();
                	}catch(Exception ex){
                		System.err.println("Problem closing settings file");
                	}
                }
                
                _dialog.setVisible(false);
                _dialog.dispose();
            }
                
        });
        
        _cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae)
            {
                _dialog.setVisible(false);
                _dialog.dispose();
            }
        });
	}
	
	private void readStoredSettings() {
        String useProxyStr = null;
        String saveInfoStr = null;
        String requiresAuthentication = null;
        
        //change here should reflect in PlotApplication
        InputStream is = null;
        try
        {
	        if(_proxySettingsFile.exists() && _proxySettingsFile.isFile())
	        {
	            Properties props = new Properties();
	            is  = new FileInputStream(_proxySettingsFile);
	            props.load(is);
	            is.close();
	            _storedProxyHost = props.getProperty(PROXY_HOST);
	            _storedProxyPort = props.getProperty(PROXY_PORT);
	            _storedNonProxyHosts = props.getProperty(NON_PROXY_HOSTS);
	            _storedUserName = props.getProperty(PROXY_USER);
	            _storedPassword = props.getProperty(PROXY_PASSWORD);	            
	            useProxyStr = 
	            	props.getProperty(USE_PROXY);
	            requiresAuthentication =
	            	props.getProperty(REQUIRES_AUTH);
	            saveInfoStr = 
	                props.getProperty(SAVE_INFO);
	        }
	        
	        if(_storedProxyHost == null)
	            _storedProxyHost = "";
	        if(_storedProxyPort == null)
	            _storedProxyPort = "";
	        if(_storedUserName == null)
	        	_storedUserName = "";
	        
	        if(_storedPassword == null)
	        	_storedPassword = "";
	        else
	        {
                //TODO: Read should be using some decrypter class if encryption
                //comes into picture
	        }
	        
	        if(useProxyStr == null)
	            _storedUseProxy =  false;
	        else
	            _storedUseProxy = new Boolean(useProxyStr).booleanValue();
	        
	        if(requiresAuthentication == null)
	        	_storedRequiresAuthentication = false;
	        else
	        	_storedRequiresAuthentication = new Boolean(
	        			requiresAuthentication).booleanValue();
	        
	        if(saveInfoStr == null)
	            _storedSaveInfo =  false;
	        else
	            _storedSaveInfo = new Boolean(saveInfoStr).booleanValue();
        }
        catch(Exception e) 
        {
            System.err.println("Problem reading settings: " + e.getMessage());
        }
        finally
        {
        	try
        	{
        		if(is != null)
        			is.close();
        	}catch(Exception e){
        		System.err.println("Problem closing settings file");
        	}
        }
        
	}

	/**
     *  
     */
    private void jbInit() {
        _dialog.setTitle("Proxy Settings");
        Container contentPane = _dialog.getContentPane();        

        //add components
        contentPane.setLayout(new BorderLayout());
        
        _proxyPanel = new JPanel();
        contentPane.add(_proxyPanel);
        _proxyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(
                5,5,5,5),BorderFactory.createTitledBorder("Proxy Settings")));
        _proxyPanel.setLayout(new GridBagLayout());//new BoxLayout(panel,
                                             // BoxLayout.Y_AXIS));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor=GridBagConstraints.WEST;
        gbc.insets =  new Insets(0, 0, 8, 0);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        _useProxyCheckBox = new JCheckBox("Use proxy");
        _proxyPanel.add(_useProxyCheckBox, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        _proxyHostLabel = new JLabel("Proxy Hostname");
        _proxyPanel.add(_proxyHostLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        _proxyHostTextField = new JTextField(30);
        _proxyPanel.add(_proxyHostTextField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        _proxyPortLabel = new JLabel("Proxy Port");
        _proxyPanel.add(_proxyPortLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        _proxyPortTextField = new JTextField(30);
        _proxyPortTextField.setPreferredSize(new Dimension(100, 30));
        _proxyPanel.add(_proxyPortTextField, gbc); 
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        _nonProxyHostsLabel = new JLabel("No Proxy For");
        _proxyPanel.add(_nonProxyHostsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        _nonProxyHostsTextField = new JTextField(30);
        _proxyPanel.add(_nonProxyHostsTextField, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        _nonProxyHostsExampleLabel = 
        	new JLabel("Example: localhost|10.*|ps4322|*.xyz.com");
        _proxyPanel.add(_nonProxyHostsExampleLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        _requireAuthenticationCheckbox = new JCheckBox("Requires Authentication");
        _proxyPanel.add(_requireAuthenticationCheckbox, gbc);
        gbc.gridwidth = 1;
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        _userNameLabel = new JLabel("User Name");
        _proxyPanel.add(_userNameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1;
        _userNameTextField = new JTextField();
        _proxyPanel.add(_userNameTextField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0;
        _passwordLabel = new JLabel("Password");
        _proxyPanel.add(_passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1;
        _passwordTextField = new JPasswordField();
        _proxyPanel.add(_passwordTextField, gbc);

        //Bottom ppanel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bottomPanel.setLayout(new BorderLayout());
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        
        _saveCheckBox = new JCheckBox("Save Settings");
        bottomPanel.add(_saveCheckBox, BorderLayout.NORTH);       

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        _okButton = new JButton("OK");
        _cancelButton = new JButton("Cancel");
        buttonPanel.add(_okButton);
        buttonPanel.add(_cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
    }    
    
    /**
     * Simple Authenticator class
     * 
     * @author vivekananda_moosani
     */
    public static class SimpleAuthenticator extends Authenticator
    {
    	protected PasswordAuthentication getPasswordAuthentication() 
    	{
    		
    		String userName = null;
    		char[] pwd = null;
    		
    		userName = System.getProperty(ProxySettingsDialog.PROXY_USER);
    		String pwdString = System.getProperty(ProxySettingsDialog.PROXY_PASSWORD);
    			
    		//if username is null we send a null
    		if(userName == null || userName.trim().equals(""))
    			return null;
    		//if pwdstring is null we send a empty string for it
    		if(pwdString == null)
    			pwd = new char[0];
    		else
    			pwd = pwdString.toCharArray();
    			
    		//System.out.println("Here");
    		return new PasswordAuthentication(userName, pwd);
    	}
    }
    //Simple Authenticator class ends

	public static void readSettingsFile(File settingsFile)
	{
	    try
	    {

	        String systemProxyHost = System.getProperty(ProxySettingsDialog.PROXY_HOST);
	        String systemProxyPort = System.getProperty(ProxySettingsDialog.PROXY_PORT);
	        String systemNonProxyHost = System.getProperty(ProxySettingsDialog.NON_PROXY_HOSTS);
	        String systemProxyUser = System.getProperty(ProxySettingsDialog.PROXY_USER);
	        String systemProxyPassword = System.getProperty(ProxySettingsDialog.PROXY_PASSWORD);
	        
	        String proxyHost, proxyPort, nonProxyHost;
	        String proxyUser, proxyPassword;
	        boolean saveInfo = false;
	        boolean useProxy = false;
	        boolean requiresAuthentication = false;

	        if(settingsFile.exists() && settingsFile.isFile())
	        {
	            Properties props = new Properties();
	            InputStream is = new FileInputStream(settingsFile);
	            props.load(is);
	            is.close();
	            //give the command line settings more preference
	            proxyHost = props.getProperty(ProxySettingsDialog.PROXY_HOST);
	            proxyPort = props.getProperty(ProxySettingsDialog.PROXY_PORT);
	            nonProxyHost = props.getProperty(ProxySettingsDialog.NON_PROXY_HOSTS);
	          	proxyUser = props.getProperty(ProxySettingsDialog.PROXY_USER);
	           	proxyPassword = props.getProperty(ProxySettingsDialog.PROXY_PASSWORD);
	           	
	           	if(proxyPassword != null) {
	           	    //TODO: should use decryption if password is encrypted
                }
	           	
	            String useProxyStr = 
	            	props.getProperty(ProxySettingsDialog.USE_PROXY);
	            String saveInfoStr = 
	                props.getProperty(ProxySettingsDialog.SAVE_INFO);
	            String requiresAuthenticationStr = 
	            	props.getProperty(ProxySettingsDialog.REQUIRES_AUTH);
	            
	            if(useProxyStr == null)
	                useProxy =  false;
	            else
	            {
	                useProxy = new Boolean(useProxyStr).booleanValue();
	            }
	            
	            if(requiresAuthenticationStr == null)
	            	requiresAuthentication = false;
	            else
	            	requiresAuthentication = new Boolean(requiresAuthenticationStr
	            			).booleanValue();
	            
	            if(useProxy)
	            {
	            	if(systemProxyHost == null && proxyHost != null)
	            		System.setProperty(ProxySettingsDialog.PROXY_HOST, proxyHost);
	            	if(systemProxyPort == null && proxyPort != null)
	            		System.setProperty(ProxySettingsDialog.PROXY_PORT, proxyPort);
	            	if(systemNonProxyHost == null && nonProxyHost != null)
	            		System.setProperty(ProxySettingsDialog.NON_PROXY_HOSTS, nonProxyHost);
	                if(requiresAuthentication)
	                {
	                	if(systemProxyUser == null && proxyUser != null)
	                		System.setProperty(ProxySettingsDialog.PROXY_USER, proxyUser);
	                	if(systemProxyPassword == null && proxyPassword != null)
	                		System.setProperty(ProxySettingsDialog.PROXY_PASSWORD, 
	                			proxyPassword);

                        Authenticator.setDefault(new SimpleAuthenticator());
	                }
	            }	            
	        }
	        else //if settings file doesnot exist
	        {
	        	//We will assume some default initialization here
	        	
	        	//TODO: Should we also make this guess in the above if part i.e
	        	//even if user has set some thing 
	        	//or has left the field empty for nonProxyHosts and append to what
	        	//ever user has typed in the textfield for nonProxyHosts
	        	String nonProxyHostsValue = 
	        		System.getProperty(ProxySettingsDialog.NON_PROXY_HOSTS);
	        	if(nonProxyHostsValue == null || nonProxyHostsValue.trim() == "")
	        	{
	        		nonProxyHostsValue = "";
	        		
	        		ArrayList nonProxyHostStrings = new ArrayList();
	        		nonProxyHostStrings.add("localhost");
	        		nonProxyHostStrings.add("127.0.0.1");
	        		
	        		try
	        		{
		        		InetAddress inetAdress = 
		        			InetAddress.getLocalHost();	        		
		        		//read address
		        		String ip = inetAdress.getHostAddress();
		        		if(!nonProxyHostStrings.contains(ip))
		        			nonProxyHostStrings.add(ip);
		        		//read host name
		        		String hostname = inetAdress.getHostName();
		        		if(!nonProxyHostStrings.contains(hostname))
		        			nonProxyHostStrings.add(hostname);
		        		//TODO: This piece of code has not been checked
		        		//This is being done for handling multiple addresses
		        		//attached to a machine and different hostnames for each
		        		//address at a Name Look up Service
		        		InetAddress[] adresses = InetAddress.getAllByName(hostname);
		        		for(int i=0; i<adresses.length; i++)
		        		{
		        			String temp = adresses[i].getHostAddress();
		        			if(!nonProxyHostStrings.contains(temp))
			        			nonProxyHostStrings.add(temp);
		        			temp = adresses[i].getCanonicalHostName();
		        			if(!nonProxyHostStrings.contains(temp))
			        			nonProxyHostStrings.add(temp);
		        		}
	        		}
	        		catch(Error e) //Security & Other exceptions //Having error
	        		//instead of exception is although a kill.
	        		{
		        		System.setProperty(ProxySettingsDialog.NON_PROXY_HOSTS, 
		        				nonProxyHostsValue);	        			        			
	        		}
	        		
	        		for(int i=0; i<nonProxyHostStrings.size(); i++)
	        		{
	        			nonProxyHostsValue += (String)(nonProxyHostStrings.get(i)) + "|";
	        		}
        			System.setProperty(ProxySettingsDialog.NON_PROXY_HOSTS,
        					nonProxyHostsValue);
	        	}
	        	else
	        	{
	        		//TODO: Should we make the guess as above and append it to
	        		//any commandline(-Dhttp.nonProxyHosts) property setting user 
	        		//has done.
	        	}
	        }
	    
	    }
	    catch(Exception e) //There also might be some security exceptions thrown
	    {
	        _logger.warn("Error while reading settings file: ", e);
	    }
	}
    
}
