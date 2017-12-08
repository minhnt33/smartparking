package hust.ict58.smartparking;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.*;
import org.graphstream.algorithm.*;

import java.util.ArrayList;
import java.util.List;

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
        graph.addNode("Start");
        graph.addNode("Slot0");
        graph.addNode("Slot1");
        graph.addNode("Slot2");
        graph.addNode("Slot3");
        graph.addNode("Slot4");
        graph.addNode("Sign0");
        graph.addNode("Sign1");

        // Add edges
        graph.addEdge("StartSign0", "Start", "Sign0").addAttribute("length", 1);
        graph.addEdge("Sign0Slot0", "Sign0", "Slot0").addAttribute("length", 1);
        graph.addEdge("Sign0Slot4", "Sign0", "Slot4").addAttribute("length", 1);
        graph.addEdge("Sign01", "Sign0", "Sign1").addAttribute("length", 1);
        graph.addEdge("Sign1Slot1", "Sign1", "Slot1").addAttribute("length", 1);
        graph.addEdge("Sign1Slot3", "Sign1", "Slot3").addAttribute("length", 1);
        graph.addEdge("Sign1Slot2", "Sign1", "Slot2").addAttribute("length", 1);

        graph.display();
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
        dijkstra.setSource(graph.getNode("Start"));
        dijkstra.compute();
        System.out.println(dijkstra.getPath(graph.getNode("Slot3")));
    }

    public void display()
    {
        graph.display();
    }
}
