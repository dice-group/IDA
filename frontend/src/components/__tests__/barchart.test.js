import React from 'react'
import { render, fireEvent } from '@testing-library/react'
import IDABarGraph from "../visualizations/barchart/barchart"
import { MOCK_COMPONENT } from '../mockData'
import IDABubbleChart from "../visualizations/bubblechart/bubblechart"
import IDALineChart from "../visualizations/linechart/linechart"
import user from "@testing-library/user-event"
import '@testing-library/jest-dom'

it('it should pass visualisation inputs', () => {
    const mockData = MOCK_COMPONENT.MOCKDATA
    console.log(mockData)
    const { debug } = render(<IDABarGraph data={mockData} />, <IDABubbleChart data={mockData} />, <IDALineChart data={mockData} />)
    debug()
})  