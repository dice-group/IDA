import React from "react";
import PropTypes from "prop-types";
import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";
import Box from "@material-ui/core/Box";
import CustomizedTables from "../datatable/datatable";
import SpanningTable from "../datatable/spanDataTable";
import IDABarChart from "./../visualizations/barchart/barchart";
import "./tabs.css";

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

export default function ScrollableTabsButtonAuto(props) {
  const data = props.detail.find((ds) => ds.id === props.selectedNodeId || ds.children.findIndex((child) => child.id === props.selectedNodeId) >= 0) || {};
  const tabs = data.id ? [{
    "label": data.name + " Metadata",
    "value": data.id,
    "data": data.data,
    "type": "metadata"
  }] : [];
  (data.children || []).forEach((child) => {
    tabs.push({
      "label": child.name,
      "value": child.id,
      "data": child.data,
      "type": child.type,
      "columns": child.type === "table" ? data.data.filter((fl) => fl.fileName === child.fileName)[0].fileColMd : null
    });
  });
  const value = props.selectedNodeId || data.id;
  const handleChange = (event, newValue) => {
    props.setSelectedNodeId(newValue);
    const selectedTab = tabs.find((tab) => tab.value === newValue && tab.type === "table") || {};
    props.setActiveTable(selectedTab.label || "");
  };
  const renderData = (tab) => {
    switch (tab.type) {
      case "table":
        return <CustomizedTables data={tab.data} columns={tab.columns} />;
      case "metadata":
        return <SpanningTable data={tab.data} />;
      case "barchart":
        return <IDABarChart />;
      default:
        return null;
    }
  };

  return (
    <div className={"root"}>
      <ul className={"tab-list"}>
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
                <Tab value={tab.value} label={tab.label} key={index} />
              ))}
          </Tabs>
        </AppBar>
        {tabs.map(
          (tab, index) => (
            <TabPanel value={value} index={tab.value} key={index}>
              {renderData(tab)}
            </TabPanel>
          )
        )}
      </ul>
    </div>
  );
}