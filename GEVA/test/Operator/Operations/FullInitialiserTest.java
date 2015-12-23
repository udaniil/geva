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
package Operator.Operations;

import Individuals.Phenotype;
import Helpers.GrammarCreator;
import Individuals.GEChromosome;
import Individuals.Genotype;
import Mapper.GEGrammar;
import Mapper.Symbol;
import Util.Constants;
import Util.Random.MersenneTwisterFast;
import Util.Random.RandomNumberGenerator;
import Util.Structures.NimbleTree;
import Util.Structures.TreeNode;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbyrne
 * @author Eoin Murphy
 */
public class FullInitialiserTest {

    private static int NUMBER_OF_TREES = 10000;

    FullInitialiser fi1;
    RandomNumberGenerator rng;
    GEGrammar geg;
    Properties p;

    public FullInitialiserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        rng = new MersenneTwisterFast();
        rng.setSeed(10);
        p = GrammarCreator.getProperties();   
        p.setProperty(Constants.DERIVATION_TREE, "Mapper.DerivationTree");
        p.setProperty(Constants.MAX_DEPTH, "8");
        p.setProperty(Constants.TAIL_PERCENTAGE, "0");
        p.setProperty(Constants.MAX_WRAPS, "0");
        geg = new GEGrammar(p);
        geg.setPhenotype(new Phenotype());
        fi1 = new FullInitialiser(rng, geg, p);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of grow method, of class FullInitialiser.
     */
    @Test
    public void testGrow() {
      int maxDepth = Integer.parseInt(p.getProperty(Constants.MAX_DEPTH));
      for (int d = geg.findRule(geg.getStartSymbol()).getMinimumDepth(); d < maxDepth; d++) {
        fi1.setMaxDepth(d);
        for (int i = 0; i < NUMBER_OF_TREES; i++) {
          NimbleTree<Symbol> dt = new NimbleTree<Symbol>();
          TreeNode<Symbol> tn = new TreeNode<Symbol>();
          tn.setData(geg.getStartSymbol());
          dt.populateStack();
          dt.setRoot(tn);
          dt.setCurrentNode(dt.getRoot());
          int expResult = dt.getDepth();
          fi1.genotype = new Genotype();
          fi1.chromosome = new GEChromosome(100);
          fi1.chromosome.setMaxChromosomeLength(1000);
          fi1.genotype.add(fi1.chromosome);
          boolean result = fi1.grow(dt);
          
          geg.setGenotype(fi1.chromosome);
          assertTrue(geg.genotype2Phenotype(true));
          assertTrue(result);
          assertTrue((dt.getDepth() <= d));
          assertEquals(dt.getDepth(), geg.getMaxCurrentTreeDepth());
          assertEquals(geg.getUsedCodons(), fi1.chromosome.getLength());
          dt = null;
        }
      }
    }
}




















