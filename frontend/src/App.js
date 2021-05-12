import React from "react";
import Dialog from '@material-ui/core/Dialog';
import IdleTimer from 'react-idle-timer';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import Button from '@material-ui/core/Button';
import Home from "./components/home";
import "./App.css";
export default function App() {
	const [open, setOpen] = React.useState(false);
	const [time, settime] = React.useState(5);
	const [msg, setmsg] = React.useState("IDA has been idle for last 15 minutes, please press continue button within next 5 minutes to avoid losing the current session");

	let idleTimer = null;

	const handleOnIdle = () => {
		if (time === 10) {
			setmsg("IDA has been idle for last 20 minutes, and now it will be reloaded!");
		}
		setOpen(true);
		settime(10)
	}

	const reload = () => {
		window.location.reload();
	}

  return (
  	<div>
	  <IdleTimer
		  ref={ref => { idleTimer = ref }}
		  timeout={1000 * time}
		  onIdle={handleOnIdle}
		  debounce={250}
		  crossTab={{
			  emitOnAllTabs: true
		  }}
	  />
    <Home />
		<Dialog open={open}>
			<DialogContent>
				<DialogContentText id="alert-dialog-slide-description">
					{msg}
				</DialogContentText>
			</DialogContent>
			<DialogActions>
				<Button onClick={reload} color="primary">
					Ok
				</Button>
			</DialogActions>
		</Dialog>
	</div>
  );
}
