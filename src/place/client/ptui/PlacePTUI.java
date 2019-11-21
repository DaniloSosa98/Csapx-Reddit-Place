package place.client.ptui;

import place.PlaceTile;
import place.model.ClientModel;
import place.model.Observer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlacePTUI implements Observer<ClientModel, PlaceTile> {
    /**
     * Connection to network interface to server
     */

    @Override
    public void update(ClientModel model, PlaceTile tile) {

    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
        }
        // Get host info from command line
        String host = args[1];
        int port = Integer.parseInt(host);
        try (
                Socket plySocket1 = new Socket(args[0], port); //plyr 1
//                Socket plySocket2 = new Socket(hostName, portNumber); //plyr 1

                PrintWriter out = new PrintWriter(plySocket1.getOutputStream(), true);
//                PrintWriter out2 =new PrintWriter(plySocket2.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(plySocket1.getInputStream())); //fr server
//                BufferedReader in2 =new BufferedReader(new InputStreamReader(plySocket2.getInputStream())); //fr server
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in)); //for the local comp

        ) {while(true) {
            out.println("is connected");
            String details = in.readLine();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
