import React, { Component } from 'react';
import * as d3 from 'd3';

export default class IDABarGraph extends Component {
  margin = {
    top: 20,
    right: 0,
    bottom: 50,
    left: 60
  };
  height = 500;
  width = 700;
  graphData = {
    xAxisLabel: "X axis label",
    yAxisLabel: "Y axis label",
    items: [{
      x: "label1",
      y: 453
    }, {
      x: "label2",
      y: 693
    }, {
      x: "label3",
      y: 264
    }, {
      x: "label4",
      y: 852
    }, {
      x: "label5",
      y: 726
    }, {
      x: "label6",
      y: 700
    }]
  };
  containerId = "";

  constructor(props) {
    super(props);
    this.containerId = props.nodeId;
    // this.graphData = props.data;
  }

  componentDidMount() {
    this.graphData && this.graphData.items && this.drawBarGraph();
  }

  drawBarGraph() {
    const svg = d3.select("#" + this.props.nodeId).append("svg").attr("height", this.height).attr("width", this.width);

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

    /**
     * 
     * @param {*} g
     * function to draw the x-axis line
     */
    const xAxis = g => g
      .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
      .call(d3.axisBottom(scaleX).tickSizeOuter(0));

    /**
   * 
   * @param {*} g
   * function to draw the y-axis line 
   */
    const yAxis = g => g
      .attr("transform", `translate(${this.margin.left},0)`)
      .call(d3.axisLeft(scaleY).tickSizeOuter(0));


    /**
     * append the bar graph to SVG
     */
    svg.append("g")
      .attr("margin-bottom", this.margin.bottom)
      .selectAll("rect")
      .data(this.graphData.items)
      .enter()
      .append("rect")
      .attr("x", (d) => scaleX(d.x))
      .attr("y", (d) => scaleY(d.y))
      .attr("width", scaleX.bandwidth())
      .attr("height", (d, i) => scaleY(0) - scaleY(d.y))
      .attr("fill", "#4f8bff");

    /**
     * append x-axis to the graph
     */
    svg.append("g")
      .call(xAxis);

    /**
   * append y-axis to the graph
   */
    svg.append("g")
      .call(yAxis);

    /**
   * append x-axis label to the graph
   */
    svg.append("text")
      .attr("class", "x label")
      .attr("text-anchor", "middle")
      .attr("x", this.width / 2)
      .attr("y", this.height - 5)
      .text(this.graphData.xAxisLabel);

    /**
   * append y-axis label to the graph
   */
    svg.append("text")
      .attr("class", "y label")
      .attr("text-anchor", "end")
      .attr("y", 4)
      .attr("x", this.width / -4)
      .attr("dy", ".75em")
      .attr("transform", "rotate(-90)")
      .text(this.graphData.yAxisLabel);
  }

  render() {
    return <div className="bargraph-container" id={this.containerId}></div>;
  }
}
