import React,{Component} from 'react';
import { withStyles } from "@material-ui/core/styles";
import * as d3 from "d3";


const styles = theme => ({
    linechart: {
        border: "1px solid black", 
        overflow:"hidden",
        width:"50%"
    },
    });
class IDAMultiLineGraph extends Component { 
    height = 270;
    width = 600;
    margin = ({top: 5, right: 5, bottom: 15, left: 15})
    graphData = {};
    containerId = "";
    val =[];
    datalines=[];
    constructor(props) {
      super(props);
      this.containerId = props.nodeId;
      this.graphData = props.data;
      this.datalines = this.graphData.lines;
      this.val= this.datalines.values;
    }
    
    componentDidMount() {
        this.graphData && this.datalines && this.drawGraph();
    }
   drawGraph(){ 
    console.log(this.val)
    const data = this.graphData;
    console.log("data",data)
    if (data && data.length) {
        const svg = d3.select(this.containerId)
            .append("svg")
            .attr("viewBox", [0, 0, this.width, this.height])
            .style("overflow", "visible");

        const x = d3.scaleUtc()
            .domain(d3.extent(data.xAxisLabels))
            .range([this.margin.left, this.width - this.margin.right])

        const y = d3.scaleLinear()
            .domain([0, d3.max(this.datalines, d => d3.max(d.this.val))]).nice()
            .range([this.height - this.margin.bottom, this.margin.top])

        var xAxis = g => g
            .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
            .call(d3.axisBottom(x).ticks(this.width / 80).tickSizeOuter(0))

        var yAxis = g => g
            .attr("transform", `translate(${this.margin.left},0)`)
            .call(d3.axisLeft(y))
            .call(g => g.select(".domain").remove())
            .call(g => g.select(".tick:last-of-type text").clone()
            .attr("x", 3)
            .attr("text-anchor", "start")
            .attr("font-weight", "bold")
            .text(data.y));

        console.log(yAxis);

        svg.append("g")
            .call(xAxis);

        svg.append("g")
            .call(yAxis);

        const line = d3.line()
            .defined(d => !isNaN(d))
            .x((d, i) => x(data.xAxisLabels[i]))
            .y(d => y(d))
        console.log(line)

        const path = svg.append("g")
            .attr("fill", "none")
            .attr("stroke", "steelblue")
            .attr("stroke-width", 1.5)
            .attr("stroke-linejoin", "round")
            .attr("stroke-linecap", "round")
            .selectAll("path")
            .data(this.datalines)
            .join("path")
            .style("mix-blend-mode", "multiply")
            .attr("d", d => line(d.this.val));
            
            /////

        svg.call(hover, path);

        function hover(svg, path) {
  
            if ("ontouchstart" in document) svg
                .style("-webkit-tap-highlight-color", "transparent")
                .on("touchmove", moved)
                .on("touchstart", entered)
                .on("touchend", left)
            else svg
                .on("mousemove", moved)
                .on("mouseenter", entered)
                .on("mouseleave", left);
            console.log(svg)
        }
        const dot = svg.append("g")
                .attr("display", "none");
            console.log(dot)
          
        dot.append("circle")
                .attr("r", 2.5);
          
        dot.append("text")
                .attr("font-family", "sans-serif")
                .attr("font-size", 10)
                .attr("text-anchor", "middle")
                .attr("y", -8);
          
        function moved(event) {
              event.preventDefault();
              const pointer = d3.pointer(event, this);
              const xm = x.invert(pointer[0]);
              const ym = y.invert(pointer[1]);
              const i = d3.bisectCenter(data.xAxisLabels, xm);
              const s = d3.least(this.datalines, d => Math.abs(d.this.val[i] - ym));
              path.attr("stroke", d => d === s ? null : "#ddd").filter(d => d === s).raise();
              dot.attr("transform", `translate(${x(data.xAxisLabels[i])},${y(s.this.val[i])})`);
              dot.select("text").text(s.labels);            
            }

        function entered() {
              path.style("mix-blend-mode", null).attr("stroke", "#ddd");
              dot.attr("display", null);
            }
          
        function left() {
              path.style("mix-blend-mode", "multiply").attr("stroke", null);
              dot.attr("display", "none");
            }
    }
    }
   render() {
    const { classes } = this.props;
        return <div classlabels={classes.linechart} id={this.containerId}></div >;
    }
  }  
  export default  withStyles(styles, { withTheme: true })(IDAMultiLineGraph);
