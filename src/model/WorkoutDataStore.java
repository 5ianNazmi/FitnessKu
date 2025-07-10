package model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WorkoutDataStore {
    private static final String FILE_PATH = "src/model/Workout.xml";

    public static List<String> getUserWorkouts(String username) {
        List<String> workouts = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return workouts;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList userNodes = doc.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element e = (Element) userNodes.item(i);
                if (e.getAttribute("username").equals(username)) {
                    NodeList workoutNodes = e.getElementsByTagName("workout");
                    for (int j = 0; j < workoutNodes.getLength(); j++) {
                        workouts.add(workoutNodes.item(j).getTextContent());
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return workouts;
    }

    public static void setUserWorkouts(String username, List<String> workouts) {
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
                root = doc.createElement("workouts");
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
                // Hapus workout lama
                NodeList oldWorkouts = userElem.getElementsByTagName("workout");
                while (oldWorkouts.getLength() > 0) {
                    userElem.removeChild(oldWorkouts.item(0));
                }
            }
            // Tambah workout baru
            for (String w : workouts) {
                Element wElem = doc.createElement("workout");
                wElem.setTextContent(w);
                userElem.appendChild(wElem);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(xmlFile));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
