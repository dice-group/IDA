import React from 'react';
import PropTypes from 'prop-types';
import { makeStyles } from '@material-ui/core/styles';
import AppBar from '@material-ui/core/AppBar';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
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
          {children}
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
  const data = props.detail.find(ds => ds.id === props.selectedNodeId || ds.children.findIndex(child => child.id === props.selectedNodeId) >= 0) || {};
  const tabs = data.id ? [{
    'label': data.name + ' Metadata',
    'value': data.id,
    'data': data.data,
    'type': 'metadata'
  }] : [];
  (data.children || []).forEach(child => {
    tabs.push({
      'label': child.name,
      'value': child.id,
      'data': child.data,
      'type': 'table',
      'columns': data.data.filter(fl => fl.fileName === child.fileName)[0].fileColMd
    });
  });
  const value = props.selectedNodeId || data.id;
  const handleChange = (event, newValue) => {
    props.setSelectedNodeId(newValue);
  };
  const renderData = (tab) => {
    if (tab.type === 'table') {
      return <CustomizedTables data={tab.data} columns={tab.columns}/>;
    } else {
      // TODO: Modify the Spanning table component to work with new design
      // return <SpanningTable data={tab.data} />
      return <h3>Metada</h3>;
    }
  }

  return (
    <div className={classes.root}>
      <ul>
        <AppBar position="static" color="default"  >
          <Tabs
            onChange={handleChange}
            indicatorColor="primary"
            textColor="primary"
            variant="scrollable"
            scrollButtons="auto"
            aria-label="scrollable auto tabs example"
            value={value}
            selectionFollowsFocus={true}
          >
            {tabs.map(
              (tab, index) => (
                <Tab value={tab.value} label={tab.label} key={index}/>
              ))}
          </Tabs>
        </AppBar>
        {tabs.map(
          (tab, index) => (
            <TabPanel value={value} index={tab.value} key={index}>
              {/* this is where the table is rendered */}
              {renderData(tab)}
            </TabPanel>
          )
        )}
      </ul>
    </div>
  );
}