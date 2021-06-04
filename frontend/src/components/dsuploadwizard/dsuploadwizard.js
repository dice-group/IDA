import React from "react";
import "./dsuploadwizard.css";

import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import Stepper from "@material-ui/core/Stepper";
import IconButton from "@material-ui/core/IconButton";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import DialogContent from "@material-ui/core/DialogContent";
import DialogActions from "@material-ui/core/DialogActions";
import BackupOutlinedIcon from "@material-ui/icons/BackupOutlined";
import Button from "@material-ui/core/Button";
import DialogContentText from "@material-ui/core/DialogContentText";
import CircularProgress from "@material-ui/core/CircularProgress";
import Snackbar from "@material-ui/core/Snackbar";
import Alert from "@material-ui/lab/Alert";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemText from "@material-ui/core/ListItemText";
import ListItemSecondaryAction from "@material-ui/core/ListItemSecondaryAction";
import DeleteOutlinedIcon from "@material-ui/icons/DeleteOutlined";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import AddCircleOutlineIcon from "@material-ui/icons/AddCircleOutline";
import DescriptionOutlinedIcon from "@material-ui/icons/DescriptionOutlined";
import CloudDoneOutlinedIcon from "@material-ui/icons/CloudDoneOutlined";
import HelpOutlineIcon from "@material-ui/icons/HelpOutline";
import Tooltip from "@material-ui/core/Tooltip";
import Accordion from "@material-ui/core/Accordion";
import AccordionSummary from "@material-ui/core/AccordionSummary";
import AccordionDetails from "@material-ui/core/AccordionDetails";
import { withStyles } from "@material-ui/core/styles";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import DehazeOutlinedIcon from "@material-ui/icons/DehazeOutlined";
import ViewAgendaOutlinedIcon from "@material-ui/icons/ViewAgendaOutlined";
import { IDA_CONSTANTS } from "../constants";
import Dropzone from "react-dropzone";

import axios from "axios";

const useStylesBootstrap = (theme) => ({
	arrow: {
		color: theme.palette.common.black
	},
	tooltip: {
		backgroundColor: theme.palette.common.black
	},
	root: {
		border: "1px solid rgba(0, 0, 0, .125)",
		boxShadow: "none",
		"&:not(:last-child)": {
			borderBottom: 0
		},
		"&:before": {
			display: "none"
		},
		"&$expanded": {
			margin: "auto"
		}
	},
	expanded: {}
});

class DSUploadWizard extends React.Component {
	initialState = {
		activeStep: 0,
		steps: ["Upload dataset", "confirm meta data", "finished"],
		isFileSelected: false,
		files: [],
		filesName: [],
		nextButtonText: "Upload",
		enableNextButton: false,
		showError: false,
		enableLoader: false,
		metaData: null,
		errorMsg: "",
		showBackBtn: false,
		showCancelBtn: true,
		showConfirm: false,
		showOkBtn: false,
		cancellingUpload: false,
		confirmationMsg: "",
		udsi: null, // It will come from Pydsmx and we will use it for metadata storage
		expandPanels: [true]
	};

	constructor(props) {
		super(props);
		this.state = Object.assign({}, this.initialState);
	}

	onFileChange = (selectedFiles) => {
		const files = Object.assign([], this.state.files);
		const filesName = Object.assign([], this.state.filesName);
		let notCSVFilesCount = 0;
		Array.from(selectedFiles).forEach((f) => {
			if (f.name.split(".").pop() === "csv" && !filesName.includes(f.name)) {
				// Making sure user selected csv file
				files.push(f);
				filesName.push(f.name);
			} else if (f.name.split(".").pop() !== "csv") {
				notCSVFilesCount++;
			}
		});
		if (files.length > 0) {
			this.setState({ isFileSelected: true, files, enableNextButton: true, filesName });
		} else {
			this.setState({ isFileSelected: false, enableNextButton: false, files: [] });
		}
		if (notCSVFilesCount > 0) {
			this.setState({
				showError: true,
				errorMsg: notCSVFilesCount + " file(s) were not added because they were not CSV file(s)."
			});
		}
	}

	removeFile = (index) => {
		const files = Object.assign([], this.state.files);
		const filesName = Object.assign([], this.state.filesName);

		filesName.splice(files[0].name, 1);
		files.splice(index, 1);

		this.setState({ files, filesName });
		if (!files.length) {
			this.setState({ isFileSelected: false });
		}
	}

	handleNext = () => {
		if (this.state.activeStep === 0) {
			this.uploadFiles();
		} else if (this.state.activeStep === 1) {
			this.saveDataset();
		}
	};

