/*
Grammatical Evolution in Java
Release: GEVA-v2.0.zip
Copyright (C) 2008 Michael O'Neill, Erik Hemberg, Anthony Brabazon, Conor Gilligan 
Contributors Patrick Middleburgh, Eliott Bartley, Jonathan Hugosson, Jeff Wrigh

Separate licences for asm, bsf, antlr, groovy, jscheme, commons-logging, jsci is included in the lib folder. 
Separate licence for rieps is included in src/com folder.

This licence refers to GEVA-v2.0.

This software is distributed under the terms of the GNU General Public License.


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
/>.
*/

package FitnessEvaluation.ParityProblem;

import Individuals.Phenotype;
import Individuals.FitnessPackage.BasicFitness;
import Individuals.Individual;
import FitnessEvaluation.FitnessFunction;
import Util.Structures.ReversePolish;
import Util.Constants;

import java.util.Properties;
import java.util.Arrays;

/**
 * Interpreter for parity problems.
 * @author erikhemberg
 */
public class BooleanInterpreter implements FitnessFunction {

    //FIXME use booleans
    int programCounter; //Counter for program
    private static int[][] values;
    int size;
    private static int[] parities;
    private int counter; //FIXME global counter
    private static int[] currentValues;
    private Phenotype phenotype;
    private boolean infix;
    private ReversePolish revesePolish;

    public BooleanInterpreter() {
    }

    public BooleanInterpreter(Properties p) {
        this.setProperties(p);
    }
    
    public BooleanInterpreter(int size) {
        this.size = size;
        this.setParitiesArray(size);
        this.setParitiesValues(size-1);
        this.setParities(size);
    }

    void setParitiesArray(int size) {
        BooleanInterpreter.values = new int[(int) Math.pow(2, size)][size];
        BooleanInterpreter.currentValues = new int[size];
        this.counter = 0;
    }

    public void setProperties(Properties p) {
        this.size = Integer.parseInt(p.getProperty(Constants.PARITY_DEGREE));
        this.setParitiesArray(this.size);
        this.setParitiesValues(this.size - 1);
        this.setParities(size);
//        for (int i = 0; i<this.values.length; i++) {
//            System.out.print(this.parities[i] + "=");
//            System.out.println(Arrays.toString(this.values[i]));
//        }
        this.infix = Boolean.parseBoolean(p.getProperty(Constants.INFIX_GRAMMAR));
        if(infix) {
            this.revesePolish = new ReversePolish();
        }
    }

    public void setParitiesValues(int size) {
        for (int i = 0; i < 2; i++) {
            BooleanInterpreter.currentValues[size] = i;
            if (size > 0) {
                setParitiesValues(size - 1);
            } else {
                System.arraycopy(BooleanInterpreter.currentValues, 0, BooleanInterpreter.values[this.counter], 0, BooleanInterpreter.currentValues.length);
                counter++;
            }                
        }
    }

    public void setParities(int size) {
        BooleanInterpreter.parities = new int[(int) Math.pow(2, size)];
        for (int i = 0; i < Math.pow(2, size); i++) {
            BooleanInterpreter.parities[i] = Integer.bitCount(i) % 2;
        }
    }

    /**
     * Split the phenotype of the individual. Calculate the fitness as the
     * sum of the squared distance to the target. 
     * Errors in the calculation will lead to default fitness
     * @param ind Individual that will be evaluated and assigned fitness
     */
    public void getFitness(final Individual ind) {
        double fitness = parities.length;//Initialise fitness
        try {
            phenotype = ind.getPhenotype();
            if(this.infix) {//convert infix to prefix
                revesePolish.clear();
                phenotype = revesePolish.toPrefixPhenotype(phenotype);
            }
	    
            for (this.counter = 0; this.counter < BooleanInterpreter.parities.length; this.counter++) {
                this.programCounter = 0; //Reset counter
		
                if (run() == BooleanInterpreter.parities[this.counter]) {
                    fitness--; //count down fitness when correct expr
                }
            }
	} catch (IllegalArgumentException e) {
            System.err.println(this.getClass().getName() + " Error getting fitness:" + e + "\n assining DEFAULT");
            fitness = BasicFitness.DEFAULT_FITNESS;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(this.getClass().getName() + " Error getting fitness:" + e + "\n assining DEFAULT");
            fitness = BasicFitness.DEFAULT_FITNESS;
        }
        ind.getFitness().setDouble(fitness);
    }

    public void setRevesePolish(ReversePolish revesePolish) {
        this.revesePolish = revesePolish;
    }

    public void setInfix(boolean infix) {
        this.infix = infix;
    }

    /**
     * Executes the function. Currently parses in prefix: &,|,^,~, x1,...,xn
     * @return value of execution
     * @throw IllegalArgumentException if the symbol cannot be executed
     */
    private int run() throws IllegalArgumentException {
        if(this.programCounter < this.phenotype.size()) {
            final String s = this.phenotype.get(this.programCounter).getSymbolString();
	    
            this.programCounter++;
	    // AND
            if (s.equals("&")) {
                return (run() & run());
            } 
	    // OR
	    else if (s.equals("|")) {
		return (run() | run());
	    }
	    // XOR
	    else if (s.equals("^")) {
		return (run() ^ run());
	    } 
	    // NOT
	    else if (s.equals("~") || s.equals("not")) {
		int value = run();
		if (value == 1) {
		    value = 0;
		} else {
		    value = 1;
		}
		return value; 
	    }
	    else if (s.matches("x\\d+") || s.matches("d\\d+")) {
		//Get which variable is used of x0,..,xn
                //Varibale number is the digits after x
		final int variableNumber = Integer.parseInt(s.substring(1));
		return BooleanInterpreter.values[this.counter][variableNumber];
	    }
	    
	    //Should not get here
	    throw new IllegalArgumentException(this.getClass().getName() + " Bad execution value:" + s);
	}

	System.out.println("Prog counter 2: " + this.programCounter);

	//Should not get here
	throw new IllegalArgumentException(this.getClass().getName() + " No more code");
    }

    public boolean canCache() {
        return true;
    }
}