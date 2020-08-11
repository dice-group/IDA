import React, {Component} from "react"
import axios from "axios";
import {Launcher} from "react-chat-window";
import "./chatbot.css";
import CustomizedTables from "./Datatable";
import Treeview from "./Treeview";
import {API_URL} from "../env.json";

export default class Demo extends Component {

  constructor() {
    super();
    this.state = {
      messageList: [],
      changeCSS:{}
    };
  }

  _onMessageWasSent(message) {
    this.setState({
      messageList: [...this.state.messageList, message]
    })
    // let changeCSS = {top:"50%" , transform: "translate(-50%, -50%)"}

    var outerscope = this;
    if (message.data.text.length > 0) {
        let obj = {
            "senderId"  : "01",
            "message" :  message.data.text,
            "timestamp" : "",
            "senderName" : "spoorthi"
        };
        const res = axios.post(API_URL + "/chatmessage", obj, {}
        ).then((response) => {
            // console.log(response); proper success msg
            //changeCSS = {top:"50%" , right: 0, transform: "translate(-10%, -60%) !important"};
            outerscope._sendMessage(response.data.message)
      }, function (err) {
            // console.log(err.status); app should show proper error message
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

  render() {
    let changeCSS = {top:"50%" , transform: "translate(-50%, -50%)"};

    return (
    <div style={{changeCSS}} >
      <Launcher
        agentProfile={{
          teamName: "IDA-ChatBot",
          imageUrl: "",
          className:{changeCSS}

        }}
        onMessageWasSent={this._onMessageWasSent.bind(this)}
        messageList={this.state.messageList}
      />

    <div>
        {/* <Treeview/>
        <CustomizedTables/>  */}
    </div>
    </div>
    )

  }
}
