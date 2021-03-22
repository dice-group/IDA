import React, { Component } from "react";
import * as d3 from "d3";
import TrendingUpIcon from "@material-ui/icons/TrendingUp";
import TrendingDownIcon from "@material-ui/icons/TrendingDown";
import "./scatterplot.css";
import { IDA_CONSTANTS } from "../../constants";
import { Grid, Fab } from "@material-ui/core";

export default class IDAScatterPLot extends Component {
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
	tooltip = null;

	constructor(props) {
		super(props);
		this.containerId = props.nodeId;
		this.graphData = props.data;
		this.originalGraphData = JSON.parse(JSON.stringify(this.graphData));
		this.state = {
			sortMode: ""
		};
		this.tooltip = document.createElement("div");
		this.tooltip.setAttribute("class", "tooltip");
		document.body.appendChild(this.tooltip);

	}

	componentDidMount() {
		this.graphData && this.graphData.items && this.drawScatterPlot();
	}


	sortGraphItems(sortMode) {
		document.getElementById(this.containerId).innerHTML = "";
		if (sortMode === this.state.sortMode) {
			this.setState({
				sortMode: ""
			});
			this.graphData = JSON.parse(JSON.stringify(this.originalGraphData));
		} else if (sortMode === IDA_CONSTANTS.SORT_MODE_ASC_Y || sortMode === IDA_CONSTANTS.SORT_MODE_DESC_Y) {
			this.sortYaxis(sortMode);
		} else if (sortMode === IDA_CONSTANTS.SORT_MODE_ASC_X || sortMode === IDA_CONSTANTS.SORT_MODE_DESC_X) {
			this.sortXaxis(sortMode);
		}
		this.setState({
			sortMode
		});

		this.drawScatterPlot();
	}

	sortYaxis(sortMode) {
		if (sortMode === IDA_CONSTANTS.SORT_MODE_ASC_Y) {
			this.graphData.items.sort((a, b) => a.y > b.y ? 1 : a.y < b.y ? -1 : 0);
		} else if (sortMode === IDA_CONSTANTS.SORT_MODE_DESC_Y) {
			this.graphData.items.sort((a, b) => a.y > b.y ? -1 : a.y < b.y ? 1 : 0);
		}
	}

	sortXaxis(sortMode) {
		if (sortMode === IDA_CONSTANTS.SORT_MODE_ASC_X) {
			this.graphData = JSON.parse(JSON.stringify(this.originalGraphData));
		} else if (sortMode === IDA_CONSTANTS.SORT_MODE_DESC_X) {
			this.graphData = JSON.parse(JSON.stringify(this.originalGraphData));
			this.graphData.items.reverse();
		}
	}


