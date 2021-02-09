import React, { Component } from "react";
import * as d3 from "d3";
import "./scatterplot.css";
import { Grid } from "@material-ui/core";

export default class IDAScatterPlotMatrix extends Component {
  margin = {
    top: 20,
    right: 0,
    bottom: 100,
    left: 100
  };
  height = 700;
  graphData = {};
  containerId = "";

  constructor(props) {
    super(props);
    this.containerId = props.nodeId;
    this.graphData = props.data;
  }

  componentDidMount() {
    this.graphData && this.graphData.items && this.drawScatterPlot();
  }

	drawScatterPlot() {
		const width = 954;
		const columns = this.graphData.columns;
		const ref_column = this.graphData.referenceColumn;
		const padding = 20;
		const size =
			(width - (columns.length + 1) * padding) / columns.length + padding;
		const data = this.graphData.items;

		const x = columns.map(c =>
			d3
				.scaleLinear()
				.domain(d3.extent(data, d => d[c]))
				.rangeRound([padding / 2, size - padding / 2])
		);

		const y = x.map(x => x.copy().range([size - padding / 2, padding / 2]));
		const z = d3
			.scaleOrdinal()
			.domain(data.map(d => d[ref_column]))
			.range(d3.schemeCategory10);

		const svg = d3
			.select("#sdsd")
			.attr("viewBox", `${-padding} 0 ${width} ${width}`)
			.style("max-width", "100%")
			.style("height", "auto");


		// Labels for x axis

		const xAxis = d3
			.axisBottom()
			.ticks(6)
			.tickSize(size * columns.length);

		svg.append("g").call(g => {
			g.selectAll("g")
				.data(x)
				.join("g")
				.attr("transform", (d, i) => `translate(${i * size},0)`)
				.each(function(d) {
					return d3.select(this).call(xAxis.scale(d));
				})
				.call(g => g.select(".domain").remove())
				.call(g => g.selectAll(".tick line").attr("stroke", "#fff"));
		});

		// lables foe y-axis

		const yAxis = d3
			.axisLeft()
			.ticks(6)
			.tickSize(-size * columns.length);

		svg.append("g").call(g => {
			g.selectAll("g")
				.data(y)
				.join("g")
				.attr("transform", (d, i) => `translate(0,${i * size})`)
				.each(function(d) {
					return d3.select(this).call(yAxis.scale(d));
				})
				.call(g => g.select(".domain").remove())
				.call(g => g.selectAll(".tick line").attr("stroke", "#fff"));
		});

		const cell = svg
			.append("g")
			.selectAll("g")
			.data(d3.cross(d3.range(columns.length), d3.range(columns.length)))
			.join("g")
			.attr("transform", ([i, j]) => `translate(${i * size},${j * size})`);

		cell
			.append("rect")
			.attr("fill", "none")
			.attr("stroke", "#aaa")
			.attr("x", padding / 2 + 0.5)
			.attr("y", padding / 2 + 0.5)
			.attr("width", size - padding)
			.attr("height", size - padding);

		cell.each(function([i, j]) {
			d3.select(this)
				.selectAll("circle")
				.data(data.filter(d => !isNaN(d[columns[i]]) && !isNaN(d[columns[j]])))
				.join("circle")
				.attr("cx", d => x[i](d[columns[i]]))
				.attr("cy", d => y[j](d[columns[j]]));
		});

		const circle = cell
			.selectAll("circle")
			.attr("r", 3.5)
			.attr("fill-opacity", 0.7)
			.attr("fill", d => z(d.species));

		svg
			.append("g")
			.style("font", "bold 10px sans-serif")
			.selectAll("text")
			.data(columns)
			.join("text")
			.attr("transform", (d, i) => `translate(${i * size},${i * size})`)
			.attr("x", padding)
			.attr("y", padding)
			.attr("dy", ".71em")
			.text(d => d);
	}

  render() {
    return <Grid>
      <Grid item xs={12}>
        <svg className="scatterplot-container" id={this.containerId}>
        </svg>
      </Grid>
    </Grid>;
  }
}
