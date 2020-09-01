import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import ChatBot from "./chatbot/chatBot";
import RecursiveTreeView from "./navbar/navbar";
import TabsWrappedLabel from "./tabs/tabs";
import "./home.css";

export default function Home(props) {
  const [detail, setDetails] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const [selectedNodeId, setSelectedNodeId] = useState("");
  const [expandedNodeId, setExpandedNodeId] = useState([]);
  const [activeDS, setActiveDS] = useState("");
  const [activeTable, setActiveTable] = useState("");
  const loadTab = (loaded) => {
    if (loaded) {
      return <TabsWrappedLabel
        loaded={loaded}
        detail={detail}
        selectedNodeId={selectedNodeId}
        setSelectedNodeId={setSelectedNodeId}
        setActiveTable={setActiveTable}
      />
    }
  }
  return (
    <div className={"root"}>
      <div>
        <Grid container >
          <Grid item xs={3} className="nav-bar-container">
            <RecursiveTreeView
              loaded={loaded}
              selectedNodeId={selectedNodeId}
              setSelectedNodeId={setSelectedNodeId}
              expandedNodeId={expandedNodeId}
              setExpandedNodeId={setExpandedNodeId}
              detail={detail}
              setActiveDS={setActiveDS}
              setActiveTable={setActiveTable}
            />
          </Grid>
          <Grid item xs={9} className={"content"}>
            {loadTab(loaded)}
          </Grid>
        </Grid>
      </div>
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
      />
    </div>
  );
}
