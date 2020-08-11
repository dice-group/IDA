const fs = require("fs");

//Obtain the environment string passed to the node script
const environment = process.argv[2] || "dev";
let envFileContent = "";

//read the content of the json file
if (environment === 'dev') {
	envFileContent = require("../envs/dev.json");
} else {
	envFileContent = require("../envs/prod.json");
}

//copy the json inside the env.json file
fs.writeFileSync("./src/env.json", JSON.stringify(envFileContent));
