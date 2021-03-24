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
import BackupOutlinedIcon from '@material-ui/icons/BackupOutlined';
import Button from '@material-ui/core/Button';
import DialogContentText from '@material-ui/core/DialogContentText';
import CircularProgress from '@material-ui/core/CircularProgress';
import Snackbar from '@material-ui/core/Snackbar';
import Alert from '@material-ui/lab/Alert';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import DeleteOutlinedIcon from '@material-ui/icons/DeleteOutlined';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';
import CloudDoneOutlinedIcon from '@material-ui/icons/CloudDoneOutlined';
import HelpOutlineIcon from '@material-ui/icons/HelpOutline';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from "@material-ui/core/styles";

import axios from "axios";

const useStylesBootstrap = (theme) => ({
	arrow: {
		color: theme.palette.common.black
	},
	tooltip: {
		backgroundColor: theme.palette.common.black
	}
});

class DSUploadWizard extends React.Component {
	initialState = {
		activeStep: 0,
		steps: ["Upload dataset", "confirm meta data", "finished"],
		isFileSelected: false,
		files: [],
		filesName: [],
		nextButtonText: 'Upload',
		enableNextButton: false,
		showError: false,
		enableLoader: false,
		metaData: null,
		errorMsg: '',
		showBackBtn: false,
		showCancelBtn: true,
		showConfirm: false,
		showOkBtn: false,
		cancellingUpload: false,
		confirmationMsg: '',
		udsi: null // It will come from Pydsmx and we will use it for metadata storage
	};

	constructor(props) {
		super(props);
		this.state = Object.assign({}, this.initialState);
	}

	onFileChange = (ev) => {
		const files = Object.assign([], this.state.files);
		const filesName = Object.assign([], this.state.filesName);
		Array.from(ev.target.files).forEach(f => {
			if (f.type === 'text/csv' && !filesName.includes(f.name)) {
				// Making sure user selected csv file
				files.push(f)
				filesName.push(f.name)
			}
		});
		if (files.length > 0) {
			this.setState({isFileSelected: true, files: files, enableNextButton: true, filesName: filesName});
		} else {
			this.setState({isFileSelected: false, enableNextButton: false, files: []});
		}
		ev.target.value = ''; // This will user to select same name file again even after removing them
	}

	removefile = (index) => {
		const files = Object.assign([], this.state.files);
		const filesName = Object.assign([], this.state.filesName);

		filesName.splice(files[0].name, 1);
		files.splice(index, 1);

		this.setState({files: files, filesName: filesName});
		if (!files.length) {
			this.setState({isFileSelected: false})
		}
	}

	handleNext = () => {
		if (this.state.activeStep === 0) {
			this.setState({enableLoader: true, enableNextButton: false})
			let formData = new FormData();
			const files = this.state.files;
			for (let i = 0; i < files.length; i++) {
				formData.append(`files[${i}]`, files[i])
			}
			axios.post("http://127.0.0.1:5000/", formData, {
				headers: {
					"Content-Type": "multipart/form-data",
				}
			}).then((resp) => {
				this.setState({
					activeStep: this.state.activeStep + 1,
					nextButtonText: 'save metadata',
					enableLoader: false,
					enableNextButton: true,
					metaData: resp.data.metadata,
					udsi: resp.data.udsi,
					showBackBtn: true
				})
			}).catch((err) => {
				this.setState({
					enableNextButton: true,
					enableLoader: false,
					showError: true,
					errorMsg: err.response.data.message
				})
			})
		} else if (this.state.activeStep === 1) {
			if (this.state.metaData.dsName.trim()) {
				this.setState({enableLoader: true, enableNextButton: false})
				axios.post("http://127.0.0.1:5000/savemetadata", {
					udsi: this.state.udsi,
					metadata: this.state.metaData
				}, {
					headers: {
						"Content-Type": "application/json",
					}
				}).then((resp) => {
					this.setState({
						activeStep: this.state.activeStep + 1,
						enableLoader: false,
						showOkBtn: true,
						showCancelBtn: false,
						showBackBtn: false
					})
				}).catch(() => {
					this.setState({
						enableLoader: false,
						enableNextButton: true,
						showError: true,
						errorMsg: 'Something happened while uploading metadata..'
					})
				})
			} else {
				this.setState({showError: true, errorMsg: 'Please provide dataset name'});
			}
		}
	};

	handleChange = (ev) => {
		const target = ev.target;
		const name = target.name;
		let new_meta_data = Object.assign({}, this.state.metaData);

		if (name.startsWith("filesMd")) {
			const tokens = name.split('.');
			const depth = tokens.length;

			const attr = tokens[depth - 1];
			let first_key = tokens[0].split('[')[0];
			let first_index = tokens[0].match(/(\d+)/)[0];

			if (depth === 2) {
				new_meta_data[first_key][first_index][attr] = target.value;
			} else if (depth === 3) {
				let second_key = tokens[1].split('[')[0];
				let second_index = tokens[1].match(/(\d+)/)[0];
				new_meta_data[first_key][first_index][second_key][second_index][attr] = target.value;
			}
		} else {
			new_meta_data[name] = target.value;
		}
		this.setState({metaData: new_meta_data});
	}

