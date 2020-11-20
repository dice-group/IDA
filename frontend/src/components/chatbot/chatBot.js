import React, { Component } from "react";
import axios from "axios";
import { Launcher } from "react-chat-window";
import { IDA_CONSTANTS } from "../constants";
import IDAChatbotActionHandler from "./../action-handler";
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
      msg: []
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
      axios.post(IDA_CONSTANTS.API_BASE + "/chatmessage", obj, { withCredentials: true, })
        .then(response => {
          outerscope._sendMessage(response.data.message);
          const actionCode = response.data.uiAction;
          const payload = response.data.payload;
          IDAChatbotActionHandler(this.props, actionCode, payload);
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
    this.props.setIsChatbotOpen(!this.props.isChatbotOpen);
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
          isOpen={this.props.isChatbotOpen}
          handleClick={this.handleClick.bind(this)}
        />
      </div>
    );
  }
}
