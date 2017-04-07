<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>DocumentReader</title>
		<link rel="stylesheet" href="/css/w3.css">
		<link rel="stylesheet" href="/css/style.css">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<script>
			<#assign logUrl = "log">
			<#assign logDiv = "logDiv">
			<#assign contentDiv = "contentDiv">
			<#assign isReadingUrl = "isReading">
			<#assign currentPageUrl = "currentPage">
			
			function changeText(elementId, text) {
				document.getElementById(elementId).innerHTML = text;
			}
			
	        function asyncRequest(url) {
			  var xhttp = new XMLHttpRequest();
			  xhttp.onreadystatechange = function() {
			    if (this.readyState == 4 && this.status == 200) {
			    	if (url === "${logUrl}") {
			    		changeText("${logDiv}", this.responseText);
			    	} else if (url === "${currentPageUrl}") {
			    		changeText("${contentDiv}", this.responseText);   	
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
				}, 2500);
				</#if>
				
				<#if isReading == true>
				setTimeout(function() {
					asyncRequest("${isReadingUrl}");
				}, 4000);
				
				setTimeout(function() {
					asyncRequest("${currentPageUrl}");
				}, 2500);
				</#if>
			}
			<#if selectedLog == "on">
			asyncRequest("${logUrl}");
			</#if>
			
			<#if isReading == true>
			asyncRequest("${isReadingUrl}");
			</#if>
		</script>
	</head>
	
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
		
		<#macro simpleSelectMacro name label iterable selectedValue>
			<label for="${name}">${label}</label>
		    <select id="${name}" name="${name}" class="w3-input">
    		<#list iterable as option>
    			<#if selectedValue?? && selectedValue == option>
    				<option selected value="${option}">${option}</option>
    			<#else>
					<option value="${option}">${option}</option>
				</#if>
			</#list>
			</select>
		</#macro>
		
		<#macro mappingSelectMacro name label mapping selectedValue>
			<label for="${name}">${label}</label>
		    <select id="${name}" name="${name}" class="w3-input">	
			<#list mapping?keys as key>
				<#if selectedValue?? && selectedValue == key>
    				<option selected value="${key}">${mapping[key]}</option>
    			<#else>
					<option value="${key}">${mapping[key]}</option>
				</#if>
			</#list>
			</select>
		</#macro>
		
		<#macro buttonMacro nameSuffix text="Apply" disabled="" buttonWithInput=true>
			<button class="w3-button w3-border w3-small w3-round w3-blue w3-padding-small" name="btn_${nameSuffix}" ${disabled}>${text}</button>
			<br />
			<#if buttonWithInput==true>
				<br />
			</#if>
		</#macro>
		
		<div class="w3-container w3-center">
		<#if infoMessage??>
			<@messageMacro header="Info" message=infoMessage color="w3-blue"/>
		<#elseif errorMessage??>
			<@messageMacro header="Error" message=errorMessage color="w3-red"/>
		</#if>
		
		<p>Library size: ${availableDocuments?size}</p>
		
		<#if selectedDocument??>
			<p>Document: ${selectedDocument} (${selectedDocumentPageCount} pages)</p>
			
			<form method='post' action="/">
				<#if isReading == true>
					<@buttonMacro nameSuffix="stop_reading" text="Stop" buttonWithInput=false/>
				<#else>
					<@buttonMacro nameSuffix="start_reading" text="Read" buttonWithInput=false/>
				</#if>
				<@buttonMacro nameSuffix="delete_document" text="Delete document" disabled=disabled buttonWithInput=false/>
				<@buttonMacro nameSuffix="reset_bookmark" text="Remove bookmark" disabled=disabled buttonWithInput=false/>
			</form>
		</#if>
	
		<br />
		
		<form method='post' enctype='multipart/form-data' action="/">
			<label for="uploaded_file">Load a document</label>
		    <input id="uploaded_file" type='file' name='uploaded_file' class="w3-input">
		    <@buttonMacro nameSuffix="upload" text="Upload"/>
		</form>
		
		<br />
		
		<form method='post' action="/">
			<@mappingSelectMacro name="set_document" label="Document" mapping=availableDocuments selectedValue=selectedDocumentHash!""/>
			<@buttonMacro nameSuffix="set_document" disabled=disabled/>
		    
			<@simpleSelectMacro name="set_reader" label="Reader" iterable=availableReaderProviders selectedValue=selectedReaderProvider/>
			<@buttonMacro nameSuffix="set_reader" disabled=disabled/>
		    
			<@simpleSelectMacro name="set_language" label="Language" iterable=supportedReaderLanguages selectedValue=selectedReaderLanguage/>
		    <@buttonMacro nameSuffix="set_language"/>
		    
			<@simpleSelectMacro name="set_reading_speed" label="Reading speed" iterable=supportedReaderSpeed selectedValue=selectedReaderSpeed/>
			<@buttonMacro nameSuffix="set_reading_speed"/>
		    
			<@mappingSelectMacro name="set_volume" label="Volume" mapping=supportedVolumeLevels selectedValue=selectedVolumeLevel/>
		    <@buttonMacro nameSuffix="set_volume"/>
		    
			<@simpleSelectMacro name="set_feature_detection" label="Feature detection" iterable=standardSwitchOptions selectedValue=selectedFeatureDetection/>
			<@buttonMacro nameSuffix="set_feature_detection" disabled=disabled/>
		    
			<@simpleSelectMacro name="set_logs" label="Logs" iterable=standardSwitchOptions selectedValue=selectedLog/>
			<@buttonMacro nameSuffix="set_logs"/>
		    
			<@simpleSelectMacro name="set_page_content" label="Page content" iterable=standardSwitchOptions selectedValue=selectedPageContent/>
			<@buttonMacro nameSuffix="set_page_content"/>
		    		
			<label for="set_device_state">Device</label>
		    <select id="set_device_state" name="set_device_state" class="w3-input">
			  <option value="reboot">reboot</option>
			  <option value="shutdown">shut down</option>
			</select>
			<@buttonMacro nameSuffix="set_device_state" disabled=disabled/>
	    </form>
	    </div>
	    
	    <#if isReading==true>
    	    <div id="${contentDiv}" class="w3-half">
    		</div>
	    </#if>
	    
	    <#if selectedLog=="on">
		    <div id="${logDiv}" class="w3-half">
	    	</div>
	    </#if>
	</body>
</html>