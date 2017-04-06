<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>DocumentReader</title>
		<link rel="stylesheet" href="/css/w3.css">
	</head>
		<script>
			<#assign logUrl = "log">
			<#assign logDiv = "logDiv">
			<#assign isReadingUrl = "isReading">
			
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
		<#assign disabled = isReading?then('disabled', '')>
		
		<#macro messageMacro header message color>
			<div class="w3-panel ${color} w3-display-container">
	  			<span onclick="this.parentElement.style.display='none'"
	  			class="w3-button ${color} w3-normal w3-display-topright">X</span>
	  			<h3>${header}</h3>
	  			<p>${message}</p>
			</div>
		</#macro>
		
		<#macro buttonMacro nameSuffix text="Apply" disabled="">
			<button class="w3-button w3-border w3-tiny w3-round w3-blue" name="btn_${nameSuffix}" ${disabled}>${text}</button>
		</#macro>
		
		<#if infoMessage??>
			<@messageMacro header="Info" message=infoMessage color="w3-blue"/>
		<#elseif errorMessage??>
			<@messageMacro header="Error" message=errorMessage color="w3-red"/>
		</#if>
		
		Library size: ${availableDocuments?size}
		
		<#if selectedDocument??>
			Selected document: ${selectedDocument}
			
			<form method='post' action="/">
				<#if isReading == true>
					<@buttonMacro nameSuffix="stop_reading" text="Stop"/>
				<#else>
					<@buttonMacro nameSuffix="start_reading" text="Read"/>
				</#if>
				<@buttonMacro nameSuffix="delete_document" text="Delete document" disabled=disabled/>
				<@buttonMacro nameSuffix="reset_bookmark" text="Remove bookmark" disabled=disabled/>
			</form>
			
		</#if>
	
		<form method='post' enctype='multipart/form-data' action="/">
			<label for="uploaded_file">Load a document</label>
		    <input id="uploaded_file" type='file' name='uploaded_file'>
		    <@buttonMacro nameSuffix="upload" text="Upload"/>
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
			<@buttonMacro nameSuffix="set_document" disabled=disabled/>
		    
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
			<@buttonMacro nameSuffix="set_reader" disabled=disabled/>
		    
		    <br />
			
			<label for="set_language">Language</label>
		    <select id="set_language" name="set_language">
	    		<#list supportedReaderLanguages as language>
	    			<#if selectedReaderLang?? && selectedReaderLang == language>
	    				<option selected value="${language}">${language}</option>
	    			<#else>
						<option value="${language}">${language}</option>
					</#if>
				</#list>
			</select>
		    <@buttonMacro nameSuffix="set_language"/>
		    
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
			<@buttonMacro nameSuffix="set_reading_speed"/>
		    
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
		    <@buttonMacro nameSuffix="set_volume"/>
		    
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
			<@buttonMacro nameSuffix="set_feature_detection" disabled=disabled/>
		    
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
			<@buttonMacro nameSuffix="set_logs"/>
		    
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
			<@buttonMacro nameSuffix="set_page_content"/>
		    
		    <br />
			
			<label for="set_device_state">Device</label>
		    <select id="set_device_state" name="set_device_state">
			  <option value="reboot">reboot</option>
			  <option value="shutdown">shut down</option>
			</select>
			<@buttonMacro nameSuffix="set_device_state" disabled=disabled/>
	    </form>
	    
	    <div id="${logDiv}">
    	</dir>
	</body>
</html>