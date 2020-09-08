import React, { Component } from "react";
import axios from "axios";
import { Launcher } from "react-chat-window";
import { IDA_CONSTANTS } from "../constants";
import "./chatbot.css";
/* eslint-disable */
export default class ChatBot extends Component {

  constructor(props) {
    super(props);
    this.state = {
      messageList: [{
        author: "them",
        type: "text",
        data: {
          text: "Hello, Welcome to IDA. How may I help you?"
        }
      }],
      changeCSS: {},
      selectClick: [],
      pyld: [],
      action: "1004",
      msg: [],
      isOpen: true
    };
  }

  _onMessageWasSent(message) {
    this.setState({
      messageList: [...this.state.messageList, message]
    });
    var outerscope = this;
    if (message.data.text.length > 0) {
      const obj = {
        "senderId": "01",
        "message": message.data.text,
        "timestamp": "",
        "senderName": "user",
        "activeDS": this.props.activeDS,
        "activeTable": this.props.activeTable
      };
      axios.post(IDA_CONSTANTS.API_BASE + "/chatmessage", obj, {withCredentials: true,})
        .then(response => {
          outerscope._sendMessage(response.data.message);
          const actionCode = response.data.uiAction;
          const payload = response.data.payload;
          if (actionCode === IDA_CONSTANTS.UI_ACTION_CODES.UIA_LOADDS) {
            const metaData = payload.dsMd || {};
            const data = payload.dsData || [];
            const children = [];
            data.forEach(table =>
              children.push({
                id: metaData.dsName + "_" + table.name,
                name: table.name,
                type: "file",
                data: table.data,
                fileName: table.name
              })
            );
            const main = {
              id: metaData.dsName,
              name: metaData.dsName,
              type: "folder",
              data: metaData.filesMd,
              children: children
            };
            const dataSets = this.props.detail || [];
            if (dataSets.findIndex(ds => ds.id === main.id) < 0) {
              dataSets.push(main);
              this.props.setDetails(dataSets);
            }
            const expandedNodes = this.props.expandedNodeId;
            expandedNodes.push(main.id);
            this.props.setExpandedNodeId(expandedNodes);
            this.props.setSelectedNodeId(main.id);
            this.props.setActiveDS(main.id);
            this.props.setLoaded(true);
          }
        })
        .catch((err) => {
          console.log("error");
          console.log(err);
        });
    }
  }

  _sendMessage(text) {
    this.setState({
      messageList: [...this.state.messageList, {
        author: "them",
        type: "text",
        data: { text }
      }]
    })
  }

  handleClick() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  render() {
    return (
      <div className={this.props.detail.length ? "" : "no-data"}>
        <Launcher
          agentProfile={{
            teamName: "IDA-ChatBot",
            imageUrl: "",
            className: ""
          }}
          onMessageWasSent={this._onMessageWasSent.bind(this)}
          messageList={this.state.messageList}
          showEmoji={false}
          isOpen={this.state.isOpen}
          handleClick={this.handleClick.bind(this)}
        />
      </div>
    );
  }
}
