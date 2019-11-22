package place.client.ptui;

import place.PlaceBoard;
import place.PlaceException;
import place.PlaceTile;
import place.model.ClientModel;
import place.model.Observer;
import place.network.NetworkClient;
import place.network.PlaceRequest;

import java.io.BufferedReader;
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
    private PlaceBoard model;

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

    }

    @Override
    public void init() {
        try {
            List<String> args = super.getArguments();

            // Get host info from command line
            String host = args.get( 0 );
            int port = Integer.parseInt( args.get( 1 ) );
            String username = args.get(2);

            // TODO Create uninitialized board.
//            this.model = new PlaceBoard();
            // Create the network connection.

            this.serverConn = new NetworkClient( host, port, this.model, username);
        }
        catch (PlaceException |
        ArrayIndexOutOfBoundsException |
        NumberFormatException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void go(Scanner consoleIn, PrintWriter consoleOut) {

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

//        // Get host info from command line
//        String host = args[1];
//        // get username from command line
//        username = args[2];
//        int port = Integer.parseInt(host);
//        try (
//                Socket plySocket1 = new Socket(args[0], port);
//
//                // network out to server
//                PrintWriter out = new PrintWriter(plySocket1.getOutputStream(), true);
//
//                BufferedReader in = new BufferedReader(new InputStreamReader(plySocket1.getInputStream()));
//                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); //for the local comp
//
//
//        ) {while(true) {
//            out.println("is connected");
//            String details = in.readLine();
//        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


}
