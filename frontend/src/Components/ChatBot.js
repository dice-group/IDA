import React, {Component} from 'react'
import axios from 'axios';
import {Launcher} from 'react-chat-window'
 
export default class Demo extends Component {
 
  constructor() {
    super();
    this.state = {
      messageList: []
    };
  }
 
  _onMessageWasSent(message) {
    this.setState({
      messageList: [...this.state.messageList, message]
    })
    var outerscope = this;
    if (message.data.text.length > 0) {
        let obj = {
            "senderId"  : "01",
            "message" :  message.data.text,
            "timestamp" : "",
            "senderName" : "spoorthi"
        };
        console.log(obj);
        const res = axios.post('http://localhost:8090/chatmessage', obj, {}
        ).then(response => {
            console.log('success');
            console.log(response);
            outerscope._sendMessage(response.data.message)
      }, function (err) {
            console.log('error');
            console.log(err.status);
      });
    }
  }
 
  _sendMessage(text) {
      this.setState({
        messageList: [...this.state.messageList, {
          author: 'them',
          type: 'text',
          data: { text }
        }]
      })
  }
 
  render() {
    return (<div>
      <Launcher
        agentProfile={{
          teamName: 'IDA-ChatBot',
          imageUrl: ''
        }}
        onMessageWasSent={this._onMessageWasSent.bind(this)}
        messageList={this.state.messageList}
        showEmoji
      />
    </div>)
  }
}