package tn.fermista.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tn.fermista.models.User;

import java.io.*;

public class UserSession {
    private static final String FILE_NAME = "user_session.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static void clearCurrentUser() {
        currentUser = null;
    }

    public static void saveUser(User user) {
        currentUser = user; // On met aussi à jour en mémoire
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(user, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadUser() {
        try (Reader reader = new FileReader(FILE_NAME)) {
            User user = gson.fromJson(reader, User.class);
            currentUser = user; // Charger aussi en mémoire
            return user;
        } catch (IOException e) {
            return null; // No user session found
        }
    }

    public static void clearSavedUser() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }
}