	uploadFiles = () => {
		this.setState({ enableLoader: true, enableNextButton: false });
		let formData = new FormData();
		const files = this.state.files;
		for (let i = 0; i < files.length; i++) {
			formData.append(`files[${i}]`, files[`${i}`]);
		}
		this.makePostRequest("/", formData).then((resp) => {
			let panelsArr = Array(resp.data.metadata.filesMd.length);
			panelsArr[0] = true;
			panelsArr = panelsArr.fill(false, 1);
			this.setState({
				activeStep: this.state.activeStep + 1,
				nextButtonText: "save metadata",
				enableLoader: false,
				enableNextButton: true,
				metaData: resp.data.metadata,
				udsi: resp.data.udsi,
				showBackBtn: true,
				expandPanels: panelsArr
			});
		}).catch((err) => {
			this.setState({
				enableNextButton: true,
				enableLoader: false,
				showError: true,
				errorMsg: err.response.data.message
			});
		});
	}

	saveDataset = () => {
		if (this.state.metaData.dsName.trim()) {
			this.setState({ enableLoader: true, enableNextButton: false });
			this.makePostRequest("/savemetadata", {
				udsi: this.state.udsi,
				metadata: this.state.metaData
			}).then((resp) => {
				this.setState({
					activeStep: this.state.activeStep + 1,
					enableLoader: false,
					showOkBtn: true,
					showCancelBtn: false,
					showBackBtn: false
				})
			}).catch((err) => {
				this.setState({
					enableLoader: false,
					enableNextButton: true,
					showError: true,
					errorMsg: err.response.data.message
				})
			})
		} else {
			this.setState({ showError: true, errorMsg: "Please provide dataset name" });
		}
	}

	makePostRequest = (apiUrl, reqData) => {
		return axios.post(IDA_CONSTANTS.PYDSMX_BASE + apiUrl, reqData, {
			headers: {
				"Content-Type": "application/json"
			}
		});
	}

	handleChange = (ev) => {
		const target = ev.target;
		const name = target.name;
		let newMetaData = Object.assign({}, this.state.metaData);

		if (name.startsWith("filesMd")) {
			const tokens = name.split(".");
			const depth = tokens.length;

			const attr = tokens[depth - 1];
			let firstKey = tokens[0].split("[")[0];
			let firstIndex = tokens[0].match(/(\d+)/)[0];

			if (depth === 2) {
				newMetaData[`${firstKey}`][`${firstIndex}`][`${attr}`] = target.value;
			} else if (depth === 3) {
				let secondKey = tokens[1].split("[")[0];
				let secondIndex = tokens[1].match(/(\d+)/)[0];
				newMetaData[`${firstKey}`][`${firstIndex}`][`${secondKey}`][`${secondIndex}`][`${attr}`] = target.value;
			}
		} else {
			newMetaData[`${name}`] = target.value;
		}
		this.setState({ metaData: newMetaData });
	}

	handleYes = () => {
		axios.post(IDA_CONSTANTS.PYDSMX_BASE + "/delete", {
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
			nextButtonText: "Upload",
			cancellingUpload: false
		})
	}

	handleClose = () => {
		if (this.state.activeStep === 0) {
			this.props.close();
		} else if (this.state.activeStep === 1) {
			this.setState({
				showConfirm: true,
				cancellingUpload: true,
				confirmationMsg: "All your uploaded files and changes will be lost! Do you really want to cancel?"
			});
		} else if (this.state.activeStep === 2) {
			this.props.close();
			// As data set upload was successful, hence resetting state
			this.setState(this.initialState);
		}
	}

	manageAccordion = (idx) => {
		let panelsArr = Object.assign([], this.state.expandPanels);
		panelsArr[`${idx}`] = !panelsArr[idx];
		this.setState({ expandPanels: panelsArr });
	}

	toggleCollapseAll = (collapseAll) => {
		let panelsArr = Object.assign([], this.state.expandPanels);
		this.setState({ expandPanels: panelsArr.fill(collapseAll) });
	}

	sendMessage = () => {
		const elem = document.getElementById("chat-input");
		elem.value = "Load " + this.state.metaData.dsName + " dataset";
		document.getElementById("send-btn").click();
		this.handleClose();
	}

