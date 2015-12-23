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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package FitnessEvaluation.MaxProblem;

import Individuals.FitnessPackage.BasicFitness;
import Individuals.GEIndividual;
import Helpers.IndividualMaker;
import java.util.Properties;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbyrne
 */
public class MaxProblemInterpreterTest {

    GEIndividual gei;
    MaxProblemInterpreter instance;

    public MaxProblemInterpreterTest() {
    }


    @Before
    public void setUp() {
        gei = IndividualMaker.makeIndividual();
        instance = new MaxProblemInterpreter();
        instance.setMaxProblemDepth(8);
    }
 
    /**
     * Test of getFitness method, of class MaxProblemInterpreter.
     */
    @Test
    public void testGetFitness() {
        System.out.println("*MaxProblemInterpreterTest: getFitness");
        gei.setPhenotype(IndividualMaker.getPhenotype("+ 0 0"));
        instance.getFitness(gei);
        assertEquals(true, instance.maxParseTreeDepth == 1);
        assertEquals(true, gei.getFitness().getDouble()== instance.maxValue);
        System.out.println("tree depth: "+instance.maxParseTreeDepth);

        gei.setPhenotype(IndividualMaker.getPhenotype("+ 5 5"));
        instance.getFitness(gei);
        assertEquals(true, instance.maxParseTreeDepth == 1);
        String msg = " "+gei.getFitness().getDouble();

        System.out.println("MSG value: "+msg);
        assertEquals(msg,true, gei.getFitness().getDouble()== instance.maxValue-10);
        System.out.println("tree depth: "+instance.maxParseTreeDepth);
        gei.setPhenotype(IndividualMaker.getPhenotype("+ + 5 5 + 5 5"));
        instance.getFitness(gei);
        System.out.println("tree depth: "+instance.maxParseTreeDepth);
        assertEquals(true, instance.maxParseTreeDepth == 2);
        assertEquals(true, gei.getFitness().getDouble()== instance.maxValue-20);
        gei.setPhenotype(IndividualMaker.getPhenotype("+ * 5 5 * 5 5"));
        instance.getFitness(gei);
        System.out.println("tree depth: "+instance.maxParseTreeDepth);
        System.out.println("fitness: "+gei.getFitness().getDouble());
        assertEquals(true, instance.maxParseTreeDepth == 2);
        assertEquals(true, gei.getFitness().getDouble()== instance.maxValue-50);

        //Too deep
        gei.setPhenotype(IndividualMaker.getPhenotype("+ + + + + + + + + 5 5 5 5 5 5 5 5 5 5"));
        instance.getFitness(gei);
        System.out.println("tree depth: "+instance.maxParseTreeDepth);
        System.out.println("fitness: "+gei.getFitness().getDouble());
        assertEquals(true, instance.maxParseTreeDepth == 9);
        assertEquals(true, gei.getFitness().getDouble()== BasicFitness.DEFAULT_FITNESS);
    }

    /**
     * Test of canCache method, of class MaxProblemInterpreter.
     */
    @Test
    public void testCanCache() {
        System.out.println("*MaxProblemInterpreterTest: canCache");
        boolean expResult = true;
        boolean result = instance.canCache();
        assertEquals(expResult, result);
    }

    /**
     * Test of setProperties method, of class MaxProblemInterpreter.
     */
    @Test
    public void testSetProperties() {
        System.out.println("*MaxProblemInterpreterTest: setProperties");
        Properties p = new Properties();
        p.setProperty("maxproblem_depth", "5");
        instance.setProperties(p);
        assertEquals(5, instance.maxProblemDepth);
    }
}