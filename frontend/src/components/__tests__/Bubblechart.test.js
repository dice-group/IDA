import React from 'react'
import { render, fireEvent } from '@testing-library/react'
import { MOCK_COMPONENT } from '../mockData'
import IDABubbleChart from "../visualizations/bubblechart/bubblechart"
import '@testing-library/jest-dom'

it('it should pass visualisation inputs', () => {
    const mockData = MOCK_COMPONENT.MOCKDATA
    console.log(mockData)
    const { debug } = render(<IDABubbleChart data={mockData} />)
    debug()
})  