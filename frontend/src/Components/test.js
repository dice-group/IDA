import React from 'react';
import Button from '@material-ui/core/Button';
import FileUpload from './FileUpload'

class MyComponent extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        showComponent: false,
      };
      this._onButtonClick = this._onButtonClick.bind(this);
    }
  
    _onButtonClick() {
      this.setState({  
        showComponent: true,
      });
    }
  
    render() {
      return (
        <div>
          <Button onClick={this._onButtonClick}>Button</Button>
          {this.state.showComponent ?
             <FileUpload /> :
             null
          }
        </div>
      );
    }
  }
export default MyComponent;