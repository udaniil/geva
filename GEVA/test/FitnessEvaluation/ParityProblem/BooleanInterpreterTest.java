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

import Individuals.Individual;
import Individuals.Phenotype;
import Helpers.IndividualMaker;
import Util.Structures.ReversePolish;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author erikhemberg
 */
public class BooleanInterpreterTest {

    public BooleanInterpreterTest() {
    }

    /**
     * Test of getFitness method, of class BooleanInterpreter.
     */
    @Test
    public void testGetFitness() {
        System.out.println("*BooleanInterpreterTest: getFitness");
        Individual ind = IndividualMaker.makeIndividual();
        Phenotype p = IndividualMaker.getPhenotype("& x0 x1");
        ind.setPhenotype(p);
        BooleanInterpreter instance = new BooleanInterpreter(5);
        instance.setParities(5);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 16);

        //Infix flag
        ind = IndividualMaker.makeIndividual();
        //d0 & ( d2 & d4 & d3 | ( not ( d3 ) & d1 ) )
        p = IndividualMaker.getPhenotype("d0 & ( d2 & d4 & d3 | ( not ( d3 ) & d1 ) )");
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(5);
        instance.setInfix(true);
        instance.setRevesePolish(new ReversePolish());
        instance.setParities(5);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 17);

        ind = IndividualMaker.makeIndividual();
        p = IndividualMaker.getPhenotype("& x0 ~ & x0 x1");
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(5);
        instance.setParities(5);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 16);

        ind = IndividualMaker.makeIndividual();
        //x0 & x0 & ~ x1 == & x0 & x0 ~ x1
        ReversePolish rp = new ReversePolish("x0 & x0 & ~ x1");
        rp.toReversePolish();
        rp.treeFromPostfix();
        p = IndividualMaker.getPhenotype(rp.toPrefixString(rp.getRoot()));
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 1);

        ind = IndividualMaker.makeIndividual();
        //x0 & ~ x0 & x1 == & x0 & ~ x0 x1
        rp = new ReversePolish("x0 & ~ x0 & x1");
        rp.toReversePolish();
        rp.treeFromPostfix();
        p = IndividualMaker.getPhenotype(rp.toPrefixString(rp.getRoot()));
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 2);

        ind = IndividualMaker.makeIndividual();
        //~ x0 & x0 & x1 == & & x0 x1 ~ x0
        rp = new ReversePolish("~ x0 & x0 & x1");
        rp.toReversePolish();
        rp.treeFromPostfix();
        p = IndividualMaker.getPhenotype(rp.toPrefixString(rp.getRoot()));
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 2);

        ind = IndividualMaker.makeIndividual();
        //~ ( x0 & x0 & x1 ) == ~ & x0 & x0 x1
        rp = new ReversePolish("~ ( x0 & x0 & x1 )");
        rp.toReversePolish();
        rp.treeFromPostfix();
        p = IndividualMaker.getPhenotype(rp.toPrefixString(rp.getRoot()));
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 1);

        ind = IndividualMaker.makeIndividual();
        //x0 & ~ ( x0 & x1 ) == & x0 ~ & x0 x1
        rp = new ReversePolish("x0 & ~ ( x0 & x1 )");
        rp.toReversePolish();
        rp.treeFromPostfix();
        p = IndividualMaker.getPhenotype(rp.toPrefixString(rp.getRoot()));
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 1);

        ind = IndividualMaker.makeIndividual();
        //~ ( ~ x0 & ~ ( ~ x0 & ~ x1 ) ) == ~ & ~ x0 ~ & ~ x0 ~ x1
        rp = new ReversePolish("~ ( ~ x0 & ~ ( ~ x0 & ~ x1 ) )");
        rp.toReversePolish();
        rp.treeFromPostfix();
        p = IndividualMaker.getPhenotype(rp.toPrefixString(rp.getRoot()));
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 3);

        //Infix flag
        ind = IndividualMaker.makeIndividual();
        p = IndividualMaker.getPhenotype("x0 & x1 & x0");
        ind.setPhenotype(p);
        instance = new BooleanInterpreter(2);
        instance.setInfix(false);
        instance.setRevesePolish(new ReversePolish());
        instance.setParities(2);
        instance.getFitness(ind);
        assertEquals((int) ind.getFitness().getDouble(), 2);        

    }

    @Test
    public void testGetParities() {
        System.out.println("*BooleanInterpreterTest: getParities");
        BooleanInterpreter instance = new BooleanInterpreter(5);
        instance.setParitiesArray(12);
        instance.setParitiesValues(11);        
    }
}