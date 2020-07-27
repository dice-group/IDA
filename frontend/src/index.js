import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

// import React from 'react';
// import SockJsClient from 'react-stomp';

ReactDOM.render(

  <React.StrictMode>
    <App/>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();

// class SampleComponent extends React.Component {
//   constructor(props) {
//     super(props);
//   }

//   sendMessage = (msg) => {
//     this.clientRef.sendMessage('/topics/all', msg);
//   }

//   render() {
//     return (
//       <React.StrictMode>
//       <App/>
//       </React.StrictMode>,
//       document.getElementById('root'),
//       <div>
//         <SockJsClient url='http://localhost:8080/ws' topics={['/topics/all']}
//             onMessage={(msg) => { console.log(msg); }}
//             ref={ (client) => { this.clientRef = client }} />
//       </div>
      
//     );
   
// x