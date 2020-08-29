import React, { Component } from 'react';
import axios from 'axios';
import { Launcher } from 'react-chat-window';
import { IDA_CONSTANTS } from './Constants';
/* eslint-disable */
export default class ChatBot extends Component {

  constructor(props) {
    super(props);
    this.state = {
      messageList: [],
      changeCSS: {},
      selectClick: [],
      pyld: [],
      action: '1004',
      msg: [],
    }
  }

  _onMessageWasSent(message, props) {
    this.setState({
      messageList: [...this.state.messageList, message]
    })
    var outerscope = this;
    if (message.data.text.length > 0) {
      const obj = {
        'senderId': '01',
        'message': message.data.text,
        'timestamp': '',
        'senderName': 'spoorthi',
        'activeDS': '',
        'activeTable': ''
      };
      axios.post(IDA_CONSTANTS.API_BASE + '/chatmessage', obj)
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
                id: metaData.dsName + '_' + table.name,
                // name: metaData.filesMd.filter(file => file.fileName.toLowerCase() === table.name.toLowerCase())[0].fileDesc,
                name: table.name,
                type: 'file',
                data: table.data,
                fileName: table.name
              })
            );
            const main = {
              id: metaData.dsName,
              name: metaData.dsName,
              type: 'folder',
              data: metaData.filesMd,
              children: children
            };
            const dataSets = this.props.detail || [];
            if(dataSets.findIndex(ds => ds.id === main.id) < 0){
              dataSets.push(main);
              this.props.setDetails(dataSets);
            }
            this.props.setLoaded(true);
            this.props.setSelectedNodeId(main.id);
            this.props.setExpandedNodeId(main.id);
          }
        })
        .catch((err) => {
          console.log('error');
          console.log(err);
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

  render(props) {
    let changeCSS = { top: '50%', transform: 'translate(-50%, -50%)' };
    return (
      <div style={{ changeCSS }} >
        <Launcher
          agentProfile={{
            teamName: 'IDA-ChatBot',
            imageUrl: '',
            className: { changeCSS }

          }}
          onMessageWasSent={this._onMessageWasSent.bind(this)}
          messageList={this.state.messageList}
        />
      </div>
    );
  }
}