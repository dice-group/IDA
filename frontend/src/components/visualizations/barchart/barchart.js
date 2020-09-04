import React from 'react';

import BarChart from "./d3bar"

class ChartData extends React.Component {

  state = {
  data: {
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
    },{
    x: "label6",
    y: 700
    }]
  },
  width: 700,
  height: 500,
  id: "root"
  }

  
   render() {
    console.log(this.state);
      return (
        <div className="App">
        <BarChart data={this.state.data} width={this.state.width} height={this.state.height} />
        </div>
      );
    }
  }
  
  export default ChartData;
