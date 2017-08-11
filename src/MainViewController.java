import lib.JsonHashMapParser;
import model.Player;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Main controller for language game application. Receives and processes any
 * event from the user interface.
 *
 * @author Sebastian Baumann, Korbinian Karl, Ehsan Moslehi
 */
public class MainViewController implements Runnable, Initializable {

    @FXML
    private Label insertLanguage;

    @FXML
    private Label questionField;

    @FXML
    private TextArea answerField;

    @FXML
    private Button sendAnswer;

    @FXML
    private TableView<Player> tableOfOpponents;

    @FXML
    private TableColumn<Player, String> opponentName;

    @FXML
    private TableColumn<Player, Number> opponentPoints;

    @FXML
    private TextField nickname;

    @FXML
    private TextField ipAddress;

    @FXML
    private TextField port;

    @FXML
    private Button buttonEnterGame;

    @FXML
    private Button buttonLeaveGame;

    @FXML
    private Label serverData;



    // ------ private variables -------------------------------------------------

    /**
     * Private variable that stores actual language resource.
     */
    private ResourceBundle res;

    /**
     * Private socket variable that stores the socket for connection to server.
     */
    private Socket socket;

    /**
     * Private ObjectOutputStream variable to send messages to the connected server.
     */
    private ObjectOutputStream out;

    /**
     * Private list that stores the players in the game to show in table of players in client GUI.
     */
    private ObservableList<Player> players = FXCollections.observableArrayList();

    /**
     * Timeout in milliseconds for connection attempt.
     */
    private static final int TIMEOUT = 3000;

    /**
     * Private variable to determine wether the Thread listener should be running or should be stopped.
     */
    private boolean runListener;

    /**
     * Private variable to store a String into a HashMap to send to server.
     */
    private Map<String, String> aMap = new HashMap<>();

    /**
     * Private variable to store, if nickname, ip-address and portnumber should be sent to ther server or just the nickname.
     */
    private boolean sendAllThreeConnectionInfos;

    /**
     * Initializes the MainViewController.
     * @param location is the location used to resolve relative paths for the root object, or null if the location is not known.
     * @param r is the resources used to localize the root object, or null if the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle r) {

        // stores actual used language resource
        this.res = r;

        // connects model and table view
        opponentName.setCellValueFactory(cellData -> cellData.getValue().nicknameProperty());
        opponentPoints.setCellValueFactory(cellData -> cellData.getValue().pointsProperty());

        // enables wrapping of too long texts in several labels
        serverData.setWrapText(true);
        questionField.setWrapText(true);
        insertLanguage.setWrapText(true);

        // enables wrapping vertical, if text is too long to show in several labels
        serverData.setTextOverrun(OverrunStyle.CLIP);
        questionField.setTextOverrun(OverrunStyle.CLIP);
        insertLanguage.setTextOverrun(OverrunStyle.CLIP);

        tableOfOpponents.setItems(players);
        tableOfOpponents.setPlaceholder(new Label(res.getString("key.emptyTable")));

        //test data for connection tests: DELETE AFTER USAGE!!!!
        ipAddress.setText("192.168.2.10");
        port.setText("10001");
        nickname.setText("Basti");


        buttonLeaveGame.setDisable(true);
        sendAnswer.setDisable(true);
        answerField.setDisable(true);

        // set variable to send all three connection infos to true
        sendAllThreeConnectionInfos = true;
    }

    /**
     * Create a new socket for IP_ADDRESS and PORT.
     * @param event is the event that leads to initialization of this method.
     * @throws InterruptedException if the thread started gets interrupted.
     * @throws IOException if there is a failure in initializing the streams.
     */
    @FXML
    public void enterGame(ActionEvent event) throws InterruptedException, IOException {

        if (nickname.getText().isEmpty() || port.getText().isEmpty() || ipAddress.getText().isEmpty()) {
            // Some data is missing
            showAlert(Alert.AlertType.ERROR, res.getString("key.connDataMissing"));
        }
        else {
            // all data exist
            try {

                if (sendAllThreeConnectionInfos) {
                    // open new client socket with a timeout in milliseconds
                    this.socket = new Socket();
                    InetSocketAddress isa = new InetSocketAddress(ipAddress.getText().trim(), Integer.parseInt(port.getText().trim()));
                    this.socket.connect(isa, TIMEOUT);

                    // disabling several GUI buttons and input fields
                    buttonEnterGame.setDisable(true);
                    nickname.setDisable(true);
                    ipAddress.setDisable(true);
                    port.setDisable(true);
                    buttonLeaveGame.setDisable(false);

                    // display connection status to GUI
                    if (socket.isConnected()) {
                        setServerInfoLabel(res.getString("key.connEstablished"));
                    }

                    // opens new ObjectOutputStream on socket
                    out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                    // opens a new thread to listen to the connected server
                    runListener = true;
                    new Thread(this).start();

                    // send nickname to gameserver
                    aMap.clear();
                    aMap.put("Nickname", nickname.getText().trim());
                    JSONObject j = JsonHashMapParser.Parser.hashMapToJson(JsonHashMapParser.Type.NICKNAME, aMap);
                    sendMessage(j.toJSONString());
                }
                else {
                    // the connection is established but another player has the same nickname,
                    // so change the nickname and just send the nickname to the server
                    // send nickname to gameserver
                    ipAddress.setDisable(true);
                    port.setDisable(true);
                    aMap.clear();
                    aMap.put("Nickname", nickname.getText().trim());
                    JSONObject j = JsonHashMapParser.Parser.hashMapToJson(JsonHashMapParser.Type.NICKNAME, aMap);
                    sendMessage(j.toJSONString());

                    // disabling several GUI buttons and input fields
                    buttonEnterGame.setDisable(true);
                    nickname.setDisable(true);
                    buttonLeaveGame.setDisable(false);
                }
            }
            catch (SocketException ce) {
                // Connection failed
                showAlert(Alert.AlertType.ERROR, res.getString("key.connFailed"));
            }
        }
    }

