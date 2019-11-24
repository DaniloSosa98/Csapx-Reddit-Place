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

    /** observers of the model (PlacePTUI and PlaceGUI - the "views") */
    private List<Observer<ClientModel, PlaceTile>> observers = new LinkedList<>();

    /**
     * Add a new observer.
     *
     * @param observer the new observer
     */
    public void addObserver(Observer<ClientModel, PlaceTile> observer) {
        this.observers.add(observer);
    }
//
    public void setTile(PlaceTile p){
        board.setTile(p);
        System.out.println("called from setBoard");
        notifyObservers(p);
    }
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
        System.out.println(observers.size());
        for (Observer<ClientModel, PlaceTile> observer: observers) {
            observer.update(this, tile);
        }
        System.out.println(observers.size());
    }
    public int getDimension(){
        return board.DIM;
    }
}
