# bnotes
(we)b notes application based on Java Spring (Boot) and Vue 

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
 
## Description
A simple web application where the user can take notes organized as chapters in documents.
 
## Details
For details see the notes document: [Notes](https://github.com/pheyse/bnotes/blob/master/doc/Notes.odt)

## Screenshots
Login

![Login](https://github.com/pheyse/bnotes/blob/master/screenshots/login.png "Login")

Viewing a document

![View Document](https://github.com/pheyse/bnotes/blob/master/screenshots/document-view.png "View Document")

Editing a document

![Edit Document](https://github.com/pheyse/bnotes/blob/master/screenshots/document-edit.png "Edit Document")

## Change History
 - Version 1.0.0 (2020-04-07)
   - initial version 
 - Version 1.1.0 (2020-04-27)
   - using BrightMarkdown so that the text can be written in markdown style
   - favicon
   - documents can be shared between users
   - export web services and PowerShell script to export all server data
   - saving w/o closing the edit window 
 - Version 1.2.0 (2020-05-16)
   - using Spring Security (JWT)
   - images in documents: upload, delete, add to Bright Markdown code
   - images are downloaded in export service
   - updated to new Bright Markdown version
   - feature to move chapters to a different document
   - feature to duplicate chapters (incl. images)
   - nicer format of chapter headings within the Bright Markdown text