    /**
     * Method to leave the actual game this client was connected to.
     * @param event is the event that leads to initialization of this method.
     */
    @FXML
    public void leaveGame(ActionEvent event) {
        try {
            // closing the socket
            runListener = false;
            socket.close();

            // enabling several GUI buttons and input fields
            buttonEnterGame.setDisable(false);
            buttonLeaveGame.setDisable(true);
            nickname.setDisable(false);
            ipAddress.setDisable(false);
            port.setDisable(false);
            sendAllThreeConnectionInfos = true;

            // display connection status to GUI
            if (socket.isClosed()) {
                setServerInfoLabel(res.getString("key.connClosed"));
            }
        } catch (IOException e) {
            System.out.println("IOException occured!");
        }
    }

    /**
     * Method to send an answer to the server.
     * @param event is the event that leads to initialization of this method.
     */
    @FXML
    public void sendAnswer(ActionEvent event) {
        // 1.) clear the map we want to store the answer in
        // 2.) add the answer the user has entered in answerField to the map
        // 3.) build a JSON-Object that we want to send to the server and fill it with the key-value-pair:
        //     key   = ANSWER
        //     value = the map me have just created
        // 4.) send the JSON-Object as a string to the server
        // 5.) disable the send-button so that no second answer to the same question could be sent
        aMap.clear();
        aMap.put("Answer", answerField.getText().trim());
        JSONObject j = JsonHashMapParser.Parser.hashMapToJson(JsonHashMapParser.Type.ANSWER, aMap);
        sendMessage(j.toJSONString());
        sendAnswer.setDisable(true);
        answerField.setDisable(true);
    }