	renderFileUpload = (open) => {
		if (!this.state.enableLoader && !this.state.isFileSelected) {
			return (
				<div className="dataset-box-flex">
					<IconButton color="primary" aria-label="upload picture" component="span" onClick={open}>
						<BackupOutlinedIcon style={{ fontSize: 80 }} />
					</IconButton>
					<CircularProgress style={{ display: this.state.enableLoader ? "block" : "none" }} />
					<div style={{ textAlign: "center" }}>Select or drag dataset (single file or multiple)..
						<br />You can
							only select
							<b> .csv</b> files
						</div>
				</div>
			);
		} else if (!this.state.enableLoader) {
			let filesRow = [];
			for (var i = 0; i < this.state.files.length; i++) {
				let a = i;
				filesRow.push(<ListItem><ListItemIcon> <DescriptionOutlinedIcon /> </ListItemIcon><ListItemText
					primary={this.state.files[`${i}`].name} /><ListItemSecondaryAction><IconButton edge="end"
						aria-label="delete"
						onClick={() => {
							this.removeFile(a);
						}}><DeleteOutlinedIcon /></IconButton></ListItemSecondaryAction></ListItem>);
			}
			return (
				<div style={{
					width: "100%"
				}}>
					<h5 style={{ padding: "10px 0", textAlign: "center", color: "#444" }}>Selected file(s)</h5>
					<List>
						<ListItem button onClick={open}>
							<ListItemIcon>
								<AddCircleOutlineIcon />
							</ListItemIcon>
							<ListItemText primary="Add more files.." />
						</ListItem>
						{filesRow}
					</List>
				</div>
			);
		} else {
			return <div className="dataset-box-flex"><CircularProgress /></div>;
		}
	};

	renderMetaDataForm = () => {
		const { classes } = this.props;
		if (!this.state.enableLoader) {
			if (this.state.metaData) {
				return (<div className="meta-data-box">
					<div className="metadata-info">IDA creates and stores a metadata file for each uploaded file.
					IDA uses these files to perform various operations. Here you can change some relavant fields
					kindly go through them all and change them as you like.
					</div>
					<form>
						<table style={{ marginLeft: "16px" }}>
							<tr>
								<td width="15%" className="heading required">Dataset name</td>
								<td><input type="text" name="dsName" value={this.state.metaData.dsName}
									onChange={this.handleChange} /></td>
							</tr>
							<tr>
								<td width="15%" className="heading">Dataset description</td>
								<td><input type="text" name="dsDesc" value={this.state.metaData.dsDesc}
									onChange={this.handleChange} /></td>
							</tr>
						</table>
						<br />
						<div style={{ marginLeft: "16px" }}> This dataset
							contains {this.state.metaData.filesMd.length} files.
						</div>
						<div style={{ display: this.state.metaData.filesMd.length > 1 ? "block" : "none" }}
							class="collapse-btns">
							<Tooltip classes={classes} title="Collapse all">
								<IconButton component="span" style={{ float: "right" }} onClick={() => {
									this.toggleCollapseAll(false);
								}}>
									<DehazeOutlinedIcon />
								</IconButton>
							</Tooltip>
							<Tooltip classes={classes} title="Expand all">
								<IconButton component="span" style={{ float: "right" }} onClick={() => {
									this.toggleCollapseAll(true);
								}}>
									<ViewAgendaOutlinedIcon />
								</IconButton>
							</Tooltip>
						</div>
						<br />
						{this.state.metaData.filesMd.map((f, i) => {
							return (
								<div>
									<Accordion classes={classes} expanded={this.state.expandPanels[`${i}`]}
										onChange={() => {
											this.manageAccordion(i);
										}}>
										<AccordionSummary expandIcon={this.state.metaData.filesMd.length > 1 ?
											<ExpandMoreIcon /> : ""}
											style={{ width: "100%" }}>{i + 1}. {f.fileName}</AccordionSummary>
										<AccordionDetails style={{ flexDirection: "column" }}>
											<table>
												<tr>
													<td className="heading">Display name</td>
													<td><input type="text" value={f.displayName}
														name={`filesMd[${i}].displayName`}
														onChange={this.handleChange} /></td>
												</tr>
												<tr>
													<td className="heading">File description</td>
													<td><input type="text" value={f.fileDesc}
														name={`filesMd[${i}].fileDesc`}
														onChange={this.handleChange} /></td>
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
													<td className="heading">Column type<Tooltip classes={classes} arrow
														title="IDA guesses columns type automatically. Guessed types can be in-accurate so here you can change them"><HelpOutlineIcon
															style={{
																fontSize: 18,
																color: "#F57C00",
																marginLeft: "3px"
															}} /></Tooltip></td>
													<td className="heading">Contains unique values</td>
												</thead>
												<tbody>
													{f.fileColMd.map((e, b) => {
														return (
															<tr key={b}>
																<td>{e.colIndex}</td>
																<td><input value={e.colName}
																	name={`filesMd[${i}].fileColMd[${b}].colName`}
																	onChange={this.handleChange} /></td>
																<td><input value={e.colDesc}
																	name={`filesMd[${i}].fileColMd[${b}].colDesc`}
																	onChange={this.handleChange} /></td>
																<td>{e.colName || e.colAttr}</td>
																<td>
																	<select value={e.colType}
																		name={`filesMd[${i}].fileColMd[${b}].colType`}
																		onChange={this.handleChange}>
																		<option value="date">Date</option>
																		<option value="string">String</option>
																		<option value="numeric">Numeric</option>
																	</select>

																	<select style={{ display: e.colType === "date" ? "block" : "none", width: "60%" }}
																		value={e.dataFormat}
																		name={`filesMd[${i}].fileColMd[${b}].dataFormat`}
																		onChange={this.handleChange}>
																		<option value="dd/MM/yyyy">dd/MM/yyyy</option>
																		<option value="dd-MMM-yyyy">dd-MMM-yyyy</option>
																		<option value="MMMM-yyyy">MMMM-yyyy</option>
																		<option value="MMM YYYY">MMM YYYY</option>
																		<option value="dd MMM">dd MMM</option>
																		<option value="dd/MM/yyyy HH:mm:ss">dd/MM/yyyy HH:mm:ss</option>
																		<option value="YYYY">YYYY</option>
																	</select>
																</td>
																<td>{e.isUnique ? "Yes" : "No"}</td>
															</tr>
														)
													})}
												</tbody>

											</table>
										</AccordionDetails>
									</Accordion>
								</div>
							)
						})}

					</form>
				</div>);
			}
		} else {
			return <div className="dataset-box-flex"><CircularProgress /></div>;
		}
	};

