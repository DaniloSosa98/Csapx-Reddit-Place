package place.client.ptui;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceColor.*;
import place.PlaceException;
import place.PlaceTile;
import place.model.ClientModel;
import place.model.Observer;
import place.network.NetworkClient;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class PlacePTUI  extends ConsoleApplication implements Observer<ClientModel, PlaceTile> {

/** all the colors, this is ugly, let's find a better solution **/
    private static HashMap<Integer, PlaceColor> colors = new HashMap<>();
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
    @Override
    public void update(ClientModel model, PlaceTile tile) {
        refresh();
    }

    /**
     * this adds the usr to model's observer
     * starts the server
     * creates an instance of
     */

    @Override
    public void init() {
        try {
            List<String> args = super.getArguments();
            model = new ClientModel();
            this.model.addObserver( this );

            // Get host info from command line
            String host = args.get( 0 );
            int port = Integer.parseInt( args.get( 1 ) );
            String username = args.get(2);

            this.serverConn = new NetworkClient( host, port, model, username);

        }
        catch (PlaceException |
        ArrayIndexOutOfBoundsException |
        NumberFormatException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    /*
    I believe this is called once right after the server runs, it calls the first refresh and sets the usr consoles

     */
    @Override
    public synchronized void go(Scanner consoleIn, PrintWriter consoleOut) {

        this.userIn = consoleIn;
        this.userOut = consoleOut;
        isReady =true;
        this.serverConn.startListener();
        refresh();

        while ( true ) {
            try {

                this.wait();
            }
            catch( InterruptedException ie ) {

            }
        }

    }

    /**
     * IDK IF THIS WILL BE NECCESSARY
     * @return
     */
    boolean canMove(){
        return  true;
    }

    /**
     * refreshs the board for the usr and also gets the input from the usr.
     */
    private void refresh() {
        System.err.println(isReady);
        if ( canMove() &&isReady) {
            System.out.println("SDF");
            model.printBoard();

            do {
                model.printBoard();
                this.userOut.flush();
                int row = this.userIn.nextInt();
                int col = this.userIn.nextInt();
                int color = userIn.nextInt();
                userOut.println(colors);

                PlaceColor targetColor= colors.get(color);

                userOut.println(targetColor);
                PlaceTile newTile = new PlaceTile(row,col,username,targetColor);
                serverConn.setTile(newTile);
            } while (true);
        }
    }
    @Override
    public void stop() {
        this.userIn.close();
        this.userOut.close();
        this.serverConn.close();
    }

    // TODO check if we need wndGame method

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
            System.exit(1);
        }
        else {
            colors.put(0,PlaceColor.BLACK);
            colors.put(5,PlaceColor.RED);
            colors.put(4,PlaceColor.WHITE);
            ConsoleApplication.launch(PlacePTUI.class, args);

        }

    }


}
