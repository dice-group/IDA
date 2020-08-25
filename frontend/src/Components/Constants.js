import React, { useState } from "react";
import ChatBot from './ChatBot';

export default function Constants(){
    const [uiActions, setUiActions] =useState([]);
    setUiActions([
        /**
         *  UI action codes
         */
	    // Load message normally
        {UAC_NrmlMsg : 1001},
        // Load dataset
        {UIA_LOADDS :1004},
        // Open Upload dataset modal window
        {UAC_UPLDDTMSG :1002},
        /**
         *  Pre-defined action codes
         */
        // Know more button
        {PDAC_KNWMR : 2001},
        // Upload dataset button
        {PDAC_UPLDDT : 2002},
        // View existing datasets button
        {PDAC_VWDTS : 2003},
    ])

    return(
        <div>
            <ChatBot uiActions={uiActions} setUiActions={setUiActions()}/>
        </div>
    )
}
