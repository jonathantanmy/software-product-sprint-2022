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
  
    setElement.appendChild(image);
    setElement.appendChild(term);
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
  