package assignments;

import java.util.HashSet;
import java.util.Random;
import umontreal.ssj.probdist.NormalDist;
import umontreal.ssj.rng.MRG32k3a;
import umontreal.ssj.simevents.Sim;
import umontreal.ssj.stat.TallyStore;

/**
 *
 * @author mctenthij
 * Edited by qvanderkaaij and jberkhout
 */
public class Assignment3 {

    // optimization variables
    State[] outputs;
    int numStates;
    int budget;
    int xmin;
    int ymin;
    int xmax;
    int ymax;

    // threshold queue variables
    int k;
    int K;
    double lambda;
    double avgService;
    double avgHighService;
    double stopTime;

    Random rng = new Random();

    static final double BIGM = 9999999999999.99;

    public Assignment3(int xmin, int xmax, int ymin, int ymax, int budget, double lambda, double muLow, double muHigh, double stopTime, int k, int K) {
       
    	// check how many states are possible
    	int xrange = xmax - xmin + 1;
        int yrange = ymax - ymin + 1;
        numStates = xrange*yrange;
        outputs = new State[numStates];

        // create states and store them in outputs[]
        for (int i = 0; i < xrange; i++) {
            for (int j = 0; j < yrange; j++) {
                State state = new State(i+xmin,j+ymin);
                outputs[(yrange)*i+j] = state;
            }
        }

        // set optimization variables
        this.budget = budget;
        this.ymin = ymin;
        this.xmin = xmin;
        this.ymax = ymax;
        this.xmax = xmax;

        // set threshold queue variables
        this.k = k;
        this.K = K;
        this.lambda = lambda;
        this.avgService = muLow;
        this.avgHighService = muHigh;
        this.stopTime = stopTime;
    }

	// A state represents a solution and has an x and y value which correspond
    // with the k and K value, respectively.
    class State {

        int xval;
        int yval;
        TallyStore values;

        public State(int x, int y) {
            this.xval = x;
            this.yval = y;
            this.values = new TallyStore("("+x+","+y+")");
            this.values.init();
        }
    }

    // Calculates the index position of a state in the output array.
    public int calcPos(int x, int y) {
        return (x-xmin)*(ymax-ymin+1)+y-ymin;
    }

    // Returns the optimal state after using a ranking algorithm based on the average costs
    public State selectOptimalState() {
        double minimum = BIGM;
        State min = null;
        for (int i = 0; i < numStates; i++) {
            if (outputs[i].values.numberObs() > 0) {
                if (outputs[i].values.average() < minimum) {
                    minimum = outputs[i].values.average();
                    min = outputs[i];
                }
            }
        }
        return min;
    }

    public State getState(int[] val) {
        int pos = calcPos(val[0],val[1]);
        return outputs[pos];
    }

    // Use this method to create a new, random MRGG32k3a variable.
    public MRG32k3a getStream() {
        long[] seed = new long[6];

        //TO DO: Fill the long[] with random seeds, for example, using rng

        MRG32k3a myrng = new MRG32k3a();
        myrng.setSeed(seed);
        return myrng;
    }

    public void runSingleRun(int k, int K) {

        // init random sources
    	MRG32k3a arrival = getStream();
        MRG32k3a service = getStream();
        
        // init simulation and create threshold queue
        Sim.init();
        ThresholdQueue model = new ThresholdQueue(lambda, avgService, avgHighService, stopTime, k, K, arrival, service);
       
        // get results and add to the right state
        double result = model.getAverageCosts().average();
        int i = calcPos(k, K);
        outputs[i].values.add(result);
    }

    public State runRankingSelection(int initialRuns, double alpha) {

        // perform initial runs

        HashSet<State> I = selectCandidateSolutions(alpha);

        // perform rest of the runs

        State opt = selectOptimalState();

        return opt;
    }

    public HashSet<State> selectCandidateSolutions(double alpha) {
        HashSet<State> I = new HashSet();

        // find all candidate solutions for the ranking and selection method

        return I;
    }

    public State runLocalSearch() {

    	// perform Local Search

        State opt = selectOptimalState();

        return opt;
    }

    public State selectBestState(State current, State neighbor){

    	// return best state

        return current;
    }

    public State selectRandomStart() {
        State state;

        // select a random state

        return state;
    }

    public State selectRandomNeighbor(State state) {
        State neighbor;

        // select a random neighbor

        return neighbor;
    }

    public double[] simulateCommonRandomNumbersRun(int k2, int K2){
        double[] results = new double[2];

        // perform CRN on (k,K) and (k2,K2) as parameters, average costs is result per run

        return results;
    }

    public static void main(String[] args) {
        int k = ;             			 // k-threshold for queue
        int K = ;             			 // K-threshold for queue
        int k2 = ;            			 // k-threshold for alternative queue
        int K2 = ;            			 // K-threshold for alternative queue
        double lambda = ;     			 //service rate	
        double muLow = ;				 // average low service time
        double muHigh = ;    			 // average high service time
        double stopTime = 10000;     	 // Simulation endtime (seconds)

        int xmin = ;					 // Lowest possible value for k
        int xmax = ;					 // Highest possible value for k
        int ymin = ;					 // Lowest possible value for K	
        int ymax = ;					 // Highest possible value for K
        int budget = 5000;				 // Budget for the initial runs
        
        int initialRuns = 2500;			  // initial runs for the Ranking and selection method
        double alpha = 0.05; 			  // alpha value for the Ranking and selection method

        Assignment3 crn = new Assignment3(xmin, xmax, ymin, ymax, budget, lambda, muLow, muHigh, stopTime, k, K);
        crn.simulateCommonRandomNumbersRun(k2,K2);

        Assignment3 optimization = new Assignment3(xmin, xmax, ymin, ymax, budget, lambda, muLow, muHigh, stopTime, k, K);
        optimization.runLocalSearch();

        Assignment3 optimization2 = new Assignment3(xmin, xmax, ymin, ymax, budget, lambda, muLow, muHigh, stopTime, k, K);
        optimization2.runRankingSelection(initialRuns, alpha);
    }
}