import { IDA_CONSTANTS } from "./constants";

export default function IDAChatbotActionHandler(props, actionCode, payload) {
    switch (actionCode) {
        case IDA_CONSTANTS.UI_ACTION_CODES.UIA_LOADDS: {
            const metaData = payload.dsMd || {};
            const data = payload.dsData || [];
            const children = [{
                id: metaData.dsName + "_metadata",
                name: metaData.dsName + " Metadata",
                type: "metadata",
                data: metaData.filesMd,
                fileName: "dsmd.json"
            }];
            data.forEach((table) =>
                children.push({
                    id: metaData.dsName + "_" + table.name,
                    name: table.name,
                    type: "table",
                    data: table.data,
                    fileName: table.name,
                    columns: metaData.filesMd.filter((fl) => fl.fileName === table.name)[0].fileColMd,
                    dsName: metaData.dsName
                })
            );
            const main = {
                id: metaData.dsName,
                name: metaData.dsName,
                type: "parent",
                children: [{
                    id: metaData.dsName + "_dataset",
                    name: "Datasets",
                    type: "parent",
                    children
                }]
            };
            const dataSets = props.detail || [];
            if (dataSets.findIndex((ds) => ds.id === main.id) < 0) {
                dataSets.push(main);
                props.setDetails(dataSets);
            }
            const tabs = props.tabs;
            if (tabs.findIndex((t) => t.id === children[0].id) < 0) {
                tabs.push(children[0]);
                props.setTabs(tabs);
            }
            const expandedNodes = props.expandedNodeId;
            expandedNodes.push(main.id);
            expandedNodes.push(metaData.dsName + "_dataset");
            props.setExpandedNodeId(expandedNodes);
            props.setNavBarClass("");
            props.setActiveTable("");
            props.setActiveDS(metaData.dsName);
            props.setSelectedNodeId(children[0].id);
            props.setLoaded(true);
            break;
        }
        case IDA_CONSTANTS.UI_ACTION_CODES.UAC_BARGRAPH: {
            const treeData = props.detail;
            const activeDS = treeData.find((node) => node.id === payload.activeDS);
            const vizChildren = activeDS.children.find((c) => c.id === payload.activeDS + "_visualizations");
            const viz = vizChildren || {
                id: payload.activeDS + "_visualizations",
                name: "Visualizations",
                type: "parent",
                children: []
            };
            if (!vizChildren) {
                activeDS.children.push(viz);
            }
            const barChartCount = viz.children.filter((c) => c.type === "barchart").length;
            const barChartNode = {
                id: payload.activeDS + "_barchart_" + (barChartCount + 1),
                name: "Bar Graph " + (barChartCount + 1),
                type: "barchart",
                data: payload.barGraphData,
                fileName: "Bar Graph " + (barChartCount + 1)
            };
            viz.children.push(barChartNode);
            const tabs = props.tabs;
            tabs.push(barChartNode);
            props.setTabs(tabs);
            props.setDetails(treeData);
            const expandedNodes = props.expandedNodeId;
            expandedNodes.indexOf(payload.activeDS + "_visualizations") < 0 && expandedNodes.push(payload.activeDS + "_visualizations");
            props.setExpandedNodeId(expandedNodes);
            props.setSelectedNodeId(payload.activeDS + "_barchart_" + (barChartCount + 1));
            break;
        }
        case IDA_CONSTANTS.UI_ACTION_CODES.UAC_BUBBLECHART: {
            const treeData = props.detail;
            const activeDS = treeData.find((node) => node.id === payload.activeDS);
            activeDS.children = activeDS.children || [];
            const bubbleChartCount = activeDS.children.filter((c) => c.type === "bubblechart").length;
            activeDS.children.push({
                id: payload.activeDS + "_bubblechart_" + (bubbleChartCount + 1),
                name: "Bubble Graph " + (bubbleChartCount + 1),
                type: "bubblechart",
                data: payload.bubbleChartData,
                fileName: "Bubble Chart " + (bubbleChartCount + 1)
            });
            props.setSelectedNodeId(payload.activeDS + "_bubblechart_" + (bubbleChartCount + 1));
            break;
        }
        default:
    }
}
