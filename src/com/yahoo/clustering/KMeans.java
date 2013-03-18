package com.yahoo.clustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * k-means clustering algorithm
 * 
 * @author Antonio Severien
 * 
 */
public class KMeans {

	File dataset;
	int kClusters;
	int dimension;
	int dataSetSize = 20;
	List<List<Double>> points;
	List<List<Double>> clustersCentroids;
	List<List<Integer>> clustersMapping;

	public KMeans() {
		kClusters = 3;
		dimension = 2;
		points = new ArrayList<List<Double>>(dataSetSize);
		clustersCentroids = new ArrayList<List<Double>>(kClusters);
		clustersMapping = new ArrayList<List<Integer>>(kClusters);
		for (int i = 0; i < kClusters; i++) {
			clustersMapping.add(i, new ArrayList<Integer>());
		}
	}
	
	public KMeans(int clusters, int dimension) {
		this.kClusters = clusters;
		this.dimension = dimension;
		this.points = new ArrayList<List<Double>>(dataSetSize);
		this.clustersCentroids = new ArrayList<List<Double>>(kClusters);
		this.clustersMapping = new ArrayList<List<Integer>>(kClusters);
		for (int i = 0; i < kClusters; i++) {
			clustersMapping.add(i, new ArrayList<Integer>());
		}
	}

	// TODO optimize kmeans algorithm for better performance and memory usage
	protected void run() {
		setKRandomPoints();
		// until objective function does not converge... do
		euclideanDistance(points, clustersCentroids);
		// loop while convergence of objective function not satisfied
		// calculate the new centroid points
		List<Double> newCentroid = new ArrayList<Double>(dimension);
		int pointsCounter = 0;
		double sum = 0d;
		boolean isStopCondition = false;
		while (!isStopCondition) {
			for (int clusterID = 0; clusterID < clustersMapping.size(); clusterID++) {
				for (int d = 0; d < dimension; d++) {
					List<Integer> indexList = clustersMapping.get(clusterID);
					for (Integer clusterPoint : indexList) {
						sum += points.get(clusterPoint).get(d);
						pointsCounter++;
					}
					newCentroid.add(d, sum / pointsCounter);
					sum = 0;
					pointsCounter = 0;
				}
				if (!compare(clustersCentroids.get(clusterID), newCentroid)) {
					clustersCentroids.set(clusterID, newCentroid);
					isStopCondition = false;
				} else {
					isStopCondition = true;
				}
				newCentroid = new ArrayList<Double>(dimension);
			}
			System.out.println(clustersCentroids.get(0));
			System.out.println(clustersCentroids.get(1));
			System.out.println(clustersCentroids.get(2));

			for (int i = 0; i < kClusters; i++) {
				clustersMapping.get(i).clear();
			}

			euclideanDistance(points, clustersCentroids);
		}
	}

	protected boolean compare(List<Double> p1, List<Double> p2) {
		boolean isEqual = true;
		for (int i = 0; i < dimension; i++) {
			isEqual = (p1.get(i).doubleValue() == p2.get(i).doubleValue()) ? true
					: false;
		}
		return isEqual;
	}

	/*
	 * Cost function of euclidean distance between points sqrt(sum(xi^2-cj^2))
	 */
	protected void euclideanDistance(List<List<Double>> x, List<List<Double>> c) {
		// TODO calculate euclidean distance between two points in a
		// d-dimensional space
		double sum = 0.0d;
		int indexC = 0;
		double minDistance = 0.0d;
		double distance = 0.0d;

		// iterate over all points N
		for (int n = 0; n < x.size(); n++) {
			// iterate over centroids C
			for (int k = 0; k < c.size(); k++) {
				// iterate over dimension
				for (int d = 0; d < dimension; d++) {
					sum += Math.pow(x.get(n).get(d), 2)
							- Math.pow(c.get(k).get(d), 2);
				}
				distance = Math.sqrt(Math.abs(sum));
				if (k == 0d) {
					minDistance = distance;
					indexC = k;
				}
				if (distance < minDistance) {
					// see after what happens when two points have the same
					// distance
					minDistance = distance;
					indexC = k;
				}
				sum = 0.0d;
			}
			// this is adding duplicate values, check it !!
			clustersMapping.get(indexC).add(n);
			indexC = 0;
		}
		this.print();

	}

	/*
	 * TODO Search for methods on picking a good seed
	 */
	protected void setKRandomPoints() {
		// for (int i = 0; i < kClusters; i++) {
		// clustersCentroids.add(points.get((int) (Math.random() *
		// dataSetSize)));

		// System.out.println(clusters.toString());
		// }
		clustersCentroids.add(0, points.get(18));
		clustersCentroids.add(1, points.get(19));
		clustersCentroids.add(2, points.get(4));
		System.out.println("Centroids 0:\n" + points.get(18).get(0) + ","
				+ points.get(18).get(1));
		System.out.println("Centroids 1:\n" + points.get(19).get(0) + ","
				+ points.get(19).get(1));
		System.out.println("Centroids 2:\n" + points.get(10).get(0) + ","
				+ points.get(10).get(1));
	}

	// generates dataset points list of 3 dimension
	// TODO read points from dataset...
	protected void generateData() {

		for (int i = 0; i < dataSetSize; i++) {
			List<Double> point = new ArrayList<Double>(dimension);
			point.add(0, Math.random() * 10);
			point.add(1, Math.random() * 150);
			point.add(2, Math.random() * 328);
			points.add(point);

			System.out.println(points.toString());
		}
	}

	// TODO Print function to plot on a graph... gnuplot??
	protected void print() {
		for (int i = 0; i < clustersMapping.size(); i++) {
			for (Integer index : clustersMapping.get(i)) {
				System.out.print(i + ",");
				System.out.println(points.get(index).get(0) + ","
						+ points.get(index).get(1));
			}
		}
		System.out.println("\n----------------------------\n");
	}

	/*
	 * Writes generated dataset to file
	 */
	protected void writeDataSet() {
		File f = new File(System.getProperty("user.dir")
				+ "/resources/dataset.txt");
		FileWriter fw;
		BufferedWriter bw;
		try {
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			StringBuilder sb = new StringBuilder();
			for (List<Double> point : points) {
				sb.append(point.get(0).doubleValue()).append(",")
						.append(point.get(1).doubleValue()).append(",")
						.append(point.get(2).doubleValue()).append("\n");

			}
			System.out.println(sb.toString());
			bw.write(sb.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Reads dataset from file
	 */
	protected void readDataSet() {
		File f = new File(System.getProperty("user.dir")
				+ "/resources/dataset.txt");
		FileReader fr;
		try {
			fr = new FileReader(f);

			BufferedReader br = new BufferedReader(fr);
			String line;
			int skipCount = 0;
			while ((line = br.readLine()) != null) {
				skipCount++;
				if (skipCount > 40) {
					StringTokenizer st = new StringTokenizer(line, ",");
					st.nextToken();
					String[] arrPoints = line.split(",");
					List<Double> point = new ArrayList<Double>(dimension);
					point.add(0, Double.valueOf(arrPoints[0]));
					point.add(1, Double.valueOf(arrPoints[1]));
					point.add(2, Double.valueOf(arrPoints[2]));
					points.add(point);
				}
				if (skipCount - 40 == dataSetSize)
					break;
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		KMeans kmeans = new KMeans();
		kmeans.readDataSet();
		kmeans.run();
	}
}
