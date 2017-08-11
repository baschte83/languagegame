package lib;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Class to parse HashMaps into JSON objects.
 * @author Sebastian Baumann, Korbinian Karl, Ehsan Moslehi
 */
public class JsonHashMapParser {

    /**
     * Enum class to give the created JSON object a special type.
     * The server and the client check the type and behave appropriate to the found type.
     */
    public enum Type {
        /**
         * Enum marks the JSON object as a list of players and their points.
         */
        SCORE("Score"),

        /**
         * Enum marks the JSON object as a new question the players must translate.
         */
        QUESTION("Question"),

        /**
         * Enum marks the JSON object as the winner of the game.
         */
        WINNER("Winner"),

        /**
         * Enum marks the JSON object as an answer (only used by players to send their answer to the server).
         */
        ANSWER("Answer"),

        /**
         * Enum marks the JSON object as an error that occured.
         */
        ERROR("Error"),

        /**
         * Enum marks the JSON object as the language the questions must be translated to.
         */
        LANGUAGE("Language"),

        /**
         * Enum marks the JSON object as the nickname the client sends to the server for connection.
         */
        NICKNAME("Nickname");

        /**
         * Variable that stores the type of this enum as a String.
         */
        private String typeString;

        /**
         * Constructor for an enum.
         * @param typeString is the type of the enum as a String.
         */
        Type(String typeString) {
            this.typeString = typeString;
        }

        /**
         * Method which returns the string of the type of the JSON object.
         * @return type as a String.
         */
        public String getTypeString() {
            return typeString;
        }

        /**
         * Method creates the corresponding enum type for the given String type.
         * @param typeString the type of the enum as a String.
         * @return the type of the given String typeString as an enum type.
         */
        public static Type fromString(final String typeString) {
            Type type;
            switch (typeString) {
                case "Score":
                    type = SCORE;
                    break;
                case "Question":
                    type = QUESTION;
                    break;
                case "Winner":
                    type = WINNER;
                    break;
                case "Answer":
                    type = ANSWER;
                    break;
                case "Error":
                    type = ERROR;
                    break;
                case "Language":
                    type = LANGUAGE;
                    break;
                case "Nickname":
                    type = NICKNAME;
                    break;
                default:
                    throw new IllegalArgumentException("No such a type!");
            }
            return type;
        }
    }

    /**
     * Static class to parse a given HashMap to a JSON object.
     */
    public static class Parser {

        /**
         * Method parses a given HashMap and a given Enum Type to a JSON object.
         * @param type - is the type of the JSON object.
         * @param map - is the HashMap which should be parsed to a JSON Object.
         * @return a complete JSON object.
         */
        public static JSONObject hashMapToJson(Type type, Map<String, ?> map) {
            JSONObject json = new JSONObject();
            json.put("Type", type.getTypeString());
            json.putAll(map);
            return json;
        }

        /**
         * Method returns the enum type of the given JSON Object.
         * @param json  - the JSON object the type should be returned.
         * @return - the type of the given JSON object as an enum type.
         */
        public static Type getType(JSONObject json) {
            return Type.fromString((String) json.remove("Type"));
        }

        /**
         * Method frees the HashMap encapsulated in an JSON object.
         * @param json - the JSON object the HashMap is encapsulated in.
         * @return - the HashMap encapsulated in the JSON object as a HashMap object.
         */
        public static HashMap<String, ?> jsonToHashMap(JSONObject json) {
            if (json.containsKey("Type")) {
                json.remove("Type");
            }
            return (HashMap<String, ?>) json.clone();
        }
    }
}
