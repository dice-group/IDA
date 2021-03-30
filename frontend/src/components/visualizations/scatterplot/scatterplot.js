import React, { Component } from "react";
import * as d3 from "d3";
import TrendingUpIcon from "@material-ui/icons/TrendingUp";
import TrendingDownIcon from "@material-ui/icons/TrendingDown";
import "./scatterplot.css";
import { IDA_CONSTANTS } from "../../constants";
import { Grid, Fab } from "@material-ui/core";
import { data } from "./data.js";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import Chip from "@material-ui/core/Chip";
import { Hidden } from "@material-ui/core";

export default class IDAScatterPLot extends Component {
	margin = {
		top: 20,
		right: 0,
		bottom: 100,
		left: 100
	};
	height = 600;
	width = 900;
	graphData = {};
	colorFunction = (label) => null;
	containerId = "";
	originalGraphData = {};
	tooltip = null;

	constructor(props) {
		super(props);
		this.containerId = props.nodeId;
		this.graphData = props.data;
		this.originalGraphData = JSON.parse(JSON.stringify(this.graphData));
		this.state = {

			sortMode: "",
			referenceValues: []
		};
		this.tooltip = document.createElement("div");
		this.tooltip.setAttribute("class", "tooltip");
		document.body.appendChild(this.tooltip);
	}

	componentDidMount() {

		if (this.graphData && this.graphData.items) {
			this.colorFunction = d3.scaleOrdinal()
				.domain(this.graphData.items.map((d) => d[`reference`]))
				.range(d3.schemeCategory10);
			this.setState({
				referenceValues: this.colorFunction.domain()
			});
			this.graphData && this.graphData.items && this.drawScatterPlot();
		}
	}
	drawScatterPlot() {
		this.graphData.items.forEach((item) => {
			item.xLabel = item.x;
			item.x = item.x.length > 16 ? item.x.substring(0, 13) + "..." : item.x;
		});

		

		/**
		 * append placeholder for the scatter plot
		 */
		const svg = d3.select("#" + this.containerId)
			.append("svg")
			.attr('class', 'chartcontainer')
			.attr('width', this.width)
			.attr("height", this.height)
			.append('g')
			.attr('class', 'chart')
			.attr('transform', 'translate(' + 0 + ', ' + this.margin.top + ')');

		/**
		 * append listener for zoom event
		 */
		var listenerRect = svg.append('rect')
			.attr('class', 'listener-rect')
			.attr('x', 0)
			.attr('y', 0)
			.attr('width', this.width)
			.attr('height', this.height)
			.style('opacity', 0);

		const z = this.colorFunction;

		/**
		* append y-axis label
		*/
		svg.append("text")
			.attr("transform", "rotate(-90)")
			.attr("x", 0 - (this.height / 2))
			.attr("y", 11)
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
			.domain([0, d3.max(this.graphData.items, (d) => d.y)]).nice()
			.range([this.height - this.margin.bottom, this.margin.top]);

		/**
		 * function to scale x axis entries
		 */
		const scaleX = d3.scaleLinear()
			.domain([0, d3.max(this.graphData.items, (d) => d.x)]).nice()
			.range([this.margin.left, this.width])


		var clip = svg.append("defs").append("svg:clipPath")
			.attr("id", "clip")
			.append("svg:rect")
			.attr("width", this.width)
			.attr("height", this.height - (this.margin.top + this.margin.bottom))
			.attr("x", 100)
			.attr("y", this.margin.top);
		/**
		 * append the scatter plot graph to SVG
		 */
		var plot = svg.append("g", '.listener-rect')
			.attr("clip-path", "url(#clip)")
			.selectAll("dot")
			.data(this.graphData.items)
			.enter()
			.append("circle")
			.attr("cx", function (d) { return scaleX(d.x); })
			// .attr("cy", function (d) { return scaleY(d.y); } )
			.attr("cy", (d) => scaleY(d.y))
			.attr("r", 3.0)
			.attr("fill", (d) => z(d[`reference`]));

		plot
			// .append("title")
			.attr("data-foo", (d) => { return d.reference + ":" + d.xLabel + "," + d.y; })
			.on("mouseover", (event) => {
				this.tooltip.style.display = "block";
				this.tooltip.style.position = "absolute";
				this.tooltip.style.top = event.clientY + "px";
				this.tooltip.style.left = event.clientX + "px";
				this.tooltip.innerText = event.srcElement.getAttribute("data-foo");
			})
			.on("mouseout", () => {
				this.tooltip.style.display = "none";
			});

		/**
	   * append y-axis to the graph
	   */
		var Xaxisdraw = svg.append("g")
			.attr("transform", `translate(0,${this.height - this.margin.bottom})`)
			.call(d3.axisBottom(scaleX).tickSizeOuter(0))

		/**
	   * append y-axis to the graph
	   */
		var Yaxisdraw = svg.append("g")
			.attr("transform", `translate(${this.margin.left},0)`)
			.call(d3.axisLeft(scaleY).tickSizeOuter(0));

		var zoom = d3.zoom().on('zoom', (event) => { zoomed(event, this) });
		listenerRect.call(zoom);

		/**
		 * Zoom event listener 
		 */
		function zoomed(event, object) {
			var transform = event.transform;
			transform.x = Math.min(-90 * (transform.k - 1), transform.x);
			transform.y = Math.min(0, transform.y);;
			var yScaleNew = transform.rescaleY(scaleY);
			var xScaleNew = transform.rescaleX(scaleX);
			plot.attr('cy', function (d) { return yScaleNew(d.y) });
			plot.attr('cx', function (d) { return xScaleNew(d.x) });
			Yaxisdraw.call(d3.axisLeft(yScaleNew).tickSizeOuter(0));
			Xaxisdraw.call((d3.axisBottom(xScaleNew).tickSizeOuter(0))).selectAll("text")
				.attr("x", -10)
				.attr("y", -5)
				.attr("transform", "rotate(-90)")
				.style("text-anchor", "end");
		}
	}

	render() {
		return <Grid container>
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
			<Grid item xs={12} md={9}>
				<div className="scatterplot-container" id={this.containerId}>
				</div>
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

		</Grid>;
	}
}
