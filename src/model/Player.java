package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Model for the LanguageGame. Represents players within the language game.
 * Connects to the table in client GUI to show playername and playerpoints.
 * @author Sebastian Baumann, Korbinian Karl, Ehsan Moslehi
 */
public class Player {

    private SimpleStringProperty nickname;
    private SimpleIntegerProperty points;

    /**
     * Player Constructor to build a new player object.
     * @param nickname is the name of this player.
     * @param points are the reached points of this player in the actual language game.
     */
    public Player(String nickname, int points) {
        this.nickname = new SimpleStringProperty(nickname);
        this.points = new SimpleIntegerProperty(points);
    }

    /**
     * Method returns property for this players nickname.
     * @return nickname of this player as a SimpleStringProperty.
     */
    public SimpleStringProperty nicknameProperty() {
        return this.nickname;
    }

    /**
     * Method returns property for this players nickname.
     * @return points of this player as a SimpleIntegerProperty.
     */
    public SimpleIntegerProperty pointsProperty() {
        return points;
    }

}
