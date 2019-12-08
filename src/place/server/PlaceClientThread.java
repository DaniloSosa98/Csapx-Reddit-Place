package place.server;

import place.PlaceTile;
import place.network.PlaceRequest;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static place.network.PlaceRequest.RequestType.*;

public class PlaceClientThread extends Thread {
    private ObjectInputStream clientIn;
    private ObjectOutputStream clientOut;
    Socket socket;
    place.server.PlaceServer placeServer;

    public PlaceClientThread(Socket socket, place.server.PlaceServer server){
        try {
            System.err.println("Dfs");
            this.socket = socket;
            System.err.println("Dfs");
            clientOut = new ObjectOutputStream(socket.getOutputStream());
            System.err.println("Dfs");

            clientIn = new ObjectInputStream(socket.getInputStream());
            System.err.println("Dfs");
            this.placeServer = server;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //this is run in terms of a server
    @Override
    public void run() {
        //TODO Removing usrName after the client exits
        String usrName;
        while (true) {
            try {
                PlaceRequest<?> req = (PlaceRequest<?>) clientIn.readObject();
                if (req.getType() == LOGIN) {
                    usrName = (String) req.getData();
                    if (placeServer.usersLogged.containsKey(usrName)) {
                        clientOut.writeUnshared(new PlaceRequest<>(ERROR,usrName+" is being used"));
                        System.err.println("error: usr already has that name");
                        socket.close();
                    } else {
                        //make sure to make a function later
                        placeServer.usersLogged.put(usrName, this);
                        clientOut.writeObject(new PlaceRequest<>(LOGIN_SUCCESS, "connected"));
                        clientOut.writeObject(new PlaceRequest<>(BOARD, placeServer.getBoard()));

                    }
                }
                if (req.getType() == CHANGE_TILE) {
                    PlaceTile updateTile = (PlaceTile) req.getData();
                    if (placeServer.getBoard().isValid(updateTile)) {
                        placeServer.setBoard(updateTile);
                        updateTile.setTime(System.currentTimeMillis());
                        clientOut.writeObject(new PlaceRequest<>(TILE_CHANGED, updateTile));
                    }
                }

            } catch (IOException | ClassNotFoundException e) {
            }
        }

    }
    synchronized boolean canPlace(){
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return  true;
    }


    public void updateBoard(PlaceTile updateTile){
        try{
            if(canPlace()) {
                System.err.println("sending out the change");
                clientOut.writeObject(new PlaceRequest<>(TILE_CHANGED, updateTile));
                clientOut.flush();
            }
        }
        catch (IOException e){

        }
    }
}
