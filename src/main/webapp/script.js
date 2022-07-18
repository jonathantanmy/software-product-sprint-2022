// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.



/** Fetches tasks from the server and adds them to the DOM. */
function loadTasks() {
    fetch('/list-tasks').then(response => response.json()).then((tasks) => {
      const taskListElement = document.getElementById('task-list');
      tasks.forEach((task) => {
        taskListElement.appendChild(createTaskElement(task));
      })
    });
  }
  
  /** Creates an element that represents a task, including its delete button. */
  function createTaskElement(task) {
    const taskElement = document.createElement('li');
    taskElement.className = 'task';
  
    const titleElement = document.createElement('span');
    titleElement.innerText = task.title;
  
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.innerText = 'Delete';
    deleteButtonElement.addEventListener('click', () => {
      deleteTask(task);
  
      // Remove the task from the DOM.
      taskElement.remove();
    });
  
    taskElement.appendChild(titleElement);
    taskElement.appendChild(deleteButtonElement);
    return taskElement;
  }
  
  /** Tells the server to delete the task. */
  function deleteTask(task) {
    const params = new URLSearchParams();
    params.append('id', task.id);
    fetch('/delete-task', {method: 'POST', body: params});
  }
  
  function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();
    //console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    //console.log('Name: ' + profile.getName());
    //console.log('Image URL: ' + profile.getImageUrl());
    //console.log('Email: ' + profile.getEmail()); // This is null if the 'email' scope is not present.
    $("#Name").text(profile.getName());
    $("#email").text(profile.getEmail());
    $("#image").attr('src', profile.getImageUrl());
    $(".data").css("display", "block");
    $("g-signin2").css("display", "none");
  }

 
  function signOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
      console.log('User signed out.');
      alert("you have signed out");
      $("g-signin2").css("display", "block");
      $(".data").css("display", "none");
    });
  }

  
