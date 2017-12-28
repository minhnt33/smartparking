package hust.ict58.smartparking.util;

import java.util.List;

public class GraphInfo {
    private int nodeNumber;
    private int edgeNumber;
    private List<NodeInfo> nodeInfoList;
    private List<EdgeInfo> edgeInfoList;

    public GraphInfo(int nodeNumber, int edgeNumber, List<NodeInfo> nodeInfoList, List<EdgeInfo> edgeInfoList)
    {
        this.nodeNumber = nodeNumber;
        this.edgeNumber = edgeNumber;
        this.nodeInfoList = nodeInfoList;
        this.edgeInfoList = edgeInfoList;
    }

    public int getEdgeNumber() {
        return edgeNumber;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public List<EdgeInfo> getEdgeInfoList() {
        return edgeInfoList;
    }

    public List<NodeInfo> getNodeInfoList() {
        return nodeInfoList;
    }
}
