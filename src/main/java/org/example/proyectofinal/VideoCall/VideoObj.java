package org.example.proyectofinal.VideoCall;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class VideoObj {
    private BooleanProperty isAbleToSee = new SimpleBooleanProperty(false);

    public final void setIsAbleToSee(boolean value) {
        isAbleToSee.set(value);
    }

    public final boolean isAbleToSee() {
        return isAbleToSee.get();
    }

    public final BooleanProperty isAbleToSeeProperty() {
        return isAbleToSee;
    }
}
