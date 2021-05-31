package br.udesc.esag.participactbrasil.domain.persistence;

public enum ActionType {
    SENSING_MOST, PHOTO, QUESTIONNAIRE, ACTIVITY_DETECTION;

    @Override
    public String toString() {
        switch (this) {
            case SENSING_MOST:
                return "Passive sensing";
            case PHOTO:
                return "Photo";
            case QUESTIONNAIRE:
                return "Questionnaire";
            case ACTIVITY_DETECTION:
                return "Activity detection";
            default:
                return "Unknown";
        }
    }

    public static ActionType convertFrom(br.udesc.esag.participactbrasil.domain.persistence.ActionType old) {
        if (old.name().equalsIgnoreCase(SENSING_MOST.name())) {
            return SENSING_MOST;
        }

        if (old.name().equalsIgnoreCase(PHOTO.name())) {
            return PHOTO;
        }

        if (old.name().equalsIgnoreCase(QUESTIONNAIRE.name())) {
            return QUESTIONNAIRE;
        }

        return null;
    }

}
