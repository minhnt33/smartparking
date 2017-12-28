package hust.ict58.smartparking;

import hust.ict58.smartparking.util.Coordinate3;
import hust.ict58.smartparking.util.EdgeInfo;
import hust.ict58.smartparking.util.GraphInfo;
import hust.ict58.smartparking.util.NodeInfo;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.ArrayList;
import java.util.List;

public class ParkingGraph {
    private Graph graph;
    private List<Node> slotNodes;
    private List<Node> signalNodes;
    private Dijkstra dijkstra;

    public ParkingGraph(String graphName) {
        graph = new SingleGraph(graphName);
        slotNodes = new ArrayList<>();
        signalNodes = new ArrayList<>();

        loadGraphData(graphName);
        extractNodeType("Slot", slotNodes);
        extractNodeType("Sign", signalNodes);
        initializeDijkstra();
        sortSlot();
    }

    private void loadGraphData(String graphName) {
        GraphLoader graphLoader = new GraphLoader();
        GraphInfo graphInfo = graphLoader.load(graphName + ".dat");

        if (graphInfo != null) {
            // Add node info
            for (NodeInfo nodeInfo : graphInfo.getNodeInfoList()) {
                Coordinate3 coordinate = nodeInfo.getCoordinate();
                graph.addNode(nodeInfo.getId()).setAttribute("xyz", coordinate.getX(), coordinate.getY(), coordinate.getZ());
                setNodeColor(nodeInfo.getId(), "green");
            }

            // Add edge info
            for (EdgeInfo edgeInfo : graphInfo.getEdgeInfoList()) {
                createEdge(edgeInfo.getId(), edgeInfo.getInNodeId(), edgeInfo.getOutNodeId(), edgeInfo.getLength(), edgeInfo.getDirection());
            }
        }
    }

    private void createEdge(String id, String node1, String node2, double length, String direction) {
        Edge edge = graph.addEdge(id, node1, node2);
        edge.addAttribute("length", length);
        edge.addAttribute("direction", direction);
    }

    private void extractNodeType(String prefix, List<Node> nodes) {
        // Iterate through all nodes of graph
        for (Node node : graph) {
            String id = node.getId();

            // Determine if current node has id contained prefix string
            if (id.contains(prefix)) {
                nodes.add(node);
            }
        }
    }

    private void initializeDijkstra() {
        dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode("Sign0"));
        dijkstra.compute();

        // Pre sort slot list from nearest to farthest slot
    }

    public List<Node> getSlotNodes() {
        return slotNodes;
    }

    public List<Node> getSignalNodes() {
        return signalNodes;
    }

    public int getSlotNumber() {
        return slotNodes.size();
    }

    public int getSignalNumber() {
        return signalNodes.size();
    }

    public void sortSlot() {
        ArrayList<Double> pathLengthList = new ArrayList<>(slotNodes.size());
        for (Node node : slotNodes) {
            pathLengthList.add(dijkstra.getPathLength(node));
        }

        // Bubble sort
        int n = pathLengthList.size();
        for (int i = 0; i < n; i++) {
            boolean sorted = true;
            for (int j = 0; j < n - i - 1; j++) {
                if (pathLengthList.get(j) > pathLengthList.get(j + 1)) {
                    sorted = false;
                    // swap length
                    double tmp = pathLengthList.get(j);
                    pathLengthList.set(j, pathLengthList.get(j + 1));
                    pathLengthList.set(j + 1, tmp);

                    // swap slot
                    Node tmpSlot = slotNodes.get(j);
                    slotNodes.set(j, slotNodes.get(j + 1));
                    slotNodes.set(j + 1, tmpSlot);
                }
            }

            if (sorted)
                return;
        }
    }

    public SlotPath findNearestAvailableSlot() {
        for (int i = 0; i < slotNodes.size(); i++) {
            Node slot = slotNodes.get(i);
            boolean status = Boolean.valueOf(slot.getAttribute("Status"));

            if (status) {
                continue;
            }

            return new SlotPath(slot.getId(), dijkstra.getPath(slot));
        }
        return null;
    }

    public void display() {
        Viewer viewer = graph.display();
        viewer.disableAutoLayout();
    }

    public void clear()
    {
        graph.clear();
    }

    public void setNodeColor(String nodeId, String color) {
        String settingStr = String.format("node#%s { fill-color: %s; }", nodeId, color);
        graph.addAttribute("ui.stylesheet", settingStr);
    }

    public void showNodeLabel() {
        // Iterate through all nodes of graph
        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }
    }

    public void setNodeLabel(String id, String label) {
        graph.getNode(id).setAttribute("ui.label", String.format("%s:\n%s", id, label));
    }

    public void setNodeAttribute(String id, String attr, String value) {
        graph.getNode(id).addAttribute(attr, value);
    }

    public String getNodeAttribute(String id, String attr) {
        return graph.getNode(id).getAttribute(attr);
    }

    public double getNodeDistanceAttribute(String id) {
        return Double.valueOf(getNodeAttribute(id, "Distance"));
    }

    public String getNodeDirectionAttribute(String id) {
        return getNodeAttribute(id, "Direction");
    }

    public boolean hasAttribute(String id, String attr) {
        return graph.getNode(id).hasAttribute(attr);
    }

    public class SlotPath {
        private Path path;
        private String slotId;

        public SlotPath(String slotId, Path path) {
            this.slotId = slotId;
            this.path = path;
        }

        public Path getPath() {
            return path;
        }

        public String getSlotId() {
            return slotId;
        }
    }
}
