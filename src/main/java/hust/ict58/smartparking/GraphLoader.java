package hust.ict58.smartparking;

import hust.ict58.smartparking.util.Coordinate3;
import hust.ict58.smartparking.util.EdgeInfo;
import hust.ict58.smartparking.util.GraphInfo;
import hust.ict58.smartparking.util.NodeInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphLoader {

    public GraphLoader()
    {
    }

    public GraphInfo load(String path)
    {
        List<NodeInfo> nodeInfoList = new ArrayList<>();
        List<EdgeInfo> edgeInfoList = new ArrayList<>();
        int nodeNumber = 0;
        int edgeNumber = 0;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(getClass().getResource("/" + path).getFile()));
            int lineCount = 0;

            // Read first line
            String line = reader.readLine();
            while (line != null) {
                lineCount++;

                // Read node number
                if(lineCount == 1)
                {
                    nodeNumber = Integer.valueOf(line);
                }
                // Read node info
                else if(lineCount >= 2 && lineCount <= nodeNumber + 1)
                {
                    NodeInfo nodeInfo = readNode(line);
                    nodeInfoList.add(nodeInfo);
                }
                // Read edge number
                else if(lineCount == nodeNumber + 2)
                {
                    edgeNumber = Integer.valueOf(line);
                }
                // Read edge info
                else
                {
                    EdgeInfo edgeInfo = readEdge(line);
                    edgeInfoList.add(edgeInfo);
                }

                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GraphInfo graphInfo = null;

        if(nodeNumber > 0 && edgeNumber > 0) {
            graphInfo = new GraphInfo(nodeNumber, edgeNumber, nodeInfoList, edgeInfoList);
        }
        return  graphInfo;
    }

    private NodeInfo readNode(String data)
    {
        String[] info = data.split(" ");
        int x = Integer.valueOf(info[1]);
        int y = Integer.valueOf(info[2]);
        int z = Integer.valueOf(info[3]);
        Coordinate3 coordinate = new Coordinate3(x, y, z);
        NodeInfo nodeInfo = new NodeInfo(info[0], coordinate);
        return  nodeInfo;
    }

    private EdgeInfo readEdge(String data)
    {
        String[] info = data.split(" ");
        double length = Double.valueOf(info[2]);
        String id = info[0] + info[1];
        EdgeInfo edgeInfo = new EdgeInfo(id, info[0], info[1], length, info[3]);
        return  edgeInfo;
    }
}
