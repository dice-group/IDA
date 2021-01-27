
import React from 'react'
import { rest } from 'msw'
import { setupServer } from 'msw/node'
import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom/extend-expect'
import IDABarGraph from "../visualizations/barchart/barchart";
import { MOCK_COMPONENT } from "../mockData";

const server = setupServer(
    rest.get('http://localhost:8080', (req, res, ctx) => {
        return res(ctx.json({ message: 'hello there' }))
    })
)

beforeAll(() => server.listen())
afterEach(() => server.resetHandlers())
afterAll(() => server.close())

test('loads and displays greeting', async () => {
    render(<IDABarGraph url="/checkservice" data={MOCK_COMPONENT} />)
    expect(screen.getByRole('dialog'))
})

test('handles server error', async () => {
    server.use(
        rest.get('/checkservice', (req, res, ctx) => {
            return res(ctx.status(500))
        })
    )
    render(<IDABarGraph url="/chatmessage" data={MOCK_COMPONENT} />)
    expect(screen.getByRole('dialog'))
})