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
    private static final String FILE_PATH = "c:/Users/favia/OneDrive/Documents/FitnessKu/src/model/Workout.xml";

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
        public static class Exercise {
        public String name;
        public String video;
        public String description;
        public int repeat;
    }

    public static List<Exercise> getExercisesByOption(String optionName) {
        List<Exercise> exercises = new ArrayList<>();
        boolean foundOption = false;
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                // Coba path alternatif jika file tidak ditemukan
                xmlFile = new File("./src/model/Workout.xml");
                if (!xmlFile.exists()) {
                    System.out.println("[DEBUG] Workout.xml tidak ditemukan di path: " + FILE_PATH + " atau ./src/model/Workout.xml");
                    return exercises;
                }
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList optionNodes = doc.getElementsByTagName("option");
            String target = optionName.trim().toLowerCase();
            System.out.println("[DEBUG] Semua opsi workout yang ditemukan di XML:");
            for (int i = 0; i < optionNodes.getLength(); i++) {
                Element optionElem = (Element) optionNodes.item(i);
                String optNameRaw = optionElem.getAttribute("name");
                String optName = optNameRaw.trim().toLowerCase();
                System.out.println("[DEBUG] - '" + optNameRaw + "' (trimmed: '" + optName + "')");
                if (optName.equals(target)) {
                    foundOption = true;
                    NodeList exerciseNodes = optionElem.getElementsByTagName("exercise");
                    for (int j = 0; j < exerciseNodes.getLength(); j++) {
                        Element exElem = (Element) exerciseNodes.item(j);
                        Exercise ex = new Exercise();
                        ex.name = getTagValue(exElem, "name");
                        ex.video = getTagValue(exElem, "video");
                        ex.description = getTagValue(exElem, "description");
                        try {
                            ex.repeat = Integer.parseInt(getTagValue(exElem, "repeat"));
                        } catch (Exception e) {
                            ex.repeat = 10;
                        }
                        exercises.add(ex);
                    }
                    break;
                }
            }
            if (!foundOption) {
                System.out.println("[DEBUG] Opsi workout tidak ditemukan di Workout.xml: " + optionName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return exercises;
    }

    private static String getTagValue(Element elem, String tag) {
        NodeList nl = elem.getElementsByTagName(tag);
        if (nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return "";
    }
}