	drawScatterPlot() {
		this.graphData.items.forEach((item) => {
			item.xLabel = item.x;
			item.x = item.x.length > 16 ? item.x.substring(0, 13) + "..." : item.x;
		});

		// Every plot will be of static width 25px
		this.width = Math.max(this.graphData.items.length * 25, this.width);

		/**
		 * append placeholder for the scatter plot
		 */
		const svg = d3.select("#" + this.containerId)
			.append("svg")
			.attr("height", this.height)
			.attr("width", this.width)
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
		var scaleX = d3.scaleBand()
			.domain(this.graphData.items.map((d) => d.x))
			.range([this.margin.left, this.width])
			.padding(0.1);
		
		var clip = svg.append("defs").append("svg:clipPath")
			.attr("id", "clip")
			.append("svg:rect")
			.attr("width", this.width )
			.attr("height", this.height )
			.attr("x", 100)
			.attr("y", -100);
		/**
		 * append the scatter plot graph to SVG
		 */
		var plot = svg.append("g", '.listener-rect')
			.attr("clip-path", "url(#clip)")
			.selectAll("dot")
			.data(this.graphData.items)
			.enter()
			.append("circle")
			.attr("cx", function (d) { return scaleX(d.x) +11 ; })
			// .attr("cy", function (d) { return scaleY(d.y); } )
			.attr("cy", (d) => scaleY(d.y) )
			.attr("r", 3.0)
			.attr("fill", "#4f8bff");

		plot
			// .append("title")
			.attr("data-foo", (d) => { return d.xLabel + ": " + d.y; })
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
		 * append x-axis to the graph
		 */
		var xAxis = g => g
    		.attr("transform", `translate(0,${this.height - this.margin.bottom})`)
			.call(d3.axisBottom(scaleX).tickSizeOuter(0))
			.selectAll("text")
			.data(this.graphData.items)
			.attr("x", -10)
			.attr("y", -5)
			.attr("transform", "rotate(-90)")
			.style("text-anchor", "end");
		
		/**
		 * append clip for plot to hide outside chart area
		 */
		svg.append("g")
		.attr("class", "x-axis")
		.attr("clip-path", "url(#clip)")
        .call(xAxis);		


		/**
	   * append y-axis to the graph
	   */
		var Yaxisdraw = svg.append("g")
			.attr("transform", `translate(${this.margin.left},0)`)
			.call(d3.axisLeft(scaleY).tickSizeOuter(0));
		
		var zoom = d3.zoom() .on('zoom', (event)=>{zoomed(event,this)});
			listenerRect.call(zoom);
		
		/**
		 * Zoom event listener 
		 */	
		function zoomed(event,object) { 
				var transform = event.transform; 
				
				transform.x = Math.min(-100*(transform.k -1), transform.x); 
                transform.y = Math.min(0, transform.y);; 
                scaleX = scaleX.range([object.margin.left, object.width].map(d => event.transform.applyX(d)));
                var yScaleNew = transform.rescaleY(scaleY);
                plot.attr('cy', function(d) {return yScaleNew(d.y) });
				plot.attr('cx',function (d) { return scaleX(d.x) + transform.k *11; });
				Yaxisdraw.call(d3.axisLeft(yScaleNew).tickSizeOuter(0));
				svg.selectAll(".x-axis").call(xAxis);
        }
	}

	render() {
		return <Grid>
			<Grid item xs={12}>
				<div className="text-center pt-2 pb-2 row align-items-center">
					<span className="text-right col-6">
						Sort the plot:
          </span>
					<div className="col-6 text-left row">
						<div>
							<Fab size="small" className="mr-2" color={this.state.sortMode === IDA_CONSTANTS.SORT_MODE_ASC_Y ? "primary" : "default"} onClick={() => this.sortGraphItems(IDA_CONSTANTS.SORT_MODE_ASC_Y)}>
								<TrendingUpIcon />
							</Fab>
							<Fab size="small" color={this.state.sortMode === IDA_CONSTANTS.SORT_MODE_DESC_Y ? "primary" : "default"} onClick={() => this.sortGraphItems(IDA_CONSTANTS.SORT_MODE_DESC_Y)}>
								<TrendingDownIcon />
							</Fab>
							<div className="mt-2 text-center">Y-Axis</div>
						</div>
						<div className="ml-md-4">
							<Fab size="small" className="mr-2" color={this.state.sortMode === IDA_CONSTANTS.SORT_MODE_ASC_X ? "primary" : "default"} onClick={() => this.sortGraphItems(IDA_CONSTANTS.SORT_MODE_ASC_X)}>
								<TrendingUpIcon />
							</Fab>
							<Fab size="small" color={this.state.sortMode === IDA_CONSTANTS.SORT_MODE_DESC_X ? "primary" : "default"} onClick={() => this.sortGraphItems(IDA_CONSTANTS.SORT_MODE_DESC_X)}>
								<TrendingDownIcon />
							</Fab>
							<div className="mt-2 text-center">X-Axis</div>
						</div>
					</div>
				</div>
			</Grid>
			<Grid item xs={12}>
				<div className="scatterplot-container" id={this.containerId}>
				</div>
			</Grid>
		</Grid>;
	}
}
