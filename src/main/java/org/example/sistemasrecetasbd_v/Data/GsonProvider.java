package org.example.sistemasrecetasbd_v.Data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDate;

public final class GsonProvider {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .serializeNulls()
            .setPrettyPrinting()
            .create();

    private GsonProvider() {}

    public static Gson get() { return GSON; }
}