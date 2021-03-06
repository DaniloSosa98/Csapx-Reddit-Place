package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import place.PlaceColor;
import place.PlaceException;
import place.PlaceTile;
import place.model.ClientModel;
import place.model.Observer;
import place.network.NetworkClient;
import place.network.PlaceRequest;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 *
 *
 * @author Aubrey Tarmu
 * @author Maham imtiaz
 * @author Danilo Sosa
 *
 */

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
        Group group = new Group();
        PannableCanvas canvas = new PannableCanvas();
        NodeGestures nodeGestures = new NodeGestures(canvas);
        BorderPane bp = new BorderPane();
        GridPane gp = new GridPane();
        generateBoard(bp,gp);
        bp.setStyle("-fx-base: black;");

        final ToggleGroup Tgroup = new ToggleGroup();
        FlowPane fp = new FlowPane();
        fp.prefWrapLengthProperty().bind(bp.widthProperty());
        gp.prefWidthProperty().bind(fp.prefWrapLengthProperty());

        for (int i = 0; i < 16; i++) {
            ToggleButton tb = new ToggleButton();
            if (i<10){
                tb.setText(String.valueOf(i));
            }else{
                char c = (char)(i+55);
                tb.setText(String.valueOf(c));
            }
            tb.setToggleGroup(Tgroup);
            tb.setStyle(toggleColor(i));
            tb.setPrefSize(31, 31);
            tb.autosize();
            fp.getChildren().addAll(tb);
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
        bp.setBottom(fp);
        canvas.getChildren().add(bp);
        canvas.setPrefSize(bp.getHeight(), bp.getWidth());
        group.getChildren().add(canvas);
        ScrollPane sp = new ScrollPane();
        sp.setContent(group);
        sp.setStyle("-fx-base: white;");
        Scene scene = new Scene(sp);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Place: "+this.username);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.show();
    }

    void generateBoard(BorderPane bp, GridPane gp){
        int dimension = model.getDimension();
        model.isGui =true;
        Random rand = new Random();
        theInputs = new Rectangle[dimension][dimension];
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                Rectangle r = new Rectangle();
                //Color c = this.colors(selectedColor);
                //System.out.println(selectedColor);
                final int iRow = row;
                final int iColl = col;
                r.setOnMouseMoved(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {

                        PlaceTile currTile = model.getTile(iRow, iColl);

                        Rectangle colorRep = new Rectangle();
                        colorRep.setWidth(20);
                        colorRep.setHeight(20);
                        Color currColor = colors(currTile.getColor().getNumber());
                        colorRep.setFill(currColor);

                        // get time the tile was last changed
                        long timeOfClick = currTile.getTime();
                        SimpleDateFormat dateAndTime = new SimpleDateFormat("MM/dd/yy \nHH:mm:ss");
                        Date resultDate = new Date(timeOfClick);
                        String date =  dateAndTime.format(resultDate);

                        Tooltip tooltip = new Tooltip("(" + iRow + ", " + iColl + ")"+ "\n"
                                + currTile.getOwner() + "\n"
                                + date);
                        tooltip.setGraphic(colorRep);
                        Tooltip.install(r, tooltip);
                    }
                });

                r.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent t) {
//                        r.setFill(colors(selectedColor));
                        PlaceTile newTile = new PlaceTile(iRow,iColl,username,convertToPlaceColor(selectedColor),System.currentTimeMillis());
                        serverConn.setTile(newTile);
                        newTile.setOwner(username);
                        long timeOfClick = System.currentTimeMillis();
                        newTile.setTime(timeOfClick);


                    }

                });
                double recsize = 400/dimension;
                r.setHeight(recsize);
                r.setWidth(recsize);
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
