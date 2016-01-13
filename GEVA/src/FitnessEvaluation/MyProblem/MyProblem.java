package FitnessEvaluation.MyProblem;

import FitnessEvaluation.util.Range;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.Individual;
import Util.Random.RandomNumberGenerator;
import Util.Random.Stochastic;
import FitnessEvaluation.FitnessFunction;
import Util.Constants;
import org.jatha.*;
import org.jatha.dynatype.*;
/**
 * Interpreter for symbolic regression.
 * @author erikhemberg
 */
public class MyProblem implements FitnessFunction, Stochastic {

    RandomNumberGenerator rng;
    double[] x; //Values of the data points for each variable x0,x1,...
    double[] calculated_target; //Values of the target at each data point
    double[][] samples; //Values for each variable at each data point
    String program[]; //Parsed phenotype string is the program
    int programCounter; //Counter for program
    private Range range; //Range of samples
    private Jatha myLisp;  // Lisp VM

    public MyProblem() {
        // create Lisp VM
        myLisp = new Jatha(false, false);
        myLisp.init();
        myLisp.start();
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    /**
     * Calcualte the value of the target function for each data point
     * in the sample.
     * @param target target function in prefix notation
     */
    public void calculateTarget(final String target) {
        this.program = target.split(" ");//Split the target at whitspace
        this.calculated_target = new double[this.samples.length];
        for (int i = 0; i < this.samples.length; i++) {
            for (int j = 0; j < this.x.length; j++) {
                this.x[j] = this.samples[i][j];//Use the data points for each variable
            }
            this.programCounter = 0;//Initialize program counter
            this.calculated_target[i] += this.run();//Sum the values of the target
        }
    }

    public void setProperties(Properties p) {
        //Set samples
        System.out.println("Setting properties");
        //FIXME (If there is a random range there must be a random number generator)
        if(p.getProperty(Constants.SR_RANGE) == null)
			System.out.println("SR_RANGE constant needs to be defined in the properties file");
		this.range = new Range(p.getProperty(Constants.SR_RANGE), this.rng);
        //Set the dimensions
        this.x = new double[this.range.getDimensions()];
        //Get the initial samples
        //FIXME(If random target must be calculated everytime)
        this.samples = this.range.getSamples();
        //Set the target
        this.calculateTarget(p.getProperty(Constants.SR_TARGET));
    }

    public RandomNumberGenerator getRNG() {
        return this.rng;
    }

    public void setRNG(RandomNumberGenerator m) {
        this.rng = m;
    }

    /**
     * Split the phenotype of the individual. Calculate the fitness as the
     * sum of the squared distance to the target. 
     * Errors in the calculation will lead to default fitness
     * @param ind Individual that will be evaluated and assigned fitness
     */
    public void getFitness(final Individual ind) {
        double fitness = 0;//Initialise fitness
        System.out.println("getting fitness");
        try {
            final String phenotype = ind.getPhenotype().getString();
            System.out.println("PHEN"+phenotype);
            this.program = phenotype.split(" ");
//Get new samples for each evaluation of fitness is the input is random
            if (this.range != null && this.range.isRandom()) {
                this.samples = this.range.getSamples();
            }
            for (int i = 0; i < samples.length; i++) {
                for (int j = 0; j < x.length; j++) {
                    x[j] = samples[i][j];
                }
                this.programCounter = 0; //Reset counter
                final double runValue = this.run(); //Value from run
                //Squared error
                fitness += Math.pow(runValue - calculated_target[i], 2.0);
            }
        } catch (IllegalArgumentException e) {
            System.err.println(this.getClass().getName() + " Error getting fitness:" + e + "\n assigning DEFAULT");
            fitness = BasicFitness.DEFAULT_FITNESS;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(this.getClass().getName() + " Error getting fitness:" + e + "\n assigning DEFAULT");
            fitness = BasicFitness.DEFAULT_FITNESS;
        }
        ind.getFitness().setDouble(fitness);
    }

    /**
     * Executes the function. Currently parses: +, -, *, /, x1,...,xn, constants
     * rnd (the next random double from the random number generator)
     * @return value of execution
     * @throw IllegalArgumentException if the symbol cannot be executed
     */
    private double run() throws IllegalArgumentException {
        /*****/
        String str1 = Arrays.toString(this.program);
        str1 = str1.substring(1, str1.length()-1).replaceAll(",", "");
        //String lispCode = String.format(Locale.US, "(let ((x0 %f) (x1 %f)) %s)", x[0], x[1], str1);
        String lispCode = String.format(Locale.US, "(let ((x0 %f)) %s)", x[0], str1);
        LispValue result1 = null;
        try {
            result1 = myLisp.eval(lispCode);
        } catch (Exception e) {
            System.err.println("LISP Exception: " + e);
        }

        return Double.parseDouble(result1.toString());
        /*****/
//        final String s = this.program[this.programCounter];
//        this.programCounter++;
//        if (s.equals("+")) {//Addition
//            return (run() + run());
//        } else if (s.equals("-")) {//Subtraction
//                return (run() - run());
//        } else if (s.equals("*")) {//Multiplication
//                    return (run() * run());
//        } else if (s.equals("/")) {
//            /* Protected division. Returns the numerator if
//            denominator <= 0.00001 */
//            final double numerator = run(),  denominator = run();
//            if (Math.abs(denominator) > 0.00001) {
//                return (numerator / denominator);
//            } else {
//                return numerator;
//            }
//        } else if (s.matches("x\\d+")) {
//            /*Get which variable is used of x0,..,xn
//             * Varibale number is the digits after x
//             */
//            final int variableNumber = Integer.parseInt(s.substring(1));
//            return x[variableNumber];
//        } else if (s.equals("rnd")) {
//            /*Insert a random number
//             * FIXME: implement other random number generation techniques
//             */
//            return this.rng.nextDouble();
//        } else if (s.matches("-?\\d+\\.?\\d*")) {
//            //Insert a constant will be parsed as double
//            return Double.parseDouble(s);
//        }
        //Should not get here
        //throw new IllegalArgumentException(this.getClass().getName() + " Bad execution value:" + s);
    }

    public boolean canCache() {
        return true;
    }

    public void setSamples(double[][] samples) {
        this.samples = samples;
    }

    public void setX(double[] x) {
        this.x = x;
    }
}