    /**
     * Method to start a thread which listens to an ObjectInputStream and handles incoming objects.
     */
    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(this.socket.getInputStream()))) {
            JSONObject j;
            JSONParser parser = new JSONParser();
            JsonHashMapParser.Type t;
            while (runListener) {
                try {

                    // parses object from input stream to a JSON-object
                    j = (JSONObject) parser.parse(in.readObject().toString());

                    // stores the type of the received message from the server to determine which message the server has sent
                    t = JsonHashMapParser.Parser.getType(j);

                    if (t.equals(JsonHashMapParser.Type.QUESTION)) {
                        // the server has sent a new question, which must be printed on the GUI
                        final String s = JsonHashMapParser.Parser.jsonToHashMap(j).get("Question").toString();
                        Platform.runLater(() -> {
                            setQuestionFieldLabel(s);
                            // new question has arrived, now the player can enter a new answer
                            answerField.clear();
                            answerField.setDisable(false);
                            sendAnswer.setDisable(false);
                        });
                    }
                    else if (t.equals(JsonHashMapParser.Type.SCORE)) {
                        // the server has sent an updated list of players and their points
                        final Map<String, Long> opponents = (Map<String, Long>) JsonHashMapParser.Parser.jsonToHashMap(j);

                        // delete former list of players and add new players to the list
                        players.clear();
                        for (String opponent : opponents.keySet()) {
                            Platform.runLater(() -> {
                                players.add(new Player(opponent, opponents.get(opponent).intValue()));
                            });
                        }

                        // reset the table visible on the GUI
                        Platform.runLater(() -> {
                            tableOfOpponents.setItems(players);
                            tableOfOpponents.setPlaceholder(new Label(res.getString("key.emptyTable")));
                        });
                    }
                    else if (t.equals(JsonHashMapParser.Type.ERROR)) {
                        // the server has sent the error message, that there is
                        // actually a player connected to the server, that has the same nickname
                        // so insert another nickname
                        showAlert(Alert.AlertType.ERROR, res.getString("key.anotherNickname"));
                        Platform.runLater(() -> {
                            // disable and enable several butons and GUI textfields and textareas
                            buttonEnterGame.setDisable(false);
                            nickname.clear();
                            nickname.setDisable(false);
                            ipAddress.setDisable(true);
                            port.setDisable(true);
                            sendAllThreeConnectionInfos = false;
                        });
                    }
                    else if (t.equals(JsonHashMapParser.Type.WINNER)) {
                        // the server has sent the nickname of the winner
                        // so we show an alert window which displays the winner nickname
                        final String s = JsonHashMapParser.Parser.jsonToHashMap(j).get("Winner").toString();
                        showAlert(Alert.AlertType.INFORMATION, s);
                    }
                    else {
                        // if the server has sent none of the former messages, the message the server has sent must be
                        // the information in which language the game has to be played
                        // so show this info on the GUI
                        final String s = JsonHashMapParser.Parser.jsonToHashMap(j).get("Language").toString();
                        Platform.runLater(() -> {
                            setInsertLanguageLabel(s);
                        });
                    }
                } catch (IOException | ClassNotFoundException ef) {
                    catchSeveralExceptions(in);
                } catch (ParseException e) {
                    System.out.println("ParseException occured!");
                }
            }
        }
        catch (IOException e) {
            // Object read failed
            System.out.println("IOException occured!");
        }
    }

    /**
     * Private method to send text to the output stream.
     * @param s is the message which should be sent to the server as a String.
     */
    private void sendMessage(String s) {
        try {
            // write message to the output stream
            this.out.writeObject(s);
            this.out.flush();
        } catch (IOException e) {
            // Transmitting the answer failed
            showAlert(Alert.AlertType.ERROR, res.getString("key.sendAnswerFailed"));
        }
    }

    /**
     * Private method to set Text of label serverData on GUI.
     * @param s is the text which should be shown in GUI as a String.
     */
    private void setServerInfoLabel(String s) {
        serverData.setText(s);
    }

    /**
     * Private method to set Text of label questionField on GUI.
     * @param s is the text which should be shown in GUI as a String.
     */
    private void setQuestionFieldLabel(String s) {
        questionField.setText(s);
    }

    /**
     * Private method to set Text of label insertLanguage on GUI.
     * @param s is the text which should be shown in GUI as a String.
     */
    private void setInsertLanguageLabel(String s) {
        insertLanguage.setText(s);
    }

    /**
     * Private Method to handle a bundle of Exceptions.
     * @param in is an ObjectInputStream, on which the exceptions could occure.
     */
    private void catchSeveralExceptions(ObjectInputStream in) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jj = (JSONObject) parser.parse(in.readObject().toString());
            final String s = JsonHashMapParser.Parser.jsonToHashMap(jj).get("Winner").toString();
            showAlert(Alert.AlertType.INFORMATION, res.getString("key.winnerIs") + " " + s + "!!!");
            Platform.runLater(() -> {
                buttonEnterGame.setDisable(false);
                nickname.clear();
                nickname.setDisable(false);
                ipAddress.setDisable(true);
                port.setDisable(true);
            });
            sendAllThreeConnectionInfos = false;
        }
        catch (EOFException eof) {
            this.runListener = false;
            System.out.println("Server has closed the connection!");
        }
        catch (ParseException | ClassNotFoundException | SocketException e) {
            System.out.println("Could not parse message!");
        } catch (IOException e) {
            System.out.println("IOException occured!");
        }
    }

    /**
     * Private Method to show an allert message.
     * @param type is the type of the alert message window as an Alert.AlertType.
     * @param s is the text that should be shown on the alert window.
     */
    private void showAlert(Alert.AlertType type, String s) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setHeaderText(null);
            alert.setContentText(s);
            alert.showAndWait();
        });
    }
}