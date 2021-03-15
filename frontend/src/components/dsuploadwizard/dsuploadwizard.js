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
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemSecondaryAction from '@material-ui/core/ListItemSecondaryAction';
import DeleteIcon from '@material-ui/icons/Delete';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import AddCircleOutlineIcon from '@material-ui/icons/AddCircleOutline';
import DescriptionOutlinedIcon from '@material-ui/icons/DescriptionOutlined';

import axios from "axios";

export default class DSUploadWizard extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			activeStep: 0,
			steps: ["Upload dataset", "confirm meta data", "finalize"],
			isFileSelected: false,
			files: [],
			nextButtonText: 'Upload',
			enableNextButton: false,
			showError: false,
			showFileUploadLoading: false,
			metaData: null
		};
	}

	onFileChange = (ev) => {
		const files = this.state.files;
		Array.from(ev.target.files).forEach(f => files.push(f));
		if (files.length > 0) {
			this.setState({isFileSelected: true, files: files, enableNextButton: true});
		} else {
			this.setState({isFileSelected: false, enableNextButton: false, files: []});
		}
	}

	handleNext = () => {
		if (this.state.activeStep === 0) {
			this.setState({showFileUploadLoading: true, enableNextButton: false})
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
				this.setState({activeStep: this.state.activeStep + 1, enableNextButton: false, metaData: resp.data.metadata })
			}).catch(() => {
				this.setState({isFileSelected: false, showFileUploadLoading: false, showError: true})
			})
		}
	};

	hideError = () => {
		this.setState({showError: false})
	}

	handleChange = (ev) => {
		console.log(ev)
	}

	removefile = (index) => {
		const files = this.state.files;
		files.splice(index, 1);
		this.setState({files: files});
		if (! files.length) {
			this.setState({isFileSelected: false})
		}
	}

	addMoreFiles  = () => {
		this.state.fileUploadBtn.click();
	}

	fileUploadBtnRef = (e) => {
		this.setState({fileUploadBtn:  e});
	}

	render() {
		const renderFileUpload = () => {
			if (!this.state.showFileUploadLoading) {
				if (!this.state.isFileSelected) {
					return (
						<div className="upload-dataset-box">
							<label htmlFor="icon-button-file" style={{ marginBottom: '-15px'}} style={{display: !this.state.showFileUploadLoading ? "block" : "none"}}>
								<IconButton color="primary" aria-label="upload picture" component="span">
									<BackupIcon style={{ fontSize: 80 }}/>
								</IconButton>
							</label>
							<CircularProgress style={{display: this.state.showFileUploadLoading ? "block" : "none"}} />
							<div style={{textAlign: 'center'}}>Select dataset (single file or multiple).. <br/>You can select
								.csv files
							</div>
						</div>
					)
				} else {
					let files_row = []
					for (var i = 0; i < this.state.files.length; i++) {
						let a = i;
						files_row.push(<ListItem><ListItemIcon> <DescriptionOutlinedIcon /> </ListItemIcon><ListItemText primary={this.state.files[i].name} /><ListItemSecondaryAction><IconButton edge="end" aria-label="delete"><DeleteIcon onClick={() => { this.removefile(a) } } /></IconButton></ListItemSecondaryAction></ListItem>)
					}
					return <div><h5 style={{padding: '10px 0', textAlign: 'center', color: '#444'}}>Selected file(s)</h5><List><ListItem button onClick={this.addMoreFiles}> <ListItemIcon> <AddCircleOutlineIcon /> </ListItemIcon> <ListItemText primary="Add more files.." /> </ListItem>{files_row}</List></div>
				}
			} else {
				return <div className="upload-dataset-box"><CircularProgress /></div>
			}
		}

		const renderMetaDataForm = () => {
			if (this.state.metaData) {
				return (<div className="meta-data-box">
					<form onChange={this.handleChange}>
					<table>
						<tr>
							<td width="15%" className="heading required">Dataset name</td>
							<td><input type="text" value={this.state.metaData.dsName}/></td>
						</tr>
						<tr>
							<td  width="15%" className="heading">Dataset description</td>
							<td><input type="text" value={this.state.metaData.dsDesc}/></td>
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
											<td><input type="text" value={f.displayName} /></td>
										</tr>
										<tr>
											<td className="heading">File description</td>
											<td><input type="text" value={f.fileDesc} /></td>
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
									<td className="heading">Column type</td>
									<td className="heading">Contains unique values</td>
									</thead>
									<tbody>
									{ f.fileColMd.map((e, i) => {
										return (
											<tr key={i}>
												<td>{e.colIndex}</td>
												<td><input value={e.colName} /></td>
												<td><input value={e.colDesc} /></td>
												<td>{e.colAttr}</td>
												<td>{e.colType}</td>
												<td>{e.isUnique? 'Yes' : 'No'}</td>
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
								<Snackbar
									open={this.state.showError}
									onClose={this.hideError}
									autoHideDuration="6000"
								>
									<Alert severity="error">Unable to upload dataset. Please try again..</Alert>
								</Snackbar>
							</div>
							<div style={{display: this.state.activeStep === 1 ? "block" : "none"}}>
								{renderMetaDataForm()}
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
