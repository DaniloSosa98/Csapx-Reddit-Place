package place.client.ptui;

import place.PlaceBoard;
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
import java.util.List;
import java.util.Scanner;

public class PlacePTUI  extends ConsoleApplication implements Observer<ClientModel, PlaceTile> {

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
    @Override
    public void update(ClientModel model, PlaceTile tile) {
        model.printBoard();

    }


    @Override
    public void init() {
        try {
            List<String> args = super.getArguments();
            model = new ClientModel();
            // Get host info from command line
            String host = args.get( 0 );
            int port = Integer.parseInt( args.get( 1 ) );
            String username = args.get(2);

            this.serverConn = new NetworkClient( host, port, model, username);
            this.serverConn.startListener();
        }
        catch (PlaceException |
        ArrayIndexOutOfBoundsException |
        NumberFormatException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void go(Scanner consoleIn, PrintWriter consoleOut) {

        this.userIn = userIn;
        this.userOut = userOut;

        // Connect UI to model. Can't do it sooner because streams not set up.
        this.model.addObserver( this );

        // Start the network listener thread
        this.serverConn.startListener();

        // Manually force a display of all board state, since it's too late
        // to trigger update().
//        this.refresh();
        while ( true ) {
            try {

                this.wait();
            }
            catch( InterruptedException ie ) {

            }
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
            ConsoleApplication.launch(PlacePTUI.class, args);
        }

    }


}
