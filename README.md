<p align="center"><img src="/docs/_media/ida_logo_oh.svg"></img></p>

# IDA
[Software Campus](https://softwarecampus.de/en/) project repository for [Intelligent Data Science Chatbot](https://softwarecampus.de/en/project/ida-intelligent-data-science-chatbot/)

<b>Documentation & Guides</b>: https://dice-group.github.io/IDA

## Development guide

In order to develop, only tool you need is `Docker` installed.
Docker is not directly supported by Windows 10 Home and Windows <= 8.1.
Use [Docker Toolbox](https://docs.docker.com/toolbox/toolbox_install_windows/) if you are using an unsupported OS.

IDA is set up via bash-scripts. If you are using Windows you will have to install bash, e.g. by installing [cmder](https://cmder.net/), by installing [Cygwin](https://www.cygwin.com/) or via [WSL](https://docs.microsoft.com/en-us/windows/wsl/install-win10).

To get everything up and running follow these steps:
1.  Setting up Docker (**Only once**)
 	1.  Start a local registry: `docker run -d -p 5000:5000 --name registry registry:2`
	2.  Create a local single-node Docker swarm: `docker swarm init --advertise-addr 127.0.0.1`
<br/><br/>
2.  Managing development environment
 	1.  Starting the IDA stack (from project's root directory): `./services/deploy-dev.sh`
	2.  The IDA web interface should now be available at `http://127.0.0.1:4200/`
	3.  To stop and remove the running development stack run `docker stack rm ida-stack-dev`
	4.  The fuseki server can be accessed at `http://127.0.0.1:3030`

<br/>

❤️ Credits to [Lixi](https://github.com/TortugaAttack/) for creating the awesome logo for IDA 🙏
