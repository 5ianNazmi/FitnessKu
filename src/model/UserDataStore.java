package model;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserDataStore {
    private static final String FILE_PATH = "src/model/User.xml";

    public static class User {
        public String username;
        public String email;
        public String password;
        public List<String> workouts = new ArrayList<>();
        public int poin = 0;
        public int level = 1;
        public int progress = 0;
        public boolean notifEnabled = false;
        public String notifHour = "";
        public String notifMinute = "";
    }

    // Load all users from XML
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return users;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList userNodes = doc.getElementsByTagName("user");
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element e = (Element) userNodes.item(i);
                User u = new User();
                u.username = getTagValue(e, "username");
                u.email = getTagValue(e, "email");
                u.password = getTagValue(e, "password");
                NodeList workoutNodes = e.getElementsByTagName("workout");
                for (int j = 0; j < workoutNodes.getLength(); j++) {
                    u.workouts.add(workoutNodes.item(j).getTextContent());
                }
                u.poin = parseIntSafe(getTagValue(e, "poin"));
                u.level = parseIntSafe(getTagValue(e, "level"));
                u.progress = parseIntSafe(getTagValue(e, "progress"));
                u.notifEnabled = Boolean.parseBoolean(getTagValue(e, "notifEnabled"));
                u.notifHour = getTagValue(e, "notifHour");
                u.notifMinute = getTagValue(e, "notifMinute");
                users.add(u);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return users;
    }

    // Save all users to XML
    public static void saveUsers(List<User> users) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element root = doc.createElement("users");
            doc.appendChild(root);
            for (User u : users) {
                Element userElem = doc.createElement("user");
                appendChild(doc, userElem, "username", u.username);
                appendChild(doc, userElem, "email", u.email);
                appendChild(doc, userElem, "password", u.password);
                Element workoutsElem = doc.createElement("workouts");
                for (String w : u.workouts) {
                    appendChild(doc, workoutsElem, "workout", w);
                }
                userElem.appendChild(workoutsElem);
                appendChild(doc, userElem, "poin", String.valueOf(u.poin));
                appendChild(doc, userElem, "level", String.valueOf(u.level));
                appendChild(doc, userElem, "progress", String.valueOf(u.progress));
                appendChild(doc, userElem, "notifEnabled", String.valueOf(u.notifEnabled));
                appendChild(doc, userElem, "notifHour", u.notifHour);
                appendChild(doc, userElem, "notifMinute", u.notifMinute);
                root.appendChild(userElem);
            }
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(new File(FILE_PATH)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Register user baru
    public static boolean registerUser(String username, String email, String password) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.username.equals(username) || u.email.equals(email)) return false;
        }
        User newUser = new User();
        newUser.username = username;
        newUser.email = email;
        newUser.password = password;
        newUser.notifEnabled = false;
        newUser.notifHour = "";
        newUser.notifMinute = "";
        users.add(newUser);
        saveUsers(users);
        return true;
    }

    // Login user
    public static User loginUser(String username, String password) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.username.equals(username) && u.password.equals(password)) {
                return u;
            }
        }
        return null;
    }

    // Update workouts untuk user tertentu
    public static void updateUserWorkouts(String username, List<String> workouts) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.username.equals(username)) {
                u.workouts = new ArrayList<>(workouts);
                break;
            }
        }
        saveUsers(users);

    }

    // Update notif setting untuk user tertentu
    public static void updateUserNotif(String username, boolean enabled, String hour, String minute) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.username.equals(username)) {
                u.notifEnabled = enabled;
                u.notifHour = hour;
                u.notifMinute = minute;
                break;
            }
        }
        saveUsers(users);
    }

    // Helper
    private static void appendChild(Document doc, Element parent, String tag, String value) {
        Element e = doc.createElement(tag);
        e.setTextContent(value);
        parent.appendChild(e);
    }
    private static String getTagValue(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() > 0) return nl.item(0).getTextContent();
        return "";
    }
    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
}
