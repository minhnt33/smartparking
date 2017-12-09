package hust.ict58.smartparking;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ParkingGraph {
    private Graph graph;
    private List<Node> slotNodes;
    private List<Node> signalNodes;

    public ParkingGraph()
    {
        graph = new SingleGraph("hust.ict58.smartparking.ParkingGraph");
        slotNodes = new ArrayList<>();
        signalNodes = new ArrayList<>();

        loadGraphData();
        extractNodeType("Slot", slotNodes);
        extractNodeType("Signal", signalNodes);
    }

    private void loadGraphData()
    {
        // Add nodes
        graph.addNode("Sign0");
        graph.addNode("Slot0");
        graph.addNode("Slot1");
        graph.addNode("Slot2");
        graph.addNode("Slot3");
        graph.addNode("Slot4");
        graph.addNode("Sign1");
        graph.addNode("Sign2");

        // Add edges
        graph.addEdge("Sign0Sign1", "Sign0", "Sign1").addAttribute("length", 1);
        graph.addEdge("Sign1Slot0", "Sign1", "Slot0").addAttribute("length", 1);
        graph.addEdge("Sign1Slot4", "Sign1", "Slot4").addAttribute("length", 1);
        graph.addEdge("Sign11", "Sign1", "Sign2").addAttribute("length", 1);
        graph.addEdge("Sign2Slot1", "Sign2", "Slot1").addAttribute("length", 1);
        graph.addEdge("Sign2Slot3", "Sign2", "Slot3").addAttribute("length", 1);
        graph.addEdge("Sign2Slot2", "Sign2", "Slot2").addAttribute("length", 1);
    }

    private void extractNodeType(String prefix, List<Node> nodes)
    {
        // Iterate through all nodes of graph
        for (Node node : graph)
        {
            String id = node.getId();

            // Determine if current node has id contained prefix string
            if(id.contains(prefix))
            {
                nodes.add(node);
            }
        }
    }

    public int getSlotNumber()
    {
        return slotNodes.size();
    }

    public int getSignalNumber()
    {
        return signalNodes.size();
    }

    public void computeShortestPath()
    {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode("Sign0"));
        dijkstra.compute();
        System.out.println(dijkstra.getPath(graph.getNode("Slot3")));
    }

    public void display()
    {
        graph.display();
    }

    public void setNodeColor(String nodeId, String color)
    {
        String settingStr = String.format("node#%s { fill-color: %s; }", nodeId, color);
        graph.addAttribute("ui.stylesheet", settingStr);
    }

    public void showNodeLabel()
    {
        // Iterate through all nodes of graph
        for (Node node : graph)
        {
            node.addAttribute("ui.label", node.getId());
        }
    }

    public void setNodeLabel(String id, String label)
    {
        graph.getNode(id).setAttribute("ui.label", String.format("%s:\n%s", id, label));
    }

    public void setNodeAttribute(String id, String attr, String value)
    {
        graph.getNode(id).addAttribute(attr, value);
    }

    public String getNodeAttribute(String id, String attr)
    {
        return graph.getNode(id).getAttribute(attr);
    }

    public double getNodeDistanceAttribute(String id)
    {
        return Double.valueOf(getNodeAttribute(id, "Distance"));
    }

    public String getNodeDirectionAttribute(String id)
    {
        return getNodeAttribute(id, "Direction");
    }

    public boolean hasAttribute(String id, String attr)
    {
        return graph.getNode(id).hasAttribute(attr);
    }
}
