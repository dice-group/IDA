import React from "react";
import IconButton from "@material-ui/core/IconButton";
import { makeStyles } from "@material-ui/core/styles";
import CloseIcon from "@material-ui/icons/Close";
const useStyles = makeStyles((theme) => ({
    root: {
        "& > *": {
            margin: theme.spacing(0.5),
        },
    },

}));

export default function FloatingActionButtons() {
    const classes = useStyles();

    return (
        <div className={classes.root}>
            <IconButton variant="outlined" aria-label="close" size="small" color="white">
                <CloseIcon fontSize="small" color="white" />
            </IconButton>
        </div>
    );
}
