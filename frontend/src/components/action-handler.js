import { IDA_CONSTANTS } from "./constants";

function addVisualizationEntry(props, vizData, label, name, activeDSName) {
    const treeData = props.detail;
    const activeDS = treeData.find((node) => node.id === activeDSName);
    const vizChildren = activeDS.children.find((c) => c.id === activeDSName + "_visualizations");
    const viz = vizChildren || {
        id: activeDSName + "_visualizations",
        name: "Visualizations",
        type: "parent",
        children: []
    };
    if (!vizChildren) {
        activeDS.children.push(viz);
    }
    const vizCount = viz.children.filter((c) => c.type === name).length;
    const vizNode = {
        id: activeDSName + "_" + name + "_" + (vizCount + 1),
        name: label + " " + (vizCount + 1),
        type: name,
        data: vizData,
        fileName: label + " " + (vizCount + 1)
    };
    viz.children.push(vizNode);
    const tabs = props.tabs;
    tabs.push(vizNode);
    props.setTabs(tabs);
    props.setDetails(treeData);
    const expandedNodes = props.expandedNodeId;
    expandedNodes.indexOf(activeDSName + "_visualizations") < 0 && expandedNodes.push(activeDSName + "_visualizations");
    props.setExpandedNodeId(expandedNodes);
    props.setSelectedNodeId(activeDSName + "_" + name + "_" + (vizCount + 1));
    if (window.matchMedia("(max-width: 991px)").matches) {
        props.setIsChatbotOpen(false);
    }
}

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
            if (window.matchMedia("(max-width: 991px)").matches) {
                props.setIsChatbotOpen(false);
            }
            break;
        }
        case IDA_CONSTANTS.UI_ACTION_CODES.UAC_BARGRAPH: {
            addVisualizationEntry(props, payload.barGraphData, "Bar Graph", "barchart", payload.activeDS);
            break;
        }
        case IDA_CONSTANTS.UI_ACTION_CODES.UAC_BUBBLECHART: {
            addVisualizationEntry(props, payload.bubbleChartData, "Bubble Chart", "bubblechart", payload.activeDS);
            break;
        }
        case IDA_CONSTANTS.UI_ACTION_CODES.UAC_LINECHART: {
            addVisualizationEntry(props, payload.lineChartData, "Line Chart", "linechart", payload.activeDS);
            break;
        }
        default:
    }
}
