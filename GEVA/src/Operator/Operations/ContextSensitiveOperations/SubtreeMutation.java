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

package Operator.Operations.ContextSensitiveOperations;

import Operator.Initialiser;
import Exceptions.BadParameterException;
import Mapper.GEGrammar;
import Mapper.Symbol;
import Util.Constants;
import Operator.RampedHalfAndHalfInitialiser;

import Operator.Operations.*;
import Individuals.GEChromosome;
import Individuals.Individual;
import Individuals.GEIndividual;
import Mapper.DerivationNode;
import Mapper.DerivationTree;
import Util.GenotypeHelper;
import Util.Random.RandomNumberGenerator;
import Util.Structures.TreeNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Pick a single point to mutate, but having mutated, remove all the codons that
 *  used to define the old branch, and then follow the new branch, randomly
 *  generating values to pick productions, until a complete branch is built
 *  or the maximum depth is reached. This preserves all branches that were not
 *  part of the chosen [mutated] branch
 * @author eliott bartley
 */
public class SubtreeMutation extends MutationOperation
{

    private CreationOperation creationOperation;
    protected Initialiser initialiser;
    
    public SubtreeMutation(RandomNumberGenerator rng, Properties p)
    {   super(rng, p);
        GEGrammar geg = null;
        String className;


        String key = Constants.GEGRAMMAR;
        try {
            className = p.getProperty(key, Constants.DEFAULT_GEGRAMMAR);
            Class clazz = Class.forName(className);
	    geg = (GEGrammar)clazz.newInstance();
	    geg.setProperties(p);
        } catch(Exception e) {
            System.err.println(this.getClass().getName()+"Could not Create Grammar"+e);
        }
        initialiser = getInitialiser(geg, this.rng, p);
        creationOperation =  (CreationOperation)initialiser.getOperation();
    }

    public SubtreeMutation(double prob, RandomNumberGenerator rng)
    {   super(prob, rng);
    }

    public void setCreationOperation(CreationOperation creationOperation)
    {   this.creationOperation = creationOperation;
    }
    
    @Override
    public void doOperation(Individual operand)
    {
        // Only mutate based on a probability that a mutate should occur
        if(super.rng.nextDouble() >= this.probability)
            return;

        DerivationTree tree = GenotypeHelper.buildDerivationTree(operand);
        boolean isValid = false;

        // Don't operate on invalids
        if(tree == null)
            return;
        
        while(isValid == false)
        {

            // Assume everything will go ok
            isValid = true;

            // Choose a branch to mutate
            GEChromosome chromosome = (GEChromosome)operand.getGenotype().get(0);

	    int maxPoint = getMaxDTIndex(tree);
            // OLD
	    //int point = super.getRNG().nextInt(chromosome.getUsedGenes());
	    int point;
	    if(maxPoint == 0){
		point = 0;
	    }
	    else{
		point = super.rng.nextInt(maxPoint);
		assert point >= 0 & point < maxPoint : "Problem mutating tree:\n" + tree + "\nat point: " + point;
	    }

	    // OLD
	    //assert point >= 0 & point < chromosome.getUsedGenes() : point;

            // Find the tree-node related to the chosen mutation point. As the
            //  point was chosen from the list of used codons, this will always
            //  find the node
            DerivationNode node = GenotypeHelper.findNodeFromCodonIndex
            (   tree,
                point,
                chromosome.getUsedGenes()
            );
            assert node != null;

            // Build a new individual (using the creation operation configured
            //  to create all individuals in the system) but instead of building
            //  a complete individual, this builds one with the root set to the
            //  specified node (so will build a sub-tree)
            Individual mutation = buildMutatedIndividual(node);

            // If the mutated sub-tree is valid..
            if(mutation != null)
            {

                // The the number of codons used to define the chosen branch.
                //  This is how many codons need to be removed to insert the
                //  new branch in its place
                int length = GenotypeHelper.calcNodeLength(node);

                // Create a new chromosome from the original chromosome
                //  (excluding the chosen point up to the number of codons used
                //  to define the branch at the chosen point), and in its place
                //  insert the whole of the chromosome used to define the
                //  mutation [sub-]tree
                chromosome = GenotypeHelper.makeNewChromosome
                (   operand,
                    point,
                    length,
                    mutation,
                    tree
                );

                // Make sure the new tree is valid
                Individual testOperand = operand.clone();
                ((GEChromosome)testOperand.getGenotype().get(0)).setAll(chromosome.data);
                DerivationTree validTree = GenotypeHelper.buildDerivationTree
                (   testOperand
                );

                // dump() is commented out below, imports for dump commented out above
//                if(isValid == true && point != 0)
//                {
//                    String tS = ((GEGrammar)operand.getMapper()).generateNameFromTree(tree);
//                    String vtS = ((GEGrammar)testOperand.getMapper()).generateNameFromTree(validTree);
//                    System.out.println(tS.equals(vtS));
//                    System.out.println(tS);
//                    System.out.println(vtS);
//                    dump("/Users/jonathanbyrne/a.dmp", "", true);
//                    dump("/Users/jonathanbyrne/a.dmp", operand.getGenotype().get(0).toString(), true);
//                    dump("/Users/jonathanbyrne/a.dmp", "point: " + point + "; length: " + length, true);
//                    dump("/Users/jonathanbyrne/a.dmp", GenotypeHelper.buildDerivationTree(mutation).toString(), true);
//                    dump("/Users/jonathanbyrne/a.dmp", "", true);
//                    dump("/Users/jonathanbyrne/a.dmp", tree.toString(), true);
//
//                    dump("/Users/jonathanbyrne/b.dmp", "", true);
//                    dump("/Users/jonathanbyrne/b.dmp", chromosome.toString(), true);
//                    dump("/Users/jonathanbyrne/b.dmp", "point: " + point + "; length: " + length, true);
//                    dump("/Users/jonathanbyrne/b.dmp", GenotypeHelper.buildDerivationTree(mutation).toString(), true);
//                    dump("/Users/jonathanbyrne/b.dmp", "", true);
//                    dump("/Users/jonathanbyrne/b.dmp", validTree.toString(), true);
//                }
                
            
                if(validTree == null)
                    isValid = false;
                else{
                    // Update the individual to use the mutated chromosome, now
                    //  that it's known to be valid
                    operand.getGenotype().set(0, chromosome);
		    operand.getMapper().setGenotype(chromosome);

		    ((GEIndividual)operand).invalidate();

		    operand.map(0);
		}
            }
            else
                // If mutation created an invalid, don't use it
                isValid = false;

            // If the attempt to mutate created an invalid, try to mutate again,
            //  but only if the probability of mutation is again in favour of it
            if(isValid == false)
                if(super.rng.nextDouble() >= this.probability)
                    return;

        }
        
    }

