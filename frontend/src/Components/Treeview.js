import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TreeView from '@material-ui/lab/TreeView';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import TreeItem from '@material-ui/lab/TreeItem';
import Box from "@material-ui/core/Box";


const data = {
  id: 'root',
  name: 'Test_Directory',
  type : 'folder',
  path:'../Test_Directory',
  children: [
    {
      id: '0',
      name: 'testfile1.txt',
      type : 'file',
      path : '../Test_Directory/testfile1.pdf'
    },
    {
      id: '1',
      name: 'testfile2.txt',
      type : 'file',
      path : '../Test_Directory/testfile2.pdf'
    },
    {
      id: '2',
      name: 'test.json',
      type : 'file',
      path : '../Test_Directory/test.json'
    },
  ],
};

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
  props.setItem(data);
  const checkItem = (nodes) => {
    // array iteration 
    props.setSelectedTab(nodes);
    console.log( "tyep", nodes.context_type);
    // let extension = nodes.name.substr(nodes.name.lastIndexOf('.') + 1).toLowerCase();
    // console.log(extension);
    // extension === "json" ? props.setSelectedTab(0) : props.setSelectedTab(1);
    // console.log(props);
  }
    
  
  const renderTree = (nodes) => (
    <TreeItem key= {Math.random()} nodeId={nodes.id} label={nodes.name}  onClick={(e) => checkItem(nodes)} >
      {Array.isArray(nodes.children) ? nodes.children.map((node) => renderTree(node)) : null}
    </TreeItem>
  );

  return (
    <div  style={{ width: "100%", }}>
    <Box p={1} height="auto" >
    <TreeView
      className={classes.root}
      defaultCollapseIcon={<ExpandMoreIcon />}
      defaultExpanded={['root']}
      defaultExpandIcon={<ChevronRightIcon />}
    >
      {renderTree(data)}
    </TreeView>
    </Box>
    </div>
  );
}
