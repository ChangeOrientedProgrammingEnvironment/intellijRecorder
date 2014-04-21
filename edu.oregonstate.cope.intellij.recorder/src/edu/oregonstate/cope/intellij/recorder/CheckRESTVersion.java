package edu.oregonstate.cope.intellij.recorder;

import com.intellij.openapi.project.Project;
import edu.oregonstate.cope.clientRecorder.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by michaelhilton on 4/17/14.
 */
public class CheckRESTVersion {

    private Project project;
    private COPEComponent copeComponent;


    public CheckRESTVersion(COPEComponent copeComponent,Project project) {
        this.project = project;
        this.copeComponent = copeComponent;
    }

    public boolean isThereNewCOPEVersion() {
        try {
            String updateURL = copeComponent.getRecorder().getInstallationProperties().getProperty("updateURL");
            URL url = new URL(updateURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
            }else {

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String xmlString = "";
                String parseString;
                while ((parseString = br.readLine()) != null) {
                    xmlString = xmlString.concat(parseString);
                }

                ByteArrayInputStream stream = new ByteArrayInputStream(xmlString.getBytes());
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(stream);
                doc.getDocumentElement().normalize();

                NodeList nodes = doc.getElementsByTagName("plugin");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node n = nodes.item(i);
                    if (n.getAttributes().getNamedItem("id").getNodeValue().equals("COPERecorder")) {
                        String installedVersion = ((COPEComponent) this.project.getComponent(COPEComponent.class)).getPluginVersion();
                        String updateVersion = n.getAttributes().getNamedItem("version").getNodeValue();
                        if (!installedVersion.equals(updateVersion)) {
                            return true;
                        } else {
                            return false;
                        }
                    }

                }
            }
            conn.disconnect();

        } catch (MalformedURLException | ProtocolException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    return false;
    }
}
