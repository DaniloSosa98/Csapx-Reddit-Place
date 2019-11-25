package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.model.ClientModel;
import place.model.Observer;
import place.network.NetworkClient;
import place.network.PlaceRequest;

import java.io.PrintWriter;
import java.nio.channels.NetworkChannel;
import java.util.*;

import static java.lang.Thread.sleep;

public class PlaceGUI extends Application implements Observer<ClientModel, PlaceTile> {

    Stage stage;
    private PlaceRequest network;

    /** all the colors, this is ugly, let's find a better solution **/
    private static HashMap<String, PlaceColor> colors = new HashMap<>();
    /** Client's username */
    private static String username;

    /**
     * conc_model for the game
     */
    private ClientModel model;

    /**
     * Connection to network interface to server
     */
    private NetworkClient serverConn;

    /**
     * What to read to see what user types
     */
    private Scanner userIn;

    /**
     * Where to send text that the user can see
     */
    private PrintWriter userOut;

    /**
     * Connection to network interface to server
     */
    boolean isReady;
    private int selectedColor;
    Rectangle[][] theInputs;
//
    /**
     * IDK IF THIS WILL BE NECCESSARY
     * @return
     */
    synchronized boolean canPlace(){
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  true;
    }

    public void init(){
        try{
            List<String> args = getParameters().getRaw();
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));
            this.username = args.get(2);

            this.model = new ClientModel();
            this.model.addObserver(this);
            this.serverConn = new NetworkClient(host, port, model, username);
            serverConn.startListener();
        }
        catch(ArrayIndexOutOfBoundsException | NumberFormatException | PlaceException e){
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates the board
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane bp = new BorderPane();
        GridPane gp = new GridPane();
        generateBoard(bp,gp);

        final ToggleGroup group = new ToggleGroup();
//        GridPane buttons = new GridPane();
        //GridPane buttons = new GridPane();
        FlowPane fp = new FlowPane();
        fp.setPrefWrapLength(gp.getWidth());

        for (int i = 0; i < 16; i++) {
            ToggleButton tb = new ToggleButton();
            if (i<10){
                tb.setText(String.valueOf(i));
            }else{
                char c = (char)(i+55);
                tb.setText(String.valueOf(c));
            }
            tb.setToggleGroup(group);
            tb.setStyle(toggleColor(i));
            tb.setPrefSize(31, 31);
//            buttons.add(tb, i, 0);
            tb.autosize();
            //tb.setPrefSize(31, 31);
            //buttons.add(tb, i, 0);
            fp.getChildren().addAll(tb);
//            tb.setPrefSize(31, 31);
//            buttons.add(tb, i, 0);
            tb.setOnMouseClicked(new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent t) {

                   try {
                        selectedColor = Integer.parseInt(tb.getText());
                    }
                    catch(NumberFormatException e){
                        selectedColor = Integer.valueOf(tb.getText().charAt(0))-55;
                    }
                   System.out.println(selectedColor);
                }
            });
        }
//        bp.setBottom(buttons);

        //bp.setBottom(buttons);
        bp.setBottom(fp);
        Scene scene = new Scene(bp);
        primaryStage.setScene(scene);
        primaryStage.setTitle(this.username);
        primaryStage.show();
    }

    void generateBoard(BorderPane bp, GridPane gp){

        int dimension = model.getDimension();
        Random rand = new Random();
        theInputs = new Rectangle[dimension][dimension];
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                Rectangle r = new Rectangle();
                //Color c = this.colors(selectedColor);
                //System.out.println(selectedColor);
                final int iRow = row;
                final int iColl = col;

                r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
//                        r.setFill(colors(selectedColor));
                        serverConn.setTile(new PlaceTile(iRow,iColl,username,convertToPlaceColor(selectedColor)));
                    }

                });
                r.setHeight(50);

                r.setWidth(50);
                model.printBoard();
                int color =0;
                try {
                    color = model.getTile(row, col).getColor().getNumber();
                } catch (NullPointerException e){
                    color= 3;
                }
                r.setFill(this.colors(color));
                gp.add(r, col, row);
                theInputs[row][col]=r;
            }
        }

        bp.setCenter(gp);
    }


    void refresh(){
        if(theInputs!=null&&canPlace()) {
            System.out.println("refresh");

            theInputs[model.getChangedTile().getRow()][model.getChangedTile().getCol()].
                    setFill(this.colors(model.getChangedTile().getColor().getNumber()));

            System.out.println("refresh");
        }
//        System.out.println("DSF");


    }



    @Override
    public void update(ClientModel model, PlaceTile tile) {
        Platform.runLater(this::refresh);
    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceGUI host port username");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }

    public final static Color colors(int color){
        switch (color){
            case 0:
                return Color.BLACK;
            case 1:
                return Color.GRAY;
            case 2:
                return Color.SILVER;
            case 3:
                return Color.WHITE;
            case 4:
                return Color.MAROON;
            case 5:
                return Color.RED;
            case 6:
                return Color.OLIVE;
            case 7:
                return Color.YELLOW;
            case 8:
                return Color.GREEN;
            case 9:
                return Color.LIME;
            case 10:
                return Color.TEAL;
            case 11:
                return Color.AQUA;
            case 12:
                return Color.NAVY;
            case 13:
                return Color.BLUE;
            case 14:
                return Color.PURPLE;
            case 15:
                return Color.FUCHSIA;
            default:
                return null;
        }
    }

    public String toggleColor(int color){
        switch (color){
            case 0:
                return "-fx-base: black;";
            case 1:
                return "-fx-base: gray;";
            case 2:
                return "-fx-base: silver;";
            case 3:
                return "-fx-base: white;";
            case 4:
                return "-fx-base: maroon;";
            case 5:
                return "-fx-base: red;";
            case 6:
                return "-fx-base: olive;";
            case 7:
                return "-fx-base: yellow;";
            case 8:
                return "-fx-base: green;";
            case 9:
                return "-fx-base: lime;";
            case 10:
                return "-fx-base: teal;";
            case 11:
                return "-fx-base: aqua;";
            case 12:
                return "-fx-base: navy;";
            case 13:
                return "-fx-base: blue;";
            case 14:
                return "-fx-base: purple;";
            case 15:
                return "-fx-base: fuchsia;";
            default:
                return null;
        }
    }


    public PlaceColor convertToPlaceColor(int selectedColor){
        for (PlaceColor color : PlaceColor.values()){
            if(selectedColor == color.getNumber()){
                return color;
            }
        }
        return  null;
    }

    //EventHandler<>

}
