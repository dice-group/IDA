import React, { Component } from "react";
import * as d3 from "d3";
import Grid from "@material-ui/core/Grid";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Chip from "@material-ui/core/Chip";
import { Hidden } from "@material-ui/core";

import "./scatterplotMatrix.css";
import { IDA_CONSTANTS } from "./../../constants";

export default class IDAScatterPlotMatrix extends Component {
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
	colorFunction = (label) => null;
	tooltip = null;

	constructor(props) {
		super(props);
		this.containerId = props.nodeId;
		this.graphData = props.data;
		this.state = {
			referenceValues: []
		};
		this.tooltip = document.createElement("div");
		this.tooltip.setAttribute("class", "tooltip");
		document.body.appendChild(this.tooltip);
	}

	componentDidMount() {
		if (this.graphData && this.graphData.items) {
			this.graphData.columns.forEach((col) => {
				this.graphData.items = this.graphData.items.filter((entry) => entry[`${col}`] !== IDA_CONSTANTS.UNKNOWN_LABEL);
			});
			const refColumn = this.graphData.referenceColumn;
			this.colorFunction = d3.scaleOrdinal()
				.domain(this.graphData.items.map((d) => d[`${refColumn}`]))
				.range(d3.schemeCategory10);
			this.setState({
				referenceValues: this.colorFunction.domain()
			});
			this.drawScatterPlot();
		}
	}

	drawScatterPlot() {
		const self = this;
		const refColumn = this.graphData.referenceColumn;
		const columns = this.graphData.columns;
		const padding = 50;
		const size =
			(this.width - (columns.length + 1) * padding) / columns.length + padding;
		this.graphData.items = this.graphData.items.map((entry) => {
			let obj = {};
			columns.forEach((c) => {
				obj[`${c}`] = parseFloat(entry[`${c}`]);
			});
			obj[`${refColumn}`] = entry[`${refColumn}`];
			return obj;
		});
		const data = this.graphData.items;

		const x = columns.map((c) =>
			d3
				.scaleLinear()
				.domain(d3.extent(data, (d) => d[`${c}`]))
				.rangeRound([padding / 2, size - padding / 2])
		);

		const y = x.map((x) => x.copy().range([size - padding / 2, padding / 2]));
		const z = this.colorFunction;

		const svg = d3.select("#" + this.containerId)
			.append("svg")
			.attr("viewBox", `${-padding} 0 ${this.width + padding} ${this.width}`)
			.attr("width", this.width)
			.style("height", "auto");

		const xAxis = d3
			.axisBottom()
			.ticks(6)
			.tickSize(size * columns.length);

		svg.append("g").call((g) => {

			let axisG = g.selectAll("g")
				.data(x)
				.join("g")
				.attr("transform", (d, i) => `translate(${(i * size) + padding},0)`)
				.each(function (d) {
					return d3.select(this)
						.call(xAxis.scale(d));
				});
			axisG.call((g) => g.select(".domain").remove());
			axisG.call((g) => g.selectAll(".tick line").attr("stroke", "#efefef"));
			axisG.selectAll("text").attr("transform", "rotate(-90)").attr("y", "-5").attr("x", `${-size * columns.length - 15}`);
		});

		const yAxis = d3
			.axisLeft()
			.ticks(6)
			.tickSize(-size * columns.length);

		svg.append("g").call((g) => {
			g.selectAll("g")
				.data(y)
				.join("g")
				.attr("transform", (d, i) => `translate(${padding}, ${i * size})`)
				.each(function (d) {
					return d3.select(this).call(yAxis.scale(d));
				})
				.call((g) => g.select(".domain").remove())
				.call((g) => g.selectAll(".tick line").attr("stroke", "#efefef"));
		});

		const cell = svg
			.append("g")
			.selectAll("g")
			.data(d3.cross(d3.range(columns.length), d3.range(columns.length)))
			.join("g")
			.attr("transform", ([i, j]) => `translate(${(i * size) + padding},${j * size})`);

		cell
			.append("rect")
			.attr("fill", "none")
			.attr("stroke", "#aaa")
			.attr("x", padding / 2 + 0.5)
			.attr("y", padding / 2 + 0.5)
			.attr("width", size - padding)
			.attr("height", size - padding);

		cell.each(function ([i, j]) {
			d3.select(this)
				.selectAll("circle")
				.data(data.filter((d) => !isNaN(d[`${columns[`${i}`]}`]) && !isNaN(d[`${columns[`${j}`]}`])))
				.join("circle")
				.attr("cx", (d) => x[`${i}`](d[`${columns[`${i}`]}`]))
				.attr("cy", (d) => y[`${j}`](d[`${columns[`${j}`]}`]))
				.attr("data-tooltip", (d) => columns[`${i}`] + ": " + d[`${columns[`${i}`]}`] + "\n" + columns[`${j}`] + ": " + d[`${columns[`${j}`]}`])
				.on("mouseover", (event) => {
					self.tooltip.style.display = "block";
					self.tooltip.style.position = "absolute";
					self.tooltip.style.top = event.clientY + "px";
					self.tooltip.style.left = event.clientX + "px";
					self.tooltip.innerText = event.srcElement.getAttribute("data-tooltip");
				})
				.on("mouseout", () => {
					self.tooltip.style.display = "none";
				});
		});

		const circle = cell
			.selectAll("circle")
			.attr("r", 3.5)
			.attr("fill-opacity", 0.7)
			.attr("fill", (d) => z(d[`${refColumn}`]));

		svg
			.append("g")
			.attr("transform", `translate(${-padding}, 0)`)
			.style("font", "bold 10px sans-serif")
			.selectAll("text")
			.data(columns)
			.join("text")
			.attr("transform", "rotate(-90)")
			.attr("x", (d, i) => `${-size * i - (size / 2)}`)
			.attr("y", 10)
			.attr("dy", ".71em")
			.text((d) => d)
			.style("text-anchor", "middle");

		const xAxisSvg = d3.select("#" + this.containerId)
			.append("svg")
			.attr("viewBox", `${-padding} 0 ${this.width + padding} 100`)
			.attr("width", this.width)
			.style("height", "100");

		xAxisSvg
			.style("font", "bold 10px sans-serif")
			.selectAll("text")
			.data(columns)
			.join("text")
			.attr("transform", (d, i) => `translate(${(i * size)}, 30)`)
			.attr("x", size / 2 + padding)
			.text((d) => d)
			.style("text-anchor", "middle");
	}

	render() {
		return <>
			<Grid container>
				<Hidden mdUp>
					<Grid item xs={12}>
						<div className="m-2">
							{
								this.state.referenceValues.map((label) => (
									<Chip
										key={label}
										size="small"
										avatar={<span className="legend-item-sm-icon mr-1" style={{ backgroundColor: this.colorFunction(label) }} />}
										label={label}
										className="mr-2 mt-2"
									/>
								))
							}
						</div>
					</Grid>
				</Hidden>
				<Grid item md={9} className="tab-container">
					<div className="scatterplot-matrix-container" id={this.containerId}></div>
				</Grid>
				<Hidden mdDown>
					<Grid item md={3}>
						<div>
							<List component="nav" dense={true} aria-label="graph legend" className="grouped-bar-chart-legend">
								{
									this.state.referenceValues.map((label) => (
										<ListItem key={label}>
											<ListItemAvatar>
												<div className="legend-item-icon" style={{ backgroundColor: this.colorFunction(label) }}></div>
											</ListItemAvatar>
											<ListItemText primary={label} />
										</ListItem>
									))
								}
							</List>
						</div>
					</Grid>
				</Hidden>
			</Grid>
		</>;
	}
}
