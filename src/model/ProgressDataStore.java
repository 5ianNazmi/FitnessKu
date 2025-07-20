package model;

import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class ProgressDataStore {
    public static class WorkoutHistory {
        public String date;
        public String option;
        public int calories;
        public int duration;
    }

    public static List<WorkoutHistory> getDailyWorkoutHistory(String username, String date) {
        List<WorkoutHistory> list = new ArrayList<>();
        System.out.println("Getting daily workout history for user: " + username + " on date: " + date);
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                System.out.println("Progress.xml file does not exist at: " + xmlFile.getAbsolutePath());
                return list;
            }
            System.out.println("Progress.xml file found, parsing...");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList userNodes = doc.getElementsByTagName("user");
            System.out.println("Found " + userNodes.getLength() + " users in XML");
            
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element e = (Element) userNodes.item(i);
                String xmlUsername = e.getAttribute("username");
                System.out.println("Checking user: '" + xmlUsername + "' (looking for: '" + username + "')");
                System.out.println("Username equals check: " + xmlUsername.equals(username));
                System.out.println("Username equals ignore case: " + xmlUsername.equalsIgnoreCase(username));
                
                if (xmlUsername.equals(username)) {
                    System.out.println("Found matching user, getting history...");
                    NodeList historyNodes = e.getElementsByTagName("history");
                    System.out.println("Found " + historyNodes.getLength() + " history entries");
                    
                    for (int j = 0; j < historyNodes.getLength(); j++) {
                        Element h = (Element) historyNodes.item(j);
                        String historyDate = h.getAttribute("date");
                        System.out.println("History entry " + j + ": date='" + historyDate + "' (looking for: '" + date + "')");
                        System.out.println("Date equals check: " + historyDate.equals(date));
                        
                        if (historyDate.equals(date)) {
                            WorkoutHistory wh = new WorkoutHistory();
                            wh.date = date;
                            wh.option = h.getAttribute("option");
                            String caloriesStr = h.getAttribute("calories");
                            String durationStr = h.getAttribute("duration");
                            System.out.println("Raw attributes: option='" + wh.option + "', calories='" + caloriesStr + "', duration='" + durationStr + "'");
                            
                            try { wh.calories = Integer.parseInt(caloriesStr); } catch (Exception ex) { 
                                wh.calories = 0; 
                                System.out.println("Failed to parse calories: " + ex.getMessage());
                            }
                            try { wh.duration = Integer.parseInt(durationStr); } catch (Exception ex) { 
                                wh.duration = 0; 
                                System.out.println("Failed to parse duration: " + ex.getMessage());
                            }
                            list.add(wh);
                            System.out.println("Added workout: " + wh.option + " - " + wh.calories + " cal - " + wh.duration + " min");
                        }
                    }
                    break;
                }
            }
            
            System.out.println("Total workouts found for today: " + list.size());
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
        return list;
    }
    public static void addWorkoutHistory(String username, String workoutOption, int calories, int duration) {
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
            }
            
            // Tambah riwayat workout dengan timestamp
            Element historyElem = doc.createElement("history");
            historyElem.setAttribute("date", java.time.LocalDate.now().toString());
            historyElem.setAttribute("time", java.time.LocalTime.now().toString().substring(0, 8)); // HH:mm:ss
            historyElem.setAttribute("option", workoutOption);
            historyElem.setAttribute("calories", String.valueOf(calories));
            historyElem.setAttribute("duration", String.valueOf(duration));
            userElem.appendChild(historyElem);
            
            // Simpan file
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.transform(new DOMSource(doc), new StreamResult(xmlFile));
            
            System.out.println("Workout history saved: " + username + " - " + workoutOption + " - " + calories + " cal - " + duration + " min");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method untuk mendapatkan total kalori dan durasi semua hari
    public static int getTotalCalories(String username) {
        int total = 0;
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
                    NodeList historyNodes = e.getElementsByTagName("history");
                    for (int j = 0; j < historyNodes.getLength(); j++) {
                        Element h = (Element) historyNodes.item(j);
                        try { total += Integer.parseInt(h.getAttribute("calories")); } catch (Exception ex) { }
                    }
                    break;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return total;
    }

    public static int getTotalDuration(String username) {
        int total = 0;
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
                    NodeList historyNodes = e.getElementsByTagName("history");
                    for (int j = 0; j < historyNodes.getLength(); j++) {
                        Element h = (Element) historyNodes.item(j);
                        try { total += Integer.parseInt(h.getAttribute("duration")); } catch (Exception ex) { }
                    }
                    break;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return total;
    }

    // Method untuk mendapatkan riwayat beberapa hari terakhir
    public static List<WorkoutHistory> getRecentWorkoutHistory(String username, int days) {
        List<WorkoutHistory> list = new ArrayList<>();
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) return list;
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            NodeList userNodes = doc.getElementsByTagName("user");
            
            java.time.LocalDate cutoffDate = java.time.LocalDate.now().minusDays(days);
            
            for (int i = 0; i < userNodes.getLength(); i++) {
                Element e = (Element) userNodes.item(i);
                if (e.getAttribute("username").equals(username)) {
                    NodeList historyNodes = e.getElementsByTagName("history");
                    for (int j = 0; j < historyNodes.getLength(); j++) {
                        Element h = (Element) historyNodes.item(j);
                        String dateStr = h.getAttribute("date");
                        try {
                            java.time.LocalDate historyDate = java.time.LocalDate.parse(dateStr);
                            if (!historyDate.isBefore(cutoffDate)) {
                                WorkoutHistory wh = new WorkoutHistory();
                                wh.date = dateStr;
                                wh.option = h.getAttribute("option");
                                try { wh.calories = Integer.parseInt(h.getAttribute("calories")); } catch (Exception ex) { wh.calories = 0; }
                                try { wh.duration = Integer.parseInt(h.getAttribute("duration")); } catch (Exception ex) { wh.duration = 0; }
                                list.add(wh);
                            }
                        } catch (Exception ex) { }
                    }
                    break;
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        
        // Sort by date (newest first)
        list.sort((a, b) -> b.date.compareTo(a.date));
        return list;
    }
    private static final String FILE_PATH = "src/model/Progress.xml";

    public static int getUserProgress(String username) {
        System.out.println("Getting user progress for: " + username + " from file: " + FILE_PATH);
        try {
            File xmlFile = new File(FILE_PATH);
            if (!xmlFile.exists()) {
                System.out.println("Progress.xml file does not exist at: " + xmlFile.getAbsolutePath());
                return 0;
            }
            System.out.println("Progress.xml file found at: " + xmlFile.getAbsolutePath());
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