    public int getMaxDTIndex(DerivationTree dT){
	Iterator<TreeNode<Symbol>> nodeItr;
	TreeNode<Symbol> currItr = dT.getRoot();
	List<TreeNode<Symbol>> visitNodes = new LinkedList<TreeNode<Symbol>>();
	visitNodes.add(currItr);
	int maxIndex = 0;

	while(visitNodes.size() > 0){
	    currItr = visitNodes.remove(0);
	    DerivationNode DN = (DerivationNode)currItr;

	    if(DN.getCodonIndex() > maxIndex){
		maxIndex = DN.getCodonIndex();
	    }
	    
	    nodeItr = currItr.iterator();

	    while(nodeItr.hasNext()){
		visitNodes.add(nodeItr.next());
	    }
	}
	return maxIndex;
    }

//    public static void dump(String output)
//    {   dump(output, true);
//    }
//
//    public static void dump(String filename, String output, boolean append)
//    {   try {
//            File file = new File(filename);
//            java.io.FileWriter writer = new FileWriter(file, append);
//            writer.write(output);
//            writer.write(String.format("%n"));
//            writer.close();
//        } catch(IOException e) {}
//    }
//
//    public static void dump(String output, boolean append)
//    {   dump("/Users/jonathanbyrne/dump.txt", output, append);
//    }

    @Override
    public void doOperation(List<Individual> operands)
    {   for(Individual operand : operands)
            doOperation(operand);
    }

    /**
     * Given the node of the chosen point to mutate, create a new individual and
     *  build a tree using the specified node as the root. The chromosome for
     *  this new tree will then be returned and inserted into the original
     *  chromosome
     */
    private Individual buildMutatedIndividual(DerivationNode node)
    {   Individual individual = creationOperation.createIndividual();
        creationOperation.doOperation(individual);
        if(GenotypeHelper.buildDerivationTree(individual, node) != null)
            return individual;
        
        return null;
        
    }
    
    /**
     * Load and initialise the initialiser class according to the parameters
     * Defualt initialiser is the RandomInitialiser.
     * To add other initialisers expand the if-statement with another clause
     * @param g GEGrammar
     * @param rng RandomNumberGenerator
     * @param p Properties
     * @return Intialiser
     */
    protected Initialiser getInitialiser(GEGrammar g, RandomNumberGenerator rng, Properties p) {
        String className;
        String key = Constants.INITIALISER;
        try {
            className = p.getProperty(key);
            if (className == null) {
                throw new BadParameterException(key);
            }
            Class clazz = Class.forName(className);
            initialiser = (Initialiser) clazz.newInstance();
            // For RampedHalfAndHalfInitialiser
            if (clazz.getName().equals(RampedHalfAndHalfInitialiser.class.getName())) {
                CreationOperation fullInitialiser = new FullInitialiser(rng, g, p);
                CreationOperation growInitialiser = new GrowInitialiser(rng, g, p);
                ArrayList<CreationOperation> opL = new ArrayList<CreationOperation>();
                opL.add(fullInitialiser);
                opL.add(growInitialiser);
                ((RampedHalfAndHalfInitialiser) initialiser).setOperations(opL);
            } else {
                // The default initialiser
                CreationOperation randomInitialiser;
                randomInitialiser = new RandomInitialiser(rng, g, p);
                initialiser.setOperation(randomInitialiser);

            }
            initialiser.setProperties(p);
            initialiser.setRNG(rng);
            initialiser.init();
        } catch (Exception e) {
            System.out.println(this.getClass().getName() + ".getInitialiser(.) Exception: " + e);
            e.printStackTrace();
        }
        return initialiser;
    }



}
