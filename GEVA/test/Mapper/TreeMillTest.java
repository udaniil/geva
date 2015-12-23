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

package Mapper;

import Individuals.GEChromosome;
import Individuals.GEIndividual;
import Util.Constants;
import Helpers.GrammarCreator;
import Helpers.IndividualMaker;
import java.util.Properties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbyrne
 */
public class TreeMillTest {

    Properties p;
    GEGrammar rgeg;
    GEGrammar pgeg;
    GEGrammar pageg;
    GEGrammar cgeg;
    GEChromosome gen;


    public TreeMillTest() {
    }

    @Before
    public void setUp() {
        gen = GrammarCreator.getGEChromosome();
        p = GrammarCreator.getProperties();
	p.setProperty(Constants.MAX_WRAPS,"0");
        p.setProperty(Constants.DERIVATION_TREE,"Mapper.ContextualDerivationTree");
        cgeg = new GEGrammar(p);
        cgeg.setGenotype(gen);
        p.setProperty(Constants.DERIVATION_TREE,"Mapper.DerivationTree");
        pageg = new GEGrammar(p);
        pageg.setGenotype(gen);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDerivationTree method, of class TreeMill.
     */
    @Test
    public void testGetDerivationTree() {

        System.out.println("testing ContextualDerivationTree");
        DerivationTree result = TreeMill.getDerivationTree(cgeg);
        assertEquals(true,result instanceof ContextualDerivationTree);

        System.out.println("testing parent");
        result = TreeMill.getDerivationTree(cgeg);
        assertEquals(true,result instanceof DerivationTree);

        System.out.println("testing false");
	result = TreeMill.getDerivationTree(pageg);
        assertEquals(false,result instanceof ContextualDerivationTree);

        //Null
        System.out.println("testing null");
        GEIndividual gei = IndividualMaker.makeIndividual();
        ((GEGrammar)gei.getMapper()).setDerivationTreeType(null);
        result = null;
        try {
            result = TreeMill.getDerivationTree((GEGrammar)gei.getMapper());
        } catch(NullPointerException e) {
            assertTrue(true);
        }
        assertEquals(false,result instanceof DerivationTree);

    }

}