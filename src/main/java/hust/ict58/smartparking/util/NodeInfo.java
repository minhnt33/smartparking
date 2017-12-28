package hust.ict58.smartparking.util;

public class NodeInfo {
    private String id;
    private Coordinate3 coordinate;

    public NodeInfo(String id, Coordinate3 coordinate)
    {
        this.id = id;
        this.coordinate = coordinate;
    }

    public String getId() {
        return id;
    }

    public Coordinate3 getCoordinate() {
        return coordinate;
    }
}
