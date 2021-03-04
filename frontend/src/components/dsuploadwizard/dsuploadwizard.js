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
import CircularProgress from '@material-ui/core/CircularProgress';
import Snackbar from '@material-ui/core/Snackbar';
import Alert from '@material-ui/lab/Alert';

import axios from "axios";

export default class DSUploadWizard extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			activeStep: 0,
			steps: ["Upload dataset", "confirm meta data", "finalize"],
			isFileSelected: false,
			fileName: '',
			file: null,
			nextButtonText: 'Upload',
			enableNextButton: false,
			showError: false,
			showFileUploadLoading: false
		};
	}

	onFileChange = (ev) => {
		const files = ev.target.files
		if (files.length > 0) {
			this.setState({isFileSelected: true, file: files[0], fileName: files[0].name, enableNextButton: true});
		} else {
			this.setState({isFileSelected: false, enableNextButton: false, fileName: '', file: null});
		}
	}

	handleNext = () => {
		if (this.state.activeStep === 0) {
			this.setState({showFileUploadLoading: true, enableNextButton: false})
			let formData = new FormData();
			formData.append("file", this.state.file);
			axios.post("http://127.0.0.1:5000/", formData, {
				headers: {
					"Content-Type": "multipart/form-data",
				}
			}).then(() => {
				this.setState({activeStep: this.state.activeStep + 1, enableNextButton: false})
			}).catch(() => {
				this.setState({isFileSelected: false, showFileUploadLoading: false, showError: true})
			})
		}
	};

	hideError = () => {
		this.setState({showError: false})
	}

	render() {
		const renderFileUploadText = () => {
			if (!this.state.showFileUploadLoading) {
				if (!this.state.isFileSelected) {
					return (
						<div>
							<div style={{textAlign: 'center'}}>Select dataset (single file or zip).. <br/>You can select
								.csv, .json, .xlsx or compressed zip file
							</div>
						</div>
					)
				} else {
					return <div>File selected: {this.state.fileName}</div>
				}
			} else {
				return <div>Uploading {this.state.fileName} ... </div>
			}
		}

		return (
			<div>
				<Dialog
					open={this.props.isOpen}
					fullWidth
					maxWidth="lg"
					PaperProps={{
						style: {
							minHeight: "600px"
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
							<div style={{display: this.state.activeStep === 0 ? "block" : "none"}}>
								<div className="upload-dataset-box">
									<input
										type="file"
										accept="text/csv, .json, .xlsx, application/zip"
										id="icon-button-file"
										onChange={this.onFileChange}
										hidden
									/>
									<label htmlFor="icon-button-file" style={{ marginBottom: '-15px'}} style={{display: !this.state.showFileUploadLoading ? "block" : "none"}}>
										<IconButton color="primary" aria-label="upload picture" component="span">
											<BackupIcon style={{ fontSize: 80 }}/>
										</IconButton>
									</label>
									<CircularProgress style={{display: this.state.showFileUploadLoading ? "block" : "none"}} />
									{renderFileUploadText()}
									<Snackbar
										open={this.state.showError}
										onClose={this.hideError}
										autoHideDuration="4000"
									>
										<Alert severity="error">Unable to upload dataset. Please try again..</Alert>
									</Snackbar>
								</div>
							</div>
							<div style={{display: this.state.activeStep === 1 ? "block" : "none"}}>
								<div className="meta-data-box">
									here is meta data
								</div>
							</div>
						</DialogContentText>
					</DialogContent>

					<DialogActions>
						<Button onClick={this.handleNext} color="primary" variant="outlined" style={{ textTransform: "Capitalize", display: this.state.enableNextButton ? 'block' : 'none'}} >
							{this.state.nextButtonText}
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
