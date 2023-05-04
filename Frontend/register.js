// This function is used to register the user.
// It takes the contents of the username and password fields and submits it to the middleware
async function registerUser() {
    // Get the login form and the after that the values from the username and password fields.
    const loginForm = document.getElementById("register-form");
    const username = loginForm.username.value;
    const password = loginForm.password.value;
    // Check if the password field is empty
    if (loginForm.password.value === "") {
        alert("Passwort kann nicht leer sein!")
        throw new Error('Error: Empty password field');
    }
    // Create a json object and fill it with that values
    let dataJson = JSON.parse('{"username": "user", "password": "pass"}')
    dataJson.username = username;
    dataJson.password = password;
    // Start of the POST request
    // Create a url variable for better code readability
    let url = "https://pva1.siebert.cloud/backend/writeUser"
    // Initialize the request with the header and add the body inform of the json object created earlier turned back into a string
    await fetch(url, {
        method: 'POST', headers: {
            'Content-Type': 'application/json'
        }, body: JSON.stringify(dataJson)
    }).then((res) => {
        // Start of the response handling
        // If the http statuscode is 400 then something is wrong with the middleware
        // If that happens then an alert will be shown and an error will be logged
        if (res.status === 400) {
            alert("Registrierung fehlgeschlagen. Bitte erneut versuchen!")
            throw new Error('Error: Registration failed');
            // If the statuscode is 418 then the user doesn't exist
            // If that happens then an alert will be shown and an error will be logged
        } else if (res.status === 418) {
            alert("E-Mail Adresse bereits vorhanden. Bitte erneut versuchen!")
            throw new Error('Error: Email already exists');
        }
        // If no error is thrown then the return value is saven in a variable
        return res.json();

    })
        .catch(error => console.log(error))
        .then(res => this.dataReceived = res)
    // The returnvalue will then be written into the session storage, signaling the website that the user is now logged in
    sessionStorage.setItem("user_id", this.dataReceived["response"])
    // Lastly reload the page so the user is shown as logged in
    alert("Registrierung abgeschlossen!")
    location.reload()
}

// This function simply shows, hides and alters the text of certain elements on the webpage on load
function checkLoginStatus() {
    // If session storage item exists, meaning the user is logged in....
    if (sessionStorage.getItem("user_id") === null) {
        document.getElementById("loginStatus").style.color = "red";
        document.getElementById("loginStatus").innerHTML = "Nicht angemeldet";
        document.getElementById("register-form").style.display = "block";
        document.getElementById('logoutButton').style.visibility = 'hidden';
        document.getElementById("infoText").innerHTML = "";
    } else {
        // if the session storage item doesn't exist, meaning the user isn't logged in ...
        document.getElementById("loginStatus").style.color = "green";
        document.getElementById("loginStatus").innerHTML = "Angemeldet mit User_ID: " + sessionStorage.getItem("user_id");
        document.getElementById("register-form").style.display = "none";
        document.getElementById('logoutButton').style.visibility = 'visible';
        document.getElementById("infoText").innerHTML = "Sie sind derzeit angemeldet. Keine Registrierung möglich!";
    }
}

// This function is for login the user out.
// It does that by removing the user_id object from the session storage and reloading the page
function logoutUser() {
    sessionStorage.removeItem("user_id");
    location.reload()
}