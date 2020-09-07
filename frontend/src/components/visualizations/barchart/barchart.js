import React, { Component } from 'react';
import * as d3 from 'd3';

import "./barchart.css";

export default class IDABarGraph extends Component {
  margin = {
    top: 20,
    right: 0,
    bottom: 50,
    left: 60
  };
  height = 700;
  width = 1000;
  graphData = {};
  containerId = "";

  constructor(props) {
    super(props);
    this.containerId = props.nodeId;
    this.graphData = props.data;
  }

  componentDidMount() {
    this.graphData && this.graphData.items && this.drawBarGraph();
  }

  drawBarGraph() {
    /**
     * append y-axis label
     */
    d3.select("#" + this.containerId).append("svg")
      .attr("height", this.height)
      .attr("width", 30)
      .append("text")
      .attr("class", "y label")
      .attr("text-anchor", "end")
      .attr("y", 4)
      .attr("x", this.width / -4)
      .attr("dy", ".75em")
      .attr("transform", "rotate(-90)")
      .text(this.graphData.yAxisLabel);

    /**
     * append placeholder for the barchart
     */
    const svg = d3.select("#" + this.containerId)
      .append("svg")
      .attr("height", this.height)
      .attr("width", this.width);

    /**
     * append x-axis label
     */
    d3.select("#" + this.containerId).append("svg")
      .attr("height", 30)
      .attr("width", this.width)
      .append("text")
      .text(this.graphData.xAxisLabel)
      .attr("text-anchor", "middle")
      .attr("x", this.width / 2)
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
      .range([this.margin.left, this.width - this.margin.right])
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
      .attr("value", (d) => d.y)
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
      .attr("y", 0)
      .attr("x", 9)
      .attr("dy", ".35em")
      .attr("transform", "rotate(90)")
      .style("text-anchor", "start");

    /**
   * append y-axis to the graph
   */
    svg.append("g")
      .attr("transform", `translate(${this.margin.left},0)`)
      .call(d3.axisLeft(scaleY).tickSizeOuter(0));
  }

  render() {
    return <div className="bargraph-container" id={this.containerId
    } ></div >;
  }
}
