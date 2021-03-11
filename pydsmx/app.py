import os
from flask import Flask, request
from werkzeug.utils import secure_filename
from flask_cors import CORS, cross_origin
import pandas as pd

app = Flask(__name__)
cors = CORS(app)

app.config['CORS_HEADERS'] = 'Content-Type'
app.config['TEMP_FOLDER'] = './temp-uploads'
app.config['MAX_CONTENT_LENGTH'] = 5000000 # Maximum fi


@app.route('/', methods=['POST'])
@cross_origin()
def upload_file():
	if request.method == 'POST':
		files = request.files.to_dict() # convert multidict to dict
		for file in files:
			file_name = secure_filename(files[file].filename)
			files[file].save(os.path.join(app.config['TEMP_FOLDER'], file_name))
		return 'Done', 200
		# if file:

		#
		# 	ds = pd.read_csv('./temp-uploads/' + file_name)
		# 	cols = [col for col in ds.columns]
		# 	no_rows, no_cols = ds.shape
		#
		# 	file_cols_md = []
		# 	for index, col_name in enumerate(cols):
		# 		file_cols_md.append({"colIndex": index+1, "colName": col_name, "colDesc":  col_name, "colType": str(ds[col_name].dtype), "colAttr": col_name, "isUnique": pd.Series(ds[col_name]).is_unique})
		#
		# 	files_meta_data = [
		# 		{
		# 			"fileName": file_name,
		# 			"displayName": file_name,
		# 			"fileDesc": "",
		# 			"rowCount": no_rows,
		# 			"colCount": no_cols,
		# 			"fileColMd": file_cols_md
		# 		}
		# 	]
		# 	meta_data = {
		# 		"dsName": file_name,
		# 		"dsDesc": "",
		# 		"filesMd": files_meta_data
		# 	}
		# 	dict = {"metadata": meta_data, "message": "dataset to uploaded successfully"}
		# 	return dict, 200
