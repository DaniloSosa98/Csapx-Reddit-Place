package place.server;

import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * The Place server is run on the command line as:
 *
 * $ java PlaceServer port DIM
 *
 * Where port is the port number of the host and DIM is the square dimension
 * of the board.
 *
 * @author Sean Strout @ RIT CS
 * @author Aubrey Tarmu
 */

public class PlaceServer implements Closeable {
    /**
     * The main method starts the server and spawns client threads each time a new
     * client connects.
     *
     * @param args the command line arguments
     */
    ServerSocket server;
    public   HashMap<String, PlaceClientThread> usersLogged = new HashMap<>();
    PlaceBoard board;

    void createBoard(int DIM){
        board = new PlaceBoard(DIM);
        for(int i=0;i<board.DIM;i++){
            for(int j=0;j<board.DIM;j++){
                board.setTile(new PlaceTile(i,j,"", PlaceColor.WHITE));
            }
        }
    }
    public PlaceBoard getBoard(){
        return board;
    }

    public PlaceServer(int port) {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java PlaceServer port DIM");
        }

        try (PlaceServer server = new PlaceServer(Integer.parseInt(args[0]))) {
            server.run(Integer.parseInt(args[1]));
//            Thread thread = new Thread();
//            thread.start();
        } catch (IOException e) {
            System.err.println("Failed to start server!");
            e.printStackTrace();
        }

    }
    public void verifyLogin(){

    }
    public void run(int DIM) {
        try {
            createBoard(DIM);
            while(true) {
                System.out.println("Waiting for player");
                Socket socket = server.accept();
                PlaceClientThread plc = new PlaceClientThread(socket, this);
                plc.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws IOException {
        try {
            this.server.close();
        } catch (IOException ioe) {
            // squash
        }
    }

    void updateEveryBoard(PlaceTile tile){
        for(PlaceClientThread usr : usersLogged.values()){
            usr.updateBoard(tile);
        }
    }
    public void setBoard(PlaceTile tile) {
        this.board.setTile(tile);
        //.Entry represents one key-value pair
        updateEveryBoard(tile);
    }
}

