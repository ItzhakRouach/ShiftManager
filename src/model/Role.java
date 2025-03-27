package model;

public enum Role {
    SHIFT_MANAGER("אחמש"),
    DISPATCHER("מוקדן"),
    GUARD("מאבטח");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}