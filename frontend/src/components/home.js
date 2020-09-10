import React, { useState } from "react";
import { makeStyles } from '@material-ui/core/styles';
import Grid from "@material-ui/core/Grid";
import ChatBot from "./chatbot/chatBot";
import IDANavbar from "./navbar/navbar";
import TabsWrappedLabel from "./tabs/tabs";
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import CssBaseline from '@material-ui/core/CssBaseline';
import IconButton from '@material-ui/core/IconButton';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';

import "./home.css";
import { Typography } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  menuButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
  }
}));

export default function Home(props) {
  const classes = useStyles();
  const [detail, setDetails] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const [selectedNodeId, setSelectedNodeId] = useState("");
  const [expandedNodeId, setExpandedNodeId] = useState([]);
  const [activeDS, setActiveDS] = useState("");
  const [activeTable, setActiveTable] = useState("");
  const [tabs, setTabs] = useState([]);
  const loadTab = (loaded) => {
    if (loaded && tabs.length) {
      return <TabsWrappedLabel
        loaded={loaded}
        detail={detail}
        selectedNodeId={selectedNodeId}
        setSelectedNodeId={setSelectedNodeId}
        setActiveTable={setActiveTable}
        tabs={tabs}
        setTabs={setTabs}
      />
    }
  }
  // const [navBarWidth, setNavBarWidth] = useState(3);
  // const [contentWidth, setContentWidth] = useState(9);
  const [navBarVisiblity, setNavBarVisiblity] = useState(true);
  const [navBarClass, setNavBarClass] = useState("");
  const toggleNavBar = () => {
    if (!navBarVisiblity) {
      // setNavBarWidth(3);
      // setContentWidth(9);
      setNavBarClass("");
    } else {
      // setNavBarWidth(1);
      // setContentWidth(11);
      setNavBarClass("navbar-hidden");
    }
    setNavBarVisiblity(!navBarVisiblity);
  }
  return (
    <>
      <CssBaseline />
      <AppBar>
        <Toolbar>
          <IconButton edge="start" className={classes.menuButton} color="inherit" onClick={toggleNavBar}>
            {
              loaded && (navBarVisiblity ? <ChevronLeftIcon fontSize="large" /> : <ChevronRightIcon fontSize="large" />)
            }
          </IconButton>
          <Typography variant="h6" className={classes.title} align="center">
            Intelligent Data Assistant
          </Typography>
        </Toolbar>
      </AppBar>
      <Toolbar />
      <div className={navBarClass}>
        <Grid container>
          <Grid item className={"nav-bar-container"}>
            <div className={"navbar"}>
              <IDANavbar
                loaded={loaded}
                selectedNodeId={selectedNodeId}
                setSelectedNodeId={setSelectedNodeId}
                expandedNodeId={expandedNodeId}
                setExpandedNodeId={setExpandedNodeId}
                detail={detail}
                setActiveDS={setActiveDS}
                setActiveTable={setActiveTable}
                tabs={tabs}
                setTabs={setTabs}
              />
            </div>
          </Grid>
          <Grid item className={"content"}>
            {loadTab(loaded)}
          </Grid>
        </Grid>
        <ChatBot
          setDetails={setDetails}
          setSelectedNodeId={setSelectedNodeId}
          detail={detail}
          expandedNodeId={expandedNodeId}
          setExpandedNodeId={setExpandedNodeId}
          setLoaded={setLoaded}
          activeDS={activeDS}
          activeTable={activeTable}
          setActiveDS={setActiveDS}
          tabs={tabs}
          setTabs={setTabs}
        />
      </div>
    </>
  );
}
