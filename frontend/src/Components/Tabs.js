import React, { useEffect } from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
import CustomizedTables from './Datatable';
import SpanningTable from "./spanDataTable";
/* eslint-disable */
function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`scrollable-auto-tabpanel-${index}`}
      aria-labelledby={`scrollable-auto-tab-${index}`}
      {...other}
    >
      {value === index && (
        <Box p={3}>
          <Typography>{children}</Typography>
        </Box>
      )}
    </div>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: '100%',
    backgroundColor: theme.palette.background.paper,
  },
}));
let tdp =[];
let tabs =[];
let tabdata = [];
let tabmeta = [];
export default function ScrollableTabsButtonAuto(props) {
  const classes = useStyles();
  // const  [selectValue , setSelectValue] = React.useState();

  const handleChange = (event, newValue) => {
    console.log("xkmmcm,m,");
    props.setSelectedTab(newValue); 
    // props.setSelectTree(newValue);
  };
  useEffect(()=>{

  console.log(props);
  if(props.item !== undefined) {
    tdp = props.item;
    tabmeta =[{id:tdp.id,name:tdp.name,type:tdp.type,data:tdp.data}]
    let newData = props.item.children;
    tabs = newData;
    tabdata = [tabmeta,tabs];
  }
  
}) 
const checkItem = (index) => {
  // array iteration
  props.setSelectTree(index)

}
  const renderData =()=>{
  if(props.loaded === 'true'){
     if (props.selectedTab !=='root'){
      return <CustomizedTables item = {props.item} selectedTab={props.selectedTab} selectTree={props.selectTree}/>
    }else{
      return <SpanningTable item = {props.item}  selectedTab={props.selectedTab} selectTree={props.selectTree}/>
    }
  }
  }

  return (
    <div className={classes.root}>
      <ul>     
      <AppBar position="static" color="default"  >
        <Tabs
        onChange={handleChange}
        /* eslint-disable */
        indicatorColor="#4f8bff"
        textColor="primary"
        variant="scrollable"
        scrollButtons="auto"
        aria-label="scrollable auto tabs example"
        value = {props.selectedTab}
      >   
      
      {tabmeta.map(
              (tabmeta, index)=>( 
                   <Tabs
                      onChange={handleChange}
                      indicatorColor="#4f8bff"
                      textColor="primary"
                      variant="scrollable"
                      scrollButtons="auto"
                      aria-label="scrollable auto tabs example"
                      value = {props.selectedTab}
                    >      
                    {/*eslint-disable-next-line*/ }
                  <Tab value={'root'} label={tabmeta.name}  onClick={(e) => checkItem(index)} /> 
                  </Tabs>
               
              )  
          )}
        {tabs.map(
              (tabs, index)=>( 
                  // <ul key = {tabs.id}> 
                   <Tabs
                      onChange={handleChange}
                      indicatorColor="#4f8bff"
                      textColor="primary"
                      variant="scrollable"
                      scrollButtons="auto"
                      aria-label="scrollable auto tabs example"
                      value = {props.selectedTab}
                      selectionFollowsFocus= "true"
                    >      
                    {/*eslint-disable-next-line*/ }
                  <Tab value={index} label={tabs.name}  onClick={(e) => checkItem(index)} /> 
                  </Tabs>
              )  
          )}
       
      </Tabs>
      </AppBar> 
      <TabPanel value={props.selectedTab} index={props.selectedTab}>
        {/* this is where the table is rendered */}
        {renderData()}                            
      </TabPanel>            
      </ul>                         
    </div>
  );
}