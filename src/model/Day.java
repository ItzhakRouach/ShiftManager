package model;

public enum Day {
    SUNDAY("ראשון"),
    MONDAY("שני"),
    TUESDAY("שלישי"),
    WEDNESDAY("רביעי"),
    THURSDAY("חמישי"),
    FRIDAY("שישי"),
    SATURDAY("שבת");

    private final String hebrewLabel;
    Day(String hebrewLabel) {
        this.hebrewLabel = hebrewLabel;
    }

    @Override
    public String toString() {
        return hebrewLabel;
    }
}