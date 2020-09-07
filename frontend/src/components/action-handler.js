import { IDA_CONSTANTS } from "./constants";

export default function IDAChatbotActionHandler(props, actionCode, payload) {
    switch (actionCode) {
        case IDA_CONSTANTS.UI_ACTION_CODES.UIA_LOADDS: {
            const metaData = payload.dsMd || {};
            const data = payload.dsData || [];
            const children = [];
            data.forEach(table =>
                children.push({
                    id: metaData.dsName + "_" + table.name,
                    name: table.name,
                    type: "table",
                    data: table.data,
                    fileName: table.name
                })
            );
            const main = {
                id: metaData.dsName,
                name: metaData.dsName,
                type: "dataset",
                data: metaData.filesMd,
                children: children
            };
            const dataSets = props.detail || [];
            if (dataSets.findIndex(ds => ds.id === main.id) < 0) {
                dataSets.push(main);
                props.setDetails(dataSets);
            }
            const expandedNodes = props.expandedNodeId;
            expandedNodes.push(main.id);
            props.setExpandedNodeId(expandedNodes);
            props.setSelectedNodeId(main.id);
            props.setActiveDS(main.id);
            props.setLoaded(true);
            break;
        }
        case IDA_CONSTANTS.UI_ACTION_CODES.UAC_BARGRAPH: {
            const treeData = props.detail;
            const activeDS = treeData.find(node => node.id === payload.activeDS);
            activeDS.children = activeDS.children || [];
            const barChartCount = activeDS.children.filter(c => c.type === "barchart").length;
            activeDS.children.push({
                id: payload.activeDS + "_barchart_" + (barChartCount + 1),
                name: "Bar Graph " + (barChartCount + 1),
                type: "barchart",
                data: payload.barGraphData,
                fileName: "Bar Graph " + (barChartCount + 1)
            });
            props.setSelectedNodeId(payload.activeDS + "_barchart_" + (barChartCount + 1));
            break;
        }
        default:
            console.log("Action code did not match");
    }
}