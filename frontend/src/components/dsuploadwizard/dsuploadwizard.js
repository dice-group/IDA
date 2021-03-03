import React from "react";
import "./dsuploadwizard.css";

import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import Stepper from "@material-ui/core/Stepper";
import IconButton from '@material-ui/core/IconButton';
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import DialogContent from '@material-ui/core/DialogContent';
import DialogActions from '@material-ui/core/DialogActions';
import BackupIcon from "@material-ui/icons/Backup";
import Button from '@material-ui/core/Button';
import DialogContentText from '@material-ui/core/DialogContentText';


export default class DSUploadWizard extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			activeStep: 0,
			steps: ["Upload dataset", "confirm meta data", "finalize"],
			isFileSelected: false,
			fileName: '',
			showNextButton: false
		};
	}

	onFileChange = (ev) => {
		const files = ev.target.files
		if (files.length > 0) {
			this.setState({isFileSelected: true, fileName: files[0].name, showNextButton: true});
		} else {
			this.setState({isFileSelected: false, showNextButton: false});
		}
	}

	render() {
		const renderFileUploadText = () => {
			if (! this.state.isFileSelected) {
				return (
					<div>
						<div style={{textAlign: 'center'}}>Select dataset (single file or zip).. <br/>You can select
							.csv, .json, .xlsx or compressed zip file
						</div>
					</div>
				)
			} else {
				return <div >File selected: {this.state.fileName}</div>
			}
		}

		return (
			<div>
				<Dialog
					open={this.props.isOpen}
					fullWidth
					maxWidth="md"
					PaperProps={{
						style: {
							minHeight: "400px"
						}
					}}
				>
					<DialogTitle id="draggable-dialog-title">
						Upload dataset wizard
					</DialogTitle>

					<Stepper activeStep={this.state.activeStep}>
						{this.state.steps.map((label, index) => {
							const stepProps = {};
							const labelProps = {};
							return (
								<Step key={label} {...stepProps}>
									<StepLabel {...labelProps}>{label}</StepLabel>
								</Step>
							);
						})}
					</Stepper>
					<DialogContent>
						<DialogContentText id="alert-dialog-description">
							<div className="upload-dataset-box">
								<input
									type="file"
									accept="text/csv, .json, .xlsx, application/zip"
									id="icon-button-file"
									onChange={this.onFileChange}
									hidden
								/>
								<label htmlFor="icon-button-file" style={{ marginBottom: '-15px'}}>
									<IconButton color="primary" aria-label="upload picture" component="span">
										<BackupIcon style={{ fontSize: 80 }}/>
									</IconButton>
								</label>
								{renderFileUploadText()}
							</div>
						</DialogContentText>
					</DialogContent>

					<DialogActions>
						<Button color="primary" variant="outlined" style={{ textTransform: "Capitalize", display: this.state.showNextButton ? 'block': 'none' }} >
							Next
						</Button>
						<Button color="secondary" variant="outlined" style={{ textTransform: "Capitalize" }} >
							Cancel dataset upload
						</Button>
					</DialogActions>
				</Dialog>
			</div>
		)
	}
}
