import React from "react";
// import Home from "./components/home";
import BarChart from "./components/visualizations/barchart/suggestedd3"
// import ChartData from "./components/visualizations/barchart/barchart"
export default function App(){
  return(
    <div  style={{ width: "100%", }}>
         <div>
          {/* <Home/>  */}
           {/* <ChartData/> */}
           <BarChart/>
        </div>
    </div>
  );
}
