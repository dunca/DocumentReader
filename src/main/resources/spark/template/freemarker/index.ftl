<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>DocumentReader</title>
	</head>
		<#assign logUrl = "log">
		<#assign logDiv = "logDiv">
		<#assign isReadingUrl = "isReading">
		
		<script>
	        function asyncRequest(url) {
			  var xhttp = new XMLHttpRequest();
			  xhttp.onreadystatechange = function() {
			    if (this.readyState == 4 && this.status == 200) {
			    	if (url === "${logUrl}") {
			    		document.getElementById("${logDiv}").innerHTML = this.responseText;
			    	} else if (url === "${isReadingUrl}" && this.responseText === "false") {
			    		location.reload();
			    	}
			    }
			  };
			  xhttp.open("GET", url, true);
			  xhttp.send();
			   
				<#if selectedLog == "on">
				setTimeout(function() {
					asyncRequest("${logUrl}");
				}, 2000);
				</#if>
				
				<#if isReading == true>
				setTimeout(function() {
					asyncRequest("${isReadingUrl}");
				}, 4000);
				</#if>
			}
			<#if selectedLog == "on">
			asyncRequest("${logUrl}");
			</#if>
			
			<#if isReading == true>
			asyncRequest("${isReadingUrl}");
			</#if>
		</script>
	
	<body>
	<div id="abcdef123"></div>
		<#assign disabled = isReading?then('disabled', '')>
	
		<#if message??>
			Info: ${message}
		</#if>
		<#if errorMessage??>
			Altert: ${errorMessage}
		</#if>	
		
		Library size: ${availableDocuments?size}
		
		<#if selectedDocument??>
			Selected document: ${selectedDocument}
			
			<form method='post' action="/">
				<#if isReading == true>
					<button name="btn_set_stop">Stop</button>
				<#else>
					<button name="btn_set_read">Read</button>
				</#if>
				<button name="btn_set_reset_bookmark" ${disabled}>Remove bookmark</button>
				<button name="btn_set_delete" ${disabled}>Delete document</button>
			</form>
			
		</#if>
	
		<form method='post' enctype='multipart/form-data' action="/">
			<label for="uploaded_file">Load a document</label>
		    <input id="uploaded_file" type='file' name='uploaded_file'>
		    <button>Upload</button>
		</form>
		
		<br />
		
		<form method='post' action="/">
			<label for="set_document">Document</label>
		    <select id="set_document" name="set_document">
	    		<#list availableDocuments?keys as key>
	    			<#if selectedDocumentHash?? && selectedDocumentHash == key>
	    				<option selected value="${key}">${availableDocuments[key]}</option>
	    			<#else>
						<option value="${key}">${availableDocuments[key]}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_document" ${disabled}>Apply</button>
		    
		    <br />
			
			<label for="set_reader">Reader</label>
		    <select id="set_reader" name="set_reader">
	    		<#list availableReaderProviders as reader>
	    			<#if selectedReaderProvider?? && selectedReaderProvider == reader>
	    				<option selected value="${reader}">${reader}</option>
	    			<#else>
						<option value="${reader}">${reader}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_reader" ${disabled}>Apply</button>
		    
		    <br />
			
			<label for="set_lang">Language</label>
		    <select id="set_lang" name="set_lang">
	    		<#list supportedReaderLanguages as language>
	    			<#if selectedReaderLang?? && selectedReaderLang == language>
	    				<option selected value="${language}">${language}</option>
	    			<#else>
						<option value="${language}">${language}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_lang">Apply</button>
		    
		    <br />
		    
			<label for="set_reading_speed">Reading speed</label>
		    <select id="set_reading_speed" name="set_reading_speed">
	    		<#list supportedReaderSpeed as speed>
	    			<#if selectedReaderSpeed?? && selectedReaderSpeed == speed>
	    				<option selected value="${speed}">${speed}</option>
	    			<#else>
						<option value="${speed}">${speed}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_reading_speed">Apply</button>
		    
		    <br />
			
			<label for="set_volume">Volume</label>
		    <select id="set_volume" name="set_volume">
  	    		<#list supportedVolumeLevels?keys as key>
  	    			<#assign value = supportedVolumeLevels[key]>
	    			<#if selectedVolumeLevel?? && selectedVolumeLevel == value>
	    				<option selected value="${value}">${key}</option>
	    			<#else>
						<option value="${value}">${key}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_volume">Apply</button>
		    
		    <br />
			
			<label for="set_feature_detection">Feature detection</label>
		    <select id="set_feature_detection" name="set_feature_detection">
  	    		<#list standardSwitchOptions as option>
	    			<#if selectedFeatureDetection?? && selectedFeatureDetection == option>
	    				<option selected value="${option}">${option}</option>
	    			<#else>
						<option value="${option}">${option}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_feature_detection" ${disabled}>Apply</button>
		    
		    <br /><br />
			
			<label for="set_logs">Logs</label>
		    <select id="set_logs" name="set_logs">
  	    		<#list standardSwitchOptions as option>
	    			<#if selectedLog?? && selectedLog == option>
	    				<option selected value="${option}">${option}</option>
	    			<#else>
						<option value="${option}">${option}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_logs">Apply</button>
		    
		    <br />
		    
			<label for="set_page_content">Page content</label>
		    <select id="set_page_content" name="set_page_content">
  	    		<#list standardSwitchOptions as option>
	    			<#if selectedPageContent?? && selectedPageContent == option>
	    				<option selected value="${option}">${option}</option>
	    			<#else>
						<option value="${option}">${option}</option>
					</#if>
				</#list>
			</select>
		    <button name="btn_set_page_content">Apply</button>
		    
		    <br />
			
			<label for="set_manage_device">Device</label>
		    <select id="set_manage_device" name="set_manage_device">
			  <option value="reboot">reboot</option>
			  <option value="shutdown">shut down</option>
			</select>
		    <button name="btn_set_manage_device" ${disabled}>Apply</button>
	    </form>
	    
	    <div id="${logDiv}">
    	</dir>
	</body>
</html>