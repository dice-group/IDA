import React,{Component} from 'react';
import { withStyles } from "@material-ui/core/styles";
import * as d3 from "d3";

// let data ={  
//             dates: [1-10-2020, 2-10-2020, 3-10-2020, 4-10-2020, 5-10-2020],     
//             series: [{
//                 "name": "City1",
//                 "val": [100, 125, 130, 90, 103]
//             },{
//                 "name": "City2",
//                 "val": [110, 95, 97, 91 ,100]
//             },{
//                 "name": "City3",
//                 "val": [75, 95, 83, 92, 80]
//             }],
//             y:"name"
//         }
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
      this.graphData.dates = this.graphData.xAxisLabels;
      this.data.push(this.graphData)
      this.graphData={};
    }
    
    componentDidMount() {
        this.graphData && this.data && this.drawGraph();
    }
   drawGraph(){ 
    console.log("lets naach")
    console.log(this.data)
    console.log(this.graphData)

    if (this.data && this.data.length) {
        console.log("haseeb bhai dance")
        const svg = d3.select("#linechart")
            .append("svg")
            .attr("viewBox", [0, 0, this.width, this.height])
            .style("overflow", "visible");
        
        // const formatDate = d3.time.format("%B-%Y") 
        const val=  this.data.map((element) =>  element.xAxisLabels);
        console.log("val",val);
        const x = d3.scaleUtc()
            .domain(d3.extent(this.data[0].dates))
            .range([this.margin.left, this.width - this.margin.right])
        console.log(x);

        const y = d3.scaleLinear()
            .domain([0, d3.max(this.data[0].series, d => d3.max(d.lineValues))]).nice()
            .range([this.height - this.margin.bottom, this.margin.top])
        console.log(y);

        var xAxis = g => g
            .attr("transform", `translate(0,${this.height - this.margin.bottom})`)
            .call(d3.axisBottom(x).ticks(this.width / 80).tickSizeOuter(0))
        console.log(xAxis);

        var yAxis = g => g
            .attr("transform", `translate(${this.margin.left},0)`)
            .call(d3.axisLeft(y))
            .call(g => g.select(".domain").remove())
            .call(g => g.select(".tick:last-of-type text").clone()
            .attr("x", 3)
            .attr("text-anchor", "start")
            .attr("font-weight", "bold")
            .text(this.data.y));

        console.log(yAxis);

        svg.append("g")
            .call(xAxis);

        svg.append("g")
            .call(yAxis);

        const line = d3.line()
            .defined(d => !isNaN(d))
            .x((d, i) => x(this.data.dates[i]))
            .y(d => y(d))
        console.log(line)

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
            .attr("d", d => line(d.val));

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
              const i = d3.bisectCenter(this.data.dates, xm);
              const s = d3.least(this.data.series, d => Math.abs(d.val[i] - ym));
              path.attr("stroke", d => d === s ? null : "#ddd").filter(d => d === s).raise();
              dot.attr("transform", `translate(${x(this.data.dates[i])},${y(s.val[i])})`);
              dot.select("text").text(s.name);            
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
        return <div className={classes.linechart} id="linechart"></div >;
    }
  }  
  export default  withStyles(styles, { withTheme: true })(IDAMultiLineGraph);