/** Fetches tasks from the server and adds them to the DOM. */
function loadHomeSet() {
    fetch('/list-home-set').then(response => response.json()).then((sets) => {
      const setListElement = document.querySelector(".grid");
      sets.forEach((set) => {
        setListElement.appendChild(createSetElement(set));
      })
    });
  }


  //creates all the elements to store the terms 
  function createSetElement(set) {
    const setElement = document.createElement("div");
    setElement.className = "preview_box";
  
    
    const image = document.createElement("div");
    image.className = "preview_img";

    var hasImage = set.hasImage;


   
    const term = document.createElement("div");
    term.className = "copy_text";
    //or is it innerHTML
    term.innerText = set.term;
    
    
    if(hasImage){
        image.style.display="block";
        image.style.backgroundImage= `url('${set.url}')`;
        //if there is image, the text height shrink to 37px
        term.style.height = "37px";
    }
    
    // not adding the controls attribute to the audio element -> so don't needa hide it - is already has display none
    const audio = document.createElement("audio");
    //adds the source of the audio file 
    audio.setAttribute('src', "https://summer22-sps-23.uc.r.appspot.com/text-speech?textss=" + set.term);
    setElement.appendChild(image);
    setElement.appendChild(term);
    setElement.appendChild(audio);

    //when click on element-- should play the sound 
    setElement.addEventListener('click', function() {
        audio.play();
    }, false);

    return setElement;
  }
  
    
  
  // loads the search result into the search page
  function createSearchResult(set) {
    const searchResult = document.createElement("div");
    searchResult.className = "searchResult_box";
  
    const title = document.createElement("h2");
    title.className = "searchResult_title";
    title.innerText = set.setname; //gets the setname

    const creator = document.createElement("p");
    creator.className = "searchResult_creator";
    creator.innerText = set.creator;

    const termAmount = document.createElement("p");
    termAmount.className = "searchResult_termAmount";
    termAmount.innerText = set.term_amount;

    const link = document.createElement("a");
    link.className = "setLink"
    link.href = "viewSet.html?kind="+ set.set_database +"&C="+ set.creator + "&A="+ set.term_amount +"&T="+set.setname;
    console.log(link.href);
    searchResult.appendChild(title);
    searchResult.appendChild(creator);
    searchResult.appendChild(termAmount);
    searchResult.appendChild(link);

    //makes the whole element a link=> when clicked should redirect to a page that will show all the terms
    searchResult.addEventListener('click', function() {
        location.href = link.href
    }, false);
    return searchResult;
  }
  
  
  // loads the search result into the search page (modified versiono fthe createSearchResult)
  function createMySetsResult(set) {
    const searchResult = document.createElement("div");
    searchResult.className = "searchResult_box";
  
    const title = document.createElement("h2");
    title.className = "searchResult_title";
    title.innerText = set.setname; //gets the setname

    const creator = document.createElement("p");
    creator.className = "searchResult_creator";
    creator.innerText = set.creator;

    const termAmount = document.createElement("p");
    termAmount.className = "searchResult_termAmount";
    termAmount.innerText = set.term_amount;

    const link = document.createElement("a");
    link.className = "setLink"
    link.href = "viewSet.html?kind="+ set.set_database +"&C="+ set.creator + "&A="+ set.term_amount +"&T="+set.setname;
    console.log(link.href);
    
    const edit = document.createElement("div");
    edit.className = "mysets_edit";

    edit.innerHTML=`<a href= "editSet.html?kind=${set.set_database}&T=${set.setname}&Id=${set.id}&uid=${set.uid}"><i class='fa fa-pencil'></i>`;
    
    searchResult.appendChild(title);
    searchResult.appendChild(creator);
    searchResult.appendChild(termAmount);
    searchResult.appendChild(link);
    searchResult.appendChild(edit);
 
    //makes the whole element a link=> when clicked should redirect to a page that will show all the terms
    searchResult.addEventListener('click', function() {
        location.href = link.href
    }, false);

    return searchResult;
  }
  
  // creates all the element to load into on the edit set page 
  function editSetElement(set){
    const duplicate_row = document.createElement("div");
    duplicate_row.className = "duplicate-row";

    //the remove button [willl be delete since the term already existed]
    const remove_button = document.createElement("button");
    remove_button.className = "old_btn-remove";
    remove_button.innerText="DELETE"

    // the input box that holds the term string/value
    const input_input_box = document.createElement("div");
    input_input_box.className="input_input_box";
    input_input_box.innerHTML=` 
    <input type="hidden" name ="set_id" class="set_id" value= ${set.id} />
    <input
    type="text"
    class="term_input"
    name="old_message"
    placeholder="TERM"
    value = ${set.term}
  />`;


  // the upload img element 
  const upload_img_input_box = document.createElement("div");
  upload_img_input_box.className="upload_img_input_box";

  var img_msg;

  if (set.hasImage){ // if has image,parse the exisiting img name from the database to only showcase the img name
      var imageName = set.imageName;
      var img_length = imageName.length();//get length of the imagename
      var subtring_length = img_length-13; //subtract 13 because the timestamp is 13 characters
      var actual_imgName =  imageName.substring(0, subtring_length)
      img_msg = actual_imgName;
  }else {
    img_msg = "No image chosen, yet"
  }

  upload_img_input_box.innerHTML=`<input
  class="old_upload_img_input"
  type="file"
  name="image"
  accept="image/*"
/>
<input type="hidden" name ="had_img" class="had_img" value= "${set.hasImage}" />
<input type="hidden" name ="changed_img" class="changed_img" value="false" />
<input type="hidden" name ="old_img_name" class="old_img_name" value="${set.imageName}" />
<button type="button" class="old_custom_img_input">
  CHOOSE A IMAGE
</button>
<span class="custom_input_file_text"
  >${img_msg} </span
>`;

//create the preview container
const preview_container = document.createElement("div");
preview_container.className="preview_container";
const preview_label = document.createElement("label");
preview_label.innerText = "Preview:";

const preview_box = document.createElement("div");
preview_box.className="preview_box";

const preview_img = document.createElement("div");
preview_img.className="preview_img";


const copy_text = document.createElement("div");
copy_text.className="copy_text";
copy_text.innerText=set.term;

const audio = document.createElement("audio");
audio.setAttribute("src","https://summer22-sps-23.uc.r.appspot.com/text-speech?textss=" + set.term )

if(set.hasImage){
    preview_img.style.backgroundImage=`url('${set.url}')`;
    preview_img.style.display ="block";
    copy_text.style.height = "37px";
}

preview_container.appendChild(preview_label);
preview_container.appendChild(preview_box);
preview_box.appendChild(preview_img);
preview_box.appendChild(copy_text);
preview_box.appendChild(audio);

duplicate_row.appendChild(remove_button);

duplicate_row.appendChild(input_input_box);

duplicate_row.appendChild(upload_img_input_box);

duplicate_row.appendChild(preview_container);

return duplicate_row;

  }