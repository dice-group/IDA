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
    datalines=[];
    data = [];
    constructor(props) {
      super(props);
      this.containerId = props.nodeId;
      this.graphData.y = props.data.yAxisLabel;
      this.graphData.series = props.data.lines;
      this.graphData.dates = [1-1-2000, 1-2-2000, 1-3-2000, 1-4-2000];
      this.data.push(this.graphData)
      this.graphData={};
    }
    
    componentDidMount() {
        this.graphData && this.data && this.drawGraph();
    }
   drawGraph(){ 
    // let data = this.graphData;
    // console.log("Data");
    // console.log(data);
    // this.datalines= data.lines;
    // console.log("DataLines");
    // console.log(this.datalines);
    // const val=  this.datalines.map((element) =>  element.lineValues);
    // console.log("val");
    console.log(this.data);

        const svg = d3.select("#id11")
            .append("svg")
            .attr("viewBox", [0, 0, this.width, this.height])
            .style("overflow", "visible");

        const x = d3.scaleUtc()
            .domain(d3.extent(this.data.dates ))
            .range([this.margin.left, this.width - this.margin.right])

        const y = d3.scaleLinear()
            .domain([0, d3.max(this.data.series, d => d3.max(d.lineValues))]).nice()
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
            .text(this.data.yAxisLabel));

      
        
        svg.append("g")
            .call(xAxis);

        svg.append("g")
            .call(yAxis);

        const line = d3.line()
            .defined(d => !isNaN(d))
            .x((d, i) => x(this.data.dates[i]))
            .y(d => y(d))
        

        const path = svg.append("g")
            .attr("fill", "none")
            .attr("stroke", "steelblue")
            .attr("stroke-width", 1.5)
            .attr("stroke-linejoin", "round")
            .attr("stroke-linecap", "round")
            .selectAll("path")
            .data(this.data.series)
            .join("path")
            .style("mix-blend-mode", "multiply")
            .attr("d", d => line(d.lineValues));
            
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
        
        }
        const dot = svg.append("g")
                .attr("display", "none");
           
          
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
              const i = d3.bisectCenter(this.data.dates, xm);
              const s = d3.least(this.data.series, d => Math.abs(d.lineValues[i] - ym));
              path.attr("stroke", d => d === s ? null : "#ddd").filter(d => d === s).raise();
              dot.attr("transform", `translate(${x(this.data.dates[i])},${y(s.lineValues[i])})`);
              dot.select("text").text(s.label);            
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
   render() {
    const { classes } = this.props;
        return <div classlabels={classes.linechart} id="id11"></div >;
    }
  }  
  export default  withStyles(styles, { withTheme: true })(IDAMultiLineGraph);
