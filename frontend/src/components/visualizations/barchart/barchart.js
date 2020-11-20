import React, { Component } from "react";
import * as d3 from "d3";
import TrendingUpIcon from '@material-ui/icons/TrendingUp';
import TrendingDownIcon from '@material-ui/icons/TrendingDown';

import "./barchart.css";
import { IDA_CONSTANTS } from "../../constants";
import { Grid, Fab } from "@material-ui/core";

export default class IDABarGraph extends Component {
  margin = {
    top: 20,
    right: 0,
    bottom: 100,
    left: 100
  };
  height = 700;
  width = 1000;
  graphData = {};
  containerId = "";
  originalGraphData = {};

  constructor(props) {
    super(props);
    this.containerId = props.nodeId;
    this.graphData = props.data;
    this.originalGraphData = JSON.parse(JSON.stringify(this.graphData));
    this.state = {
      sortMode: ""
    };
  }

  componentDidMount() {
    this.graphData && this.graphData.items && this.drawBarGraph();
  }

  sortGraphItems(sortMode) {
    document.getElementById(this.containerId).innerHTML = "";
    if (sortMode === this.state.sortMode) {
      this.setState({
        sortMode: ""
      });
      this.graphData = this.originalGraphData;
    } else {
      if (sortMode === IDA_CONSTANTS.SORT_MODE_ASC) {
        this.graphData.items.sort((a, b) => a.y > b.y ? 1 : a.y < b.y ? -1 : 0);
      } else if (sortMode === IDA_CONSTANTS.SORT_MODE_DESC) {
        this.graphData.items.sort((a, b) => a.y > b.y ? -1 : a.y < b.y ? 1 : 0);
      }
      this.setState({
        sortMode: sortMode
      });
    }
    this.drawBarGraph();
  }

  drawBarGraph() {

    this.graphData.items.forEach(item => {
      item.xLabel = item.x;
      item.x = item.x.length > 16 ? item.x.substring(0, 13) + "..." : item.x;
    });

    // Every bar will be of static width 25px
    this.width = this.graphData.items.length * 25;

    /**
     * append placeholder for the barchart
     */
    const svg = d3.select("#" + this.containerId)
      .append("svg")
      .attr("height", this.height)
      .attr("width", this.width);


    /**
    * append y-axis label
    */
    svg.append("text")
      .attr("transform", "rotate(-90)")
      .attr("x", 0 - (this.height / 2))
      .attr("y", 10)
      .attr("dy", "1em")
      .style("text-anchor", "middle")
      .text(this.graphData.yAxisLabel);

    /**
     * append x-axis label
     */
    d3.select("#" + this.containerId).append("svg")
      .attr("height", 30)
      .attr("width", this.width)
      .append("text")
      .text(this.graphData.xAxisLabel)
      .attr("text-anchor", "middle")
      .attr("x", 515)
      .attr("y", 25);

    /**
     * function to scale the y axis entries
     */
    const scaleY = d3.scaleLinear()
      .domain([0, d3.max(this.graphData.items, d => d.y)]).nice()
      .range([this.height - this.margin.bottom, this.margin.top]);

    /**
     * function to scale x axis entries
     */
    const scaleX = d3.scaleBand()
      .domain(this.graphData.items.map(d => d.x))
      .range([this.margin.left, this.width])
      .padding(0.1);

    const div = d3.select("body").append("div")
      .attr("class", "tooltip")
      .style("opacity", 0);

    /**
     * append the bar graph to SVG
     */
    svg.append("g")
      .selectAll("rect")
      .data(this.graphData.items)
      .enter()
      .append("rect")
      .attr("x", (d) => scaleX(d.x))
      .attr("y", (d) => scaleY(d.y))
      .attr("value", (d) => d.xLabel + ": " + d.y)
      .attr("width", scaleX.bandwidth())
      .attr("height", (d, i) => scaleY(0) - scaleY(d.y))
      .attr("fill", "#4f8bff")
      .attr("class", "tooltip")
      .on("mouseover", function (d) {
        div.transition()
          .duration(200)
          .style("opacity", .9);
        div.html(d.currentTarget.getAttribute("value"))
          .style("left", (d.pageX) + "px")
          .style("top", (d.pageY - 28) + "px");
      })
      .on("mouseout", function (d) {
        div.transition()
          .duration(500)
          .style("opacity", 0);
      });

    /**
     * append x-axis to the graph
     */
    svg.append("g")
      .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
      .call(d3.axisBottom(scaleX).tickSizeOuter(0))
      .selectAll("text")
      .attr("x", -10)
      .attr("y", -5)
      .attr("transform", "rotate(-90)")
      .style("text-anchor", "end")
      .style("fill", (d) => d === IDA_CONSTANTS.UNKNOWN_LABEL ? "#F00" : "#000")
      .style("font-size", (d) => d === IDA_CONSTANTS.UNKNOWN_LABEL ? "14px" : "11px")
      .attr("class", "x-axis-label");

    /**
   * append y-axis to the graph
   */
    svg.append("g")
      .attr("transform", `translate(${this.margin.left},0)`)
      .call(d3.axisLeft(scaleY).tickSizeOuter(0));
  }

  render() {
    return <Grid>
      <Grid item xs={12}>
        <div className="text-center pt-2 pb-2">
          <span className="mr-3">
            Sort the bars:
          </span>
          <Fab size="small" className="mr-2" color={this.state.sortMode === IDA_CONSTANTS.SORT_MODE_ASC ? "primary" : "default"} onClick={() => this.sortGraphItems(IDA_CONSTANTS.SORT_MODE_ASC)}>
            <TrendingUpIcon />
          </Fab>
          <Fab size="small" color={this.state.sortMode === IDA_CONSTANTS.SORT_MODE_DESC ? "primary" : "default"} onClick={() => this.sortGraphItems(IDA_CONSTANTS.SORT_MODE_DESC)}>
            <TrendingDownIcon />
          </Fab>
        </div>
      </Grid>
      <Grid item xs={12}>
        <div className="bargraph-container" id={this.containerId}>
        </div>
      </Grid>
    </Grid>;
  }
}