package io.github.isysdcore.genericAutoCrud.utils;


public class AuditContext {
    private static final ThreadLocal<String> currentActor = new ThreadLocal<>();

    public static void setCurrentActor(String actor) {
        currentActor.set(actor);
    }

    public static String getCurrentActor() {
        return currentActor.get();
    }

    public static void clear() {
        currentActor.remove();
    }
}
