import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import TreeItem from '@material-ui/lab/TreeItem';
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
  const main = props.detail;
  const expanded = [props.expandedNodeId];
  const chooseSelect = () => {
    return props.selectedNodeId || '';
  }

  const checkItem = (main) => {
    props.setSelectedNodeId(main.id);
    if(main.type === 'folder') {
      props.setExpandedNodeId(main.id);
    }
  }
  const renderTree = (main) => (
    main.id && <TreeItem key={Math.random()} nodeId={main.id} label={main.name} onClick={(e) => checkItem(main)}  >
      {Array.isArray(main.children) ? main.children.map((node) => renderTree(node)) : null}
    </TreeItem>
  );

  return (

    <div style={{ width: "100%", }}>
      <Box p={1} height="auto" >
        <TreeView
          className={classes.root}
          defaultCollapseIcon={<ExpandMoreIcon />}
          expanded={expanded}
          defaultExpandIcon={<ChevronRightIcon />}
          selected={chooseSelect()}
        >
          {renderTree(main)}
        </TreeView>
      </Box>
    </div>
  );
}