	render() {
		return (
			<div>
				<Dialog
					open={this.props.isOpen}
					fullWidth
					maxWidth="lg"
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
							<div style={{ display: this.state.activeStep === 0 ? "block" : "none" }}>
								<Dropzone onDrop={this.onFileChange} noClick={true}>
									{({ getRootProps, open, getInputProps, isDragActive }) => (
										<div {...getRootProps()} style={{ height: "100%", backgroundColor: isDragActive ? "#f1f1f1" : "" }}>
											<input {...getInputProps()} />
											{this.renderFileUpload(open)}
										</div>
									)}
								</Dropzone>
							</div>
							<div style={{ display: this.state.activeStep === 1 ? "block" : "none" }}>
								{this.renderMetaDataForm()}
							</div>
							<div style={{ display: this.state.activeStep === 2 ? "block" : "none" }}>
								<div className="dataset-box-flex">
									<CloudDoneOutlinedIcon style={{ fontSize: 80, color: "#4CAF50" }} />
									<div style={{ textAlign: "center" }}>Your dataset was uploaded successfully.<br />
										<button className={"default"} onClick={this.sendMessage}>Load {this.state.metaData ? this.state.metaData.dsName : ""} dataset</button>
									</div>
								</div>
							</div>
						</DialogContentText>
					</DialogContent>

					<DialogActions>
						<Button onClick={() => {
							this.setState({
								showConfirm: true,
								confirmationMsg: "All your uploaded files and changes will be lost! Do you really want to go back?"
							});
						}} style={{
							textTransform: "Capitalize",
							display: this.state.showBackBtn && !this.state.enableLoader ? "block" : "none"
						}}>
							Back
						</Button>
						<Button onClick={this.handleNext} color="primary" variant="outlined" style={{
							textTransform: "Capitalize",
							display: this.state.enableNextButton ? "block" : "none"
						}}>
							{this.state.nextButtonText}
						</Button>
						<Button onClick={this.handleClose} color="secondary" variant="outlined" style={{
							textTransform: "Capitalize",
							display: this.state.showCancelBtn && !this.state.enableLoader ? "block" : "none"
						}}>
							Cancel dataset upload
						</Button>
						<Button onClick={this.handleClose} color="primary" variant="outlined"
							style={{ textTransform: "Capitalize", display: this.state.showOkBtn ? "block" : "none" }}>
							Close
						</Button>
					</DialogActions>
				</Dialog>
				<Snackbar
					anchorOrigin={{ vertical: "top", horizontal: "center" }}
					open={this.state.showError}
					onClose={() => {
						this.setState({ showError: false });
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
							style={{ textTransform: "Capitalize" }}>
							Yes!
						</Button>
						<Button onClick={() => {
							this.setState({ showConfirm: false });
						}} color="primary" variant="outlined" style={{ textTransform: "Capitalize" }}>
							No
						</Button>
					</DialogActions>
				</Dialog>
			</div>
		)
	}
}

export default withStyles(useStylesBootstrap)(DSUploadWizard);
