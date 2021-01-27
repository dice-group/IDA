import React from 'react'
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom/extend-expect'
import IDALineChart from "../visualizations/linechart/linechart";
import { MOCK_COMPONENT } from "../mockLineData";
import { IDA_CONSTANTS } from "../constants"
// baseapi = IDA_CONSTANTS.API_BASE;
const server = setupServer(
    rest.get(IDA_CONSTANTS.API_BASE, (req, res, ctx) => {
        return res(ctx.json({ message: 'hello there' }))
    })
)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

test('loads and displays greeting', async () => {
    render(<IDALineChart url="http://localhost:8080/chatmessage" data={MOCK_COMPONENT.MOCKDATA} />)
    expect(screen.getByRole('dialog'))
})

test('handles server error', async () => {
    server.use(
        rest.get('http://localhost:8080/checkservice', (req, res, ctx) => {
            return res(ctx.status(500))
        })
    )
    render(<IDALineChart url="http://localhost:8080/chatmessage" data={MOCK_COMPONENT.MOCKDATA} />)
    expect(screen.getByRole('dialog'))
})