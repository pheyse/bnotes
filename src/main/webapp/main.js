var app = new Vue({
  el: '#app',
  data: {
    message: 'Hello Vue!',
	todos: [
      { text: 'Learn JavaScript' },
      { text: 'Learn Vue' },
      { text: 'Build something awesome' }
    ],
    levels:[1, 2, 3, 4, 5, 6],
	mode: 'login', /*login, view, confirmDelete, confirmDeleteDocument, newDocumentChooseName, renameDocument*/
	documentTitle: 'This is the document title',
	documentId: null,
	userName: null,
	password: null,
	loginMessage: "",
	chapterToDelete: -1,
	chapterInEdit: -1,
	chapterInEditLevel: 0,
	chapterInEditTitle: "",
	chapterInEditBody: "",
	documentEditedTitle: "",
	selectedDocumentId: null,
	editChapterHierarchy: false,
	showDebugInfo: false,
	chapters2: [],
	chapters: [
      { title: 'Chapter One', indexLabel: '1.', level: 1, chapterId: 1, body: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' },
      { title: 'Chapter One Part 1', indexLabel: '1.1', level: 2, chapterId: 2, body: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' },
      { title: 'Chapter One Part 2', indexLabel: '1.2', level: 2, chapterId: 3, body: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' }, 
      { title: 'Chapter Two', indexLabel: '2.', level: 1, chapterId: 4, body: 'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.' }
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
  methods: { 
		toggleShowDebugInfo: function(){
			//: debug info disabled:
			//this.showDebugInfo = !this.showDebugInfo; 
		},
		login: function(){
			var request = createRequest("login");
			request.parameters["userName"] = this.userName;		
			executeRequest(request);
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
    editChapter: function (chapterId, chapterLevel, title, body) {
      this.message = "Edit chapter: " + chapterId;
			this.chapterInEdit = chapterId;
			this.chapterInEditLevel = chapterLevel;
			this.chapterInEditTitle = title;
			this.chapterInEditBody = body;
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
    saveChapter: function (chapterId, index) {
      this.message = "save chapter : " + chapterId + ", with text of index" + index;
	  	this.chapterInEdit = -1;
	  
			try{
			var request = createRequest("saveChapter");
				request.parameters["chapterId"] = chapterId;
				request.parameters["level"] = this.chapterInEditLevel;
				request.parameters["title"] = this.chapterInEditTitle;
				request.parameters["body"] = this.chapterInEditBody;
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
				
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
    }, 
    cancelEditChapter: function () {
      this.message = "cancel edit chapter";
	  this.chapterInEdit = -1;
		}, 
		loadDocumentsButtonClicked : function () {
			try{
				var request = createRequest("getDocuments");
				executeRequest(request);
			} catch (err){
				console.log("Error: " + errorToString(err));
			}
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
		request.parameters["userName"] = app.userName;
		request.parameters["password"] = app.password;
    return request;
};

executeRequest = function(request){
	var xhr = new XMLHttpRequest();
	console.log("Bnotes: executeRequest: 1")
	var url = "request";
	xhr.open("POST", url, true);
	xhr.setRequestHeader("Content-Type", "application/json");
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
		app.loginMessage = "Wrong username or password"
		return;
	}

	app.mode = "view";

	if ((typeof reply.chapterToEdit  != "undefined") && (reply.chapterToEdit != null)){
		app.editChapter(reply.chapterToEdit, reply.chapterToEditLevel, "", "");
	}
	
	if ((typeof reply.chapters  != "undefined") && (reply.chapters != null)){
		app.chapters = reply.chapters;
	}
	if ((typeof reply.documents  != "undefined") && (reply.documents != null)){
		app.editChapterHierarchy = false;
		app.documents = reply.documents;

		if (app.documents.length == 0){
			app.newDocumentChooseName();			
		}

	}
	if ((typeof reply.documentTitle != "undefined") && (reply.documentTitle != null)){
		app.documentTitle = reply.documentTitle;
	}
	if ((typeof reply.selectedDocumentId != "undefined") && (reply.selectedDocumentId != null)){
		var useId = reply.selectedDocumentId;
		if (useId == -1){
			useId = null;
		}
		app.selectedDocumentId = useId;
		app.documentId = useId;
	}

}

