package place.network;

import place.PlaceBoard;
import place.PlaceException;
import place.network.PlaceRequest;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static place.network.PlaceRequest.*;

/**
 * The client side network interface to a Place Board server.
 * Each of the two players in a game gets its own connection to the server.
 * This class represents the controller part of a model-view-controller
 * triumvirate, in that part of its purpose is to forward user actions
 * to the remote server.
 *
 * @author Robert St Jacques @ RIT SE
 * @author Sean Strout @ RIT CS
 * @author James Heliotis @ RIT CS
 */
public class NetworkClient {

    /**
     * Turn on if standard output debug messages are desired.
     */
    private static final boolean DEBUG = false;


    /**
     * Print method that does something only if DEBUG is true
     *
     * @param logMsg the message to log
     */
    private static void dPrint( Object logMsg ) {
        if ( NetworkClient.DEBUG ) {
            System.out.println( logMsg );
        }

    }

    /**
     * The {@link Socket} used to communicate with the reversi server.
     */
    private Socket sock;

    /**
     * The {@link Scanner} used to read requests from the Place server.
     */
    private Scanner networkIn;

    /**
     * The {@link PrintStream} used to write responses to the Place server.
     */
    private PrintStream networkOut;

    /**
     * The {@link PlaceBoard} used to keep track of the state of the game.
     */
    private PlaceBoard placeGame;

    /**
     * Sentinel used to control the main game loop.
     */
    private boolean go;

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo() {
        return this.go;
    }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() {
        this.go = false;
    }

    /**
     * Hook up with a Place server already running and waiting for
     * client to connect.
     * A thread that listens for server messages and forwards
     * them to the game object is started.
     *
     * @param hostname the name of the host running the server program
     * @param port     the port of the server socket on which the server is
     *                 listening
     * @param model    the local object holding the state of the board that
     *                 must be updated upon receiving server messages
     * @throws place.PlaceException If there is a problem opening the connection
     */
    // TODO check if we need to pass username to this class
    public NetworkClient( String hostname, int port, PlaceBoard model, String username ) throws PlaceException {

        try {

            this.sock = new Socket(hostname, port);
            this.networkIn = new Scanner( sock.getInputStream() );
            this.networkOut = new PrintStream( sock.getOutputStream() );
            this.placeGame = model;
            this.go = true;

            // messages from server
            String request = this.networkIn.next();
            String arguments = this.networkIn.nextLine();
            NetworkClient.dPrint("Connected to server " + this.sock);
        }
        catch (IOException e) {
            throw new PlaceException(e);
        }
    }

    public void startListener() {
        // Run rest of client in separate thread.
        // This threads stops on its own at the end of the game and
        // does not need to rendezvous with other software components.
        Thread netThread = new Thread( () -> this.run() );
        netThread.start();
    }

    // TODO check if we need connect

    /** Send out login request to server */
    public void login(String username) {
         this.networkOut.println(PlaceRequest.RequestType.LOGIN + " " + username);
    }

    // TODO add method for change tile
    // TODO add method for tile changed

    /**
     * Called when the server sends a message saying that
     * was an error with the request. For example, if
     * the client tried to login with a username that was already taken
     *
     * @param arguments The error message sent from the Place.server.
     */
    public void error( String arguments ) {

//        NetworkClient.dPrint( '!' + ERROR + ',' + arguments );
        dPrint( "Fatal error: " + arguments );
        // TODO check error message
      //  dPrint("Log in failed!");
        this.stop();
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close() {
        try {
            this.sock.close();
        }
        catch( IOException ioe ) {
            // squash
        }
        // TODO: We don't have the close method in our board unlike reversi board, it notified observers

        //this.placeGame.close();
    }

    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {

        while (this.goodToGo()) {
            try {
                String request = this.networkIn.next();
                String arguments = this.networkIn.nextLine().trim();
                NetworkClient.dPrint("Next message in = \"" + request + '"');

                switch (request) {

                    // TODO add PlaceRequests from server to client
                }
            }
            catch (NoSuchElementException e) {
                this.error("lost Connextion to server.");
                this.stop();
            }
            catch (Exception e) {
                this.error(e.getMessage() + "?");
                this.stop();
            }
        }
        this.close();
    }


}
