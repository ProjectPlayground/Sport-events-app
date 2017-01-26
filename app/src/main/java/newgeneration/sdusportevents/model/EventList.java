package newgeneration.sdusportevents.model;



import com.firebase.client.ServerValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashMap;

import newgeneration.sdusportevents.utils.Constants;



public class EventList {
    private String listName;
    private String owner;
    private HashMap<String, User> usersTurn;


    public EventList() {
    }


    public EventList(String listName, String owner) {
        this.listName = listName;
        this.owner = owner;
        this.usersTurn = new HashMap<>();
    }

    public String getListName() {
        return listName;
    }

    public String getOwner() {
        return owner;
    }


    public HashMap getUsersTurn() {
        return usersTurn;
    }


}

