import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import LinearProgress from "@material-ui/core/LinearProgress";
import Box from "@material-ui/core/Box";

const useStyles = makeStyles({
    root: {
        width: "100%",
        margin: 0
    },
});

export default function IDALinearProgress(props) {
    const classes = useStyles();
    return (
        <div className={classes.root}>
            <Box display="flex" alignItems="center" visibility={props.hide ? "hidden" : "visible"}>
                <Box width="100%">
                    <LinearProgress variant="indeterminate" />
                </Box>
            </Box>
        </div>
    );
}
