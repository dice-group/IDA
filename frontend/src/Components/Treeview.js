import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import TreeView from "@material-ui/lab/TreeView";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import TreeItem from "@material-ui/lab/TreeItem";
import Box from "@material-ui/core/Box";

const useStyles = makeStyles({
  Box: {
    height: 1000,
    width: 450,
    backgroundcolor: "lavender",
  },

  root: {
    height: 216,
    flexGrow: 1,
    maxWidth: 400,
  },
});

export default function RecursiveTreeView(props) {
  const classes = useStyles();
  const detail = props.detail;
  const expanded = props.expandedNodeId;
  const chooseSelect = () => {
    return props.selectedNodeId || "";
  };
  const renderTree = (main) => (
    main.id && <TreeItem key={Math.random()} nodeId={main.id} label={main.name}>
      {Array.isArray(main.children) ? main.children.map((node) => renderTree(node)) : null}
    </TreeItem>
  );
  const handleToggle = (event, nodeIds) => {
    props.setExpandedNodeId(nodeIds);
  };
  const handleSelect = (event, nodeId) => {
    props.setSelectedNodeId(nodeId);
    const parentNode = props.detail.find((ds) => ds.id === nodeId || ds.children.findIndex((child) => child.id === nodeId) >= 0);
    props.setActiveDS(parentNode.id || "");
    if (parentNode.id && parentNode.id !== nodeId) {
      props.setActiveTable(parentNode.children.find((child) => child.id === nodeId).name);
    }
  };

  return (

    <div style={{ width: "100%", }}>
      <Box p={1} height="auto" >
        <TreeView
          className={classes.root}
          defaultCollapseIcon={<ExpandMoreIcon />}
          expanded={expanded}
          defaultExpandIcon={<ChevronRightIcon />}
          selected={chooseSelect()}
          onNodeToggle={handleToggle}
          onNodeSelect={handleSelect}
        >
          {detail.map(
            (main) => (
              renderTree(main)
            )
          )}
        </TreeView>
      </Box>
    </div>
  );
}
