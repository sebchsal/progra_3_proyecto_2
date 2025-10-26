package org.example.sistemasrecetasbd_v.Controller;

import javafx.application.Platform;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Async {
    private Async() {}

    // Pool de hilos compartido por toda la aplicación
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true); // permite cerrar la app sin esperar a los hilos
        t.setName("AsyncThread-" + t.getId());
        return t;
    });

    public static <T> void run(Supplier<T> task, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        EXECUTOR.submit(() -> {
            try {
                T result = task.get();
                if (onSuccess != null) {
                    Platform.runLater(() -> onSuccess.accept(result));
                }
            } catch (Throwable ex) {
                if (onError != null) {
                    Platform.runLater(() -> onError.accept(ex));
                } else {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static <T> void run(Supplier<T> task, Consumer<T> onSuccess) {
        run(task, onSuccess, null);
    }

    public static void runVoid(Runnable action) {
        runVoid(action, null);
    }

    public static void runVoid(Runnable action, Consumer<Throwable> onError) {
        EXECUTOR.submit(() -> {
            try {
                action.run();
            } catch (Throwable ex) {
                if (onError != null) {
                    Platform.runLater(() -> onError.accept(ex));
                } else {
                    ex.printStackTrace();
                }
            }
        });
    }

    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }

}
