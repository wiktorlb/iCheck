package dev.app.iCheck.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "plane")
public class Plane {
    @Id
    private String id;
    private String model;
/*     private List<List<String>> seatMap; */
private List<String> seatMap;
    private Set<String> occupiedSeats = new HashSet<>(); // Przechowuje zajęte miejsca

    public Plane(String id, String model) {
        this.id = id;
        this.model = model;
        this.seatMap = initializeSeatMap(); // Inicjalizacja mapy miejsc
    }

/*  private List<String> initializeSeatMap() {
    List<String> seatMap = new ArrayList<>();
    char[] seatLetters = { 'A', 'B', 'C', 'D', 'E', 'F' };

    for (int row = 1; row <= 22; row++) {
        StringBuilder rowSeats = new StringBuilder(row + " ");
        for (int i = 0; i < seatLetters.length; i++) {
            rowSeats.append(seatLetters[i]);
            if (i == 2)
                rowSeats.append(" "); // Podział na ABC i DEF
        }
        seatMap.add(rowSeats.toString());
    }
    return seatMap;
} */
private List<String> initializeSeatMap() {
    List<String> seatMap = new ArrayList<>();
    char[] seatLetters = { 'A', 'B', 'C', 'D', 'E', 'F' };

    for (int row = 1; row <= 22; row++) {
        StringBuilder rowSeats = new StringBuilder();
        for (int i = 0; i < seatLetters.length; i++) {
            rowSeats.append(row).append(seatLetters[i]);
            if (i < seatLetters.length - 1) {
                rowSeats.append(",");
            }
        }
        seatMap.add(rowSeats.toString());
    }
    return seatMap;
}

   public List<String> getFlatSeatMap() {
    return new ArrayList<>(seatMap);
}

    public boolean isSeatAvailable(String seatNumber) {
        return !occupiedSeats.contains(seatNumber); // Jeśli miejsce nie jest w zbiorze, to jest dostępne
    }

    public void assignSeat(String seatNumber) {
        if (!isSeatAvailable(seatNumber)) {
            throw new IllegalArgumentException("Seat " + seatNumber + " is already occupied!");
        }
        occupiedSeats.add(seatNumber); // Dodaje miejsce do listy zajętych
    }

    public void freeSeat(String seatNumber) {
        occupiedSeats.remove(seatNumber); // Usuwa miejsce z listy zajętych
    }

    public Set<String> getOccupiedSeats() {
        return occupiedSeats;
    }

    public void setOccupiedSeats(Set<String> occupiedSeats) {
        this.occupiedSeats = occupiedSeats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

/*     public List<List<String>> getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(List<List<String>> seatMap) {
        this.seatMap = seatMap;
    } */

    public List<String> getSeatMap() {
    return seatMap;
}
}
