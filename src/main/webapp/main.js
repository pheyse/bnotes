var app = new Vue({
  el: '#app',
  data: {
    message: 'Hello',
	todos: [
      { text: 'Learn JavaScript' },
      { text: 'Learn Vue' },
      { text: 'Build something awesome' }
    ],
    levels:[1, 2, 3, 4, 5, 6],
	mode: 'login', /*initially: 'login', use e.g. 'view' for debugging. values: login, view, confirmDelete, confirmDeleteDocument, newDocumentChooseName, renameDocument, grantAccess, uploadImage, chooseDocumentToMoveTo*/
	documentTitle: 'This is the document title',
	documentId: null, /* initially: null. Use 1 for debugging. */
	userName: null,
	password: null,
	jwt: null,
	loginMessage: "",
	chapterToDelete: -1,
	chapterInEdit: -1,
	chapterInEditLevel: 0,
	chapterInEditTitle: "",
	chapterInEditBody: "",
	chapterToMove: -1,
	documentEditedTitle: "",
	selectedDocumentId: null,
	editChapterHierarchy: false,
	showDebugInfo: false,
	editChapterMessage: "",
	userChoices: [],
	uploadImageMessage: "",
	possibleDocumentsToMoveTo: [],
	chapters: [],
	chapters2: [
      { title: 'Chapter One', indexLabel: '1.', level: 1, chapterId: 1, bodyRaw: 'Dummy Body Raw:\n\n#Bullet Points\n - item *one*\n - item two', bodyHtml: '<span><h1>Bullet Points</h1><ul><li><span>item </span><b>one</b></li><ul><li><span>item </span><span style="color:red">the red</span></li></ul><li><i>item two</i></li><ul><li>items two-a</li><li><span/><pre style="background:lightgrey"><code><span><br/></span><span style="color:purple;font-weight:bold">int</span><span> x = 2;<br/></span><span style="color:purple;font-weight:bold">if</span><span> (z == 42){<br/>   processData();<br/>}<br/>String y = </span><span style="color:blue">"hello"</span><span>;</span></code></pre></li></ul><li>item three</li><ul><li><img align="top" border="1mm" height="50mm" src="logo.png"/></li></ul><li><span>item four contains the code </span><code style="background:lightgrey"><span style="color:purple;font-weight:bold">int</span><span> x = </span><span style="color:blue">"hello"</span><span>;</span></code><span> within the text</span></li></ul><p/><h1>Table:</h1><table class="brightmarkdown"><tr><th>col 1</th><th>col 2</th></tr><tr><td>one</td><td>a</td></tr><tr><td>two</td><td>b</td></tr><tr><td>three</td><td>c</td></tr></table><p><span>More text...</span><br/><span>Text with code:</span><br/><span>End of the text.</span></p></span>' },
      { title: 'Chapter One Part 1', indexLabel: '1.1', level: 2, chapterId: 2, bodyRaw: 'Dummy Body Raw', bodyHtml: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' },
      { title: 'Chapter One Part 2', indexLabel: '1.2', level: 2, chapterId: 3, bodyRaw: 'Dummy Body Raw', bodyHtml: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' }, 
      { title: 'Chapter Two', indexLabel: '2.', level: 1, chapterId: 4, bodyRaw: 'Dummy Body Raw', bodyHtml: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' }
		], 
	documents: [], 
	documents2: [
      { title: 'Document One with a very, very long title', documentId: 1 },
      { title: 'Document Two', documentId: 2 },
      { title: 'Document Three', documentId: 3},
    ], 
	rowHover: [
	]
	},
	mounted: function(){
		//: debug settings:
		//this.showDebugInfo = true;
		/*
		this.mode = 'view';
		this.documentId = 1;
		this.chapters = this.chapters2;
		*/
	},
  methods: { 
		toggleShowDebugInfo: function(){
			//: debug info disabled:
			//this.showDebugInfo = !this.showDebugInfo; 
		},
		login: function(){
			//var request = createRequest("login");
			//request.parameters["userName"] = this.userName;		
			//executeRequest(request);

			performLogin(this.userName, this.password);
	},
		toggleEditChapterHierarchy: function(){
			this.editChapterHierarchy = !this.editChapterHierarchy;
		},
		onDocumentSelected: function(event, documentId){
			this.message = "Open document: documentId = " + documentId;
			this.editChapterHierarchy = false;
			this.documentId = documentId;
			this.chapters = [];

			if (this.documentId != null){
				var request = createRequest("getChapters");
				executeRequest(request);
			}
		},
    requestEditChapter: function (chapterId) {
			//: instead of editing the chapter right away by calling "editChapter",
			//: a server request is sent. This way if there is a timeout in the JWT or a connection problem
			//: the user is prompted to login before editing the chapter to prevent loosing the user's changes
			var request = createRequest("editChapter");
			request.parameters["chapterId"] = chapterId;
			executeRequest(request);			
		},
    editChapter: function (chapterId, chapterLevel, title, body) {
      this.message = "Edit chapter: " + chapterId;
			this.chapterInEdit = chapterId;
			this.chapterInEditLevel = chapterLevel;
			this.chapterInEditTitle = title;
			this.chapterInEditBody = body;
			this.editChapterMessage = "Editing chapter..."
    }, 
    moveChapter: function (chapterId, direction) {
      this.message = "Move chapter: " + chapterId + ", direction = " + direction;
			try{
				var request = createRequest("moveChapter");
				request.parameters["chapterId"] = chapterId;		
				request.parameters["direction"] = direction;		
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
	  	}
    }, 
    grantDocumentAccess: function () {
			if ((typeof this.documentId  === "undefined") || (this.documentId == null)){
				return;
			}
			this.mode = "grantAccess";
			this.userChoices = [];
			try{
				var request = createRequest("getPossibleUsersToGrantAccess");
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
		}, 
		grantAccessDone: function() {
			this.mode = "view";
		},
		grantAccessChosen: function(userName) {
			try{
				var request = createRequest("grantAccess");
				request.parameters["grantUserName"] = userName;		
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
	  	}
		},
    deleteChapterUnconfirmed: function (chapterId) {
      this.message = "Delete chapter unconfirmed: " + chapterId;
	  	this.chapterToDelete = chapterId;
	  	this.mode = "confirmDelete";
    }, 
    deleteCancelled: function () {
	  	this.message = "Deleted cancelled";
	  	this.chapterToDelete = -1;
	  	this.mode = "view";
    }, 
    deleteConfirmed: function () {
	  	this.message = "Deleted chapter: " + this.chapterToDelete;
	  	try{
				var request = createRequest("deleteChapter");
				request.parameters["chapterId"] = this.chapterToDelete;		
				executeRequest(request);
	  	} catch (err){
			console.log("Error: " + errorToString(err));
			}
			this.chapterToDelete = -1;
			this.mode = "view";
		}, 
		renameDocument: function() {
			if ((typeof this.documentId  === "undefined") || (this.documentId == null)){
				return;
			}
			this.documentEditedTitle = this.documentTitle;
			this.mode = "renameDocument";
		},
		renameDocumentCancelled: function() {
			this.mode = "view";
		},
		renameDocumentConfirmed: function() {
      this.message = "Rename document. confirmed.";
			try{
				var request = createRequest("renameDocument");
				request.parameters["title"] = this.documentEditedTitle;		
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
	  	}
			this.mode = "view";
		},
    newDocumentChooseName: function () {
      this.message = "new document";
			this.mode = "newDocumentChooseName";
			this.documentEditedTitle = "new document";
    }, 
    newDocumentCancelled: function () {
      this.message = "new document: canceled";
	  	this.mode = "view";
    }, 
    newDocumentWithName: function () {
			this.message = "new document: create";
			try{
				var request = createRequest("addDocument");
				request.parameters["title"] = this.documentEditedTitle;
				executeRequest(request);
	  	} catch (err){
				console.log("Error: " + errorToString(err));
	  	}

	  	this.mode = "view";
    }, 
    deleteDocumentUnconfirmed: function () {
			if ((typeof this.documentId  === "undefined") || (this.documentId == null)){
				return;
			}
      this.message = "Delete document unconfirmed";
	  	this.mode = "confirmDeleteDocument";
    }, 
    deleteDocumentCancelled: function () {
	  	this.message = "Deleted document cancelled";
	  	this.mode = "view";
    }, 
    deleteDocumentConfirmed: function () {
	  	this.message = "Delete document";
	  	try{
				var request = createRequest("deleteDocument");
				executeRequest(request);
	  	} catch (err){
				console.log("Error: " + errorToString(err));
			}
			this.mode = "view";
    }, 
    addChapter: function (chapterId, level) /*: -1: at the end */ {
      this.message = "add chapter below: " + chapterId;
	  
  	  try{
				var request = createRequest("addChapter");
				request.parameters["chapterId"] = chapterId;
				request.parameters["level"] = level;
				executeRequest(request);
	  	} catch (err){
				console.log("Error: " + errorToString(err));
	  	}
    }, 
    saveChapter: function (chapterId, index, finishEditing) {
			this.message = "save chapter : " + chapterId + ", with text of index" + index;
			if (finishEditing){
				this.chapterInEdit = -1;
			}
	  
			try{
			var request = createRequest("saveChapter");
				request.parameters["chapterId"] = chapterId;
				request.parameters["level"] = this.chapterInEditLevel;
				request.parameters["title"] = this.chapterInEditTitle;
				request.parameters["body"] = this.chapterInEditBody;
				request.parameters["finishEditing"] = finishEditing;
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
    }, 
    changeChapter: function (chapterId, newLevel, newTitle, newBody) {
			if ((newLevel < 1) || (newLevel > 6)){
				return;
			}

			try{
				var request = createRequest("saveChapter");
				request.parameters["chapterId"] = chapterId;
				request.parameters["level"] = newLevel;
				request.parameters["title"] = newTitle;
				request.parameters["body"] = newBody;
				request.parameters["finishEditing"] = false;
				
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
		},
		cloneChapter: function(chapterId){
			try{
				var request = createRequest("cloneChapter");
				request.parameters["chapterId"] = chapterId;
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
		},
		chooseDocToMoveChapter: function(chapterId){
			this.chapterToMove = chapterId;
		 	try{
				var request = createRequest("chooseDocToMoveChapter");
				request.parameters["chapterId"] = chapterId;
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			} 
		},
		cancelMoveChapterToOtherDoc: function(){
			this.mode = "view";
		},
		moveChapterToOtherDoc: function(destDocId){
			try{
				var request = createRequest("moveChapterToOtherDoc");
				request.parameters["chapterId"] = this.chapterToMove;
				request.parameters["destDocId"] = destDocId;
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
		},
    cancelEditChapter: function () {
      this.message = "cancel edit chapter";
	  	this.chapterInEdit = -1;
		}, 
		uploadImage: function (chapterId) {
			this.mode = "uploadImage";
			this.uploadImageMessage = "";
			this.imageUploadJwt = this.jwt;
			this.imageUploadDocumentId = this.documentId;
			this.imageUploadChapterId = chapterId;
			this.imageUploadName = "";
		}, 
    showBrightMarkdownInfo: function () {
			window.open("BrightMarkdownHelp", "_blank");    
		}, 
		loadDocumentsButtonClicked : function () {
			try{
				var request = createRequest("getDocuments");
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
		},
		uploadImageForm: function(){
			uploadImageForm(); //: call the method outside of the vue method array
		},
		cancelUploadImage: function(){
			this.mode = "view";
		},
		getImgUrl: function(imageId){
			return "image/" + imageId;
		},
		getImgUrlWithJWT: function(imageId){
			return "image/" + imageId + "/" + this.jwt;
		},
		confirmDeleteImage: function(imageId){
			if (confirm("Really delete this image?")){
				try{
					var request = createRequest("deleteImage");
					request.parameters["imageId"] = imageId;
					executeRequest(request);
				} catch (err){
					console.log("Error: " + errorToString(err));
				}
			};
		},
		testButtonClicked : function () {
			try{
				console.log("Test button was clicked!");
				var request = createRequest("testButtonClicked");
				request.parameters["myTextFieldText"] = "TextFieldValue";
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
		}
  }
})

errorToString = function(err){
	if (err == null){
		return "error: null";
	}
	return "error properties: " + JSON.stringify(err) + ", toString(): " + err.toString();
}

createRequest = function(actionName){
    request = new Object();
		request.action = actionName;
    request.parameters = new Object();
		request.parameters["docId"] = app.documentId;
/* 		request.parameters["userName"] = app.userName;
		request.parameters["password"] = app.password; */
    return request;
};


getDocumentElementById = function(id){
	return document.getElementById(id)
};

uploadImageForm = function(request){
	console.log("Bnotes: uploadImageForm: 1")
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "uploadImage"); 
	xhr.setRequestHeader("Authorization", "Bearer " + app.jwt);

	var url = "uploadImage";
	var formData = new FormData(document.getElementById("imageUploadForm")); 

	xhr.onload = function(event){ 
		console.log("uploadImageForm: success. Response: " + event.target.response)
		processReply(xhr.responseText);
    //alert("Success, server responded with: " + event.target.response); 
	}; 
	xhr.onerror = function(event){ 
    alert("Error, server responded with: " + event.target.response); 
	}; 
	xhr.send(formData);
};

performLogin = function(username, password){
	var xhr = new XMLHttpRequest();
	console.log("Bnotes: performLogin: start")
	var url = "users/signin";
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onreadystatechange = function (vm) {
		if (xhr.readyState === 4 && xhr.status === 200) {
			var result = xhr.responseText;
			console.log("Bnotes: performLogin: result = >>" + result + "<<")
			app.jwt = result;
			app.loginMessage = "";

			//: once the login is done and the JWT has been received, get the documents (the response will also close the login form)
			var request = createRequest("getDocuments");
			executeRequest(request);

		}	else if (xhr.readyState === 4) {
			app.loginMessage = "Login failed";
			console.log("Bnotes: performLogin: error = :" + xhr.readyState + ", " + xhr.status);
			console.log("Bnotes: performLogin: other state. responseText = :" + xhr.responseText);
			console.log("Bnotes: performLogin: other state. response = :" + xhr.response);
			console.log("Bnotes: performLogin: other state. responseType = :" + xhr.responseType);
			console.log("Bnotes: performLogin: other state. responseType = :" + xhr.responseType);
			console.log("Bnotes: performLogin: other state. responseXML = :" + xhr.responseXML);
			console.log("Bnotes: performLogin: other state. status = :" + xhr.status);
			console.log("Bnotes: performLogin: other state. statusText = :" + xhr.statusText);
			console.log("Bnotes: performLogin: other state object = :" + xhr);
			console.log("Bnotes: execuperformLoginteRequest: other state object as JSON:" + JSON.stringify(xhr));
		}
	}.bind(xhr, this);
	var loginDto = new Object();
	loginDto.username = username;
	loginDto.password = password;

	var data = JSON.stringify(loginDto);
	console.log("Bnotes: performLogin: url = >>" + url + "<<")
	console.log("Bnotes: executeRequest: data = >>" + data + "<<")
	xhr.send(data);
};

executeRequest = function(request){
	var xhr = new XMLHttpRequest();
	console.log("Bnotes: executeRequest: 1")
	var url = "request";
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.setRequestHeader("Authorization", "Bearer " + app.jwt);
	xhr.onreadystatechange = function (vm) {
		if (xhr.readyState === 4 && xhr.status === 200) {
			processReply(xhr.responseText);
		}	else if (xhr.readyState === 4) {
			console.log("Bnotes: executeRequest: error = :" + xhr.readyState + ", " + xhr.status);
			console.log("Bnotes: executeRequest: other state. responseText = :" + xhr.responseText);
			console.log("Bnotes: executeRequest: other state. response = :" + xhr.response);
			console.log("Bnotes: executeRequest: other state. responseType = :" + xhr.responseType);
			console.log("Bnotes: executeRequest: other state. responseType = :" + xhr.responseType);
			console.log("Bnotes: executeRequest: other state. responseXML = :" + xhr.responseXML);
			console.log("Bnotes: executeRequest: other state. status = :" + xhr.status);
			console.log("Bnotes: executeRequest: other state. statusText = :" + xhr.statusText);
			console.log("Bnotes: executeRequest: other state object = :" + xhr);
			console.log("Bnotes: executeRequest: other state object as JSON:" + JSON.stringify(xhr));
		} else {
			// console.log("Bnotes: executeRequest: other state = :" + xhr.readyState + ", " + xhr.status);
			// console.log("Bnotes: executeRequest: other state. responseText = :" + xhr.responseText);
			// console.log("Bnotes: executeRequest: other state. response = :" + xhr.response);
			// console.log("Bnotes: executeRequest: other state. responseType = :" + xhr.responseType);
			// console.log("Bnotes: executeRequest: other state. responseType = :" + xhr.responseType);
			// console.log("Bnotes: executeRequest: other state. responseXML = :" + xhr.responseXML);
			// console.log("Bnotes: executeRequest: other state. status = :" + xhr.status);
			// console.log("Bnotes: executeRequest: other state. statusText = :" + xhr.statusText);
			// console.log("Bnotes: executeRequest: other state object = :" + xhr);
			// console.log("Bnotes: executeRequest: other state object as JSON:" + JSON.stringify(xhr));
		}
	}.bind(xhr, this);
	var data = JSON.stringify(request);
	console.log("Bnotes: executeRequest: url = >>" + url + "<<")
	console.log("Bnotes: executeRequest: data = >>" + data + "<<")
	xhr.send(data);
};

processReply = function(jsonString){
	console.log("processReply start reply:>>" + jsonString + "<<");
	var reply = JSON.parse(jsonString);
	var scope = app;

  if (reply.status == 403){
		app.mode = "login";
		app.loginMessage = "Server response: No Access, session expired, wrong username or wrong password"
		return;
	}

	app.mode = "view";

	if ((typeof reply.chapterToEdit != "undefined") && (reply.chapterToEdit != null)){
		app.editChapter(reply.chapterToEdit, reply.chapterToEditLevel, reply.chapterToEditTitle, reply.chapterToEditBody);
	}
	
	if ((typeof reply.chapters != "undefined") && (reply.chapters != null)){
		app.chapters = reply.chapters;
	}
	if ((typeof reply.editChapterMessage != "undefined") && (reply.editChapterMessage != null)){
		app.editChapterMessage = reply.editChapterMessage;
	}
	if ((typeof reply.documents != "undefined") && (reply.documents != null)){
		app.editChapterHierarchy = false;
		app.documents = reply.documents;

		if (app.documents.length == 0){
			app.newDocumentChooseName();			
		}

	}
	if ((typeof reply.documentTitle != "undefined") && (reply.documentTitle != null)){
		app.documentTitle = reply.documentTitle;
	}
	if ((typeof reply.userChoices != "undefined") && (reply.userChoices != null)){
		app.userChoices = reply.userChoices;
		app.mode = "grantAccess";
	}

	if ((typeof reply.selectedDocumentId != "undefined") && (reply.selectedDocumentId != null)){
		var useId = reply.selectedDocumentId;
		if (useId == -1){
			useId = null;
		}
		app.selectedDocumentId = useId;
		app.documentId = useId;
	}

	if ((typeof reply.possibleDocumentsToMoveTo != "undefined") && (reply.possibleDocumentsToMoveTo != null)){
		app.possibleDocumentsToMoveTo = reply.possibleDocumentsToMoveTo;
		app.mode = "chooseDocumentToMoveTo";
	}

	if ((typeof reply.alertMessage != "undefined") && (reply.alertMessage != null)){
		alert(reply.alertMessage); 
	}


}

