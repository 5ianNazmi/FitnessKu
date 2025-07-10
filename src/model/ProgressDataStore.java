package model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class ProgressDataStore {
    private static final String FILE_PATH = "src/model/Progress.xml";

    public static int getUserProgress(String username) {
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return 0;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList userNodes = doc.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element e = (Element) userNodes.item(i);
                if (e.getAttribute("username").equals(username)) {
                    String val = e.getElementsByTagName("progress").item(0).getTextContent();
                    return Integer.parseInt(val);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static void setUserProgress(String username, int progress) {
        try {
            File xmlFile = new File(FILE_PATH);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            Element root;
            if (xmlFile.exists()) {
                doc = dBuilder.parse(xmlFile);
                root = doc.getDocumentElement();
            } else {
                doc = dBuilder.newDocument();
                root = doc.createElement("progresses");
                doc.appendChild(root);
            }
            // Cari user node
            Element userElem = null;
            NodeList userNodes = root.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element e = (Element) userNodes.item(i);
                if (e.getAttribute("username").equals(username)) {
                    userElem = e;
                    break;
                }
            }
            if (userElem == null) {
                userElem = doc.createElement("user");
                userElem.setAttribute("username", username);
                root.appendChild(userElem);
            } else {
                // Hapus progress lama
                NodeList oldProgress = userElem.getElementsByTagName("progress");
                while (oldProgress.getLength() > 0) {
                    userElem.removeChild(oldProgress.item(0));
                }
            }
            // Tambah progress baru
            Element pElem = doc.createElement("progress");
            pElem.setTextContent(String.valueOf(progress));
            userElem.appendChild(pElem);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(xmlFile));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
