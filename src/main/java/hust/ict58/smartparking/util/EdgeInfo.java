package hust.ict58.smartparking.util;

public class EdgeInfo {
    private String id;
    private String inNodeId;
    private String outNodeId;
    private double length;
    private String direction;

    public EdgeInfo(String id, String inNodeId, String outNodeId, double length, String direction)
    {
        this.id = id;
        this.inNodeId = inNodeId;
        this.outNodeId = outNodeId;
        this.length = length;
        this.direction = direction;
    }

    public String getId() {
        return id;
    }

    public String getInNodeId() {
        return inNodeId;
    }

    public String getOutNodeId() {
        return outNodeId;
    }

    public double getLength() {
        return length;
    }

    public String getDirection() {
        return direction;
    }
}
