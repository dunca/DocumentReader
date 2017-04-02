<!DOCTYPE html>
<html>
	<head>
		<title>DocumentReader</title>
	</head>

	<body>
		<#if documentCount??>
			Library size: ${documentCount}
		</#if>
		
		<#if selectedDocumentName??>
			Selected document: ${selectedDocumentName}
			
			<button name="readButton">Read</button>
			<button name="continueButton">Continue</button>
			<button name="stopButton">Stop</button>
			
		</#if>
	
		<#if message??>
			Info: ${message}
		</#if>
		<#if errorMessage??>
			Altert: ${errorMessage}
		</#if>
	
		<form method='post' enctype='multipart/form-data' action="/">
			<label for="uploaded_file">Load a document</label>
		    <input id="uploaded_file" type='file' name='uploaded_file'>
		    <button>Upload</button>
		</form>
		
		<br />
		
		<form method='post' action="/">
			<label for="set_book">Document</label>
		    <select id="set_book" name="set_book">
			  <option value="volvo">Volvo</option>
			</select>
		    <button name="btn_set_book">Apply</button>
		    
		    <br />
			
			<label for="set_lang">Language</label>
		    <select id="set_lang" name="set_lang">
			  <option value="volvo">Volvo</option>
			</select>
		    <button name="btn_set_lang">Apply</button>
		    
		    <br />
			
			<label for="set_volume">Volume</label>
		    <select id="set_volume" name="set_volume">
			  <option value="volvo">Volvo</option>
			</select>
		    <button name="btn_set_volume">Apply</button>
		    
		    <br />
			
			<label for="set_feature_detection">Feature detection</label>
		    <select id="set_feature_detection" name="set_feature_detection">
			  <option value="on">on</option>
			  <option value="off">off</option>
			</select>
		    <button name="btn_set_feature_detection">Apply</button>
		    
		    <br /><br />
			
			<label for="set_logs">Logs</label>
		    <select id="set_logs" name="set_logs">
			  <option value="on">on</option>
			  <option value="off">off</option>
			</select>
		    <button name="btn_set_logs">Apply</button>
		    
		    <br />
		    
			<label for="set_page_content">Page content</label>
		    <select id="set_page_content" name="set_page_content">
			  <option value="on">on</option>
			  <option value="off">off</option>
			</select>
		    <button name="btn_set_page_content">Apply</button>
		    
		    <br />
			
			<label for="set_manage_device">Device</label>
		    <select id="set_manage_device" name="set_manage_device">
			  <option value="reboot">reboot</option>
			  <option value="shutdown">shut down</option>
			</select>
		    <button name="btn_set_manage_device">Apply</button>
	    </form>
	</body>
</html>