import React, { Component } from "react";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Typography from "@material-ui/core/Typography";
import Grid from "@material-ui/core/Grid";
import LaunchIcon from "@material-ui/icons/Launch";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import Button from "@material-ui/core/Button";

import "./vizSuggestion.css";

export default class IDAVisualizationSuggestion extends Component {

  suggestionData = {};
  nodeId = "";
  tableName = "";
  vizInfo = [];
  paramData = {};
  props;

  constructor(props) {
    super(props);
    this.props = props;
    this.suggestionData = props.data;
    this.nodeId = props.nodeId;
    this.tableName = props.tableName;
    this.vizInfo = [];
    this.suggestionData.forEach((viz) => {
      let suggestionParams = [];
      let vizParams = [];
      Object.keys(viz.visualizationParamTypeList).forEach((param, typeParamIndex) => {
        const typesList = viz.visualizationParamTypeList[`${param}`];
        if (typeParamIndex === 0) {
          if (typesList.length <= 1) {
            suggestionParams = [viz.suggestionParamList];
            vizParams = [viz.visualizationParams];
          } else {
            const updatedParams = this.initializeParams(viz, typesList, suggestionParams, vizParams, param);
            suggestionParams = updatedParams.suggestionParams;
            vizParams = updatedParams.vizParams;
          }
        } else if (typesList.length > 1) {
          const updatedParams = this.getUpdatedParams(typesList, suggestionParams, vizParams, param);
          suggestionParams = updatedParams.updatedSuggestionParams;
          vizParams = updatedParams.updatedVizParams;
        }
      });
      this.vizInfo.push({
        vizName: viz.vizName,
        visualizationInfo: viz.visualizationInfo,
        suggestionInfo: {
          suggestionParams,
          vizParams
        }
      });
    });
  }

  initializeParams(viz, typesList, suggestionParams, vizParams, param) {
    typesList.forEach((type) => {
      const sParams = JSON.parse(JSON.stringify(viz.suggestionParamList));
      const vParams = JSON.parse(JSON.stringify(viz.visualizationParams));
      const index = sParams.findIndex((paramEntry) => paramEntry.attributeName === param);
      sParams[`${index}`].value += " (" + type + ")";
      vParams[`${param}_type`] = type;
      suggestionParams.push(sParams);
      vizParams.push(vParams);
    });
    return {
      suggestionParams,
      vizParams
    };
  }

  getUpdatedParams(typesList, suggestionParams, vizParams, param) {
    let updatedSuggestionParams = [];
    let updatedVizParams = [];
    typesList.forEach((type) => {
      suggestionParams.forEach((paramList) => {
        const sParams = JSON.parse(JSON.stringify(paramList));
        const index = sParams.findIndex((paramEntry) => paramEntry.attributeName === param);
        sParams[index].value += " (" + type + ")";
        updatedSuggestionParams.push(sParams);
      });
      vizParams.forEach((vParamList) => {
        const vParams = JSON.parse(JSON.stringify(vParamList));
        vParams[`${param}_type`] = type;
        updatedVizParams.push(vParams);
      });
    });
    return {
      updatedSuggestionParams,
      updatedVizParams
    };
  }

  drawVisualization(vizParams) {
    if (!this.props.isChatbotOpen) {
      this.props.setIsChatbotOpen(true);
    }
    const elem = document.getElementById("chat-input");
    elem.value = "Draw " + vizParams.intentname;
    document.getElementById("send-btn").setAttribute("data-params", JSON.stringify(vizParams));
    document.getElementById("send-btn").click();
  }

  render() {
    return <>
      <Typography varient="h5" className="mb-2 p-3">
        Suggested parameters for available visualizations for the <b>{this.tableName}</b> table are listed below.
        You can render these visualizations in new tabs by clicking the 'Draw' button.
      </Typography>
      <div className="m-2">
        {
          this.vizInfo.map(
            (viz, i) => (
              <Accordion key={i} defaultExpanded={i === 0}>
                <AccordionSummary expandIcon={<ExpandMoreIcon />} id={this.nodeId + "-" + viz.vizName}>
                  <Typography varient="h5"><b>{viz.vizName}</b></Typography>
                </AccordionSummary>
                <AccordionDetails>
                  <Grid container spacing={3}>
                    <Grid item xs={12}>
                      <Typography varient="h6">
                        {viz.visualizationInfo.description}
                      For more information you can go to <a target="_blank" href={viz.visualizationInfo.link}>{viz.visualizationInfo.linkLabel}<LaunchIcon fontSize="small" /></a>
                      </Typography>
                    </Grid>
                    {
                      <Grid container item xs={12}>
                        <TableContainer component={Paper}>
                          <Table aria-label="suggestion table">
                            <TableHead>
                              <TableRow>
                                {
                                  viz.suggestionInfo.suggestionParams[0].map(
                                    (entry, j) => (
                                      <TableCell align="center" key={j}>
                                        <b>{entry.param}</b>
                                      </TableCell>
                                    )
                                  )
                                }
                                <TableCell align="center"></TableCell>
                              </TableRow>
                            </TableHead>
                            <TableBody>
                              {
                                viz.suggestionInfo.suggestionParams.map(
                                  (suggestionParamList, rowIndex) => ((
                                    <TableRow key={rowIndex}>
                                      {
                                        suggestionParamList.map(
                                          (row, j) => (
                                            <TableCell align="center" key={j}>{row.value}</TableCell>
                                          )
                                        )
                                      }
                                      <TableCell align="center">
                                        <Button variant="contained" color="primary" onClick={() => this.drawVisualization(viz.suggestionInfo.vizParams[`${rowIndex}`])}>Draw</Button>
                                      </TableCell>
                                    </TableRow>
                                  ))
                                )
                              }
                            </TableBody>
                          </Table>
                        </TableContainer>
                      </Grid>
                    }
                  </Grid>
                </AccordionDetails>
              </Accordion>
            )
          )
        }
      </div>
    </>;
  }
}