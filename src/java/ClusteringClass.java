/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Daniele
 */
import static com.sun.org.apache.xpath.internal.XPathAPI.eval;
import java.awt.BorderLayout;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DBSCAN;
import weka.clusterers.HierarchicalClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.Normalize;
import weka.gui.explorer.ClustererPanel;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

public class ClusteringClass {

    public static void main(String[] args) throws Exception {
        String filename = "C:\\Users\\Daniele\\Desktop\\Humoradio2.csv";

        try {
            FileWriter fw = new FileWriter(filename);
            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/HumoRadioDB", "dani", "dani");

            String query = "SELECT * FROM SONG_RATING2";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            for (int i = 1; i < 23; i++) {
                if (i != 2) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    String name = rsmd.getColumnName(i);
                    fw.append(name);
                    if (i != 22) {
                        fw.append(',');
                    } else {
                        fw.append('\n');
                    }
                }
            }

            String query1 = "SELECT * FROM SONG_DATA";
            Statement stmt1 = conn.createStatement();
            ResultSet rs1 = stmt1.executeQuery(query1);

            String[] titles = new String[150];

            for (int ii = 0; ii < 150; ii++) {
                rs1.next();
                titles[ii] = rs1.getString("TITLE");
            }

            while (rs.next()) {
                fw.append(rs.getString(1));
                fw.append(',');
                fw.append(rs.getString(3));
                fw.append(',');
                fw.append(rs.getString(4));
                fw.append(',');
                fw.append(rs.getString(5));
                fw.append(',');
                fw.append(rs.getString(6));
                fw.append(',');
                fw.append(rs.getString(7));
                fw.append(',');
                fw.append(rs.getString(8));
                fw.append(',');
                fw.append(rs.getString(9));
                fw.append(',');
                fw.append(rs.getString(10));
                fw.append(',');
                fw.append(rs.getString(11));
                fw.append(',');
                fw.append(rs.getString(12));
                fw.append(',');
                fw.append(rs.getString(13));
                fw.append(',');
                fw.append(rs.getString(14));
                fw.append(',');
                fw.append(rs.getString(15));
                fw.append(',');
                fw.append(rs.getString(16));
                fw.append(',');
                fw.append(rs.getString(17));
                fw.append(',');
                fw.append(rs.getString(18));
                fw.append(',');
                fw.append(rs.getString(19));
                fw.append(',');
                fw.append(rs.getString(20));
                fw.append(',');
                fw.append(rs.getString(21));
                fw.append(',');
                fw.append(rs.getString(22));
                fw.append('\n');
            }

            fw.flush();
            fw.close();
            conn.close();
            System.out.println("CSV File is created successfully.");

            /*
             Clustering part
             */
            DataSource source = new DataSource("C:\\Users\\Daniele\\Desktop\\Humoradio2.csv");
            Instances train = source.getDataSet();

            /*
             Applichiamo il filtro Remove fornito da Weka per non considerare un
             attributo nell'algoritmo di Clustering.
             */
            Remove filter = new Remove();
            filter.setAttributeIndices("1");
            filter.setInputFormat(train);
            Instances train2 = Filter.useFilter(train, filter);
            System.out.println("Nominal attributes removed from computation.");

            /*
             Applichiamo il filtro Normalize fornito da Weka per normalizzare il 
             nostro dataset.
             */
            Normalize norm = new Normalize();
            norm.setInputFormat(train2);
            Instances train3 = Filter.useFilter(train2, norm);
            System.out.println("Dataset normalized.");

            /*
             First Clustering Algorithm
             */
            EuclideanDistance df = new EuclideanDistance();
            SimpleKMeans clus1 = new SimpleKMeans();
            int k = 10;
            clus1.setNumClusters(k);
            clus1.setDistanceFunction(df);
            clus1.setPreserveInstancesOrder(true);
            clus1.buildClusterer(train3);

            /*
             First Evaluation
             */
            ClusterEvaluation eval1 = new ClusterEvaluation();
            eval1.setClusterer(clus1);
            eval1.evaluateClusterer(train3);
            System.out.println(eval1.clusterResultsToString());

            int[] assignments = clus1.getAssignments();
            String[][] dati = new String[150][4];

            for (int kk = 0; kk < 150; kk++) {
                dati[kk][0] = String.valueOf(kk);
                dati[kk][1] = train2.instance(kk).toString();
                dati[kk][2] = String.valueOf(assignments[kk]);
                dati[kk][3] = titles[kk];
            }

            for (int w = 0; w < 10; w++) {
                System.out.println();
                for (int i = 0; i < 150; i++) {
                    if (dati[i][2].equals(String.valueOf(w))) {
                        for (int j = 0; j < 4; j++) {
                            if (j != 3) {
                                System.out.print(dati[i][j] + "-> \t");
                            } else {
                                System.out.println(dati[i][j]);
                            }
                        }
                    }
                }
            }

            /*first graph  
            
             PlotData2D predData = ClustererPanel.setUpVisualizableInstances(train, eval1);
             //String name = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
             String name = "";
             String cname = clus1.getClass().getName();
             if (cname.startsWith("weka.clusterers."))
             name += cname.substring("weka.clusterers.".length());
             else
             name += cname;
            
            
             VisualizePanel vp = new VisualizePanel();
             vp.setName(name + " (" + train.relationName() + ")");
             predData.setPlotName(name + " (" + train.relationName() + ")");
             vp.addPlot(predData);
            
             String plotName = vp.getName();
             final javax.swing.JFrame jf = new javax.swing.JFrame("Weka Clusterer Visualize: " + plotName);
             jf.setSize(500,400);
             jf.getContentPane().setLayout(new BorderLayout());
             jf.getContentPane().add(vp, BorderLayout.CENTER);
             jf.dispose();
             jf.addWindowListener(new java.awt.event.WindowAdapter() {
             public void windowClosing(java.awt.event.WindowEvent e) {
             jf.dispose();
             }
             });
             jf.setVisible(true);
            
             end first graph
             */
            
            /*
             Second Clustering Algorithm
             */
            
            System.out.println();
            
            DBSCAN clus3 = new DBSCAN();
            clus3.setEpsilon(0.7);
            clus3.setMinPoints(2);
            clus3.buildClusterer(train3);

            /*
             Second Evaluation
             */
            ClusterEvaluation eval3 = new ClusterEvaluation();
            eval3.setClusterer(clus3);
            eval3.evaluateClusterer(train3);
            System.out.println(eval3.clusterResultsToString());

            double[] assignments3 = eval3.getClusterAssignments();
            String[][] dati3 = new String[150][4];

            for (int kk = 0; kk < 150; kk++) {
                dati3[kk][0] = String.valueOf(kk);
                dati3[kk][1] = train2.instance(kk).toString();
                dati3[kk][2] = String.valueOf(assignments3[kk]);
                dati3[kk][3] = titles[kk];
            }

            for (int w = 0; w < eval3.getNumClusters(); w++) {
                System.out.println();
                for (int i = 0; i < 150; i++) {
                    if (Double.parseDouble(dati3[i][2]) == w) {
                        for (int j = 0; j < 4; j++) {
                            if (j != 3) {
                                System.out.print(dati3[i][j] + "-> \t");
                            } else {
                                System.out.println(dati3[i][j]);
                            }
                        }
                    }
                }
            }
            System.out.println();
            for (int i = 0; i < 150; i++) {
                if (Double.parseDouble(dati3[i][2]) == -1.0) {
                    for (int j = 0; j < 4; j++) {
                        if (j != 3) {
                            System.out.print(dati3[i][j] + "-> \t");
                        } else {
                            System.out.println(dati3[i][j]);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
