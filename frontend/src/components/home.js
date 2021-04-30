import React, { useRef, useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
// import ChatBot from "./chatbot/chatBot";
import ChatApp from "./chatbot/chatbotcomp";
import IDANavbar from "./navbar/navbar";
import TabsWrappedLabel from "./tabs/tabs";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import CssBaseline from "@material-ui/core/CssBaseline";
import ChevronLeftIcon from "@material-ui/icons/ChevronLeft";
import ChevronRightIcon from "@material-ui/icons/ChevronRight";
import ChatIcon from "@material-ui/icons/Chat";
import InfoIcon from "@material-ui/icons/Info";
import SpeakerNotesOffIcon from "@material-ui/icons/SpeakerNotesOff";
import { Fab, Hidden, Typography, IconButton, Zoom } from "@material-ui/core";
import MenuIcon from "@material-ui/icons/Menu";
import Popover from "@material-ui/core/Popover";
import CopyrightIcon from "@material-ui/icons/Copyright";

import "./home.css";

const useStyles = makeStyles((theme) => ({
	menuButton: {
		marginRight: theme.spacing(2),
	},
	title: {
		flexGrow: 1,
	}
}));

export default function Home(props) {
	const classes = useStyles();
	const [detail, setDetails] = useState([]);
	const [loaded, setLoaded] = useState(false);
	const [selectedNodeId, setSelectedNodeId] = useState("");
	const [expandedNodeId, setExpandedNodeId] = useState([]);
	const [activeDS, setActiveDS] = useState("");
	const [activeTable, setActiveTable] = useState("");
	const [tabs, setTabs] = useState([]);
	const [isChatbotOpen, setIsChatbotOpen] = useState(true);
	const [activeTableData, setActiveTableData] = useState(null);
	const [anchorEl, setAnchorEl] = useState(null);
	const buttonRef = useRef(null);
	const [contexts, setContexts] = useState([]);
	const loadTab = (loaded) => {
		if (loaded && tabs.length) {
			return <TabsWrappedLabel
				loaded={loaded}
				detail={detail}
				selectedNodeId={selectedNodeId}
				setSelectedNodeId={setSelectedNodeId}
				setActiveTable={setActiveTable}
				tabs={tabs}
				setTabs={setTabs}
				setActiveTableData={setActiveTableData}
				isChatbotOpen={isChatbotOpen}
				setIsChatbotOpen={setIsChatbotOpen}
			/>
		}
	}
	const [navBarVisiblity, setNavBarVisiblity] = useState(true);
	const [navBarClass, setNavBarClass] = useState("no-navbar");
	const toggleNavBar = () => {
		if (!navBarVisiblity) {
			setNavBarClass("");
		} else {
			setNavBarClass("navbar-hidden");
		}
		setNavBarVisiblity(!navBarVisiblity);
	};
	const toggleChatWindow = () => {
		setIsChatbotOpen(!isChatbotOpen);
	};
	const toggleNavWindow = () => {
		setNavBarVisiblity(!navBarVisiblity);
		if (!navBarVisiblity) {
			setNavBarClass("");
		} else {
			setNavBarClass("navwindow-shown");
		}
	};
	const getAnchorEl = () => {
		return anchorEl;
	};
	const handleContextPopover = () => {
		setAnchorEl(buttonRef.current);
	};
	const handleContextPopoverClose = () => {
		setAnchorEl(null);
	};
	return (
		<>
			<CssBaseline />
			<AppBar>
				<Toolbar>
					<Hidden mdUp>
						<MenuIcon onClick={toggleNavWindow} />
					</Hidden>
					<Typography variant="h6" className={classes.title} align="center">
						Intelligent Data Science Chatbot
					</Typography>
					<CopyrightIcon className="context-icon" ref={buttonRef} onClick={handleContextPopover} />
					<Popover
						open={Boolean(anchorEl)}
						anchorEl={getAnchorEl()}
						onClose={handleContextPopoverClose}
						anchorOrigin={{
							vertical: "bottom",
							horizontal: "center",
						}}
						transformOrigin={{
							vertical: "top",
							horizontal: "center",
						}}
					>
						<div>
							<ul>
								{
									contexts.map(
										(context, index) => (
											<li key={index}>{context}</li>
										)
									)
								}
							</ul>
						</div>
					</Popover>
					<a href="https://softwarecampus.de/en/project/ida-intelligent-data-science-chatbot/"
						target="_blank">
						<IconButton style={{ color: "#fff", marginRight: "10px" }} aria-label="info about the project">
							<InfoIcon />
						</IconButton>
					</a>
					<IconButton size="small" style={{ color: "#fff" }} aria-label="toggle" onClick={toggleChatWindow}>
						{
							isChatbotOpen ? (
								<Zoom in={isChatbotOpen} mountOnEnter unmountOnExit>
									<SpeakerNotesOffIcon />
								</Zoom>
							) : null
						}
						{
							!isChatbotOpen ? (
								<Zoom direction="left" in={!isChatbotOpen} mountOnEnter unmountOnExit>
									<ChatIcon />
								</Zoom>
							) : null
						}

					</IconButton>
				</Toolbar>
			</AppBar>
			<Toolbar />
			<div className={navBarClass}>
				<Grid container>
					<Grid item className={"nav-bar-container"}>
						<div className={"navbar"}>
							<IDANavbar
								loaded={loaded}
								selectedNodeId={selectedNodeId}
								setSelectedNodeId={setSelectedNodeId}
								expandedNodeId={expandedNodeId}
								setExpandedNodeId={setExpandedNodeId}
								detail={detail}
								setActiveDS={setActiveDS}
								setActiveTable={setActiveTable}
								tabs={tabs}
								setTabs={setTabs}
								navBarVisiblity={navBarVisiblity}
								setNavBarClass={setNavBarClass}
								isOpen={isChatbotOpen}
								setNavBarVisiblity={setNavBarVisiblity}
								setActiveTableData={setActiveTableData}
							/>
						</div>
						<Hidden mdDown>
							<Fab size="small" color="primary" aria-label="toggle" className={"navbar-toggle-icon"}
								onClick={toggleNavBar}>
								{
									loaded && (navBarVisiblity ? <ChevronLeftIcon /> : <ChevronRightIcon />)
								}
							</Fab>
						</Hidden>
					</Grid>
					<Grid item className={"content"}>
						{loadTab(loaded)}
					</Grid>
				</Grid>
				<ChatApp
					setDetails={setDetails}
					setSelectedNodeId={setSelectedNodeId}
					detail={detail}
					expandedNodeId={expandedNodeId}
					setExpandedNodeId={setExpandedNodeId}
					setLoaded={setLoaded}
					activeDS={activeDS}
					activeTable={activeTable}
					setActiveDS={setActiveDS}
					setActiveTable={setActiveTable}
					tabs={tabs}
					setTabs={setTabs}
					setNavBarClass={setNavBarClass}
					isChatbotOpen={isChatbotOpen}
					setIsChatbotOpen={setIsChatbotOpen}
					activeTableData={activeTableData}
					setActiveTableData={setActiveTableData}
					setContexts={setContexts}
				/>

			</div>
		</>
	);
}
