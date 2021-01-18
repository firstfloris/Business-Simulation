package assignments;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import umontreal.ssj.charts.EmpiricalChart;
import umontreal.ssj.charts.EmpiricalSeriesCollection;
import umontreal.ssj.probdist.EmpiricalDist;
import umontreal.ssj.stat.Tally;
import java.io.PrintStream;

/**
 * @author mctenthij
 * Edited by qvanderkaaij and jberkhout
 */
public class Assignment1 {
	
	// LCG parameters (notation from slides used)
	double seed = 0;
	double m = Math.pow(2, 48); /// EXPLAIN
	double a = 25214903917.0; /// EXPLAIN
	double c = 11; /// EXPLAIN

	int raceTo = 5; // number of games to win the game
	double winThreshold = 0.5; // winning probability of a player

	LCG prng;
	EmpiricalDist durationDist;

	PrintStream out;

	Assignment1() {
		out = new PrintStream(System.out);
	}

	/* DO NOT CHANGE THE CODE IN QUESTION1 AND QUESTION2 BELOW */
	
	public double[] Question1(double givenSeed, int numOutputs, boolean normalize) {
		prng = new LCG(givenSeed,a,c,m);
		double[] result = new double[numOutputs];
		for (int i = 0; i < numOutputs; i++) {
			result[i] = prng.generateNext(normalize);
		}
		return result;
	}

	public EmpiricalDist Question2(String csvFile) throws IOException{
		EmpiricalDist myDist = getDurationDist(csvFile);
		return myDist;
	}

	public void plotEmpiricalCDF(EmpiricalDist myDist) {
		// Use EmpiricalChart to plot the CDF
		EmpiricalChart chartCDF = new EmpiricalChart("ECDF of the game length", "Game length in seconds", "F(x)", durationDist.getParams());
		chartCDF.view(500, 500);
	}

	public Tally Question3() { // QUESTION 3  
		Tally durations = new Tally("Random numbers");
		int nrOfSimulations = 5000; 
		prng = new LCG(seed,a,c,m);
		for (int i = 0; i < nrOfSimulations;i++) {
			durations.add(simulateMatch(raceTo));
		}; 
		return durations;	
	}
		
	public double simulateMatch(int raceTo) {
		
		int scoreOne = 0;
		int scoreTwo = 0;
		double matchDuration = 0;
		
		while(scoreOne < raceTo && scoreTwo < raceTo) {
			
			double winProb = prng.generateNext(true);
			if (winProb < winThreshold) {
				scoreOne ++;
			} else {
				scoreTwo ++;
			}
			
			matchDuration += durationDist.inverseF(prng.generateNext(true)); 
			
		}
		
		return matchDuration;
	} 
	
	public EmpiricalDist getDurationDist(String csvFile) throws IOException{
	
		FileReader file = new FileReader(csvFile);
		durationDist = new EmpiricalDist(file);
		
		return durationDist;
	}

	/*  ONLY CHANGE generateNext in the LCG class */
	public class LCG {
		public double seed;
		public final double m;
		public final double a;
		public final double c;

		public double lastOutput;

		public LCG(double seed, double a,double c,double m){
			this.seed = seed;
			this.m = m;
			this.a = a;
			this.c = c;

			this.lastOutput = seed;
		}

		public double generateNext(boolean normalize){
			
			double normalized = (((a * this.lastOutput) + c) % m);
			this.lastOutput = normalized;
			
			if (normalize) {
				return (normalized +1)/(m+1);
			} else {
				return normalized;
			}
		}
	}

	public void start() throws IOException {
		// This is your test function. During grading we will execute the function that are called here directly.
		double givenSeed = seed;
		int numOutputs = 3;
		
		// Run Question 1: once regularly and once normalized 
		double[] outputRegRNG = Question1(givenSeed, numOutputs, false);
		double[] outputNormRNG = Question1(givenSeed, numOutputs, true);
		for (int i = 0; i < numOutputs; i++) {
			out.println("Regular:" + outputRegRNG[i]);
			out.println("Normalized:" + outputNormRNG[i]);
		}
		
		// Name of CSV file is read and passed on to Question2 for loading 
		String csvFile = "game_lengths.csv";
		EmpiricalDist myDist = Question2(csvFile);
		
		// Quantiles are printed and the ECDF plotted
		out.println(myDist.inverseF(0.0));
		out.println(myDist.inverseF(0.25));
		out.println(myDist.inverseF(0.5));
		out.println(myDist.inverseF(0.75));
		out.println(myDist.inverseF(1.0));
		plotEmpiricalCDF(myDist);
		
		// Run Question 3
		Tally durations = Question3();
		out.println(durations.report());
	}

	public static void main(String[] args) throws IOException{
		new Assignment1().start();
	}
}