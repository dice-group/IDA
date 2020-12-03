import React from "react";
import TreeView from "@material-ui/lab/TreeView";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import TreeItem from "@material-ui/lab/TreeItem";
import Box from "@material-ui/core/Box";

import "./navbar.css";

export default function IDANavbar(props) {
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
  const fetchNodeFromId = (nodeId, tree) => {
    let found = tree.find((t) => t.id === nodeId);
    if (!found) {
      tree.forEach((t) => {
        if (t.children && t.children.length && !found) {
          found = fetchNodeFromId(nodeId, t.children);
        }
      });
    }
    return found;
  };
  const handleSelect = (event, nodeId) => {
    const selectedNode = fetchNodeFromId(nodeId, detail);
    if (selectedNode && selectedNode.type !== "parent") {
      props.setSelectedNodeId(nodeId);
      const tabs = props.tabs;
      if (tabs.findIndex((t) => t.id === nodeId) < 0) {
        tabs.push(selectedNode);
        props.setTabs(tabs);
      }
      if (selectedNode.type === "table" || selectedNode.type === "clustering") {
        props.setActiveTable(selectedNode.fileName);
        if (selectedNode.type === "clustering") {
          props.setActiveTableData(selectedNode.data);
        } else {
          props.setActiveTableData(null);
        }
      } else {
        props.setActiveTable("");
      }
      props.setActiveDS(selectedNode.dsName);
      if (window.matchMedia("(max-width: 991px)").matches) {
        props.setNavBarVisiblity(!props.navBarVisiblity);
        if (!props.navBarVisiblity) {
          props.setNavBarClass("");
        } else {
          props.setNavBarClass("navwindow-shown");
        }
      }
    }
  };

  return (
    <div>
      <Box p={1} height="auto" >
        <TreeView
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
