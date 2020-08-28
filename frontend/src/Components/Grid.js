import React, { useState } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import ChatBot from './ChatBot';
import RecursiveTreeView from './Treeview';
import TabsWrappedLabel from './Tabs';
// import './chatbot.css';
/* eslint-disable */
const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
  },
  paper: {
    height: "100%",
    padding: theme.spacing(1),
    textAlign: 'center',
    color: theme.palette.text.secondary,
  },
}));

export default function CenteredGrid(props) {
  const classes = useStyles();
  const [detail, setDetails] = useState([]);
  const [loaded, setLoaded] = useState('false');
  const [selectedNodeId, setSelectedNodeId] = useState('');
  const [expandedNodeId, setExpandedNodeId] = useState('');
  const loadTab = (loaded) => {
    if (loaded === 'true') {
      return <TabsWrappedLabel loaded={loaded} detail={detail} selectedNodeId={selectedNodeId} setSelectedNodeId={setSelectedNodeId} />
    }
  }
  return (
    <div className={classes.root}>
      <div>
        <Grid container >
          <Grid item xs={3}>
            <RecursiveTreeView loaded={loaded} selectedNodeId={selectedNodeId} setSelectedNodeId={setSelectedNodeId} expandedNodeId={expandedNodeId} setExpandedNodeId={setExpandedNodeId} detail={detail} />
          </Grid>
          <Grid item xs={9}>
            {loadTab(loaded)}
          </Grid>
        </Grid>
      </div>
      <ChatBot setDetails={setDetails} setSelectedNodeId={setSelectedNodeId} setExpandedNodeId={setExpandedNodeId} setLoaded={setLoaded} />
    </div>
  );
}
