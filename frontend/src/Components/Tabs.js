import React from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Typography from '@material-ui/core/Typography';
import Box from '@material-ui/core/Box';
// import CustomizedTables from './Datatable';

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

export default function ScrollableTabsButtonAuto(props) {
  const classes = useStyles();
  
  const handleChange = (event, newValue) => {
    console.log("xkmmcm,m,");
    props.setSelectedTab(newValue); 
  };

  let tabs = [];
  console.log(props);
  if(props.item !== undefined) {
    tabs = props.item.children;
    console.log("tab",props.item.children)
  }
  
  const checkItem = (index) => {
    // array iteration
    props.setSelectTree(index)

  }

  const renderData =()=>{
   
      // return <CustomizedTables item = {props.item} />
    
  }
  return (
    <div className={classes.root}>
      <ul>     
      <AppBar position="static" color="default" >
        <Tabs
        onChange={handleChange}
        indicatorColor="#4f8bff"
        textColor="primary"
        variant="scrollable"
        scrollButtons="auto"
        aria-label="scrollable auto tabs example"
        value = {props.selectedTab}
      >   
        {tabs.map(
              (tabs, index)=>(  
                  <ul key = {tabs.id}> 
                  <Tab value={index} label={tabs.name}  onClick={(e) => checkItem(index)} /> 
                  </ul> 
              )  
          )}
      </Tabs>
    </AppBar> 
          {/* </AppBar> 
          */}
      <TabPanel value={props.selectedTab} index={props.selectedTab}>
        {/* this is where the table is rendered */}
        {renderData()}                            
      </TabPanel>            
      </ul>                         
    </div>
  );
}