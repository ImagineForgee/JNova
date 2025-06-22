package jnova.core.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jnova.core.util.gson.ClassTypeAdapter;

public class GsonFactory {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .setPrettyPrinting()
            .create();

    public static Gson get() {
        return gson;
    }
}
