/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package backup;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 */
public class FovastInstrumentGuiConfig {

    public static FovastInstrumentGuiConfig getFovastInstrumentConfig(URL configURL)
            throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(configURL);

        FovastInstrumentGuiConfig guiConfig = new FovastInstrumentGuiConfig();

        return guiConfig;
    }
}
