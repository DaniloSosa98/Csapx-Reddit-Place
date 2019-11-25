package place.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

/**
 * The Place server is run on the command line as:
 *
 * $ java PlaceServer port DIM
 *
 * Where port is the port number of the host and DIM is the square dimension
 * of the board.
 *
 * @author Sean Strout @ RIT CS
 */
public class PlaceServer {
    /**
     * The main method starts the server and spawns client threads each time a new
     * client connects.
     *
     * @param args the command line arguments
     */
    Set<String> usersLogged;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java PlaceServer port DIM");
        } else {
            while(true) {
                try (
                        ServerSocket ss1 = new ServerSocket(Integer.parseInt(args[0]));
                        Socket socket = ss1.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ) {
                    if (socket != null) {
                        System.out.println("player 1 connected");
                        System.out.println(in.readLine());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    void verifyLogin(){

    }
}