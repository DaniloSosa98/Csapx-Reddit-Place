package place.model;

import javafx.scene.paint.Color;
import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;

import java.util.LinkedList;
import java.util.List;

/**
 * The client side model that is used as the "M" in the MVC paradigm.  All client
 * side applications (PTUI, GUI, bots) are observers of this model.
 *
 * @author Sean Strout @ RIT CS
 */
public class ClientModel {
    /** the actual board that holds the tiles */
    private PlaceBoard board;
    public boolean isGui;
    private PlaceTile changedTile;

    /** observers of the model (PlacePTUI and PlaceGUI - the "views") */
    private List<Observer<ClientModel, PlaceTile>> observers = new LinkedList<>();

    public  PlaceTile getTile(int x, int y){
        return board.getTile(x,y);
    }
    public  void setChangedTile(PlaceTile pt){
        board.setTile(pt);
        changedTile = pt;
        if(isGui)
            notifyObservers(null);
    }
    public PlaceTile getChangedTile(){
        return  changedTile;
    }

    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<ClientModel, PlaceTile> observer) {
        this.observers.add(observer);
    }
//
   public void setBoard(PlaceBoard board){
        this.board = board;
        notifyObservers(null);
    }
    public   void printBoard(){
        System.out.println(board);
        System.out.println();
    }
    /**
     * Notify observers the model has changed.
     */
    private void notifyObservers(PlaceTile tile){
        System.err.println(tile);
        for (Observer<ClientModel, PlaceTile> observer: observers) {
            System.err.println("observer");
            observer.update(this, tile);
            System.err.println(observer);
        }
    }
    public int getDimension(){
        return board.DIM;
    }
}
