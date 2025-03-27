package model;

public enum ShiftTime{
    MORNING("בוקר"),
    AFTERNOON("צהורים"),
    NIGHT("לילה");

    private final String displayName;

    ShiftTime(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