	handleYes = () => {
		axios.post("http://127.0.0.1:5000/delete", {
			udsi: this.state.udsi,
		}, {
			headers: {
				"Content-Type": "application/json",
			}
		})

		if (this.state.cancellingUpload) {
			this.props.close();
		}

		this.setState({
			udsi: null,
			metaData: null,
			showConfirm: false,
			activeStep: 0,
			showBackBtn: false,
			nextButtonText: 'Upload',
			cancellingUpload: false
		})
	}

	handleClose = () => {
		if (this.state.activeStep === 0) {
			this.props.close();
		} else if (this.state.activeStep === 1) {
			this.setState({showConfirm: true, cancellingUpload: true, confirmationMsg: 'All your uploaded files and changes will be lost! Do you really want to cancel?'});
		} else if (this.state.activeStep === 2) {
			this.props.close();
			// As data set upload was successful, hence resetting state
			this.setState(this.initialState);
		}
	}

	addMoreFiles = () => {
		this.state.fileUploadBtn.click();
	}

	fileUploadBtnRef = (e) => {
		this.setState({fileUploadBtn: e});
	}

	render() {
		const { classes } = this.props;
		const renderFileUpload = () => {
			if (!this.state.enableLoader) {
				if (!this.state.isFileSelected) {
					return (
						<div className="upload-dataset-box">
							<label htmlFor="icon-button-file" style={{marginBottom: '-15px'}}
								   style={{display: !this.state.enableLoader ? "block" : "none"}}>
								<IconButton color="primary" aria-label="upload picture" component="span">
									<BackupOutlinedIcon style={{fontSize: 80}}/>
								</IconButton>
							</label>
							<CircularProgress style={{display: this.state.enableLoader ? "block" : "none"}}/>
							<div style={{textAlign: 'center'}}>Select dataset (single file or multiple).. <br/>You can
								only select
								<b> .csv</b> files
							</div>
						</div>
					)
				} else {
					let files_row = []
					for (var i = 0; i < this.state.files.length; i++) {
						let a = i;
						files_row.push(<ListItem><ListItemIcon> <DescriptionOutlinedIcon/> </ListItemIcon><ListItemText
							primary={this.state.files[i].name}/><ListItemSecondaryAction><IconButton edge="end"
																									 aria-label="delete"
																									 onClick={() => {
																										 this.removefile(a)
																									 }}><DeleteOutlinedIcon/></IconButton></ListItemSecondaryAction></ListItem>)
					}
					return <div><h5 style={{padding: '10px 0', textAlign: 'center', color: '#444'}}>Selected
						file(s)</h5><List><ListItem button onClick={this.addMoreFiles}> <ListItemIcon>
						<AddCircleOutlineIcon/> </ListItemIcon> <ListItemText primary="Add more files.."/>
					</ListItem>{files_row}</List></div>
				}
			} else {
				return <div className="upload-dataset-box"><CircularProgress/></div>
			}
		}

		const renderMetaDataForm = () => {
			if (!this.state.enableLoader) {
				if (this.state.metaData) {
					return (<div className="meta-data-box">
						<div className="metadata-info">IDA creates and stores a metadata file for each uploaded file. IDA uses these files to perform various operations. Here you can change some relavant fields kindly go through them all and change them as you like.</div>
						<form>
							<table>
								<tr>
									<td width="15%" className="heading required">Dataset name</td>
									<td><input type="text" name="dsName" value={this.state.metaData.dsName}
											   onChange={this.handleChange}/></td>
								</tr>
								<tr>
									<td width="15%" className="heading">Dataset description</td>
									<td><input type="text" name="dsDesc" value={this.state.metaData.dsDesc}
											   onChange={this.handleChange}/></td>
								</tr>
							</table>
							<br/>
							This dataset contains {this.state.metaData.filesMd.length} files.
							<br/>
							<hr/>
							{this.state.metaData.filesMd.map((f, i) => {
								return (
									<div>
										<table>
											<tr>
												<td className="heading">#</td>
												<td>{i}</td>
											</tr>
											<tr>
												<td className="heading">File name</td>
												<td>{f.fileName}</td>
											</tr>
											<tr>
												<td className="heading">Display name</td>
												<td><input type="text" value={f.displayName}
														   name={`filesMd[${i}].displayName`}
														   onChange={this.handleChange}/></td>
											</tr>
											<tr>
												<td className="heading">File description</td>
												<td><input type="text" value={f.fileDesc}
														   name={`filesMd[${i}].fileDesc`}
														   onChange={this.handleChange}/></td>
											</tr>
											<tr>
												<td className="heading">Columns count</td>
												<td>{f.colCount}</td>
											</tr>
											<tr>
												<td className="heading">Row count</td>
												<td>{f.rowCount}</td>
											</tr>
										</table>
										<table>
											<thead>
											<td className="heading">Column index</td>
											<td className="heading">Column name</td>
											<td className="heading">Column description</td>
											<td className="heading">Column attribute</td>
											<td className="heading">Column type<Tooltip classes={classes} arrow title="IDA guesses columns type automatically. Guessed types can be in-accurate so here you can change them"><HelpOutlineIcon style={{fontSize: 18, color: '#F57C00', marginLeft: '3px'}}/></Tooltip></td>
											<td className="heading">Contains unique values</td>
											</thead>
											<tbody>
											{f.fileColMd.map((e, b) => {
												return (
													<tr key={b}>
														<td>{e.colIndex}</td>
														<td><input value={e.colName}
																   name={`filesMd[${i}].fileColMd[${b}].colName`}
																   onChange={this.handleChange}/></td>
														<td><input value={e.colDesc}
																   name={`filesMd[${i}].fileColMd[${b}].colDesc`}
																   onChange={this.handleChange}/></td>
														<td>{e.colAttr}</td>
														<td>
															<select value={e.colType}
																	name={`filesMd[${i}].fileColMd[${b}].colType`}
																	onChange={this.handleChange}>
																<option value="date">Date</option>
																<option value="string">String</option>
																<option value="numeric">Numeric</option>
															</select>
														</td>
														<td>{e.isUnique ? 'Yes' : 'No'}</td>
													</tr>
												)
											})}
											</tbody>

										</table>
										<br/>
										<hr/>
									</div>
								)
							})}

						</form>
					</div>)
				}
			} else {
				return <div className="upload-dataset-box"><CircularProgress/></div>
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
							minHeight: "800px"
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
								<input
									type="file"
									accept="text/csv"
									id="icon-button-file"
									onChange={this.onFileChange}
									multiple
									hidden
									ref={this.fileUploadBtnRef}
								/>
								{renderFileUpload()}
							</div>
							<div style={{display: this.state.activeStep === 1 ? "block" : "none"}}>
								{renderMetaDataForm()}
							</div>
							<div style={{display: this.state.activeStep === 2 ? "block" : "none"}}>
								<div className="upload-dataset-box">
									<CloudDoneOutlinedIcon style={{fontSize: 80, color: '#4CAF50'}}/>
									<div style={{textAlign: 'center'}}>Your dataset was uploaded successfully.<br/>You
										can start using it now.
									</div>
								</div>
							</div>
						</DialogContentText>
					</DialogContent>

					<DialogActions>
						<Button onClick={() => {
							this.setState({showConfirm: true, confirmationMsg: 'All your uploaded files and changes will be lost! Do you really want to go back?'})
						}} style={{
							textTransform: "Capitalize",
							display: this.state.showBackBtn ? 'block' : 'none'
						}}>
							Back
						</Button>
						<Button onClick={this.handleNext} color="primary" variant="outlined" style={{
							textTransform: "Capitalize",
							display: this.state.enableNextButton ? 'block' : 'none'
						}}>
							{this.state.nextButtonText}
						</Button>
						<Button onClick={this.handleClose} color="secondary" variant="outlined" style={{
							textTransform: "Capitalize",
							display: this.state.showCancelBtn ? 'block' : 'none'
						}}>
							Cancel dataset upload
						</Button>
						<Button onClick={this.handleClose} color="primary" variant="outlined"
								style={{textTransform: "Capitalize", display: this.state.showOkBtn ? 'block' : 'none'}}>
							Close
						</Button>
					</DialogActions>
				</Dialog>
				<Snackbar
					open={this.state.showError}
					onClose={() => {
						this.setState({showError: false})
					}}
					autoHideDuration="5000"
				>
					<Alert severity="error">{this.state.errorMsg}</Alert>
				</Snackbar>
				{/*cancel confirmatiom dialog*/}
				<Dialog
					open={this.state.showConfirm}
					aria-labelledby="alert-dialog-title"
					aria-describedby="alert-dialog-description"
				>
					<DialogContent>
						<DialogContentText id="alert-dialog-description">
							{this.state.confirmationMsg}
						</DialogContentText>
					</DialogContent>
					<DialogActions>
						<Button onClick={this.handleYes} color="primary" color="secondary"
								style={{textTransform: "Capitalize"}}>
							Yes!
						</Button>
						<Button onClick={() => {
							this.setState({showConfirm: false})
						}} color="primary" variant="outlined" style={{textTransform: "Capitalize"}}>
							No
						</Button>
					</DialogActions>
				</Dialog>
			</div>
		)
	}
}
export default withStyles(useStylesBootstrap)(DSUploadWizard)
