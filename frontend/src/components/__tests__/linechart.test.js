import React from "react";
import { render, fireEvent } from "@testing-library/react";
import { MOCK_COMPONENT } from "../mockLineData";
import IDALineChart from "../visualizations/linechart/linechart";
import "@testing-library/jest-dom";

it("it should pass visualisation inputs", () => {
    const mockData = MOCK_COMPONENT.MOCKDATA;
    console.log(mockData);
    const { debug } = render(<IDALineChart data={mockData} />)
    debug()
})  