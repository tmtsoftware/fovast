/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */

package org.tmt.fovast.instrumentconfig;

/**
 *
 */
public class OrCondition {

    private Condition leftCondition;

    private Condition rightCondition;

    public OrCondition(Condition leftCondition, Condition rightCondition) {
        this.leftCondition = leftCondition;
        this.rightCondition = rightCondition;
    }

    public boolean isTrue() {
        if(leftCondition.isTrue())
            return true;
        else if(rightCondition.isTrue())
            return true;
        else
            return false;
    }
}

