/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tmt.fovast.astro.util;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class DegreeCoverterTest {

    public DegreeCoverterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of parseAndConvertRa method, of class DegreeCoverter.
     */
    @Test
    public void testForPassingParseAndConvertRa() throws Exception {
        System.out.println("parseAndConvertRaPassing");
        String[] value = {"+0h42m44.323s","2h10m","10m30s","2h30s","2h","10m","30s","0:42:44.323","0 42 44.323"};
        double[] expResult = {10.684679166666665,32.5000,2.6250,30.12499,30,2.5,0.125,10.684679166666665,10.684679166666665};       
        double result;
        for(int i=0;i<value.length;i++){
            result = DegreeCoverter.parseAndConvertRa(value[i]);
            assertEquals(expResult[i], result,0.001F);
        }
        // TODO review the generated test code and remove the default call to fail.
       
    }
//    @Test
//    public void testForFailingParseAndConvertRa() throws Exception {
//        System.out.println("parseAndConvertRaFailing");
//        String[] value = {"-0h42m44.323s","25h"};
//        double[] expResult = {-10.684679166666665,375};
//        double result;
//        for(int i=0;i<value.length;i++){
//            try{
//                result = DegreeCoverter.parseAndConvertRa(value[i]);
//                fail("Test fail");
//            }
//            catch(DegreeCoverter.IllegalFormatException ife){
//            }
//            catch(IllegalArgumentException iae){
//            }
//        }
//        // TODO review the generated test code and remove the default call to fail.
//
//    }
    /**
     * Test of parseAndConvertDec method, of class DegreeCoverter.
     */
    @Test
    public void testParseAndConvertDec() throws Exception {
        System.out.println("parseAndConvertDec");
        String[] value = {"-41d16m8.544s","2d10m","10m30s","2d30s","2d","10m","30s","+41:16:8.544","41 16 8.544"};
        double[] expResult = {-41.26904,2.1667,0.1750,2.00833,2,0.16666,0.00833,41.26904,41.26904};
        double result;
        for(int i=0;i<value.length;i++){
            result = DegreeCoverter.parseAndConvertDec(value[i]);
            assertEquals(expResult[i], result,0.001F);
        }
        // TODO review the generated test code and remove the default call to fail.
        
    }

    /**
     * Test of main method, of class DegreeCoverter.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        DegreeCoverter.main(args);
        // TODO review the generated test code and remove the default call to fail.
        
    }

}