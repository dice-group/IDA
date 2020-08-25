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
let main =[];
export default function RecursiveTreeView(props) {
  const classes = useStyles();
  const callFunction =()=>{
    // eslint-disable-next-line 
    let parent =[];
    let baseName = props.detail.dsMd; 
    let childData = props.detail.dsData;
    let temp=[];

    if(childData !== undefined) {
      parent = childData.map((ch,idx) =>   
        temp.push({id:idx, name: ch.name,type:'file',data:ch.data})
      );
     main = {id:'root', name: baseName.dsName,type:'folder',data:baseName.filesMd,children:temp}
     console.log(main)
    }  
   
}

  
  const checkItem = (main) => {
    props.setSelectedTab(main.id)
    if (main.id === 'root'){
    props.setItem(main);
    }

  }
  
  const renderTree = (main) => (
    <TreeItem key= {Math.random()} nodeId={main.id} label={main.name}  onClick={(e) => checkItem(main)} >
      {Array.isArray(main.children) ? main.children.map((node) => renderTree(node)) : null}
    </TreeItem>
  );
  
  return (
    
    <div  style={{ width: "100%", }}>
      {callFunction()}
    <Box p={1} height="auto" >
    <TreeView
      className={classes.root}
      defaultCollapseIcon={<ExpandMoreIcon />}
      defaultExpanded={['root']}
      defaultExpandIcon={<ChevronRightIcon />}
      selected = {props.selectTree}
      > 
      
      {renderTree(main)}
    </TreeView>
    </Box>
    </div>
  );
}
